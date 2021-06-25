package com.mybatis.plus.join.column;

import com.baomidou.mybatisplus.core.conditions.SharedString;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.mybatis.plus.join.ColumnData;
import com.mybatis.plus.join.SubQueryWrapper;
import lombok.Data;

import java.util.function.Consumer;

/**
 * 子查询列
 *
 * @author by zhaojin
 * @since 2021/6/22 18:15
 */
@Data
public class SubQueryColumn implements Column {
    private Consumer<SubQueryWrapper<?>> consumer;
    private SubQueryWrapper<?> wrapper;
    private String asName;

    @Override
    public String selectColumn() {
        String s = wrapper.getSqlSegment();
        if (asName == null || "".equals(asName)) {
            return s;
        }
        return s + " AS " + asName;
    }

    @Override
    public void fillData(ColumnData join) {
        wrapper = new SubQueryWrapper<>(
                null,
                join.getWrapper().getEntityClass(),
                join.getWrapper().getParamNameSeq(),
                join.getWrapper().getParamNameValuePairs(),
                new MergeSegments(),
                SharedString.emptyString(),
                SharedString.emptyString());
        consumer.accept(wrapper);
    }
}
