package com.mybatis.plus.join;

import com.mybatis.plus.join.column.Column;
import lombok.Data;

/**
 * 列=列的判断条件
 * is null 或者 is not null
 *
 * @author by zhaojin
 * @since 2021/6/18 9:49
 */
@Data
public class ColumnCondition implements Condition {
    private ConditionEnum sqlKeyword;
    private Column column1;
    private Column column2;

    // 满足 is null 或者 is not null的情况
    public ColumnCondition(ConditionEnum sqlKeyword, Column column2) {
        this.sqlKeyword = sqlKeyword;
        this.column2 = column2;
    }

    public ColumnCondition(Column column1, ConditionEnum sqlKeyword) {
        this.sqlKeyword = sqlKeyword;
        this.column1 = column1;
    }

    public ColumnCondition(ConditionEnum sqlKeyword, Column column1, Column column2) {
        this.sqlKeyword = sqlKeyword;
        this.column1 = column1;
        this.column2 = column2;
    }

    @Override
    public String toString() {
        if (column1 == null) {
            return column2.selectColumn() + sqlKeyword.getSqlSegment();
        } else if (column2 == null) {
            return column1.selectColumn() + sqlKeyword.getSqlSegment();
        } else {
            return column1.selectColumn() + sqlKeyword.getSqlSegment() + column2.selectColumn();
        }
    }
}
