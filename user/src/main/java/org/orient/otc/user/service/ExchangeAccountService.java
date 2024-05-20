package org.orient.otc.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.api.user.dto.ExchangeAccountQueryDto;
import org.orient.otc.api.user.vo.ExchangeAccountFeignVO;
import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.user.dto.*;
import org.orient.otc.user.entity.ExchangeAccount;
import org.orient.otc.user.vo.ExchangeAccountVO;

import java.util.List;
import java.util.Set;

public interface ExchangeAccountService extends IServicePlus<ExchangeAccount> {
    /**
     * 获取场内账号列表
     * @return  场内账号
     */
    List<ExchangeAccountFeignVO> getList();

    /**
     * 通过账号名称获取场内账号
     * @param exchangeAccountQuery 账号名称
     * @return 场内账号
     */
    ExchangeAccountFeignVO getVoByname(ExchangeAccountQueryDto exchangeAccountQuery);

    /**
     * 场内账号列表查询
     * @param dto 查询条件
     * @return 账号列表
     */
    IPage<ExchangeAccountVO> getListByPage(ExchangeAccountPageListDto dto);

    /**
     * 获取场内账号详情
     * @param dto 场内账号
     * @return 账号信息
     */
    ExchangeAccount getExchangeAccountDetail(ExchangeAccountDetailDto dto);

    String addExchangeAccount(ExchangeAccountAddDto dto);

    String updateExchangeAccount(ExchangeAccountUpdateDto dto);

    String deleteExchangeAccount(ExchangeAccountDeleteDto dto);

    // 根据簿记账户id查询对冲账户
    List<ExchangeAccountFeignVO> getVoByAssetUnitIds(Set<Integer> ids);

    List<ExchangeAccountFeignVO>  getVoByAccounts(Set<String> accounts);

    String updateStatus(ExchangeAccountUpdateStatusDto dto);
}
