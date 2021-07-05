package com.mybatis.plus.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ParentMapper<T> extends BaseMapper<T> {

    List<Object> selectObjsJoin(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);


    Object selectObjJoin(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);


    T selectOneJoin(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);


    Integer selectCountJoin(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);


    List<T> selectListJoin(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);


    IPage<T> selectPageJoin(IPage<T> page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper);


    Map<String, Object> selectMapJoin(@Param(Constants.WRAPPER) Wrapper<?> queryWrapper);


    List<Map<String, Object>> selectMapsJoin(@Param(Constants.WRAPPER) Wrapper<?> queryWrapper);


    IPage<Map<String, Object>> selectMapsPageJoin(IPage<?> page, @Param(Constants.WRAPPER) Wrapper<?> queryWrapper);
}
