package org.orient.otc.quote.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.extra.cglib.CglibUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.common.security.dto.AuthorizeInfo;
import org.orient.otc.common.security.util.ThreadContext;
import org.orient.otc.quote.dto.quote.QuoteContentDTO;
import org.orient.otc.quote.dto.quote.QuoteMakeUpTotalDTO;
import org.orient.otc.quote.dto.quote.QuoteTemplateDto;
import org.orient.otc.quote.dto.quote.QuoteTempleIdDto;
import org.orient.otc.quote.entity.QuoteTemplate;
import org.orient.otc.quote.entity.QuoteTemplateContent;
import org.orient.otc.quote.entity.QuoteTemplateContentData;
import org.orient.otc.quote.entity.QuoteTemplateContentSnowball;
import org.orient.otc.quote.exeption.BussinessException;
import org.orient.otc.quote.mapper.QuoteTemplateContentMapper;
import org.orient.otc.quote.mapper.QuoteTemplateContentSnowballMapper;
import org.orient.otc.quote.mapper.QuoteTemplateMapper;
import org.orient.otc.quote.service.QuoteTemplateService;
import org.orient.otc.quote.util.QuoteUtil;
import org.orient.otc.quote.vo.quote.QuoteMakeUpTotalVo;
import org.orient.otc.quote.vo.template.QuoteTemplateContentVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 模板服务实现
 * @author dzrh
 */
@Service
public class QuoteTemplateServiceImpl extends ServiceImpl<QuoteTemplateMapper, QuoteTemplate> implements QuoteTemplateService {
    @Resource
    QuoteTemplateContentMapper quoteTemplateContentMapper;
    @Resource
    QuoteTemplateMapper quoteTemplateMapper;
    @Resource
    RedissonClient redissonClient;
    @Resource
    QuoteUtil quoteUtil;

    @Override
    @Transactional
    public String insertTemplate(QuoteTemplateDto quoteTemplateDto) {
        RLock lock = redissonClient.getLock("lock:insertQuoteTemplate");
        lock.lock();
        try {
            //插入模板
            List<QuoteTemplate> quoteTemplates = quoteTemplateMapper.selectList(new LambdaQueryWrapper<QuoteTemplate>()
                    .eq(QuoteTemplate::getName, quoteTemplateDto.getTemplateName())
                    .eq(QuoteTemplate::getIsDeleted, 0));
            BussinessException.E_300104.assertTrue(Objects.isNull(quoteTemplates) || quoteTemplates.isEmpty());
            QuoteTemplate quoteTemplate = new QuoteTemplate();
            quoteTemplate.setName(quoteTemplateDto.getTemplateName());

            Integer isPublic = Boolean.parseBoolean(quoteTemplateDto.getIsPublic()) ? 1 : 0;
            quoteTemplate.setIsPublic(isPublic);

            quoteTemplateMapper.insert(quoteTemplate);

            //插入模板对应的报价
            List<QuoteContentDTO> quoteList = quoteTemplateDto.getQuoteList();
            for (QuoteContentDTO quoteContentDto : quoteList) {
                QuoteTemplateContent quoteTemplateContent = new QuoteTemplateContent();
                quoteTemplateContent.setTemplateId(quoteTemplate.getId());
                quoteTemplateContent.setMaturityDate(quoteContentDto.getMaturityDate());
                quoteTemplateContent.setSort(quoteContentDto.getSort());
                quoteTemplateContent.setData(CglibUtil.copy(quoteContentDto, QuoteTemplateContentData.class));
                quoteTemplateContentMapper.insert(quoteTemplateContent);

            }
        } finally {
            lock.unlock();
        }
        return "insert success";
    }

    @Override
    @Transactional
    public String deleteTemple(QuoteTempleIdDto quoteTempleIdDto) {
        //删除模板
        LambdaUpdateWrapper<QuoteTemplate> quoteTemplateLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        QuoteTemplate quoteTemplate = new QuoteTemplate();
        quoteTemplate.setIsDeleted(IsDeletedEnum.YES.getFlag());

        quoteTemplateLambdaUpdateWrapper.eq(QuoteTemplate::getIsDeleted, IsDeletedEnum.NO);
        quoteTemplateLambdaUpdateWrapper.eq(QuoteTemplate::getId, quoteTempleIdDto.getTemplateId());
        quoteTemplateMapper.update(quoteTemplate, quoteTemplateLambdaUpdateWrapper);

        //删除模板对于的报价
        LambdaUpdateWrapper<QuoteTemplateContent> quoteTemplateContentLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        quoteTemplateContentLambdaUpdateWrapper.set(QuoteTemplateContent::getIsDeleted, IsDeletedEnum.YES);
        QuoteTemplateContent quoteTemplateContent = new QuoteTemplateContent();
        quoteTemplateContent.setIsDeleted(IsDeletedEnum.YES.getFlag());
        quoteTemplateContentLambdaUpdateWrapper.eq(QuoteTemplateContent::getTemplateId, quoteTempleIdDto.getTemplateId());
        quoteTemplateContentLambdaUpdateWrapper.eq(QuoteTemplateContent::getIsDeleted, IsDeletedEnum.NO);

        quoteTemplateContentMapper.update(quoteTemplateContent, quoteTemplateContentLambdaUpdateWrapper);
        return "delete success";
    }

