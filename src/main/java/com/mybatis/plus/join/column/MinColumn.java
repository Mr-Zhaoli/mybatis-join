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
            return "min(" + column.selectColumn() + ")";
        }
        return "min(" + column.selectColumn() + ") as " + asName;
    }

    @Override
    public void fillData(ColumnData columnData) {
        column.fillData(columnData);
    }
}