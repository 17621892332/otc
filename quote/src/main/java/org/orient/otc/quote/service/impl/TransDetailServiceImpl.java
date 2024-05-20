package org.orient.otc.quote.service.impl;

import cn.hutool.extra.cglib.CglibUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.orient.otc.api.client.feign.BankCardInfoClient;
import org.orient.otc.api.openapi.feign.TransDetailClient;
import org.orient.otc.api.openapi.vo.TransDetailVo;
import org.orient.otc.api.quote.enums.CapitalDirectionEnum;
import org.orient.otc.quote.dto.capitalrecords.CapitalRecordsAddDto;
import org.orient.otc.quote.dto.capitalrecords.CapitalRecordsDeleteDto;
import org.orient.otc.quote.dto.daily.transdetail.StatusConvertDto;
import org.orient.otc.quote.dto.daily.transdetail.TransDetailPageDto;
import org.orient.otc.quote.entity.TransDetail;
import org.orient.otc.quote.exeption.BussinessException;
import org.orient.otc.quote.mapper.TransDetailMapper;
import org.orient.otc.quote.service.CapitalRecordsService;
import org.orient.otc.quote.service.TransDetailService;
import org.orient.otc.quote.util.MybatisPlusUtil;
import org.orient.otc.quote.vo.transdetail.TransDetailListVO;
import org.orient.otc.quote.vo.transdetail.TransDetailVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransDetailServiceImpl extends ServiceImpl<TransDetailMapper, TransDetail> implements TransDetailService {
    @Resource
    private TransDetailClient transDetailClient;
    @Resource
    private TransDetailMapper transDetailMapper;
    @Resource
    private CapitalRecordsService capitalRecordsService;
    @Resource
    private BankCardInfoClient bankCardInfoClient;
    public String getTransDetail() {
        // 从 erp系统 获取资金数据
        List<TransDetailVo> transDetail = transDetailClient.getTransDetail();
        if(CollectionUtils.isEmpty(transDetail)){
            return "同步成功";
        }
        LambdaQueryWrapper<TransDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TransDetail::getIsconfirm,0);
        List<TransDetail> allTransDetails = transDetailMapper.selectList(queryWrapper);
        // 将 TransDetail 实体对象转换为 originalId 的集合，并返回
        Set<String> originalIdsInDatabase = allTransDetails.stream()
                .map(TransDetail::getOriginalId)
                .collect(Collectors.toSet());
        // 过滤掉已经存在于数据库中的数据
        List<TransDetailVo> filteredTransDetail = transDetail.stream()
                .filter(vo -> !originalIdsInDatabase.contains(vo.getId()))
                .collect(Collectors.toList());
        // 获取从 ERP 系统拉回的 ID 集合
        Set<String> originalIdsInErp = transDetail.stream()
                .map(TransDetailVo::getId)
                .collect(Collectors.toSet());
        // 找出数据库中存在但在 erp 系统中不存在的数据，并且状态为0的数据
        List<TransDetail> missingTransDetail = allTransDetails.stream()
                .filter(vo -> !originalIdsInErp.contains(vo.getOriginalId()))
                .filter(vo -> vo.getIsconfirm() == 0)
                .peek(vo -> {
                    vo.setIsDeleted(1);
                    vo.setIsconfirm(1);
                })
                .collect(Collectors.toList());
        // 将 filteredTransDetail 转换为 TransDetail 对象列表，并设置 originalId 属性
        List<TransDetail> dbList = CglibUtil.copyList(filteredTransDetail, TransDetail::new, (vo, db) -> {
            db.setOriginalId(vo.getId());
            db.setHappentime(vo.getCreatetime());
            db.setBizdate(vo.getBizdate().toLocalDate());
        });
        // 合并 missingTransDetail 和 dbList 两个集合
        List<TransDetail> allToUpdate = new ArrayList<>();
        allToUpdate.addAll(missingTransDetail);
        allToUpdate.addAll(dbList);
        // 批量更新到数据库
        saveOrUpdateBatch(allToUpdate);
        return "同步成功";
    }

    @Override
    public IPage<TransDetailListVO> getListByPage(TransDetailPageDto dto) {
        LocalDate bizdatestart = dto.getBizdatestart();
        LocalDate bizdateend = dto.getBizdateend();
        LocalDate transdatestart = dto.getTransdatestart();
        LocalDate transdateend = dto.getTransdateend();
        // 创建一个 TransDetail 实体对象，并将 TransDetailPageDto 对象中的属性复制到该对象中
        TransDetail transDetail = new TransDetail();
        BeanUtils.copyProperties(dto, transDetail);
        // 根据 TransDetail 实体对象构建查询包装器
        QueryWrapper<TransDetail> queryWrapper = MybatisPlusUtil.queryWrapperBuilder(transDetail);
        if (bizdatestart != null) {
            queryWrapper.ge("bizdate", bizdatestart);
        }
        if (bizdateend != null) {
            queryWrapper.le("bizdate", bizdateend);
        }
        if (transdatestart != null) {
            queryWrapper.ge("transdate", transdatestart);
        }
        if (transdateend != null) {
            queryWrapper.le("transdate", transdateend);
        }
        queryWrapper.eq("isDeleted",0);
        queryWrapper.orderByDesc("happentime");
        // 根据查询包装器进行分页查询
        IPage<TransDetail> ipage = this.page(new Page<>(dto.getPageNo(), dto.getPageSize()), queryWrapper);
        // 将查询结果中的 TransDetail 实体对象转换为 TransDetailListVO 视图对象，并返回分页结果
        return ipage.convert(item -> {
            TransDetailListVO vo = new TransDetailListVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        });
    }

    @Override
    public TransDetailVO getDetail(StatusConvertDto dto) {
        TransDetailVO transDetailVO = new TransDetailVO();
        String originalId = dto.getOriginalId();
        // 根据原始 ID 查询数据库中的交易详情记录
        TransDetail transDetail = getOne(new LambdaQueryWrapper<TransDetail>().eq(TransDetail::getOriginalId, originalId));
        // 将查询到的交易详情对象的属性复制到交易详情值对象中
        BeanUtils.copyProperties(transDetail, transDetailVO);
        return transDetailVO;
    }

    @Override
    @Transactional
    public String statusConvertY(StatusConvertDto dto) {
        String originalId = dto.getOriginalId();
        TransDetail transDetail = getOne(new LambdaQueryWrapper<TransDetail>().eq(TransDetail::getOriginalId, originalId));
        CapitalRecordsAddDto capitalRecordsAddDto = new CapitalRecordsAddDto();
        capitalRecordsAddDto.setMoney(transDetail.getCreditamount());
        capitalRecordsAddDto.setDirection(CapitalDirectionEnum.in);
        capitalRecordsAddDto.setCurrency("人民币");
        Integer clientIdByBankAccount = bankCardInfoClient.getClientIdByBankAccount(transDetail.getOppbanknumber());
        if (clientIdByBankAccount == null) {
            BussinessException.E_300506.assertTrue(Boolean.FALSE);
        }
        capitalRecordsAddDto.setClientId(clientIdByBankAccount);
        capitalRecordsAddDto.setBankAccount(transDetail.getOppbanknumber());
        capitalRecordsAddDto.setHappenTime(transDetail.getHappentime());
        capitalRecordsAddDto.setVestingDate(dto.getVestingDate());
        Integer capitalId = capitalRecordsService.addConfirm(capitalRecordsAddDto);
        TransDetail transDetailNew = new TransDetail();
        transDetailNew.setIsconfirm(1);
        transDetailNew.setCapitalId(capitalId);
        transDetailNew.setTransdate(dto.getVestingDate());
        // 更新操作
        boolean update = update(transDetailNew, new LambdaUpdateWrapper<TransDetail>().eq(TransDetail::getOriginalId, originalId));
        // 如果更新失败，则直接抛出异常
        if (!update) {
            throw new RuntimeException("otc更新 TransDetail 失败");
        }
        Boolean convert = transDetailClient.statusConvertY(originalId);
        if (!convert) {
            throw new RuntimeException("erp更新 TransDetail 失败");
        }


        return "操作成功";
    }

    @Override
    @Transactional
    public String statusConvertN(StatusConvertDto dto) {
        String originalId = dto.getOriginalId();
        TransDetail transDetail = getOne(new LambdaQueryWrapper<TransDetail>().eq(TransDetail::getOriginalId, originalId));
        CapitalRecordsDeleteDto capitalRecordsDeleteDto = new CapitalRecordsDeleteDto();
        capitalRecordsDeleteDto.setId(transDetail.getCapitalId());
        capitalRecordsDeleteDto.setIsTransDetail(true);
        capitalRecordsService.delete(capitalRecordsDeleteDto);
        TransDetail transDetailNew = new TransDetail();
        transDetailNew.setIsconfirm(0);
        transDetailNew.setTransdate(null);
        // 更新操作
        boolean update = update(transDetailNew, new LambdaUpdateWrapper<TransDetail>().eq(TransDetail::getOriginalId, originalId).set(TransDetail::getTransdate, null));
        // 如果更新失败，则直接抛出异常
        if (!update) {
            throw new RuntimeException("更新 TransDetail 失败");
        }
        Boolean convert = transDetailClient.statusConvertN(originalId);
        if (!convert) {
            throw new RuntimeException("更新 TransDetail 失败");
        }
        return "撤销确认暂不支持同步镒链，请前往镒链手动删除资金记录";
    }

}
