package org.orient.otc.quote.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.orient.otc.api.dm.feign.InstrumentClient;
import org.orient.otc.api.dm.feign.UnderlyingManagerClient;
import org.orient.otc.api.dm.vo.InstrumentInfoVo;
import org.orient.otc.api.dm.vo.UnderlyingManagerVO;
import org.orient.otc.api.quote.enums.TradeRiskCacularResultType;
import org.orient.otc.api.user.feign.AssetUnitClient;
import org.orient.otc.api.user.feign.ExchangeAccountClient;
import org.orient.otc.api.user.vo.AssetunitVo;
import org.orient.otc.api.user.vo.ExchangeAccountFeignVO;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.quote.dto.trade.ExchangeTradePageListDto;
import org.orient.otc.quote.entity.ExchangeTrade;
import org.orient.otc.quote.mapper.ExchangeTradeMapper;
import org.orient.otc.quote.service.ExchangeTradeService;
import org.orient.otc.quote.util.HutoolUtil;
import org.orient.otc.quote.vo.ExchangeTradeExportVo;
import org.orient.otc.quote.vo.ExchangeTradeVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 场内交易记录服务实现
 */
@Service
@Slf4j
public class ExchangeTradeServiceImpl implements ExchangeTradeService {
    @Resource
    ExchangeTradeMapper exchangeTradeMapper;

    @Resource
    AssetUnitClient assetUnitClient;

    @Resource
    InstrumentClient instrumentClient;

    @Resource
    ExchangeAccountClient exchangeAccountClient;

    @Resource
    UnderlyingManagerClient underlyingManagerClient;


