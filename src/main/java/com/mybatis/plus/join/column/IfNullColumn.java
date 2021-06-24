package com.mybatis.plus.join.column;

import com.mybatis.plus.join.ColumnData;
import lombok.Data;

@Data
public class IfNullColumn implements Column {

    private Column column;
    private Column otherValue;
    private String asName;

    public IfNullColumn(Column column, Column otherValue) {
        this.column = column;
        this.otherValue = otherValue;
    }

    @Override
    public String selectColumn() {
        String s = "IFNULL("
                + column.selectColumn()
                + ","
                + otherValue.selectColumn()
                + ")";
        if (asName == null || "".equals(asName)) {
            return s;
        }
        return s + " as " + asName;
    }

    @Override
    public void fillData(ColumnData columnData) {
        column.fillData(columnData);
        otherValue.fillData(columnData);
    }
}
