package org.orient.otc.common.database.config;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface IServicePlus<T> extends IService<T> {
    default int insertDto(Object dto, Class<T> kClass){
        return getBaseMapper().insert(BeanUtil.toBean(dto,kClass));
    }
    /**
     * 根据 ID 查询
     *
     * @param kClass vo类型
     * @param id     主键ID
     */
     default <K> K getVoById(Serializable id, Class<K> kClass) {
         T t = getBaseMapper().selectById(id);
         if(t == null){
             return null;
         }
        return BeanUtil.toBean(t,kClass);
    }

    /**
     * 根据 ID 查询
     *
     * @param id        主键ID
     * @param convertor 转换函数
     * @param <K>       vo类型
     */
    default <K> K getVoById(Serializable id, Function<T, K> convertor) {
        T t = getBaseMapper().selectById(id);
        if(t == null){
            return null;
        }
        return convertor.apply(t);
    }

    /**
     * 查询（根据ID 批量查询）
     *
     * @param kClass vo类型
     * @param idList 主键ID列表
     */
    default <K> List<K> listVoByIds(Collection<? extends Serializable> idList, Class<K> kClass) {
        List<T> list = getBaseMapper().selectBatchIds(idList);
        if (list == null) {
            return null;
        }
        return list.stream()
                .map(any -> BeanUtil.toBean(any, kClass))
                .collect(Collectors.toList());
    }

    /**
     * 查询（根据ID 批量查询）
     *
     * @param convertor 转换函数
     * @param idList    主键ID列表
     */
    default <K> List<K> listVoByIds(Collection<? extends Serializable> idList,
                                    Function<Collection<T>, List<K>> convertor) {
        List<T> list = getBaseMapper().selectBatchIds(idList);
        if (list == null) {
            return null;
        }
        return convertor.apply(list);
    }

    /**
     * 查询（根据 columnMap 条件）
     *
     * @param kClass    vo类型
     * @param columnMap 表字段 map 对象
     */
    default  <K> List<K> listVoByMap(Map<String, Object> columnMap, Class<K> kClass) {
        List<T> list = getBaseMapper().selectByMap(columnMap);
        if (list == null) {
            return null;
        }
        return list.stream()
                .map(any -> BeanUtil.toBean(any, kClass))
                .collect(Collectors.toList());
    }

    /**
     * 查询（根据 columnMap 条件）
     *
     * @param convertor 转换函数
     * @param columnMap 表字段 map 对象
     */
    default <K> List<K> listVoByMap(Map<String, Object> columnMap,
                                    Function<Collection<T>, List<K>> convertor) {
        List<T> list = getBaseMapper().selectByMap(columnMap);
        if (list == null) {
            return null;
        }
        return convertor.apply(list);
    }

    /**
     * 根据 Wrapper，查询一条记录 <br/>
     * <p>结果集，如果是多个会抛出异常，随机取一条加上限制条件 wrapper.last("LIMIT 1")</p>
     *
     * @param kClass       vo类型
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     */
    default <K> K getVoOne(Wrapper<T> queryWrapper, Class<K> kClass) {
        T t = getOne(queryWrapper, true);
        if(t == null){
            return null;
        }
        return BeanUtil.toBean(t, kClass);
    }

    /**
     * 根据 Wrapper，查询一条记录 <br/>
     * <p>结果集，如果是多个会抛出异常，随机取一条加上限制条件 wrapper.last("LIMIT 1")</p>
     *
     * @param convertor    转换函数
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     */
    default <K> K getVoOne(Wrapper<T> queryWrapper, Function<T, K> convertor) {
        T t = getOne(queryWrapper, true);
        if(t == null){
            return null;
        }
        return convertor.apply(t);
    }

    /**
     * 查询列表
     *
     * @param kClass       vo类型
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     */
    default <K> List<K> listVo(Wrapper<T> queryWrapper, Class<K> kClass) {
        List<T> list = getBaseMapper().selectList(queryWrapper);
        if (list == null) {
            return null;
        }
        return list.stream()
                .map(any -> BeanUtil.toBean(any, kClass))
                .collect(Collectors.toList());
    }

    /**
     * 查询列表
     *
     * @param convertor    转换函数
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     */
    default <K> List<K> listVo(Wrapper<T> queryWrapper, Function<Collection<T>, List<K>> convertor) {
        List<T> list = getBaseMapper().selectList(queryWrapper);
        if (list == null) {
            return null;
        }
        return convertor.apply(list);
    }

    /**
     * 查询所有
     *
     * @param kClass vo类型
     * @see Wrappers#emptyWrapper()
     */
    default <K> List<K> listVo(Class<K> kClass) {
        return listVo(Wrappers.emptyWrapper(), kClass);
    }

    /**
     * 查询所有
     *
     * @param convertor 转换函数
     * @see Wrappers#emptyWrapper()
     */
    default <K> List<K> listVo(Function<Collection<T>, List<K>> convertor) {
        return listVo(Wrappers.emptyWrapper(), convertor);
    }
}
