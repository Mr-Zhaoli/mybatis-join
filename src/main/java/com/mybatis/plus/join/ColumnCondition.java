package com.mybatis.plus.join;

import com.mybatis.plus.join.column.Column;
import lombok.Data;

/**
 * TODO
 *
 * @author by zhaojin
 * @since 2021/6/18 9:49
 */
@Data
public class ColumnCondition implements Condition {
    private ConditionEnum sqlKeyword;
    private Column column1;
    private Column column2;

    public ColumnCondition() {
    }

    public ColumnCondition(ConditionEnum sqlKeyword, Column column1, Column column2) {
        this.sqlKeyword = sqlKeyword;
        this.column1 = column1;
        this.column2 = column2;
    }

    @Override
    public String toString() {
        return column1.selectColumn() + sqlKeyword.getSqlSegment() + column2.selectColumn();
    }
}
