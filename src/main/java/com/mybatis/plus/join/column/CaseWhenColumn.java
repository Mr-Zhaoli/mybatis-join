package com.mybatis.plus.join.column;

import com.mybatis.plus.join.ColumnData;
import com.mybatis.plus.join.ConditionEnum;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * case when
 *
 * @author by zhaojin
 * @since 2021/6/25 10:22
 */
@Data
public class CaseWhenColumn implements Column {
    private String asName;
    private List<Condition> conditions;
    private Column elseColumn;

    @Override
    public String selectColumn() {
        String whens = conditions.stream().map(o -> "WHEN " +
                o.col1.selectColumn() + " " + o.condition.getSqlSegment() + " " + o.col2.selectColumn() +
                " THEN " + o.value.selectColumn())
                .collect(Collectors.joining(" "));
        String s = "(CASE "
                + whens;
        if (elseColumn != null) {
            s += " ELSE " + elseColumn.selectColumn();
        }
        s += " END";
        if (asName == null || "".equals(asName)) {
            return s;
        }
        return s + ") as " + asName;
    }

    @Override
    public void fillData(ColumnData join) {
        for (Condition condition : conditions) {
            condition.col1.fillData(join);
            condition.col2.fillData(join);
            condition.value.fillData(join);
        }
        if (elseColumn != null) {
            elseColumn.fillData(join);
        }
    }

    @Override
    public void setAsName(String asName) {
        this.asName = asName;
    }

    @Data
    public static class Condition {
        private Column col1;
        private Column col2;
        private ConditionEnum condition;
        private Column value;

        public Condition(Column col1, ConditionEnum condition, Column col2, Column value) {
            this.col1 = col1;
            this.col2 = col2;
            this.condition = condition;
            this.value = value;
        }

        public Condition(ConditionEnum condition, Column col2, Column value) {
            this.col2 = col2;
            this.condition = condition;
            this.value = value;
        }
    }

}
