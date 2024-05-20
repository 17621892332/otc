package org.orient.otc.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.orient.otc.api.user.dto.ExchangeAccountQueryDto;
import org.orient.otc.api.user.vo.ExchangeAccountFeignVO;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.user.dto.*;
import org.orient.otc.user.entity.Assetunit;
import org.orient.otc.user.entity.ExchangeAccount;
import org.orient.otc.user.exception.BussinessException;
import org.orient.otc.user.mapper.ExchangeAccountMapper;
import org.orient.otc.user.service.AssetunitService;
import org.orient.otc.user.service.ExchangeAccountService;
import org.orient.otc.user.vo.ExchangeAccountLoginVO;
import org.orient.otc.user.vo.ExchangeAccountVO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 场内账号服务实现
 */
@Service
public class ExchangeAccountServiceImpl extends ServiceImpl<BaseMapper<ExchangeAccount>, ExchangeAccount> implements ExchangeAccountService {
    @Resource
    ExchangeAccountMapper exchangeAccountMapper;

    @Resource
    AssetunitService assetunitService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public String updateStatus(ExchangeAccountUpdateStatusDto dto) {
        LambdaUpdateWrapper<ExchangeAccount> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(ExchangeAccount::getId,dto.getId())
                .set(ExchangeAccount::getStatus,dto.getStatus());
        exchangeAccountMapper.update(null,lambdaUpdateWrapper);
        return "修改成功";
    }

    @Override
    public List<ExchangeAccountFeignVO> getList() {
        return this.listVo(new LambdaQueryWrapper<ExchangeAccount>().eq(ExchangeAccount::getIsDeleted, 0), ExchangeAccountFeignVO.class);
    }

    @Override
    public ExchangeAccountFeignVO getVoByname(ExchangeAccountQueryDto exchangeAccountQuery) {
        return this.getVoOne(new LambdaQueryWrapper<ExchangeAccount>().eq(ExchangeAccount::getIsDeleted, 0).eq(ExchangeAccount::getAccount, exchangeAccountQuery.getAccount()), ExchangeAccountFeignVO.class);
    }