    @Override
    public IPage<ExchangeTradeVo> selectOptionListByPage(ExchangeTradePageListDto dto) {
        LambdaQueryWrapper<ExchangeTrade> lambdaQueryWrapper = getLambdaQueryWrapper(dto);
        IPage<ExchangeTrade> ipage = exchangeTradeMapper.selectPage(new Page(dto.getPageNo(),dto.getPageSize()),lambdaQueryWrapper);
        if(ipage.getRecords()==null || ipage.getRecords().isEmpty()) {
            return ipage.convert(item-> new ExchangeTradeVo());
        }
        // 获取簿记账户
        Set<String> accountSet = ipage.getRecords().stream().map(ExchangeTrade::getInvestorID).collect(Collectors.toSet());
        Map<Integer,AssetunitVo> assetunitVoMap = getAssetunitVo(accountSet);
        // 获取合约
        Set<String> instIDs = ipage.getRecords().stream().map(ExchangeTrade::getInstrumentID).collect(Collectors.toSet());
        Map<String,InstrumentInfoVo> instrumentInfoMap = getInstrumentMap(instIDs);
        // 获取期权合约对应的标的信息
        Set<String> underlyingInstrIds = instrumentInfoMap.values().stream().filter(item->!StringUtils.isEmpty(item.getUnderlyingInstrId()) && item.getProductClass()==2).map(item-> item.getUnderlyingInstrId()).collect(Collectors.toSet());
        //Map<String,UnderlyingManagerVo> underlyingManagerMap = getUnderlyingManagerMap(underlyingInstrIds);
        Map<String,InstrumentInfoVo> underlyingManagerMap = getInstrumentMap(underlyingInstrIds);
        // 获取对冲账户
        Map<String, ExchangeAccountFeignVO> exchangeAccountMap= getExchangeAccountMap(accountSet);

        return ipage.convert(item->{
            ExchangeAccountFeignVO exchangeAccountFeignVO = exchangeAccountMap.get(item.getInvestorID());
            InstrumentInfoVo instrumentInfoVo = instrumentInfoMap.get(item.getInstrumentID().toUpperCase());
            ExchangeTradeVo vo = new ExchangeTradeVo();
            vo.setInstrumentId(item.getInstrumentID());
            if(null!= exchangeAccountFeignVO && assetunitVoMap.containsKey(exchangeAccountFeignVO.getAssetunitId())) {
                vo.setAssetUnitName(assetunitVoMap.get(exchangeAccountFeignVO.getAssetunitId()).getName());
            }
            if(null != instrumentInfoVo) {
                int productClass = instrumentInfoVo.getProductClass();
                if (productClass==1) {
                    vo.setTradeType(TradeRiskCacularResultType.european.getDesc());
                    vo.setUnderlyingCode(instrumentInfoVo.getInstrumentId());
                    vo.setUnderlyingName(instrumentInfoVo.getInstrumentName());
                } else {
                    InstrumentInfoVo underlyingManagerVo = underlyingManagerMap.get(instrumentInfoVo.getUnderlyingInstrId().toUpperCase());
                    vo.setTradeType(TradeRiskCacularResultType.option.getDesc());
                    vo.setUnderlyingCode(instrumentInfoVo.getUnderlyingInstrId());
                    if(underlyingManagerVo != null) {
                        vo.setUnderlyingName(underlyingManagerVo.getInstrumentName());
                    }
                }
                vo.setVolumeCount(item.getVolume()*instrumentInfoVo.getVolumeMultiple());
            }
            LocalDate tradeDate = LocalDate.parse(item.getTradingDay(), DateTimeFormatter.ofPattern("yyyyMMdd"));
            vo.setTradetingDay(tradeDate.format( DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            String directionName = null;
            if("1".equals(item.getDirection())) {
                if (!"0".equals(item.getOffsetFlag())) {
                    directionName = "多头平仓";
                } else if ("0".equals(item.getOffsetFlag())) {
                    directionName = "空头开仓";
                }
            } else if ("0".equals(item.getDirection())){
                if (!"0".equals(item.getOffsetFlag())) {
                    directionName = "空头平仓";
                } else if ("0".equals(item.getOffsetFlag())) {
                    directionName = "多头开仓";
                }
            }
            vo.setDirection(directionName);
            vo.setVolume(item.getVolume());
            vo.setPrice(item.getPrice());
            vo.setOperationTime(item.getCreateTime().format( DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            return vo;
        });
    }

    public LambdaQueryWrapper<ExchangeTrade> getLambdaQueryWrapper(ExchangeTradePageListDto dto){
        LambdaQueryWrapper<ExchangeTrade> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 簿记账户组不为空
        if (CollectionUtils.isNotEmpty(dto.getAssetUnitGroupIds())) {
            Set<Integer> gids = dto.getAssetUnitGroupIds();
            List<AssetunitVo> assetunitVoList = assetUnitClient.getAssetunitByGroupIds(gids); // 根据簿记账户组查询簿记账户信息
            Set<Integer> asseUniteIds = assetunitVoList.stream().map(AssetunitVo::getId).collect(Collectors.toSet());
            if(CollectionUtils.isNotEmpty(asseUniteIds)){ // 簿记账户不为空
                List<ExchangeAccountFeignVO> exchangeAccountFeignVOS = exchangeAccountClient.getVoByAssetUnitIds(asseUniteIds);// 根据簿记账户获取对冲账户
                List<String> accounts = exchangeAccountFeignVOS.stream().map(ExchangeAccountFeignVO::getAccount).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(accounts)){
                    lambdaQueryWrapper.in(ExchangeTrade::getInvestorID,accounts);
                } else {
                    lambdaQueryWrapper.eq(ExchangeTrade::getInvestorID,"返回一个查不到数据的wrapper");
                    return lambdaQueryWrapper;
                }
            } else {
                lambdaQueryWrapper.eq(ExchangeTrade::getInvestorID,"返回一个查不到数据的wrapper");
                return lambdaQueryWrapper;
            }
        }
        // 簿记账户不为空
        if (CollectionUtils.isNotEmpty(dto.getAssetUnitIds())) {
            List<ExchangeAccountFeignVO> exchangeAccountFeignVOS = exchangeAccountClient.getVoByAssetUnitIds(dto.getAssetUnitIds());// 根据簿记账户获取对冲账户
            List<String> accounts = exchangeAccountFeignVOS.stream().map(ExchangeAccountFeignVO::getAccount).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(accounts)){
                lambdaQueryWrapper.in(ExchangeTrade::getInvestorID,accounts);
            } else {
                lambdaQueryWrapper.eq(ExchangeTrade::getInvestorID,"返回一个查不到数据的wrapper");
                return lambdaQueryWrapper;
            }
        }

        if ("1".equals(dto.getDirection())) {
            lambdaQueryWrapper
                    .eq(ExchangeTrade::getDirection,0)
                    .eq(ExchangeTrade::getOffsetFlag,0);
        }else if ("2".equals(dto.getDirection())) {
            lambdaQueryWrapper
                    .eq(ExchangeTrade::getDirection,1)
                    .ne(ExchangeTrade::getOffsetFlag,0);
        }else if ("3".equals(dto.getDirection())) {
            lambdaQueryWrapper
                    .eq(ExchangeTrade::getDirection,1)
                    .eq(ExchangeTrade::getOffsetFlag,0);
        }else if ("4".equals(dto.getDirection())) {
            lambdaQueryWrapper
                    .eq(ExchangeTrade::getDirection,0)
                    .ne(ExchangeTrade::getOffsetFlag,0);
        }
        if  (null != dto.getTradeDateEnd() && null != dto.getTradeDateStart()) {
            lambdaQueryWrapper
                    .le(ExchangeTrade::getTradingDay,dto.getTradeDateEnd().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                    .ge(ExchangeTrade::getTradingDay,dto.getTradeDateStart().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        }
        // 以字符开头 , 后面接数字 , 再接C或-C或P或P- ,再以数字结尾
        //String regex = "^[a-z]+[0-9]+[-]?[C|P][-]?[0-9]+$"; // 期权正则
        if(!StringUtils.isEmpty(dto.getTraderType())){
            if(dto.getTraderType() == 2) { // 期权
                lambdaQueryWrapper.apply("REGEXP_LIKE (instrumentId,'^[a-z]+[0-9]+[-]?[C|P][-]?[0-9]+$')","");
            } else if (dto.getTraderType() == 1){ // 期货
                lambdaQueryWrapper.apply("REGEXP_LIKE (instrumentId,'^[a-z]+[0-9]+[-]?[C|P][-]?[0-9]+$') != 1","");
            }
        }
        // 标的代码过滤条件
        if(CollectionUtils.isNotEmpty(dto.getUnderlyingCodes())) {
            List<InstrumentInfoVo> list = instrumentClient.getInstrumentInfoByUndeingCodes(dto.getUnderlyingCodes());
            Set<String> instrumentId = list.stream().map(item->item.getInstrumentId().toUpperCase()).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(instrumentId)){
                lambdaQueryWrapper.in(ExchangeTrade::getInstrumentID,instrumentId);
            } else { // 查不到入参中标的代码对应的合约信息 , 返回一个查询结果为空的构造器
                lambdaQueryWrapper.eq(ExchangeTrade::getIsDeleted,IsDeletedEnum.NO)
                        .eq(ExchangeTrade::getIsDeleted,IsDeletedEnum.YES);
            }
        }
        /*lambdaQueryWrapper.orderByDesc(ExchangeTrade::getTradeDate);
        lambdaQueryWrapper.orderByDesc(ExchangeTrade::getTradeTime);*/
        lambdaQueryWrapper.orderByDesc(ExchangeTrade::getCreateTime);
        return lambdaQueryWrapper;
    }

    /**
     * 根据对冲账户获取簿记账户
     * key=簿记账户id , value=簿记账户信息
     * @param accounts
     * @return
     */
    public Map<Integer,AssetunitVo> getAssetunitVo(Set<String> accounts) {
        if (CollectionUtils.isEmpty(accounts)) {
            return new HashMap<>();
        }
        List<AssetunitVo> list = assetUnitClient.getVoByAccounts(accounts);
        return list.stream().collect(Collectors.toMap(AssetunitVo::getId, item->item,(v1, v2)->v2));
    }

    /**
     * 根据合约id获取合约信息
     * key=合约id , value=合约信息
     * @param instIDs
     * @return
     */
    public Map<String,InstrumentInfoVo> getInstrumentMap(Set<String> instIDs){
        if (CollectionUtils.isEmpty(instIDs)) {
            return new HashMap<>();
        }
        List<InstrumentInfoVo> list = instrumentClient.getInstrumentInfoByIds(instIDs);
        return list.stream().collect(Collectors.toMap(item->item.getInstrumentId().toUpperCase(),item->item,(v1,v2)->v2));
    }

    /**
     * 根据标的代码获取标的信息
     * key=标的代码 , value=标的信息
     * @param underlyingInstrIds
     * @return
     */
    public Map<String, UnderlyingManagerVO> getUnderlyingManagerMap(Set<String> underlyingInstrIds){
        if (CollectionUtils.isEmpty(underlyingInstrIds)) {
            return new HashMap<>();
        }
        List<UnderlyingManagerVO> list = underlyingManagerClient.getUnderlyingByCodes(underlyingInstrIds);
        return list.stream().collect(Collectors.toMap(item->item.getUnderlyingCode().toUpperCase(),item->item,(v1,v2)->v2));
    }

    /**
     * 获取对冲账户
     * key=account, value=对冲账户
     * @param accounts
     * @return
     */
    public Map<String, ExchangeAccountFeignVO> getExchangeAccountMap(Set<String> accounts){
        if (CollectionUtils.isEmpty(accounts)) {
            return new HashMap<>();
        }
        List<ExchangeAccountFeignVO> list = exchangeAccountClient.getVoByAccounts(accounts);
        return list.stream().collect(Collectors.toMap(ExchangeAccountFeignVO::getAccount, item->item,(v1, v2)->v2));
    }



    @Override
    public void tradeExport(ExchangeTradePageListDto dto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LambdaQueryWrapper<ExchangeTrade> lambdaQueryWrapper = getLambdaQueryWrapper(dto);
        List<ExchangeTrade> list = exchangeTradeMapper.selectList(lambdaQueryWrapper);
        if(list==null || list.isEmpty()) {
            throw new Exception("导出数据不能空!");
        }
        // 获取簿记账户
        Set<String> accountSet = list.stream().map(ExchangeTrade::getInvestorID).collect(Collectors.toSet());
        Map<Integer,AssetunitVo> assetunitVoMap = getAssetunitVo(accountSet);
        // 获取合约
        Set<String> instIDs = list.stream().map(ExchangeTrade::getInstrumentID).collect(Collectors.toSet());
        Map<String,InstrumentInfoVo> instrumentInfoMap = getInstrumentMap(instIDs);
        // 获取期权合约对应的标的信息
        Set<String> underlyingInstrIds = instrumentInfoMap.values().stream().filter(item->!StringUtils.isEmpty(item.getUnderlyingInstrId()) && item.getProductClass()==2).map(item->item.getUnderlyingInstrId()).collect(Collectors.toSet());

        //Map<String,UnderlyingManagerVo> underlyingManagerMap = getUnderlyingManagerMap(underlyingInstrIds);
        Map<String,InstrumentInfoVo> underlyingManagerMap = getInstrumentMap(underlyingInstrIds);
        // 获取对冲账户
        Map<String, ExchangeAccountFeignVO> exchangeAccountMap= getExchangeAccountMap(accountSet);

        List<ExchangeTradeExportVo> exportDataList = list.stream().map(
                item->{
                    ExchangeAccountFeignVO exchangeAccountFeignVO = exchangeAccountMap.get(item.getInvestorID());
                    InstrumentInfoVo instrumentInfoVo = instrumentInfoMap.get(item.getInstrumentID().toUpperCase());
                    ExchangeTradeExportVo vo = new ExchangeTradeExportVo();
                    vo.setInstrumentId(item.getInstrumentID());
                    if(null!= exchangeAccountFeignVO && assetunitVoMap.containsKey(exchangeAccountFeignVO.getAssetunitId())) {
                        vo.setAssetUnitName(assetunitVoMap.get(exchangeAccountFeignVO.getAssetunitId()).getName());
                    }
                    if(null != instrumentInfoVo) {
                        int productClass = instrumentInfoVo.getProductClass();
                        if (productClass==1) {
                            vo.setTradeType(TradeRiskCacularResultType.european.getDesc());
                            vo.setUnderlyingCode(instrumentInfoVo.getInstrumentId());
                            vo.setUnderlyingName(instrumentInfoVo.getInstrumentName());
                        } else {
                            InstrumentInfoVo underlyingManagerVo = underlyingManagerMap.get(instrumentInfoVo.getUnderlyingInstrId().toUpperCase());
                            vo.setTradeType(TradeRiskCacularResultType.option.getDesc());
                            vo.setUnderlyingCode(instrumentInfoVo.getUnderlyingInstrId());
                            if(null != underlyingManagerVo) {
                                vo.setUnderlyingName(underlyingManagerVo.getInstrumentName());
                            } else {
                                log.info("没有查到对应的标的信息,underlyingInstrId="+JSON.toJSON(instrumentInfoVo.getUnderlyingInstrId()));
                            }
                        }
                        vo.setVolumeCount(item.getVolume()*instrumentInfoVo.getVolumeMultiple());
                    }
                    LocalDate tradeDate = LocalDate.parse(item.getTradingDay(), DateTimeFormatter.ofPattern("yyyyMMdd"));
                    vo.setTradetingDay(tradeDate.format( DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    String directionName = null;
                    if("1".equals(item.getDirection())) {
                        if (!"0".equals(item.getOffsetFlag())) {
                            directionName = "多头平仓";
                        } else if ("0".equals(item.getOffsetFlag())) {
                            directionName = "空头开仓";
                        }
                    } else if ("0".equals(item.getDirection())){
                        if (!"0".equals(item.getOffsetFlag())) {
                            directionName = "空头平仓";
                        } else if ("0".equals(item.getOffsetFlag())) {
                            directionName = "多头开仓";
                        }
                    }
                    vo.setDirection(directionName);
                    vo.setVolume(item.getVolume());
                    vo.setPrice(item.getPrice());
                    vo.setOperationTime(item.getCreateTime().format( DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    return vo;
                }
        ).collect(Collectors.toList());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        // 文件名称 = 风险导出+时间戳
        String fileName = "交易流水"+sdf.format(calendar.getTime())+calendar.get(Calendar.MILLISECOND);
        HutoolUtil.export(exportDataList,fileName,"交易流水",ExchangeTradeExportVo.class,request,response);
    }
}
