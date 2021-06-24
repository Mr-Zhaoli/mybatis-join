package com.mybatis.plus.join.column;

import com.mybatis.plus.join.ColumnData;
import com.mybatis.plus.join.JoinWrapper;
import lombok.Data;

import java.io.Serializable;

@Data
public class ConstColumn implements Column {
    private final Object value;
    private JoinWrapper<?> wrapper;

    public ConstColumn(Object value) {
        this.value = value;
    }

    @Override
    public String selectColumn() {
        return wrapper.fmtSql(value);
    }

    @Override
    public void fillData(ColumnData columnData) {
        wrapper = columnData.getWrapper();
    }

    @Override
    public void setAsName(String asName) {
    }
}
