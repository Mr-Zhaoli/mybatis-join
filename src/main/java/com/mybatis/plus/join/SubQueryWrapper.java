package com.mybatis.plus.join;


import com.baomidou.mybatisplus.core.conditions.SharedString;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mybatis.plus.join.column.Column;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 子查询
 *
 * @author by zhaojin
 * @since 2021/6/25 14:56
 */
public class SubQueryWrapper<T> extends JoinWrapper<T> {

    public SubQueryWrapper(T entity, Class<T> cl, AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments, SharedString lastSql, SharedString sqlComment) {
        super(entity, cl, paramNameSeq, paramNameValuePairs, mergeSegments, lastSql, sqlComment);
    }

    @SuppressWarnings("unchecked")
    public <M> SubQueryWrapper<M> query(Class<M> s) {
        this.entityClass = (Class<T>) s;
        tableName = TableInfoHelper.getTableInfo(entityClass).getTableName();
        return (SubQueryWrapper<M>) this;
    }

    @Override
    public String getSqlSegment() {
        String sqlSegment = expression.getSqlSegment();
        String prefix = "(SELECT " + getSqlSelect() + " FROM " + tableName + getJoins();
        String suffix = ")";
        if (StringUtils.isNotEmpty(sqlSegment)) {
            prefix += " WHERE " + sqlSegment;
        }
        if (StringUtils.isNotEmpty(lastSql.getStringValue())) {
            prefix += " " + lastSql.getStringValue();
        }
        return prefix + suffix;
    }

    @Override
    public String getSqlSelect() {
        if (selectList.isEmpty()) {
            return "*";
        }
        return selectList.values().stream().flatMap(Collection::stream)
                .map(Column::selectColumn)
                .collect(Collectors.joining(","));
    }
}
