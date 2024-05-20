package org.orient.otc.quote.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.quote.dto.scenario.ScenarioTemplateAddDTO;
import org.orient.otc.quote.dto.scenario.ScenarioTemplateDeleteDTO;
import org.orient.otc.quote.dto.scenario.ScenarioTemplateSelectDTO;
import org.orient.otc.quote.entity.ScenarioTemplate;
import org.orient.otc.quote.exeption.BussinessException;
import org.orient.otc.quote.mapper.ScenarioTemplateMapper;
import org.orient.otc.quote.service.ScenarioTemplateService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ScenarioTemplateServiceImpl extends ServiceImpl<BaseMapper<ScenarioTemplate>, ScenarioTemplate> implements ScenarioTemplateService {
    @Resource
    private ScenarioTemplateMapper scenarioTemplateMapper;

    @Override
    @Transactional
    public String save(ScenarioTemplateAddDTO dto) {
        LambdaQueryWrapper<ScenarioTemplate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ScenarioTemplate::getName, dto.getName());
        lambdaQueryWrapper.eq(ScenarioTemplate::getReportType,dto.getReportType());
        lambdaQueryWrapper.eq(ScenarioTemplate::getIsDeleted, IsDeletedEnum.NO);
        List<ScenarioTemplate> list = scenarioTemplateMapper.selectList(lambdaQueryWrapper);
        BussinessException.E_300102.assertTrue(list.isEmpty() || list.get(0).getId().equals(dto.getId()), "名称已存在");
        ScenarioTemplate scenarioTemplate = BeanUtil.toBean(dto, ScenarioTemplate.class);
        this.saveOrUpdate(scenarioTemplate);
        return "操作成功";
    }

    @Override
    public ScenarioTemplate getById(ScenarioTemplateSelectDTO dto) {
        return scenarioTemplateMapper.selectById(dto.getId());
    }

    @Override
    public String delete(ScenarioTemplateDeleteDTO dto) {
        ScenarioTemplate scenarioTemplate = new ScenarioTemplate();
        BeanUtils.copyProperties(dto, scenarioTemplate);
        scenarioTemplate.setIsDeleted(IsDeletedEnum.YES.getFlag());
        scenarioTemplateMapper.updateById(scenarioTemplate);
        return "操作成功";
    }

    @Override
    public List<ScenarioTemplate> selectByList() {
        LambdaQueryWrapper<ScenarioTemplate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ScenarioTemplate::getIsDeleted, IsDeletedEnum.NO);
        lambdaQueryWrapper.orderByDesc(ScenarioTemplate::getCreateTime);
        return scenarioTemplateMapper.selectList(lambdaQueryWrapper);
    }
}
