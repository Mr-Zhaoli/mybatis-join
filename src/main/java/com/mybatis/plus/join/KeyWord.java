package com.mybatis.plus.join;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;

/**
 * TODO
 *
 * @author by zhaojin
 * @since 2021/6/22 19:14
 */
public enum KeyWord implements ISqlSegment {

    INNER_JOIN("INNER JOIN");


    private final String keyword;

    KeyWord(final String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String getSqlSegment() {
        return this.keyword;
    }
}
