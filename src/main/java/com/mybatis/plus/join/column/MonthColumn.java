package com.mybatis.plus.join.column;

import com.mybatis.plus.join.ColumnData;
import lombok.Data;

@Data
public class MonthColumn implements Column {
    private Column column;
    private String asName;

    public MonthColumn(Column column) {
        this.column = column;
    }

    @Override
    public String selectColumn() {
        if (asName == null || "".equals(asName)) {
            return "MONTH(" + column.selectColumn() + ")";
        }
        return "MONTH(" + column.selectColumn() + ") AS " + asName;
    }

    @Override
    public void fillData(ColumnData columnData) {
        column.fillData(columnData);
    }

    @Override
    public void setAsName(String asName) {
        this.asName = asName;
    }
}
