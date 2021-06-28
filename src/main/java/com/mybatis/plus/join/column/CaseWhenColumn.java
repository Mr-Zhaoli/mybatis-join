package com.mybatis.plus.join.column;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.mybatis.plus.join.ColumnData;
import com.mybatis.plus.join.ConditionEnum;
import lombok.Data;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
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
    private List<Condition> conditions = new ArrayList<>();
    private Column elseColumn;
    private Column conditionColumn;

    @Override
    public String selectColumn() {
        String whens = conditions.stream().map(o -> "WHEN " +
                o.col.selectColumn() + " " + o.condition.getSqlSegment() + " " + o.value.selectColumn() +
                " THEN " + o.then.selectColumn())
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
        return s + ") AS " + asName;
    }


    public <T> CaseWhenColumn when(Object value, Object then) {
        Assert.notNull(conditionColumn,"conditionColumn不能为空");
        conditions.add(new CaseWhenColumn.Condition(conditionColumn, ConditionEnum.EQ, new ConstColumn(value), new ConstColumn(then)));
        return this;
    }


    public <T> CaseWhenColumn when(SFunction<T, ?> col, ConditionEnum condition, Object value, Object then) {
        conditions.add(new CaseWhenColumn.Condition(new TableColumn<>(col), condition, new ConstColumn(value), new ConstColumn(then)));
        return this;
    }

    public CaseWhenColumn when(Column column, ConditionEnum condition, Object value, Object then) {
        conditions.add(new CaseWhenColumn.Condition(column, condition, new ConstColumn(value), new ConstColumn(then)));
        return this;
    }

    public CaseWhenColumn el(Column elseColumn) {
        this.elseColumn = elseColumn;
        return this;
    }

    public CaseWhenColumn el(Serializable elseColumn) {
        this.elseColumn = new ConstColumn(elseColumn);
        return this;
    }

    public <T> CaseWhenColumn el(SFunction<T,?> col) {
        this.elseColumn = new TableColumn<>(col);
        return this;
    }

    @Override
    public void fillData(ColumnData join) {
        for (Condition condition : conditions) {
            condition.col.fillData(join);
            condition.value.fillData(join);
            condition.then.fillData(join);
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
        private Column col;
        private ConditionEnum condition;
        private Column value;
        private Column then;

        public Condition(Column col, ConditionEnum condition, Column value, Column then) {
            this.col = col;
            this.value = value;
            this.condition = condition;
            this.then = then;
        }

        public Condition(ConditionEnum condition, Column value, Column then) {
            this.value = value;
            this.condition = condition;
            this.then = then;
        }
    }

}
