package com.mybatis.plus.join.column;

import com.mybatis.plus.join.ColumnData;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class JSONColumn implements Column {
    /**
     * k1,v1,k2,v2,k3,v3...
     */
    private Column[] columns;
    private String asName;

    public JSONColumn(Column... columns) {
        this.columns = columns;
    }

    public JSONColumn(String asName, Column... columns) {
        this.columns = columns;
        this.asName = asName;
    }

    @Override
    public String selectColumn() {
        StringBuilder builder = new StringBuilder();
        for (Column column : columns) {
            builder.append(column.selectColumn()).append(",");
        }
        String s = "JSON_OBJECT(" + StringUtils.trimTrailingCharacter(builder.toString(), ',') + ")";
        if (asName == null || "".equals(asName)) {
            return s;
        }
        return s + " as " + asName;
    }

    @Override
    public void fillData(ColumnData columnData) {
        for (Column o : columns) {
            o.fillData(columnData);
        }
    }
}