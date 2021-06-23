package com.mybatis.plus.join.column;

import com.baomidou.mybatisplus.core.conditions.SharedString;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.mybatis.plus.join.ColumnData;
import com.mybatis.plus.join.ExistWrapper;
import lombok.Data;

import java.util.function.Consumer;

/**
 * TODO
 *
 * @author by zhaojin
 * @since 2021/6/22 18:15
 */
@Data
public class ExistColumn implements Column {
    private Consumer<ExistWrapper> consumer;
    private ExistWrapper wrapper;
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
        wrapper = new ExistWrapper<>(
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
