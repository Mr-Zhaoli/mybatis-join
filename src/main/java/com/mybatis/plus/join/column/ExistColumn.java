package com.mybatis.plus.join.column;

import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.mybatis.plus.join.ColumnData;
import lombok.Data;

/**
 * TODO
 *
 * @author by zhaojin
 * @since 2021/6/22 18:15
 */
@Data
public class ExistColumn implements Column {
    private SubQueryColumn subQuery;
    private String asName;

    public ExistColumn(SubQueryColumn subQuery) {
        this.subQuery = subQuery;
    }

    @Override
    public String selectColumn() {
        String s = SqlKeyword.EXISTS.getSqlSegment() + subQuery.selectColumn();
        if (asName == null || "".equals(asName)) {
            return s;
        }
        return s + " AS " + asName;
    }

    @Override
    public void fillData(ColumnData join) {
        subQuery.fillData(join);
    }
}
