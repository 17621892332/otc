package org.orient.otc.common.dictionary.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.api.system.feign.DictionaryClient;
import org.orient.otc.common.dictionary.annotion.Dictionary;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * 字典序列化
 */
@Component
@Slf4j
public class DictionaryTransformer {

    @Resource
    private DictionaryClient dictionaryClient;

    /**
     * 将对象列表中所有带有 @Dictionary 注解的字符串字段转换为对应的字典值。
     * 遍历每个对象的字段，如果字段被 @Dictionary 注解并且是String类型，
     * 则根据注解提供的字典类型和字段的当前值从字典服务中获取相应的转换值。
     * 如果找到转换值，则更新该字段的值。
     *
     * @param objects 要转换的对象列表。这些对象可能包含一个或多个被 @Dictionary 注解的字符串字段。
     */
    public void transform2ValueList(List<?> objects) {
        // 从字典客户端获取字典映射，其中键是字典类型和值的组合，值是字典的转换值。
        Map<String, String> map = dictionaryClient.getDictionaryMap();
        for (Object object : objects) {
            Class<?> clazz = object.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                // 检查字段是否标记了Dictionary注解并且是String类型
                if (field.isAnnotationPresent(Dictionary.class) && field.getType().equals(String.class)) {
                    Dictionary dictAnnotation = field.getAnnotation(Dictionary.class);
                    field.setAccessible(true); // 设置私有字段可访问
                    try {
                        // 获取当前字段的值
                        Object fieldValue = field.get(object);
                        // 如果字段值不为空，则尝试进行转换
                        if (fieldValue != null) {
                            // 构造查找键并从映射中获取转换后的值
                            String transformedValue = map.get(dictAnnotation.type() + "_" + fieldValue);
                            // 如果找到了转换后的值，则更新对象的字段值
                            if (StringUtils.isNotBlank(transformedValue)) {
                                field.set(object, transformedValue);
                            }
                        }
                    } catch (Exception e) {
                        // 处理可能的异常，如访问字段失败
                        log.error("字典转换异常:",e);
                    }
                }
            }
        }
    }

}

