package com.mybatis.plus.join.column;

import com.mybatis.plus.join.ColumnData;
import lombok.Data;

import java.io.Serializable;

@Data
public class ConstColumn implements Column {
    private final Serializable value;
    private String tableName;
    private String asName;

    public ConstColumn(Serializable value) {
        this.value = value;
    }

    @Override
    public String selectColumn() {
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        } else {
            return "'" + value.toString() + "'";
        }
    }

    @Override
    public void fillData(ColumnData columnData) {

    }

    @Override
    public void setAsName(String asName) {
        this.asName = asName;
    }
}
