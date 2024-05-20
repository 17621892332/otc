package org.orient.otc.system.service.impl;

import cn.hutool.extra.cglib.CglibUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.common.security.dto.AuthorizeInfo;
import org.orient.otc.common.security.util.ThreadContext;
import org.orient.otc.system.dto.dictionary.*;
import org.orient.otc.system.entity.Dictionary;
import org.orient.otc.system.exception.BussinessException;
import org.orient.otc.system.mapper.DictionaryMapper;
import org.orient.otc.system.service.DictionaryService;
import org.orient.otc.system.vo.DictionaryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DictionaryServiceImpl extends ServiceImpl<BaseMapper<Dictionary>,Dictionary> implements DictionaryService {
    @Resource
    DictionaryMapper dictionaryMapper;
    @Override
    public List<Dictionary> getList(String dictTypeCode) {
        LambdaQueryWrapper<Dictionary> queryWrapper = new LambdaQueryWrapper<Dictionary>().eq(Dictionary::getIsDeleted, IsDeletedEnum.NO);
        if(StringUtils.isNotEmpty(dictTypeCode)){
            queryWrapper.eq(Dictionary :: getDicTypeCode, dictTypeCode);
        }
        queryWrapper.orderByAsc(Dictionary :: getDicSort);
       AuthorizeInfo authorizeInfo= ThreadContext.getAuthorizeInfo();
        List<Dictionary>  dictionaryList=  dictionaryMapper.selectList(queryWrapper);
        //客户端过滤自定义期权字典
       if (authorizeInfo!=null && authorizeInfo.getLoginForm()==0){
           dictionaryList= dictionaryList.stream().filter(item->!item.getDicValue().equals(OptionTypeEnum.AICustomPricer.name())).collect(Collectors.toList());
       }
        return dictionaryList;
    }

    /**
     * 根据字典dicTypeCode , dicName , dicValue 查看字典记录是否存在
     * @param dictionary
     * @return
     */
    public boolean hasExist(Dictionary dictionary){
        LambdaQueryWrapper<Dictionary> queryWrapper = new LambdaQueryWrapper<Dictionary>();
        queryWrapper.eq(Dictionary::getIsDeleted, IsDeletedEnum.NO);
        queryWrapper.eq(Dictionary::getDicTypeCode, dictionary.getDicTypeCode());
        queryWrapper.eq(Dictionary::getDicName, dictionary.getDicName());
        queryWrapper.eq(Dictionary::getDicValue, dictionary.getDicValue());
        queryWrapper.ne(StringUtils.isNotBlank(dictionary.getDicId()),Dictionary::getDicId, dictionary.getDicId());
        long count = dictionaryMapper.selectCount(queryWrapper);
        if (count>0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String add(DictionaryAddDto dto) {
        Dictionary entity = new Dictionary();
        BeanUtils.copyProperties(dto,entity);
        // 校验字典表唯一约束
        if (hasExist(entity)){
            String msg = "字典类型code="+dto.getDicTypeCode()+", 字典名称="+dto.getDicName()+", 字典值="+dto.getDicValue();
            BussinessException.E_200112.assertTrue(false,"新增失败, 字典记录已存在, "+msg);
        }
        this.saveOrUpdate(entity);
        return "添加成功";
    }

    @Override
    public String updateDictionary(DictionaryUpdateDto dto) {
        Dictionary entity = new Dictionary();
        BeanUtils.copyProperties(dto,entity);
        // 校验字典表唯一约束
        if (hasExist(entity)){
            String msg = "字典类型code="+dto.getDicValue()+", 字典名称="+dto.getDicName()+", 字典值="+dto.getDicValue();
            BussinessException.E_200112.assertTrue(false,"修改失败, 字典记录已存在, "+msg);
        }
        this.saveOrUpdate(entity);
        return "修改成功";
    }

    @Override
    public String deleteDictionary(DictionaryDeleteDto dto) {
        LambdaUpdateWrapper<Dictionary> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Dictionary::getDicId,dto.getDicId());
        lambdaUpdateWrapper.set(Dictionary::getIsDeleted,IsDeletedEnum.YES);
        dictionaryMapper.update(null,lambdaUpdateWrapper);
        return "删除成功";
    }

    @Override
    public String updateSort(DictionarySortDto dto) {
        for (DictionarySortItemDto item : dto.getSortItemDtoList()) {
            LambdaUpdateWrapper<Dictionary> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(Dictionary::getDicId,item.getDicId());
            lambdaUpdateWrapper.set(Dictionary::getDicSort,item.getDicSort());
            dictionaryMapper.update(null,lambdaUpdateWrapper);
        }
        return "更新排序成功";
    }

    @Override
    public IPage<Dictionary> selectByPage(DictionaryPageDto dto) {
        LambdaQueryWrapper<Dictionary> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Dictionary::getIsDeleted,IsDeletedEnum.NO);
        lambdaQueryWrapper.eq(Dictionary::getDicTypeCode,dto.getDicTypeCode()); // 字典类型code 精准查询且不为空
        lambdaQueryWrapper.like(StringUtils.isNotBlank(dto.getDicName()),Dictionary::getDicName,dto.getDicName()); // 名称模糊查询
        lambdaQueryWrapper.like(StringUtils.isNotBlank(dto.getDicValue()),Dictionary::getDicValue,dto.getDicValue()); // 值模糊查询
        lambdaQueryWrapper.orderByAsc(Dictionary::getDicSort);
        IPage<Dictionary> ipage = this.page(new Page<>(dto.getPageNo(),dto.getPageSize()),lambdaQueryWrapper);
        return ipage;
    }

    @Override
    public Map<String, List<DictionaryVo>> getListAll() {
        // 使用LambdaQueryWrapper构造查询条件，这里的条件是筛选未被删除的字典项
        LambdaQueryWrapper<Dictionary> queryWrapper = new LambdaQueryWrapper<Dictionary>().eq(Dictionary::getIsDeleted, IsDeletedEnum.NO);
        List<Dictionary> dictionaries = dictionaryMapper.selectList(queryWrapper);
        // 将查询得到的Dictionary对象列表转换为DictionaryVo对象列表
        List<DictionaryVo> dictionaryVos = CglibUtil.copyList(dictionaries, DictionaryVo::new, (vo, db) -> {
        });
        // 1. 首先使用Collectors.groupingBy按DictionaryVo的dicTypeCode属性进行分组
        // 2. 然后使用Collectors.collectingAndThen对每个分组后的列表进行额外操作，这里是排序操作
        return dictionaryVos.stream()
                .collect(Collectors.groupingBy(
                        DictionaryVo::getDicTypeCode, // 分组依据：dicTypeCode
                        Collectors.collectingAndThen(
                                Collectors.toList(), // 将每组元素收集到List中
                                list -> {
                                    list.sort(Comparator.comparingInt(DictionaryVo::getDicSort)); // 对每个分组的列表按dicSort属性排序
                                    return list; // 返回排序后的列表
                                }
                        )
                ));
    }

    @Override
    public String getDictionaryValue(String type, String name) {
        String dicValue=name;
        LambdaQueryWrapper<Dictionary> queryWrapper = new LambdaQueryWrapper<Dictionary>();
        queryWrapper.eq(Dictionary :: getDicTypeCode, type).eq(Dictionary :: getDicName,name).eq(Dictionary::getIsDeleted, IsDeletedEnum.NO);
        Dictionary dictionary = dictionaryMapper.selectOne(queryWrapper);
        if(dictionary!=null){
            dicValue = dictionary.getDicValue();
        }
        return dicValue;
    }

    @Override
    public String getDictionaryName(String type, String code) {
        String dicName=code;
        LambdaQueryWrapper<Dictionary> queryWrapper = new LambdaQueryWrapper<Dictionary>();
        queryWrapper.eq(Dictionary :: getDicTypeCode, type).eq(Dictionary :: getDicValue,code).eq(Dictionary::getIsDeleted, IsDeletedEnum.NO);
        Dictionary dictionary = dictionaryMapper.selectOne(queryWrapper);
        if(dictionary!=null){
            dicName = dictionary.getDicName();
        }
        return dicName;
    }

    /**
     * 根据字典类型代码获取未删除的字典项列表，并按照字典排序字段升序排列。
     * 如果提供了字典类型代码，则进一步筛选结果。
     *
     * @param dictTypeCode 字典类型代码，用于筛选字典项。如果为null或空，则返回所有未删除的字典项。
     * @return 返回符合条件的字典项视图对象列表。
     */
    @Override
    public List<DictionaryVo> getListByDictTypeCode(String dictTypeCode) {
        // 构建查询条件
        LambdaQueryWrapper<Dictionary> queryWrapper = new LambdaQueryWrapper<Dictionary>()
                .eq(Dictionary::getIsDeleted, IsDeletedEnum.NO)
                .orderByAsc(Dictionary::getDicSort);
        // 如果提供了dictTypeCode，则添加额外的筛选条件
        if (StringUtils.isNotEmpty(dictTypeCode)) {
            queryWrapper.eq(Dictionary::getDicTypeCode, dictTypeCode);
        }
        // 执行查询获取字典项列表
        List<Dictionary> dictionaries = dictionaryMapper.selectList(queryWrapper);
        // 将字典项列表转换为视图对象列表
        return CglibUtil.copyList(dictionaries, DictionaryVo::new, (vo, db) -> {
        });
    }

    /**
     * 获取未被删除的字典项，并转换为Map。
     *
     * @return Map，其中键是dicTypeCode和dicName的组合，值是dicValue。
     */
    @Override
    public Map<String, String> getDictionaryMap() {
        // 使用LambdaQueryWrapper构造查询条件，筛选未被删除的字典项
        LambdaQueryWrapper<Dictionary> queryWrapper = new LambdaQueryWrapper<Dictionary>()
                .eq(Dictionary::getIsDeleted, IsDeletedEnum.NO);
        // 直接从数据库查询并转换为Map
        return dictionaryMapper.selectList(queryWrapper).stream()
                .collect(Collectors.toMap(
                        d -> d.getDicTypeCode() +"_"+ d.getDicName(), // 键: dicTypeCode + dicName
                        Dictionary::getDicValue,                 // 值: dicValue
                        (existing, replacement) -> existing      // 如果存在冲突，保留现有的值
                ));
    }
}
