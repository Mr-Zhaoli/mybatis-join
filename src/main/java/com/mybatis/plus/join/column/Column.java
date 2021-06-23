package com.mybatis.plus.join.column;

import com.mybatis.plus.join.ColumnData;

public interface Column {
    String selectColumn();

    void fillData(ColumnData join);

    void setAsName(String asName);
}
