package org.orient.otc.quote.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.orient.otc.api.client.feign.ClientClient;
import org.orient.otc.api.dm.feign.UnderlyingManagerClient;
import org.orient.otc.api.dm.feign.VarietyClient;
import org.orient.otc.api.dm.vo.UnderlyingManagerVO;
import org.orient.otc.api.market.feign.MarketClient;
import org.orient.otc.api.quote.enums.CollateralEnum;
import org.orient.otc.api.user.feign.UserClient;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.database.entity.BaseEntity;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.quote.dto.collateral.*;
import org.orient.otc.quote.entity.Collateral;
import org.orient.otc.quote.exeption.BussinessException;
import org.orient.otc.quote.mapper.CollateralMapper;
import org.orient.otc.quote.service.CollateralService;
import org.orient.otc.quote.vo.collateral.CollateralVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CollateralServiceImpl extends ServiceImpl<CollateralMapper, Collateral> implements CollateralService {

    @Resource
    CollateralMapper collateralMapper;

    @Resource
    ClientClient client;

    @Resource
    UnderlyingManagerClient underlyingManagerClient;

    @Resource
    MarketClient marketClient;

    @Resource
    VarietyClient varietyClient;

    @Resource
    UserClient userClient;

    /**
     * 获取查询构造器
     * @param dto 请求参数
     * @return 查询构造器
     */
    public LambdaQueryWrapper<Collateral> getLambdaQueryWrapper(CollateralPageListDto dto){
        LambdaQueryWrapper<Collateral> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Collateral::getIsDeleted, IsDeletedEnum.NO);
        // 前段只能选择到日期 , 后端匹配到时分秒 , 所以开始日期加上0时0份0秒,结束日期加上23时59分59秒
        if (dto.getStartCollateralDate()!=null) {
            LocalDate startCollateralDate = dto.getStartCollateralDate();
            LocalDateTime localDateTime = LocalDateTime.of(
                    startCollateralDate.getYear(),
                    startCollateralDate.getMonth(),
                    startCollateralDate.getDayOfMonth(),
                    0,
                    0,
                    0
            );
            lambdaQueryWrapper.ge(Collateral::getCollateralTime, localDateTime);
        }
        if (dto.getEndCollateralDate()!=null) {
            LocalDate endCollateralDate = dto.getEndCollateralDate();
            LocalDateTime localDateTime = LocalDateTime.of(
                    endCollateralDate.getYear(),
                    endCollateralDate.getMonth(),
                    endCollateralDate.getDayOfMonth(),
                    23,
                    59,
                    59
            );
            lambdaQueryWrapper.le(Collateral::getCollateralTime, localDateTime);
        }
        if (dto.getStartRedemptionDate()!=null){
            LocalDate startRedemptionDate = dto.getStartRedemptionDate();
            LocalDateTime localDateTime = LocalDateTime.of(
                    startRedemptionDate.getYear(),
                    startRedemptionDate.getMonth(),
                    startRedemptionDate.getDayOfMonth(),
                    0,
                    0,
                    0
            );
            lambdaQueryWrapper.ge(Collateral::getRedemptionTime, localDateTime);
        }
        if (dto.getEndRedemptionDate()!=null){
            LocalDate endRedemptionDate = dto.getEndRedemptionDate();
            LocalDateTime localDateTime = LocalDateTime.of(
                    endRedemptionDate.getYear(),
                    endRedemptionDate.getMonth(),
                    endRedemptionDate.getDayOfMonth(),
                    23,
                    59,
                    59
            );
            lambdaQueryWrapper.le(Collateral::getRedemptionTime, localDateTime);
        }
        lambdaQueryWrapper.in(CollectionUtils.isNotEmpty(dto.getClientIdList()),Collateral::getClientId, dto.getClientIdList());
        lambdaQueryWrapper.in(CollectionUtils.isNotEmpty(dto.getCollateralStatusList()),Collateral::getCollateralStatus, dto.getCollateralStatusList());
        lambdaQueryWrapper.in(CollectionUtils.isNotEmpty(dto.getExecuteStatusList()),Collateral::getExecuteStatus, dto.getExecuteStatusList());
        lambdaQueryWrapper.in(CollectionUtils.isNotEmpty(dto.getVarietyIdList()),Collateral::getVarietyId, dto.getVarietyIdList());
        return lambdaQueryWrapper;
    }

    @Override
    public IPage<CollateralVO> selectListByPage(CollateralPageListDto dto) {
        LambdaQueryWrapper<Collateral> lambdaQueryWrapper = getLambdaQueryWrapper(dto);
        lambdaQueryWrapper.orderByDesc(Collateral::getCreateTime);
        IPage<Collateral> ipage = this.page(new Page<>(dto.getPageNo(),dto.getPageSize()),lambdaQueryWrapper);
        IPage<CollateralVO> returnPage = ipage.convert(item->{
            CollateralVO vo = new CollateralVO();
            BeanUtils.copyProperties(item,vo);
            return vo;
        });
        // 品种map
        Map<Integer,String> varietyMap = varietyClient.getVarietyNameMap();
        // 设置客户名称
        Set<Integer> clientIdSet = returnPage.getRecords().stream().map(CollateralVO::getClientId).collect(Collectors.toSet());
        Map<Integer,String> clientMap = client.getClientMapByIds(clientIdSet);
        // 设置创建人和修改人名称
        Set<Integer> createrUserIdSet = returnPage.getRecords().stream().map(BaseEntity::getCreatorId).collect(Collectors.toSet());
        Set<Integer> updateUserIdSet = returnPage.getRecords().stream().map(BaseEntity::getUpdatorId).collect(Collectors.toSet());
        Set<Integer> userIdSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(createrUserIdSet)) {
            userIdSet.addAll(createrUserIdSet);
        }
        if (CollectionUtils.isNotEmpty(updateUserIdSet)) {
            userIdSet.addAll(updateUserIdSet);
        }
        Map<Integer,String> userMap = userClient.getUserMapByIds(userIdSet);
        for (CollateralVO vo : returnPage.getRecords()) {
            vo.setClientName(clientMap.get(vo.getClientId()));
            vo.setCollateralStatusName(vo.getCollateralStatus().getValue());
            vo.setExecuteStatusNAme(vo.getExecuteStatus().getValue());
            vo.setVarietyName(varietyMap.get(vo.getVarietyId()));
            vo.setCreatorName(userMap.get(vo.getCreatorId()));
            vo.setUpdatorName(userMap.get(vo.getUpdatorId()));
        }
        return returnPage;
    }

    @Override
    public String add(CollateralAddDto dto) {
        Collateral collateral = new Collateral();
        BeanUtils.copyProperties(dto,collateral);
        collateral.setExecuteStatus(CollateralEnum.ExecuteStatusEnum.unconfirmed); // 新增的执行状态=未确认
        collateral.setCollateralStatus(CollateralEnum.CollateralStatusEnum.collateral); // 新增的抵押状态=抵押
        collateral.setRedemptionTime(null);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        // 出入金单号 = 当前时间的年月日时分秒毫秒
        String capitalCode = sdf.format(calendar.getTime())+calendar.get(Calendar.MILLISECOND);
        collateral.setCapitalCode(capitalCode);
        collateralMapper.insert(collateral);
        return "操作成功";
    }

    @Override
    public HttpResourceResponse check(CollateralCheckDto dto) {
        LambdaQueryWrapper<Collateral> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Collateral::getIsDeleted, IsDeletedEnum.NO);
        lambdaQueryWrapper.in(Collateral::getId, dto.getIdList());
        lambdaQueryWrapper.ne(Collateral::getExecuteStatus, dto.getExecuteStatus()); // 查询修改前后的状态不一致的记录
        List<Collateral> list = collateralMapper.selectList(lambdaQueryWrapper);
        if (dto.getIdList().size() != list.size()){ // 校验修改前后的状态是否一致
            if (dto.getExecuteStatus() == CollateralEnum.ExecuteStatusEnum.confirmed) {
                return  HttpResourceResponse.error(-1,"已确认的记录不能再次确认");
            } else if (dto.getExecuteStatus() == CollateralEnum.ExecuteStatusEnum.refuse) {
                return  HttpResourceResponse.error(-1,"已拒绝的记录不能再次拒绝");
            } else {
                return HttpResourceResponse.success("操作成功");
            }
        } else {
            // 修改前状态=已拒绝的数量
            int refuseSize = list.stream().filter(item->item.getExecuteStatus()==CollateralEnum.ExecuteStatusEnum.refuse).collect(Collectors.toSet()).size();
            if (refuseSize>0) {
                BussinessException.E_300102.assertTrue(false,"已拒绝的不可以再审核");
            }
            LambdaUpdateWrapper<Collateral> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.in(Collateral::getId,dto.getIdList());
            lambdaUpdateWrapper.set(Collateral::getExecuteStatus,dto.getExecuteStatus());
            collateralMapper.update(null,lambdaUpdateWrapper);
        }
        return HttpResourceResponse.success("操作成功");
    }

    /**
     * 赎回操作
     * 把已确认的抵押记录进行赎回操作 , 修改执行状态为未确认 , 抵押状态为赎回
     * @param dto
     * @return
     */
    @Override
    public HttpResourceResponse redemption(CollateralRedemptionDto dto) {
        Collateral collateral = collateralMapper.selectById(dto.getId());
        if (collateral.getExecuteStatus() != CollateralEnum.ExecuteStatusEnum.confirmed) {
            return HttpResourceResponse.error(-1,"不是已确认状态的抵押记录,不能进行赎回操作");
        } else {
            /**
             * todo 赎回操作要校验客户的实际可用资金 是否>抵押品抵押价值 实际可用资金=现金(期初结存和期末结存有关)-保证金占用
             * 若小于给出提示:
             * 执行失败，客户:xxxx 赎回抵押品:品种名称，实际可用资金:xxxx，小于抵押品抵押价值;
             */
            LambdaUpdateWrapper<Collateral> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(Collateral::getId,dto.getId());
            lambdaUpdateWrapper.set(Collateral::getCollateralStatus,CollateralEnum.CollateralStatusEnum.redemption);
            lambdaUpdateWrapper.set(Collateral::getRedemptionTime,LocalDateTime.now());
            lambdaUpdateWrapper.set(Collateral::getExecuteStatus, CollateralEnum.ExecuteStatusEnum.unconfirmed);
            collateralMapper.update(null,lambdaUpdateWrapper);
            return HttpResourceResponse.success("操作成功");
        }
    }

    @Override
    @Transactional
    public String update(CollateralUpdateDto dto) {
        Collateral entity = collateralMapper.selectById(dto.getId());
        if (entity.getExecuteStatus() == CollateralEnum.ExecuteStatusEnum.confirmed) {
            BussinessException.E_300102.assertTrue(false,"已确认的抵押品不可以再修改");
        }
        Collateral collateral = new Collateral();
        BeanUtils.copyProperties(dto,collateral);
        collateral.setExecuteStatus(CollateralEnum.ExecuteStatusEnum.unconfirmed);
        this.saveOrUpdate(collateral);
        return "操作成功";
    }

    @Override
    public String updateMarketPrice(CollateralUpdateMarketPriceDto dto) {
        Collateral collateral = collateralMapper.selectById(dto.getId());
        LambdaUpdateWrapper<Collateral> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Collateral::getId,dto.getId());
        lambdaUpdateWrapper.set(Collateral::getMarkPrice,dto.getMarkPrice());
        lambdaUpdateWrapper.set(Collateral::getCollateralPrice,dto.getMarkPrice().multiply(collateral.getQuantity()).multiply(collateral.getRate()).divide(new BigDecimal(100),2, RoundingMode.HALF_UP));
        collateralMapper.update(null,lambdaUpdateWrapper);
        return "操作成功";
    }

    @Override
    public BigDecimal getMarketPrice(CollateralGetMarketPriceDto dto) {
        // 获取品种对应的合约信息
        List<UnderlyingManagerVO> underlyingManagerVOList = underlyingManagerClient.getUnderlyingListByVarietyId(dto.getVarietyId());
        if (CollectionUtils.isNotEmpty(underlyingManagerVOList)){
            // 最近到期的合约的收盘价
            underlyingManagerVOList.sort(Comparator.comparing(UnderlyingManagerVO::getUnderlyingCode));
            UnderlyingManagerVO underlyingManagerVo = underlyingManagerVOList.get(0);
            return  marketClient.getSettlementPriceByUnderlyingCode(underlyingManagerVo.getUnderlyingCode());
        }
        return BigDecimal.ZERO;
    }

    @Override
    public  Map<Integer,BigDecimal> getCollateralPrice(Set<Integer> clientIdList, LocalDate endDate) {
        LambdaQueryWrapper<Collateral> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(clientIdList!=null && !clientIdList.isEmpty(),Collateral::getClientId,clientIdList);
        lambdaQueryWrapper.le(endDate!=null,Collateral::getCollateralTime,endDate);
        lambdaQueryWrapper.and(
                wrapper -> wrapper
                        .and(
                                //处于质押状态并且还是已确认
                                query -> query.eq(Collateral::getCollateralStatus, CollateralEnum.CollateralStatusEnum.collateral)
                                        .eq(Collateral::getExecuteStatus, CollateralEnum.ExecuteStatusEnum.confirmed)
                        )
                        .or(
                                //处于赎回状态，并且赎回时间在查询日期之后
                                query -> query.eq(Collateral::getCollateralStatus, CollateralEnum.CollateralStatusEnum.redemption)
                                        .gt(Collateral::getRedemptionTime, endDate)
                        )
        );
        List<Collateral> list = collateralMapper.selectList(lambdaQueryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.stream().collect(Collectors.toMap(Collateral::getClientId,Collateral::getCollateralPrice, BigDecimal::add));
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public List<Collateral> getCollateral(Integer clientId, LocalDate endDate) {
        LambdaQueryWrapper<Collateral> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Collateral::getClientId,clientId);
        lambdaQueryWrapper.le(endDate!=null,Collateral::getCollateralTime,endDate);
        return collateralMapper.selectList(lambdaQueryWrapper);
    }
}
