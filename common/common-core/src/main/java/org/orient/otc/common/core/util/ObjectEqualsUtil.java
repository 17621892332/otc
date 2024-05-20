package org.orient.otc.common.core.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.orient.otc.common.core.vo.DiffObjectVO;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 比较2个类型相同的对象字段内容,并返回不同
 * @author chengqiang
 */
@Data
@Component
@Slf4j
public class ObjectEqualsUtil {
    /**
     * 两个对象的字段内容是否相同(字段类型相同) , 字段内容不同的返回(返回字段名称,原内容,变更后内容)
     * 2个对象必须用特定注解FieldAlias注解添加别名 , 否则取不到变更字段的中文描述(取不到职中文描述,只能取字段的英文)
     * @param org  比较对象1
     * @param dest 比较对象2
     * @return 比对结果
     */
    public List<DiffObjectVO> equalsObjectField(Object org, Object dest) {
        if (org == null || dest == null) {
            return new ArrayList<>();
        }
        if (!org.getClass().equals(dest.getClass())) {
            return new ArrayList<>();
        }
        List<DiffObjectVO> list = new ArrayList<>();
        Class<?> orgClass = org.getClass();
        Class<?> destClass = dest.getClass();
        List<Field> orgList= Arrays.asList(orgClass.getDeclaredFields());
        List<Field> destList = Arrays.asList(destClass.getDeclaredFields());
        if (orgList.isEmpty() || destList.isEmpty()) {
            return new ArrayList<>();
        }
        // key = 字段名 value = field对象
        Map<String,Field> descMap = new HashMap<>();
        for (Field descField : destList) {
            descField.setAccessible(true);
            descMap.put(descField.getName(),descField);
        }
        for (Field orgfField : orgList) {
            orgfField.setAccessible(true);
            // org字段名称
            String orfFieldName = orgfField.getName();
            // org字段值
            Object orgValue;
            try {
                orgValue = orgfField.get(org);
            } catch (IllegalAccessException e) {
                log.error("获取对应的字段值异常");
                continue;
            }
            Class<?> orgFieldType = orgfField.getType(); // org字段类型
            DiffObjectVO vo = new DiffObjectVO();
            // 获取字段注解 , 取字段的中文描述
            FieldAlias annotation = orgfField.getAnnotation(FieldAlias.class);

            if (annotation != null) {
                // 获取注解值
                String value = annotation.value();
                // 当前字段是否需要记录入库
                boolean need = annotation.need();
                if (!need) {
                    continue;
                }
                vo.setName(value);
            } else {
                vo.setName(orfFieldName);
            }
            vo.setOrgValue(orgValue);
            if (descMap.containsKey(orfFieldName)) {
                Field descField = descMap.get(orfFieldName);
                Object destValue; // dest字段值
                try {
                    destValue = descField.get(dest);
                } catch (IllegalAccessException e) {
                    log.error("获取对应的字段值异常");
                    continue;
                }
                // 两个字段都为空不记录
                if ( (orgValue==null || "".equals(orgValue)) && (destValue==null || "".equals(destValue)) ) {
                    continue;
                }
                // dest字段类型
                Class<?> descFieldType = descField.getType();
                if (descFieldType==orgFieldType) {
                    vo.setDestValue(destValue);
                    // 值是否相同 , 相同的不返回
                    boolean valueIsSame;
                    String orgStringValue = String.valueOf(orgValue);
                    String destStringValue = String.valueOf(destValue);
                    valueIsSame = orgStringValue.equals(destStringValue);
                    // 值不相同
                    if (!valueIsSame) {
                        list.add(vo);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 获取某一个对象的所有字段值(只适用于新增)
     * @param addObj 内容
     * @return 结果
     */
    public List<DiffObjectVO> getObjectFields(Object addObj) {
        if (addObj == null) {
           return  new ArrayList<>();
        }
        // 返回值
        List<DiffObjectVO> list = new ArrayList<>();
        Class<?> addClass = addObj.getClass();
        List<Field> fieldList = Arrays.asList(addClass.getDeclaredFields());
        if (CollectionUtils.isEmpty(fieldList)) {
            return  new ArrayList<>();
        }
        for (Field item : fieldList) {
            DiffObjectVO vo = new DiffObjectVO();
            item.setAccessible(true);
            // 获取字段注解 , 取字段的中文描述
            FieldAlias annotation = item.getAnnotation(FieldAlias.class);
            if (annotation != null) {
                // 获取注解值
                String value = annotation.value();
                // 当前字段是否需要记录入库
                boolean need = annotation.need();
                if (!need) {
                    continue;
                }
                vo.setName(value);
            } else {
                vo.setName(item.getName());
            }
            vo.setOrgValue("");
            try {
                // 为空不添加
                if (item.get(addObj)==null || StringUtils.isBlank(item.get(addObj).toString())) {
                    continue;
                }
                vo.setDestValue(item.get(addObj));
            } catch (IllegalAccessException e) {
                log.error("获取对应值出错");
            }
            list.add(vo);
        }
        return list;
    }

    /**
     * 获取一个删除的字段变更对象
     * @return 返回对象
     */
    public DiffObjectVO getDeleteDiffObjectVO() {
        DiffObjectVO vo = new DiffObjectVO();
        vo.setName("状态");
        vo.setOrgValue("正常");
        vo.setDestValue("已删除");
        return vo;
    }
    /**
     * 构建一个指定的字段变更对象
     * @param name 变更字段名称
     * @param orgValue 变更前的值
     * @param destValue 变更后的值
     * @return 返回对象
     */
    public DiffObjectVO buildDiffObjectVO(String name,String orgValue,String destValue) {
        DiffObjectVO vo = new DiffObjectVO();
        vo.setName(name);
        vo.setOrgValue(orgValue);
        vo.setDestValue(destValue);
        return vo;
    }
}
