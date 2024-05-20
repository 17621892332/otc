package org.orient.otc.client.service;

import org.orient.otc.client.dto.BankCardInfoAddDto;
import org.orient.otc.client.dto.BankCardInfoDeleteDto;
import org.orient.otc.client.dto.BankCardInfoQueryByClientIdDto;
import org.orient.otc.client.dto.BankCardInfoUpdateDto;
import org.orient.otc.client.entity.BankCardInfo;
import org.orient.otc.client.vo.BankCardInfoVO;
import org.orient.otc.common.database.config.IServicePlus;

import java.util.List;

public interface BankCardInfoService extends IServicePlus<BankCardInfo> {
    /**
     * 根据客户id查询银行账户信息
     * @param dto
     * @return
     */
    List<BankCardInfoVO> getByClientId(BankCardInfoQueryByClientIdDto dto);

    /**
     * 新增客户银行信息
     * @param dto
     * @return
     */
    String add(BankCardInfoAddDto dto);

    /**
     * 修改客户银行账号信息
     * @param dto
     * @return
     */
    String update(BankCardInfoUpdateDto dto);

    /**
     * 删除客户银行账号信息
     * @param dto
     * @return
     */
    String delete(BankCardInfoDeleteDto dto);

    /**
     * 设置有效或无效
     * isEffective = 0 设置为有效 , 反之设为无效
     * @param dto
     * @return
     */
    String enable(BankCardInfoUpdateDto dto);


    int updateByIdCardNo(BankCardInfo bankCardInfo);

    long getBankCardInfoByBankAccount(BankCardInfo bankCardInfo);
    /**
     * 新增客户银行信息
     * @return
     */
    int add(BankCardInfo bankCardInfo);
}
