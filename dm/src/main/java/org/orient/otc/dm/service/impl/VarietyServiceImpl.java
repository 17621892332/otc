package org.orient.otc.dm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.cglib.CglibUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.api.dm.enums.AssetTypeEnum;
import org.orient.otc.api.dm.vo.VarietyVo;
import org.orient.otc.api.system.feign.DictionaryClient;
import org.orient.otc.api.user.feign.UserClient;
import org.orient.otc.api.user.vo.UserVo;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.dm.dto.variety.VarietyAddDto;
import org.orient.otc.dm.dto.variety.VarietyEditDto;
import org.orient.otc.dm.dto.variety.VarietyIdDto;
import org.orient.otc.dm.dto.variety.VarietyQueryDto;
import org.orient.otc.dm.entity.Variety;
import org.orient.otc.dm.entity.VarietyType;
import org.orient.otc.dm.mapper.VarietyMapper;
import org.orient.otc.dm.mapper.VarietyTypeMapper;
import org.orient.otc.dm.service.UnderlyingManagerService;
import org.orient.otc.dm.service.VarietyService;
import org.orient.otc.dm.vo.VarietyByTraderIdVO;
import org.orient.otc.dm.vo.VarietyByVarietyTypeVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 品种服务实现
 */
@Service
public class VarietyServiceImpl extends ServiceImpl<VarietyMapper, Variety> implements VarietyService {

    @Resource
    private VarietyTypeMapper varietyTypeMapper;

    @Resource
    private UnderlyingManagerService underlyingManagerService;

    @Resource
    private DictionaryClient dictionaryClient;

    @Resource
    private UserClient userClient;

    @Override
    public List<Variety> getList() {
        return this.getBaseMapper().selectList(new LambdaQueryWrapper<Variety>().eq(Variety :: getIsDeleted,0));
    }