    @Override
    public List<ExchangeAccountFeignVO> getVoByAccounts(Set<String> accounts) {
        LambdaQueryWrapper<ExchangeAccount> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ExchangeAccount::getIsDeleted, IsDeletedEnum.NO)
                .in(ExchangeAccount::getAccount, accounts);
        List<ExchangeAccount> list = exchangeAccountMapper.selectList(lambdaQueryWrapper);
        return list.stream().map(item -> {
            ExchangeAccountFeignVO vo = new ExchangeAccountFeignVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public IPage<ExchangeAccountVO> getListByPage(ExchangeAccountPageListDto dto) {
        LambdaQueryWrapper<ExchangeAccount> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(!StringUtils.isEmpty(dto.getName()), ExchangeAccount::getName, dto.getName())
                .eq(!StringUtils.isEmpty(dto.getAccount()), ExchangeAccount::getAccount, dto.getAccount())
                .eq(!StringUtils.isEmpty(dto.getAssetunitId()), ExchangeAccount::getAssetunitId, dto.getAssetunitId())
                .eq(ExchangeAccount::getIsDeleted, IsDeletedEnum.NO)
        ;
        IPage<ExchangeAccount> ipage = this.page(new Page<>(dto.getPageNo(), dto.getPageSize()), lambdaQueryWrapper);
        Map<Integer, Assetunit> assetunitMap = getExchangeAccountAssetUnit(ipage);
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(RedisAdapter.EXCHANGE_ACCOUNT_LOGIN_STATUS);
        Map<String, ExchangeAccountLoginVO> loginVoMap = entries.entrySet().stream().collect(
                Collectors.toMap(e -> String.valueOf(e.getKey()), e -> JSONObject.parseObject(String.valueOf(e.getValue()), ExchangeAccountLoginVO.class)));
        return ipage.convert(item -> {
            ExchangeAccountVO vo = new ExchangeAccountVO();
            BeanUtils.copyProperties(item, vo);
            vo.setAssetunit(assetunitMap.get(item.getAssetunitId()));
            vo.setLoginInfo(loginVoMap.get(item.getAccount()));
            return vo;
        });
    }

    /**
     * 获取对冲账户对应的簿记账户
     * @param ipage 分页信息
     * @return key账号ID value 簿记信息
     */
    public Map<Integer, Assetunit> getExchangeAccountAssetUnit(IPage<ExchangeAccount> ipage) {
        Set<Integer> assetUniteIds = ipage.getRecords().stream().map(ExchangeAccount::getAssetunitId).collect(Collectors.toSet());
        List<Assetunit> list = assetunitService.queryByIds(assetUniteIds);
        return list.stream().collect(Collectors.toMap(Assetunit::getId, item -> item, (v1, v2) -> v2));
    }

    @Override
    public ExchangeAccount getExchangeAccountDetail(ExchangeAccountDetailDto dto) {
        return this.getById(dto.getId());
    }

    @Override
    public String addExchangeAccount(ExchangeAccountAddDto dto) {
        ExchangeAccount exchangeAccount = new ExchangeAccount();
        BeanUtils.copyProperties(dto, exchangeAccount);
        //校验账号是否存在
        BussinessException.E_100301.assertTrue(exchangeAccountMapper.selectCount(new LambdaQueryWrapper<ExchangeAccount>()
                .eq(ExchangeAccount::getAccount, dto.getAccount())
                .eq(ExchangeAccount::getIsDeleted, IsDeletedEnum.NO)) == 0);
        exchangeAccountMapper.insert(exchangeAccount);
        stringRedisTemplate.opsForHash().put(RedisAdapter.EXCHANGE_ACCOUNT, exchangeAccount.getAccount(), JSONObject.toJSONString(exchangeAccount));
        return "ExchangeAccount add success";
    }

    @Override
    public String updateExchangeAccount(ExchangeAccountUpdateDto dto) {
        ExchangeAccount exchangeAccount = new ExchangeAccount();
        BeanUtils.copyProperties(dto, exchangeAccount);
        //校验账号是否存在
        BussinessException.E_100301.assertTrue(exchangeAccountMapper.selectCount(new LambdaQueryWrapper<ExchangeAccount>()
                .eq(ExchangeAccount::getAccount, dto.getAccount())
                .ne(ExchangeAccount::getId, dto.getId())
                .eq(ExchangeAccount::getIsDeleted, IsDeletedEnum.NO)) == 0);
        exchangeAccountMapper.updateById(exchangeAccount);
        stringRedisTemplate.opsForHash().put(RedisAdapter.EXCHANGE_ACCOUNT, exchangeAccount.getAccount(), JSONObject.toJSONString(exchangeAccount));
        return "ExchangeAccount update success";
    }

    @Override
    public String deleteExchangeAccount(ExchangeAccountDeleteDto dto) {
        ExchangeAccount exchangeAccount = new ExchangeAccount();
        BeanUtils.copyProperties(dto, exchangeAccount);
        exchangeAccount.setIsDeleted(IsDeletedEnum.YES.getFlag());
        exchangeAccountMapper.updateById(exchangeAccount);
        stringRedisTemplate.opsForHash().delete(RedisAdapter.EXCHANGE_ACCOUNT, exchangeAccount.getAccount());
        return "ExchangeAccount delete success";
    }

    @Override
    public List<ExchangeAccountFeignVO> getVoByAssetUnitIds(Set<Integer> ids) {
        LambdaQueryWrapper<ExchangeAccount> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(ExchangeAccount::getAssetunitId, ids)
                .eq(ExchangeAccount::getIsDeleted, IsDeletedEnum.NO);
        List<ExchangeAccount> list = exchangeAccountMapper.selectList(lambdaQueryWrapper);
        return list.stream().map(item -> {
            ExchangeAccountFeignVO vo = new ExchangeAccountFeignVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}

