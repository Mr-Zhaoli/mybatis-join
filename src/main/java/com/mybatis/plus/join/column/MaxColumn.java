package com.mybatis.plus.join.column;

import com.mybatis.plus.join.ColumnData;
import lombok.Data;

@Data
public class MaxColumn implements Column {
    private Column column;
    private String asName;

    public MaxColumn(Column column) {
        this.column = column;
    }

    public MaxColumn(Column column, String asName) {
        this.column = column;
        this.asName = asName;
    }

    @Override
    public String selectColumn() {
        if (asName == null || "".equals(asName)) {
            return "max(" + column.selectColumn() + ")";
        }
        return "max(" + column.selectColumn() + ") as " + asName;
    }

    @Override
    public void fillData(ColumnData columnData) {
        column.fillData(columnData);
    }
}