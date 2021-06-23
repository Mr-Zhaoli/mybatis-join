package com.mybatis.plus.join.column;

import com.mybatis.plus.join.ColumnData;
import lombok.Data;

@Data
public class IfNullColumn implements Column {

    private Column column;
    private Column trueValue;
    private Column falseValue;
    private String asName;

    public IfNullColumn(Column column, Column trueValue, Column falseValue) {
        this.column = column;
        this.trueValue = trueValue;
        this.falseValue = falseValue;
    }

    @Override
    public String selectColumn() {
        String s = "IFNULL("
                + column.selectColumn()
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
        column.fillData(columnData);
        trueValue.fillData(columnData);
        falseValue.fillData(columnData);
    }
}