    @Override
    public List<QuoteTemplate> getQuoteTemplateList() {
        //获取用户信息
        AuthorizeInfo authorizeInfo = ThreadContext.getAuthorizeInfo();

        //先查询未过期的模板ID
        LambdaQueryWrapper<QuoteTemplateContent> quoteTemplateContentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        quoteTemplateContentLambdaQueryWrapper.select(QuoteTemplateContent::getTemplateId);
        quoteTemplateContentLambdaQueryWrapper.ge(QuoteTemplateContent::getMaturityDate, LocalDate.now());
        quoteTemplateContentLambdaQueryWrapper.eq(QuoteTemplateContent::getIsDeleted, IsDeletedEnum.NO);
        List<QuoteTemplateContent> quoteTemplateContentList = quoteTemplateContentMapper.selectList(quoteTemplateContentLambdaQueryWrapper);
        Set<Integer> idSet = quoteTemplateContentList.stream().map(QuoteTemplateContent::getTemplateId).collect(Collectors.toSet());
        if (idSet.isEmpty()) {
            return new ArrayList<>();
        }
        //组装查询条件
        LambdaQueryWrapper<QuoteTemplate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(QuoteTemplate::getId, idSet);
        queryWrapper.eq(QuoteTemplate::getIsDeleted, IsDeletedEnum.NO);
        queryWrapper.and(query -> query.eq(QuoteTemplate::getIsPublic, 1).or().eq(QuoteTemplate::getCreatorId, authorizeInfo.getId()));
        return quoteTemplateMapper.selectList(queryWrapper);
    }

    @Override
    public List<QuoteTemplateContentVO> getQuoteTemplateContentByTemplateId(QuoteTempleIdDto quoteTempleIdDto) {

        LambdaQueryWrapper<QuoteTemplateContent> quoteTemplateContentLambdaQueryWrapper = new LambdaQueryWrapper<QuoteTemplateContent>()
                .eq(QuoteTemplateContent::getTemplateId, quoteTempleIdDto.getTemplateId())
                .eq(QuoteTemplateContent::getIsDeleted, 0)
                .orderByAsc(QuoteTemplateContent::getSort);
       List<QuoteTemplateContent> contentList= quoteTemplateContentMapper.selectList(quoteTemplateContentLambdaQueryWrapper);
        if (contentList == null || contentList.isEmpty()) {
            BussinessException.E_300006.assertTrue(Boolean.FALSE);
            return null;
        }
        List<QuoteTemplateContentData> contentDataList = contentList.stream().map(QuoteTemplateContent::getData).collect(Collectors.toList());
        List<QuoteTemplateContentVO> voList = CglibUtil.copyList(contentDataList,QuoteTemplateContentVO::new);

        //组合的话求和
        if (Objects.nonNull(voList.get(0).getOptionCombType())) {
            QuoteTemplateContentVO quoteTemplateContent = new QuoteTemplateContentVO();
            BeanUtils.copyProperties(voList.get(0), quoteTemplateContent);
            List<QuoteMakeUpTotalDTO> quoteMakeUpTotalDTOList = JSONArray.parseArray(JSONArray.toJSONString(voList), QuoteMakeUpTotalDTO.class);
            QuoteMakeUpTotalVo makeUpTotal = quoteUtil.getMakeUpTotal(quoteMakeUpTotalDTOList);
            BeanUtils.copyProperties(makeUpTotal, quoteTemplateContent);
            //sort为1表示汇总的数据，前端要求这样
            quoteTemplateContent.setSort(1);
            voList.add(0, quoteTemplateContent);
        }
        return voList;
    }

    @Override
    @Transactional
    public Boolean deleteMaturityTemplate() {
        LambdaQueryWrapper<QuoteTemplateContent> quoteTemplateContentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        quoteTemplateContentLambdaQueryWrapper.select(QuoteTemplateContent::getTemplateId);
        quoteTemplateContentLambdaQueryWrapper.le(QuoteTemplateContent::getMaturityDate, LocalDate.now());
        quoteTemplateContentLambdaQueryWrapper.eq(QuoteTemplateContent::getIsDeleted, IsDeletedEnum.NO);
        List<QuoteTemplateContent> quoteTemplateContentList = quoteTemplateContentMapper.selectList(quoteTemplateContentLambdaQueryWrapper);
        QuoteTemplateContent quoteTemplateContent = new QuoteTemplateContent();
        quoteTemplateContent.setIsDeleted(IsDeletedEnum.YES.getFlag());
        quoteTemplateContentMapper.update(quoteTemplateContent, quoteTemplateContentLambdaQueryWrapper);
        if (!quoteTemplateContentList.isEmpty()) {
            List<Integer> templateIdList = quoteTemplateContentList.stream().map(QuoteTemplateContent::getTemplateId).distinct().collect(Collectors.toList());
            QuoteTemplate quoteTemplate = new QuoteTemplate();
            quoteTemplate.setIsDeleted(IsDeletedEnum.YES.getFlag());
            LambdaQueryWrapper<QuoteTemplate> queryWrapper= new LambdaQueryWrapper<>();
            queryWrapper.in(QuoteTemplate::getId,templateIdList);
            queryWrapper.eq(QuoteTemplate::getIsDeleted,IsDeletedEnum.NO);
            this.getBaseMapper().update(quoteTemplate,queryWrapper);
        }
        return true;
    }
}
