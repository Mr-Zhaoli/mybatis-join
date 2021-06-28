package com.mybatis.plus.join.column;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.mybatis.plus.join.ColumnData;
import com.mybatis.plus.join.SFuncUtils;
import lombok.Data;

@Data
public class TableColumn<T> implements Column {

    private SFunction<T, ?> columnFunction;
    private String asName;

    public TableColumn() {
    }

    public TableColumn(SFunction<T, ?> columnFunction) {
        this.columnFunction = columnFunction;
    }

    @Override
    public String selectColumn() {
        String s = SFuncUtils.getColumnNameWithTable(columnFunction);
        if (asName == null || "".equals(asName)) {
            return s;
        }
        return s + " AS " + asName;
    }

    @Override
    public void fillData(ColumnData columnData) {

    }

    @Override
    public void setAsName(String asName) {
        this.asName = asName;
    }
}
