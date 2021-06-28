package com.mybatis.plus.join.column;

import com.mybatis.plus.join.ColumnData;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class JSONColumn implements Column {
    /**
     * k1,v1,k2,v2,k3,v3...
     */
    private Entry[] columns;
    private String asName;

    public JSONColumn(Entry... columns) {
        this.columns = columns;
    }

    public JSONColumn(String asName, Entry... columns) {
        this.columns = columns;
        this.asName = asName;
    }

    @Override
    public String selectColumn() {
        StringBuilder builder = new StringBuilder();
        for (Entry column : columns) {
            builder.append(column.key.selectColumn()).append(",").append(column.value.selectColumn()).append(",");
        }
        String s = "JSON_OBJECT(" + StringUtils.trimTrailingCharacter(builder.toString(), ',') + ")";
        if (asName == null || "".equals(asName)) {
            return s;
        }
        return s + " AS " + asName;
    }

    @Override
    public void fillData(ColumnData columnData) {
        for (Entry o : columns) {
            o.value.fillData(columnData);
            o.key.fillData(columnData);
        }
    }


    @Data
    public static class Entry {
        private ConstColumn key;
        private Column value;

        public Entry(ConstColumn key, Column value) {
            this.key = key;
            this.value = value;
        }
    }

}