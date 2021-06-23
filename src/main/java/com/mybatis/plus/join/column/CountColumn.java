package com.mybatis.plus.join.column;

import com.mybatis.plus.join.ColumnData;
import lombok.Data;

@Data
public class CountColumn implements Column {
    private Column column;
    private String asName;

    public CountColumn(Column column) {
        this.column = column;
    }

    public CountColumn(Column column, String asName) {
        this.column = column;
        this.asName = asName;
    }

    @Override
    public String selectColumn() {
        if (asName == null || "".equals(asName)) {
            return "count(" + column.selectColumn() + ")";
        }
        return "count(" + column.selectColumn() + ") as " + asName;
    }

    @Override
    public void fillData(ColumnData columnData) {
        column.fillData(columnData);
    }
}