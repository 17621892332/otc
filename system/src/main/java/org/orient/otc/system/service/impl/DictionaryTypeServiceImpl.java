package org.orient.otc.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.system.dto.dictionarytype.*;
import org.orient.otc.system.entity.Dictionary;
import org.orient.otc.system.entity.DictionaryType;
import org.orient.otc.system.exception.BussinessException;
import org.orient.otc.system.mapper.DictionaryMapper;
import org.orient.otc.system.mapper.DictionaryTypeMapper;
import org.orient.otc.system.service.DictionaryTypeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 字典类型服务实现
 */
@Service
public class DictionaryTypeServiceImpl extends ServiceImpl<BaseMapper<DictionaryType>,DictionaryType> implements DictionaryTypeService {
    @Resource
    DictionaryTypeMapper dictionaryTypeMapper;
    @Resource
    DictionaryMapper dictionaryMapper;

    @Override
    public List<DictionaryType> getDictionaryTypeList() {

        return this.list(new LambdaQueryWrapper<DictionaryType>().eq(DictionaryType::getIsDeleted, IsDeletedEnum.NO));
    }

    /**
     * 根据dicTypeCode , dicTypeName 查看字典类型记录是否存在
     * @param dictionaryType
     * @return
     */
    public boolean hasExist(DictionaryType dictionaryType){
        LambdaQueryWrapper<DictionaryType> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DictionaryType::getDicTypeCode,dictionaryType.getDicTypeCode());
        lambdaQueryWrapper.eq(DictionaryType::getDicTypeName,dictionaryType.getDicTypeName());
        lambdaQueryWrapper.eq(DictionaryType::getIsDeleted,IsDeletedEnum.NO);
        lambdaQueryWrapper.ne(StringUtils.isNotBlank(dictionaryType.getDicTypeId()),DictionaryType::getDicTypeId,dictionaryType.getDicTypeId());
        long count = dictionaryTypeMapper.selectCount(lambdaQueryWrapper);
        if (count>0){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String add(DictionaryTypeAddDto dto) {
        DictionaryType entity = new DictionaryType();
        BeanUtils.copyProperties(dto,entity);
        if (hasExist(entity)) {
            String msg = "字典类型code="+dto.getDicTypeCode()+", 字典名称="+dto.getDicTypeName();
            BussinessException.E_200112.assertTrue(false,"新增失败, 字典记录已存在, "+msg);
        }
        this.saveOrUpdate(entity);
        return "新增成功";
    }

    @Override
    public String update(DictionaryTypeUpdateDto dto) {
        DictionaryType entity = new DictionaryType();
        BeanUtils.copyProperties(dto,entity);
        if (hasExist(entity)) {
            String msg = "字典类型code="+dto.getDicTypeCode()+", 字典名称="+dto.getDicTypeName();
            BussinessException.E_200112.assertTrue(false,"修改失败, 字典记录已存在, "+msg);
        }
        this.saveOrUpdate(entity);
        return "修改成功";
    }

    /**
     * 删除字典类型时，字典详情也跟着一并删除
     * @param dto
     * @return
     */
    @Override
    public String delete(DictionaryTypeDeleteDto dto) {
        LambdaUpdateWrapper<DictionaryType> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(DictionaryType::getDicTypeId,dto.getDicTypeId());
        lambdaUpdateWrapper.set(DictionaryType::getIsDeleted,IsDeletedEnum.YES);
        dictionaryTypeMapper.update(null,lambdaUpdateWrapper);
        DictionaryType dictionaryType = dictionaryTypeMapper.selectById((dto.getDicTypeId()));
        LambdaUpdateWrapper<Dictionary> dictionaryLambdaUpdateWrapper = new LambdaUpdateWrapper<Dictionary>();
        dictionaryLambdaUpdateWrapper.eq(Dictionary::getDicTypeCode,dictionaryType.getDicTypeCode());
        dictionaryLambdaUpdateWrapper.set(Dictionary::getIsDeleted,IsDeletedEnum.YES);
        dictionaryMapper.update(null,dictionaryLambdaUpdateWrapper);
        return "删除成功";
    }

    @Override
    public String updateSort(DictionaryTypeSortDto dto) {
        for (DictionaryTypeSortItemDto item : dto.getSortItemDtoList()) {
            LambdaUpdateWrapper<DictionaryType> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(DictionaryType::getDicTypeId,item.getDicTypeId());
            lambdaUpdateWrapper.set(DictionaryType::getDicTypeSort,item.getDicTypesort());
            dictionaryTypeMapper.update(null,lambdaUpdateWrapper);
        }
        return "更新排序成功";
    }

    @Override
    public IPage<DictionaryType> selectByPage(DictionaryTypePageDto dto) {
        LambdaQueryWrapper<DictionaryType> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DictionaryType::getIsDeleted,IsDeletedEnum.NO);
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(dto.getDicTypeCode()),DictionaryType::getDicTypeCode,dto.getDicTypeCode()); // code精准查询
        lambdaQueryWrapper.like(StringUtils.isNotBlank(dto.getDicTypeName()),DictionaryType::getDicTypeName,dto.getDicTypeName()); // name模糊查询
        lambdaQueryWrapper.like(StringUtils.isNotBlank(dto.getDeScript()),DictionaryType::getDeScript,dto.getDeScript()); // 描述也是牧户查询
        lambdaQueryWrapper.orderByAsc(DictionaryType::getDicTypeSort);
        IPage<DictionaryType> ipage = this.page(new Page<>(dto.getPageNo(),dto.getPageSize()),lambdaQueryWrapper);
        return ipage;
    }
}
