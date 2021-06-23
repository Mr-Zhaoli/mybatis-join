package com.mybatis.plus.join;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

/**
 * TODO
 *
 * @author by zhaojin
 * @since 2021/6/22 16:58
 */
public interface CommonFunction<T> {

    String columnToStringWithoutTableName(SFunction<T, ?> column);
}
