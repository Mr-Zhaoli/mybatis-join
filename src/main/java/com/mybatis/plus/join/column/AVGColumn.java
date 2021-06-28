package com.mybatis.plus.join.column;

import com.mybatis.plus.join.ColumnData;
import lombok.Data;

@Data
public class AVGColumn implements Column {
    private Column column;
    private String asName;

    public AVGColumn(Column column) {
        this.column = column;
    }

    public AVGColumn(Column column, String asName) {
        this.column = column;
        this.asName = asName;
    }

    @Override
    public String selectColumn() {
        if (asName == null || "".equals(asName)) {
            return "AVG(" + column.selectColumn() + ")";
        }
        return "AVG(" + column.selectColumn() + ") AS " + asName;
    }

    @Override
    public void fillData(ColumnData columnData) {
        column.fillData(columnData);
    }
}