    @Override
    public Page<VarietyVo> queryVarietyList(VarietyQueryDto varietyQueryDto) {

        LambdaQueryWrapper<Variety> queryWrapper = new LambdaQueryWrapper<>();
        //产业链ID
        queryWrapper.eq(varietyQueryDto.getVarietyTypeId()!=null ,Variety::getVarietyTypeId,varietyQueryDto.getVarietyTypeId());
        //品种名称
        queryWrapper.like(StringUtils.isNotBlank(varietyQueryDto.getVarietyName()),Variety::getVarietyName,varietyQueryDto.getVarietyName());
        //品种代码
        queryWrapper.like(StringUtils.isNotBlank(varietyQueryDto.getVarietyCode()),Variety::getVarietyCode,varietyQueryDto.getVarietyCode());
        queryWrapper.eq(Variety::getIsDeleted,IsDeletedEnum.NO);
        Page<Variety> dbPage= this.page(new Page<>(varietyQueryDto.getPageNo(),varietyQueryDto.getPageSize()),queryWrapper);
        Map<Integer,String> varietyTypeMap =varietyTypeMapper.selectList(new LambdaQueryWrapper<VarietyType>()
                .eq(VarietyType::getIsDeleted,IsDeletedEnum.NO))
                .stream().collect(Collectors.toMap(VarietyType::getId,VarietyType::getTypeName));
        //转换数据
        Page<VarietyVo> voPage = new Page<>();
        BeanUtil.copyProperties(dbPage,voPage);
        List<Variety> records = dbPage.getRecords();
        List<VarietyVo> voList = CglibUtil.copyList(records,VarietyVo::new,(db,vo)->{
            vo.setVarietyTypeName(varietyTypeMap.get(db.getVarietyTypeId()));
        });
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public String addVariety(VarietyAddDto addDto) {
        Variety variety = CglibUtil.copy(addDto,Variety.class);
        int c= this.getBaseMapper().insert(variety);
        if (c>0){
            return "新增成功";
        }else {
            return "新增失败";
        }
    }

    @Override
    public String editVariety(VarietyEditDto editDto) {
        Variety variety = CglibUtil.copy(editDto,Variety.class);
        int c= this.getBaseMapper().updateById(variety);
        if (c>0){
            //更新对应的合约的涨跌停信息
            underlyingManagerService.updateUnderlyingUpDownLimit(variety.getId(),variety.getUpDownLimit());
            return "保存成功";
        }else {
            return "保存失败";
        }
    }

    @Override
    public String deleteVariety(VarietyIdDto varietyIdDto) {
        Variety variety = new Variety();
        variety.setIsDeleted(IsDeletedEnum.YES.getFlag());
        variety.setId(varietyIdDto.getVarietyId());
        int c= this.getBaseMapper().updateById(variety);
        if (c>0){
            return "删除成功";
        }else {
            return "删除失败";
        }
    }

    @Override
    public List<VarietyVo> queryVarietyListById(Set<Integer> idSet) {
        LambdaQueryWrapper<Variety> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(idSet!=null &&!idSet.isEmpty(),Variety::getId,idSet);
        queryWrapper.eq(Variety::getIsDeleted,IsDeletedEnum.NO);
        List<VarietyVo> list= this.listVo(queryWrapper,VarietyVo.class);
        Map<Integer,String> varietyTypeMap =varietyTypeMapper.selectList(new LambdaQueryWrapper<VarietyType>()
                        .eq(VarietyType::getIsDeleted,IsDeletedEnum.NO))
                .stream().collect(Collectors.toMap(VarietyType::getId,VarietyType::getTypeName));
        //转换数据
        list.forEach(item->item.setVarietyTypeName(varietyTypeMap.get(item.getVarietyTypeId())));
        return  list;
    }

    @Override
    public VarietyVo getVarietyById(Integer varietyId) {
        return this.getVoOne(new LambdaQueryWrapper<Variety>()
                .eq(Variety::getId,varietyId)
                .eq(Variety::getIsDeleted, IsDeletedEnum.NO),VarietyVo.class);
    }

    @Override
    public List<Variety> getVarietListByAssetType(AssetTypeEnum assetType) {
        Map<String, String> assetTypeMap = dictionaryClient.getDictionaryMapByIds("AssetType");
        List<String> assetTypeList = assetTypeMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(assetType.name()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return this.list(new LambdaQueryWrapper<Variety>().in(Variety::getUnderlyingAssetType, assetTypeList).eq(Variety::getIsDeleted, IsDeletedEnum.NO));
    }

    @Override
    public List<VarietyByVarietyTypeVO> getVarietyByVarietyTypeList() {
        LambdaQueryWrapper<Variety> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Variety::getIsDeleted,IsDeletedEnum.NO);
        List<Variety> list = this.list(queryWrapper);
        Map<Integer,List<Variety>> map=list.stream().collect(Collectors.groupingBy(x -> x.getVarietyTypeId()==null ? 0 : x.getVarietyTypeId()));
        Map<Integer,String> varietyTypeMap =varietyTypeMapper.selectList(new LambdaQueryWrapper<VarietyType>()
                        .eq(VarietyType::getIsDeleted,IsDeletedEnum.NO))
                .stream().collect(Collectors.toMap(VarietyType::getId,VarietyType::getTypeName));
        map.remove(0);
        List<VarietyByVarietyTypeVO> varietyByVarietyTypeVOList = new ArrayList<>();
        for (Map.Entry<Integer,List<Variety>> entry : map.entrySet()){
            VarietyByVarietyTypeVO vo = new VarietyByVarietyTypeVO();
            vo.setVarietyTypeId(entry.getKey());
            vo.setVarietyCodeList(entry.getValue().stream().map(Variety::getVarietyCode).collect(Collectors.toList()));
            vo.setVarietyTypeName(varietyTypeMap.get(entry.getKey()));
            varietyByVarietyTypeVOList.add(vo);
        }
        return varietyByVarietyTypeVOList;
    }

    @Override
    public List<VarietyByTraderIdVO> getVarietyByTraderIdList() {
        LambdaQueryWrapper<Variety> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Variety::getIsDeleted,IsDeletedEnum.NO);
        List<Variety> list = this.list(queryWrapper);
        Map<Integer,List<Variety>> map=list.stream().collect(Collectors.groupingBy(x -> x.getTraderId()==null ? 0 : x.getTraderId()));
        Map<Integer, String> traderMap = userClient.getUserList().stream()
                .collect(Collectors.toMap(UserVo::getId, UserVo::getName));
        map.remove(0);
        List<VarietyByTraderIdVO> varietyByTraderIdVOList = new ArrayList<>();
        for (Map.Entry<Integer,List<Variety>> entry : map.entrySet()){
            VarietyByTraderIdVO vo = new VarietyByTraderIdVO();
            vo.setTraderId(entry.getKey());
            vo.setVarietyCodeList(entry.getValue().stream().map(Variety::getVarietyCode).collect(Collectors.toList()));
            vo.setTraderName(traderMap.get(entry.getKey()));
            varietyByTraderIdVOList.add(vo);
        }
        return varietyByTraderIdVOList;
    }
}
