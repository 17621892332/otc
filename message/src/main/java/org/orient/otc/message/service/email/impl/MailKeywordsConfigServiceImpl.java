package org.orient.otc.message.service.email.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.message.dto.MailKeywordsConfigAddDto;
import org.orient.otc.message.dto.MailKeywordsConfigDeleteDto;
import org.orient.otc.message.dto.MailKeywordsConfigUpdateDto;
import org.orient.otc.message.entity.MailKeywordsConfig;
import org.orient.otc.message.exception.BussinessException;
import org.orient.otc.message.mapper.MailKeywordsConfigMapper;
import org.orient.otc.message.service.email.MailKeywordsConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chengqiang
 */
@Service
public class MailKeywordsConfigServiceImpl extends ServiceImpl<MailKeywordsConfigMapper, MailKeywordsConfig> implements MailKeywordsConfigService {
    @Autowired
    MailKeywordsConfigMapper mailKeywordsConfigMapper;

    /**
     * 添加通配符配置 : 关键字不可以重复出现
     * @param dto 入参
     * @return 返回值
     */
    @Override
    public String add(MailKeywordsConfigAddDto dto) {
        LambdaQueryWrapper<MailKeywordsConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MailKeywordsConfig::getIsDeleted, IsDeletedEnum.NO);
        queryWrapper.eq(MailKeywordsConfig::getKeyWord, dto.getKeyWord());
        long sameNameCount = mailKeywordsConfigMapper.selectCount(queryWrapper);
        // 关键字不能重复
        if (sameNameCount > 0) {
            BussinessException.E_100101.assertTrue(false);
        }
        MailKeywordsConfig entity = new MailKeywordsConfig();
        BeanUtils.copyProperties(dto,entity);
        mailKeywordsConfigMapper.insert(entity);
        return "添加成功";
    }

    @Override
    public List<MailKeywordsConfig> getAll() {
        LambdaQueryWrapper<MailKeywordsConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MailKeywordsConfig::getIsDeleted, IsDeletedEnum.NO);
        queryWrapper.orderByDesc(MailKeywordsConfig::getCreateTime);
        return mailKeywordsConfigMapper.selectList(queryWrapper);
    }

    /**
     * 更新通配符配置 : 关键字不可以重复出现
     * @param dto 更新入参
     * @return 返回操作信息
     */
    @Override
    public String updateConfig(MailKeywordsConfigUpdateDto dto) {
        LambdaQueryWrapper<MailKeywordsConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MailKeywordsConfig::getIsDeleted, IsDeletedEnum.NO);
        queryWrapper.eq(MailKeywordsConfig::getKeyWord, dto.getKeyWord());
        queryWrapper.ne(MailKeywordsConfig::getId, dto.getId());
        long sameNameCount = mailKeywordsConfigMapper.selectCount(queryWrapper);
        // 关键字不能重复
        if (sameNameCount > 0) {
            BussinessException.E_100101.assertTrue(false);
        }
        MailKeywordsConfig entity = new MailKeywordsConfig();
        BeanUtils.copyProperties(dto,entity);
        this.updateById(entity);
        return "修改成功";
    }

    @Override
    public String deleteConfig(MailKeywordsConfigDeleteDto dto) {
        LambdaUpdateWrapper<MailKeywordsConfig> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(MailKeywordsConfig::getIsDeleted, IsDeletedEnum.YES);
        updateWrapper.in(MailKeywordsConfig::getId, dto.getIdList());
        mailKeywordsConfigMapper.update(null,updateWrapper);
        return "删除成功";
    }
}
