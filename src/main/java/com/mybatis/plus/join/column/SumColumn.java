package com.mybatis.plus.join.column;

import com.mybatis.plus.join.ColumnData;
import lombok.Data;

@Data
public class SumColumn implements Column {
    private Column column;
    private String asName;

    public SumColumn(Column column) {
        this.column = column;
    }

    public SumColumn(Column column, String asName) {
        this.column = column;
        this.asName = asName;
    }

    @Override
    public String selectColumn() {
        if (asName == null || "".equals(asName)) {
            return "SUM(" + column.selectColumn() + ")";
        }
        return "SUM(" + column.selectColumn() + ") AS " + asName;
    }

    @Override
    public void fillData(ColumnData columnData) {
        column.fillData(columnData);
    }
}