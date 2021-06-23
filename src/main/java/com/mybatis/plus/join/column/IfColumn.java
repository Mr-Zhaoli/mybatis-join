package com.mybatis.plus.join.column;

import com.mybatis.plus.join.ColumnData;
import com.mybatis.plus.join.ConditionEnum;
import lombok.Data;

@Data
public class IfColumn implements Column {

    private Column column1;
    private ConditionEnum condition;
    private Column column2;
    private Column trueValue;
    private Column falseValue;
    private String asName;

    public IfColumn(Column column1, ConditionEnum condition, Column column2, Column trueValue, Column falseValue) {
        this.column1 = column1;
        this.condition = condition;
        this.column2 = column2;
        this.trueValue = trueValue;
        this.falseValue = falseValue;
    }

    @Override
    public String selectColumn() {
        String s = "IF("
                + (column1 != null ? column1.selectColumn() : "") + (condition != null ? condition.getSqlSegment() : "") + (column2 != null ? column2.selectColumn() : "")
                + ","
                + trueValue.selectColumn()
                + ","
                + falseValue.selectColumn()
                + ")";
        if (asName == null || "".equals(asName)) {
            return s;
        }
        return s + " as " + asName;
    }

    @Override
    public void fillData(ColumnData columnData) {
        if (column1 != null) {
            column1.fillData(columnData);
        }
        if (column2 != null) {
            column2.fillData(columnData);
        }
        if (trueValue != null) {
            trueValue.fillData(columnData);
        }
        if (falseValue != null) {
            falseValue.fillData(columnData);
        }
    }
}
