package com.mybatis.plus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mybatis.plus.join.JoinWrapper;
import com.mybatis.plus.mapper.ParentMapper;

public interface IBaseService<T> extends IService<T> {

    default JoinWrapper<T> query(Class<T> cl) {
        JoinWrapper<T> wrapper = new JoinWrapper<>(cl);
        wrapper.setBaseMapper((ParentMapper<T>) getBaseMapper());
        return wrapper;
    }

}
