package org.orient.otc.message.service.email.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.message.dto.MailTemplateAddDto;
import org.orient.otc.message.dto.MailTemplateDeleteDto;
import org.orient.otc.message.dto.MailTemplateUpdateAsDefaultDto;
import org.orient.otc.message.dto.MailTemplateUpdateDto;
import org.orient.otc.message.entity.MailTemplate;
import org.orient.otc.message.exception.BussinessException;
import org.orient.otc.message.mapper.MailTemplateMapper;
import org.orient.otc.message.service.email.MailTemplateService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailTemplateServiceImpl extends ServiceImpl<MailTemplateMapper, MailTemplate> implements MailTemplateService {
    @Autowired
    MailTemplateMapper mailTemplateMapper;

    @Override
    public List<MailTemplate> listAll() {
        LambdaQueryWrapper<MailTemplate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MailTemplate::getIsDeleted, IsDeletedEnum.NO);
        lambdaQueryWrapper.orderByDesc(MailTemplate::getCreateTime);
        return mailTemplateMapper.selectList(lambdaQueryWrapper);
    }
    @Override
    public MailTemplate getDefaultTemplate() {
        LambdaQueryWrapper<MailTemplate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MailTemplate::getIsDeleted, IsDeletedEnum.NO);
        lambdaQueryWrapper.eq(MailTemplate::getDefaultFlag, 1);
        lambdaQueryWrapper.orderByDesc(MailTemplate::getCreateTime);
        List<MailTemplate> list = mailTemplateMapper.selectList(lambdaQueryWrapper);
        if (list.isEmpty()) {
            BussinessException.E_100104.assertTrue(false,"暂无默认的邮件模板");
        } else if (list.size()>1) {
            BussinessException.E_100104.assertTrue(false,"存在多个默认的邮件模板, 请保留一个");
        }
        return list.get(0);
    }

    /**
     * 添加模板 模板名称不能重复
     * @param dto 入参
     * @return 返回操作提示
     */
    @Override
    public String addMailTemplate(MailTemplateAddDto dto) {
        LambdaQueryWrapper<MailTemplate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MailTemplate::getIsDeleted, IsDeletedEnum.NO);
        lambdaQueryWrapper.eq(MailTemplate::getTemplateName,dto.getTemplateName());
        long sameCount = mailTemplateMapper.selectCount(lambdaQueryWrapper);
        if (sameCount > 0) {
            BussinessException.E_100102.assertTrue(false);
        }
        MailTemplate entity = new MailTemplate();
        BeanUtils.copyProperties(dto,entity);
        mailTemplateMapper.insert(entity);
        return "添加成功";
    }

    @Override
    public String updateTemplate(MailTemplateUpdateDto dto) {
        LambdaQueryWrapper<MailTemplate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MailTemplate::getIsDeleted, IsDeletedEnum.NO);
        lambdaQueryWrapper.eq(MailTemplate::getTemplateName,dto.getTemplateName());
        lambdaQueryWrapper.ne(MailTemplate::getId,dto.getId());
        long sameCount = mailTemplateMapper.selectCount(lambdaQueryWrapper);
        if (sameCount > 0) {
            BussinessException.E_100102.assertTrue(false);
        }
        MailTemplate entity = new MailTemplate();
        BeanUtils.copyProperties(dto,entity);
        mailTemplateMapper.updateById(entity);
        return "修改成功";
    }

    @Override
    public String deleteMailTemplate(MailTemplateDeleteDto dto) {
        LambdaUpdateWrapper<MailTemplate> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(MailTemplate::getIsDeleted,IsDeletedEnum.YES);
        lambdaUpdateWrapper.eq(MailTemplate::getId,dto.getId());
        mailTemplateMapper.update(null,lambdaUpdateWrapper);
        return "操作成功";
    }

    /**
     * 更新模板为默认模板
     * @param dto
     * @return
     */
    @Override
    public String updateAsDefault(MailTemplateUpdateAsDefaultDto dto) {
        // 先把所有的更新成非默认 , 再把当前这个一个更新成默认
        LambdaUpdateWrapper<MailTemplate> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(MailTemplate::getDefaultFlag,0);
        lambdaUpdateWrapper.eq(MailTemplate::getIsDeleted,IsDeletedEnum.NO);
        int count = mailTemplateMapper.update(null,lambdaUpdateWrapper);
        if (count > 0){
            LambdaUpdateWrapper<MailTemplate> lambdaUpdateWrapperDefault = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapperDefault.set(MailTemplate::getDefaultFlag,1);
            lambdaUpdateWrapperDefault.eq(MailTemplate::getId,dto.getId());
            mailTemplateMapper.update(null,lambdaUpdateWrapperDefault);
        }
        return "操作成功";
    }
}
