package org.orient.otc.client.feign;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.orient.otc.api.client.feign.BankCardInfoClient;
import org.orient.otc.client.dto.BankCardInfoQueryByClientIdDto;
import org.orient.otc.client.entity.BankCardInfo;
import org.orient.otc.client.service.BankCardInfoService;
import org.orient.otc.client.vo.BankCardInfoVO;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bankCardInfoClient")
public class BankCardInfoFeignController implements BankCardInfoClient {
    @Autowired
    BankCardInfoService bankCardInfoService;

    @Override
    public List<org.orient.otc.api.client.vo.BankCardInfoVO> getBankCardInfoByClientId(Integer id) {
        BankCardInfoQueryByClientIdDto dto = new BankCardInfoQueryByClientIdDto();
        dto.setClientId(id);
        List<BankCardInfoVO> list = bankCardInfoService.getByClientId(dto);
        if (CollectionUtils.isNotEmpty(list)) {
            List<org.orient.otc.api.client.vo.BankCardInfoVO> returnList = list.stream().map(item->{
                org.orient.otc.api.client.vo.BankCardInfoVO vo = new org.orient.otc.api.client.vo.BankCardInfoVO();
                BeanUtils.copyProperties(item,vo);
                return vo;
            }).collect(Collectors.toList());
            return returnList;
        } else {
            return null;
        }
    }
    @Override
    public Integer getClientIdByBankAccount(String bankAccount) {
        LambdaQueryWrapper<BankCardInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BankCardInfo::getBankAccount, bankAccount).eq(BankCardInfo::getIsDeleted, IsDeletedEnum.NO).eq(BankCardInfo::getIsEffective,1);
        BankCardInfo bankCardInfo = bankCardInfoService.getOne(queryWrapper);
        if (bankCardInfo != null) {
            return bankCardInfo.getClientId();
        } else {
            // 查询结果为空，返回 null 或者抛出异常，根据业务需求而定
            return null;
        }
    }
}
