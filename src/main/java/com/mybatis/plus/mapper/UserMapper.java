package com.mybatis.plus.mapper;

import com.mybatis.plus.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends ParentMapper<User> {
}
