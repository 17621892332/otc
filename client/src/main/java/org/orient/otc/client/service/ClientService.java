package org.orient.otc.client.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.client.dto.AffiliatedOrganizationDto;
import org.orient.otc.client.dto.ClientDetailDto;
import org.orient.otc.client.dto.ClientPageDto;
import org.orient.otc.client.entity.Client;
import org.orient.otc.client.vo.AffiliatedOrganizationVo;
import org.orient.otc.client.vo.ClientDetailVo;
import org.orient.otc.client.vo.ClientVo;
import org.orient.otc.common.database.config.IServicePlus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author pjc
 */
public interface ClientService extends IServicePlus<Client> {
    List<Client> getList();

    /**
     * 获取客户列表
     * @param ids 客户ID集合
     * @return 返回客户列表
     */
    List<Client> queryByIds(Set<Integer> ids);

    IPage<ClientVo> getListByPage(ClientPageDto dto);

    /**
     * 获取客户列表 , 并查询出每个客户对应的银行账户
     * @return 返回客户VO列表
     */
    List<ClientVo> getClientAndBankInfoList();
    /**
     * 获取客户保证金系数
     * @param idSet 客户ID
     * @return key 客户ID value 保证金系数
     */
    Map<Integer, BigDecimal> getClientMarginRate(Set<Integer> idSet);

    /**
     * 查询客户详情
     * @return 返回客户详情VO
     */
    ClientDetailVo getClientDetail(String clientCode);

    String add(ClientDetailDto clientDetailDto);

    String update(ClientDetailDto clientDetailDto);

    List<AffiliatedOrganizationVo> getAffiliatedOrganization(AffiliatedOrganizationDto dto);

    String delete(String clientCode);

    List<Client> selectByClientCodeSet(Set<String> clientCodeSet);
    List<Client> selectByClientNameSet(Set<String> clientNameSet);
}
