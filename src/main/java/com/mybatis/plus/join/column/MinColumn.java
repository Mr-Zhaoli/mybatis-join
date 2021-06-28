package com.mybatis.plus.join.column;

import com.mybatis.plus.join.ColumnData;
import lombok.Data;

@Data
public class MinColumn implements Column {
    private Column column;
    private String asName;

    public MinColumn(Column column) {
        this.column = column;
    }

    public MinColumn(Column column, String asName) {
        this.column = column;
        this.asName = asName;
    }

    @Override
    public String selectColumn() {
        if (asName == null || "".equals(asName)) {
            return "MIN(" + column.selectColumn() + ")";
        }
        return "MIN(" + column.selectColumn() + ") AS " + asName;
    }

    @Override
    public void fillData(ColumnData columnData) {
        column.fillData(columnData);
    }
}