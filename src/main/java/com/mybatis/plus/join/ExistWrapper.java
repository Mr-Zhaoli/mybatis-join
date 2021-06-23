package com.mybatis.plus.join;


import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.conditions.SharedString;
import com.baomidou.mybatisplus.core.conditions.interfaces.Compare;
import com.baomidou.mybatisplus.core.conditions.interfaces.Func;
import com.baomidou.mybatisplus.core.conditions.interfaces.Nested;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.enums.SqlLike;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.mybatis.plus.join.column.Column;
import com.mybatis.plus.join.column.ConstColumn;
import com.mybatis.plus.join.column.TableColumn;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.baomidou.mybatisplus.core.enums.SqlKeyword.*;
import static com.baomidou.mybatisplus.core.enums.WrapperKeyword.*;
import static java.util.stream.Collectors.joining;

/**
 * TODO
 *
 * @author by zhaojin
 * @since 2021/6/22 16:17
 */
public class ExistWrapper<T, K> extends AbstractLambdaWrapper<T, ExistWrapper<T, K>> {

    private final List<Join<?, ?>> joinList = new ArrayList<>();
    protected List<Condition> conditions = new ArrayList<>();
    private String tableName;


    public ExistWrapper(T entity, Class<T> cl, AtomicInteger paramNameSeq,
                        Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments,
                        SharedString lastSql, SharedString sqlComment) {
        super.setEntity(entity);
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
        this.lastSql = lastSql;
        this.sqlComment = sqlComment;
        this.entityClass = cl;
        tableName = TableInfoHelper.getTableInfo(entityClass).getTableName();
    }


    public String getJoins() {
        if (CollectionUtils.isEmpty(joinList)) {
            return " ";
        }
        StringBuilder s = new StringBuilder(" ");
        for (Join<?, ?> join : joinList) {
            s.append(" ").append(join.getSql());
        }
        return s.toString();
    }

    @Override
    protected String columnToString(SFunction<T, ?> column) {
        return SFuncUtils.getColumnNameWithTable(column);
    }

    @Override
    public ExistWrapper<T, K> eq(boolean condition, SFunction<T, ?> column, Object val) {
        return super.eq(condition, column, val);
    }

    @SuppressWarnings("unchecked")
    public <M> ExistWrapper<M, ?> query(Class<M> s) {
        this.entityClass = (Class<T>) s;
        tableName = TableInfoHelper.getTableInfo(entityClass).getTableName();
        return (ExistWrapper<M, ?>) this;
    }


    @Override
    public String getSqlSegment() {
        String sqlSegment = expression.getSqlSegment();
        String on = conditions.stream().map(Object::toString).collect(Collectors.joining(" AND "));
        String prefix = "EXISTS(SELECT 1 FROM " + tableName + getJoins() + " WHERE " + (StringUtils.isEmpty(on.trim()) ? "" : on + " AND ");
        String suffix = ")";
        if (StringUtils.isNotEmpty(sqlSegment)) {
            return prefix + sqlSegment + lastSql.getStringValue() + suffix;
        }
        if (StringUtils.isNotEmpty(lastSql.getStringValue())) {
            return prefix + lastSql.getStringValue() + suffix;
        }
        return null;
    }

//    public ExistWrapper<T, K> on(SFunction<T, ?> col1, SFunction<K, ?> col2, ConditionEnum sqlKeyword) {
//        TableColumn<T> tableColumn1 = new TableColumn<>(col1);
//        TableColumn<K> tableColumn2 = new TableColumn<>(col2);
//        ColumnCondition condition = new ColumnCondition(sqlKeyword, tableColumn1, tableColumn2);
//        condition.setSqlKeyword(sqlKeyword);
//        conditions.add(condition);
//        return this;
//    }

    public <M> ExistWrapper<T, K> on(SFunction<T, ?> col1, SFunction<M, ?> col2, ConditionEnum sqlKeyword) {
        TableColumn<T> tableColumn1 = new TableColumn<>(col1);
        TableColumn<?> tableColumn2 = new TableColumn<>(col2);
        ColumnCondition condition = new ColumnCondition(sqlKeyword, tableColumn1, tableColumn2);
        condition.setSqlKeyword(sqlKeyword);
        conditions.add(condition);
        return this;
    }

    @Override
    protected ExistWrapper<T, K> instance() {
        return null;
    }


    public <M> Join<T, M> innerJoin(Class<M> targetTable) {
        Join<T, M> innerJoin = this.new Join<>("INNER", targetTable, joinList.size());
        joinList.add(innerJoin);
        return innerJoin;
    }

    public <M> Join<T, M> leftJoin(Class<M> targetTable) {

        Join<T, M> innerJoin = this.new Join<>("LEFT", targetTable, joinList.size());
        joinList.add(innerJoin);
        return innerJoin;
    }

    public <M> Join<T, M> rightJoin(Class<M> targetTable) {
        Join<T, M> innerJoin = this.new Join<>("RIGHT", targetTable, joinList.size());
        joinList.add(innerJoin);
        return innerJoin;
    }

    private void addConditionWithTable(boolean condition, SFunction<?, ?> column, SqlKeyword sqlKeyword, Object val) {
        doIt(condition, () -> SFuncUtils.getColumnNameWithTable(column), sqlKeyword, () -> formatSql("{0}", val));
    }

    protected ISqlSegment inExpression(Collection<?> value) {
        return () -> value.stream().map(i -> formatSql("{0}", i))
                .collect(joining(StringPool.COMMA, StringPool.LEFT_BRACKET, StringPool.RIGHT_BRACKET));
    }


    @SuppressWarnings("unchecked")
    public class Join<SourceTable, TargetTable> implements Compare<Join<SourceTable, TargetTable>, SFunction<TargetTable, ?>>,

            Nested<Join<SourceTable, TargetTable>, Join<SourceTable, TargetTable>>,
            com.baomidou.mybatisplus.core.conditions.interfaces.Join<Join<SourceTable, TargetTable>>,
            Func<Join<SourceTable, TargetTable>, SFunction<TargetTable, ?>>,
            ISqlSegment {


        private final String joinType;
        private final int index;
        protected List<Condition> conditions = new ArrayList<>();
        protected String tableName;
        protected Class<TargetTable> t;

        public Join(String joinType, Class<TargetTable> t, int index) {
            this.joinType = joinType;
            this.index = index;
            this.t = t;
            tableName = JoinWrapper.getTableName(t);
        }


        public <M> Join<T, M> innerJoin(Class<M> targetTable) {
            return innerJoin(targetTable);
        }

        public <M> Join<T, M> leftJoin(Class<M> targetTable) {
            return leftJoin(targetTable);
        }

        public <M> Join<T, M> rightJoin(Class<M> targetTable) {
            return rightJoin(targetTable);
        }

        @Override
        public String getSqlSegment() {
            String sqlSegment = expression.getSqlSegment();
            if (StringUtils.isNotEmpty(sqlSegment)) {
                return sqlSegment + lastSql.getStringValue();
            }
            if (StringUtils.isNotEmpty(lastSql.getStringValue())) {
                return lastSql.getStringValue();
            }
            return null;
        }

        @Override
        public <V> Join<SourceTable, TargetTable> allEq(boolean condition, Map<SFunction<TargetTable, ?>, V> params, boolean null2IsNull) {
            return this;
        }

        @Override
        public <V> Join<SourceTable, TargetTable> allEq(boolean condition, BiPredicate<SFunction<TargetTable, ?>, V> filter, Map<SFunction<TargetTable, ?>, V> params, boolean null2IsNull) {
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> eq(boolean condition, SFunction<TargetTable, ?> column, Object val) {
            addConditionWithTable(condition, column, EQ, val);
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> ne(boolean condition, SFunction<TargetTable, ?> column, Object val) {
            addConditionWithTable(condition, column, NE, val);
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> gt(boolean condition, SFunction<TargetTable, ?> column, Object val) {
            addConditionWithTable(condition, column, NE, val);
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> ge(boolean condition, SFunction<TargetTable, ?> column, Object val) {
            addConditionWithTable(condition, column, NE, val);
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> lt(boolean condition, SFunction<TargetTable, ?> column, Object val) {
            addConditionWithTable(condition, column, NE, val);
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> le(boolean condition, SFunction<TargetTable, ?> column, Object val) {
            addConditionWithTable(condition, column, NE, val);
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> between(boolean condition, SFunction<TargetTable, ?> column, Object val1, Object val2) {
            doIt(condition, () -> SFuncUtils.getColumnNameWithTable(column), BETWEEN, () -> formatSql("{0}", val1), AND,
                    () -> formatSql("{0}", val2));
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> notBetween(boolean condition, SFunction<TargetTable, ?> column, Object val1, Object val2) {
            not(condition);
            between(condition, column, val1, val2);
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> like(boolean condition, SFunction<TargetTable, ?> column, Object val) {
            likeValue(condition, (SFunction<T, ?>) column, val, SqlLike.DEFAULT);
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> notLike(boolean condition, SFunction<TargetTable, ?> column, Object val) {
            not(condition);
            like(condition, column, val);
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> likeLeft(boolean condition, SFunction<TargetTable, ?> column, Object val) {
            likeValue(condition, (SFunction<T, ?>) column, val, SqlLike.LEFT);
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> likeRight(boolean condition, SFunction<TargetTable, ?> column, Object val) {
            likeValue(condition, (SFunction<T, ?>) column, val, SqlLike.RIGHT);
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> isNull(boolean condition, SFunction<TargetTable, ?> column) {
            doIt(condition, () -> SFuncUtils.getColumnNameWithTable(column), IS_NULL);
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> isNotNull(boolean condition, SFunction<TargetTable, ?> column) {
            doIt(condition, () -> SFuncUtils.getColumnNameWithTable(column), IS_NOT_NULL);
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> in(boolean condition, SFunction<TargetTable, ?> column, Collection<?> coll) {
            doIt(condition, () -> SFuncUtils.getColumnNameWithTable(column), IN, inExpression(coll));
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> notIn(boolean condition, SFunction<TargetTable, ?> column, Collection<?> coll) {
            not(condition).in(condition, (SFunction<T, ?>) column, coll);
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> inSql(boolean condition, SFunction<TargetTable, ?> column, String inValue) {
            doIt(condition, () -> SFuncUtils.getColumnNameWithTable(column), IN, () -> String.format("(%s)", inValue));
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> notInSql(boolean condition, SFunction<TargetTable, ?> column, String inValue) {
            not(condition).inSql(condition, (SFunction<T, ?>) column, inValue);
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> groupBy(SFunction<TargetTable, ?> column) {
            SFunction<TargetTable, ?>[] columns = new SFunction[]{column};
            return groupBy(true, columns);
        }

        @SafeVarargs
        @Override
        public final Join<SourceTable, TargetTable> groupBy(boolean condition, SFunction<TargetTable, ?>... columns) {
            if (ArrayUtils.isEmpty(columns)) {
                return this;
            }
            doIt(condition, GROUP_BY,
                    () -> columns.length == 1
                            ? SFuncUtils.getColumnNameWithTable(columns[0])
                            : Arrays.stream(columns)
                            .map(SFuncUtils::getColumnNameWithTable)
                            .collect(joining(",")));
            return this;
        }

        @SafeVarargs
        @Override
        public final Join<SourceTable, TargetTable> orderBy(boolean condition, boolean isAsc, SFunction<TargetTable, ?>... columns) {
            if (ArrayUtils.isEmpty(columns)) {
                return this;
            }
            SqlKeyword mode = isAsc ? ASC : DESC;
            for (SFunction<TargetTable, ?> column : columns) {
                doIt(condition, ORDER_BY, () -> SFuncUtils.getColumnNameWithTable(column), mode);
            }
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> having(boolean condition, String sqlHaving, Object... params) {
            doIt(condition, HAVING, () -> formatSqlIfNeed(condition, sqlHaving, params));
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> or(boolean condition) {
            doIt(condition, OR);
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> apply(boolean condition, String applySql, Object... value) {
            doIt(condition, APPLY, () -> formatSql(applySql, value));
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> last(boolean condition, String lastSql) {
            if (condition) {
                ExistWrapper.this.lastSql.setStringValue(StringPool.SPACE + lastSql);
            }
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> comment(boolean condition, String comment) {
            if (condition) {
                ExistWrapper.this.sqlComment.setStringValue(comment);
            }
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> exists(boolean condition, String existsSql) {
            doIt(condition, EXISTS, () -> String.format("(%s)", existsSql));
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> notExists(boolean condition, String notExistsSql) {
            not(condition).exists(condition, notExistsSql);
            return this;
        }

        protected Join<SourceTable, TargetTable> addNestedConditionJoin(boolean condition, Consumer<Join<SourceTable, TargetTable>> consumer) {
            final ExistWrapper<T, K> instance = instance();
            Join<SourceTable, TargetTable> join = instance.new Join<>(joinType, (Class<TargetTable>) t, index);
            consumer.accept(join);
            doIt(condition, LEFT_BRACKET, join, RIGHT_BRACKET);
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> and(boolean condition, Consumer<Join<SourceTable, TargetTable>> consumer) {
            ExistWrapper.this.and(condition);
            return addNestedConditionJoin(condition, consumer);
        }

        @Override
        public Join<SourceTable, TargetTable> or(boolean condition, Consumer<Join<SourceTable, TargetTable>> consumer) {
            ExistWrapper.this.or(condition);
            return addNestedConditionJoin(condition, consumer);
        }

        @Override
        public Join<SourceTable, TargetTable> nested(boolean condition, Consumer<Join<SourceTable, TargetTable>> consumer) {
            return addNestedConditionJoin(condition, consumer);
        }


        public Join<SourceTable, TargetTable> on(SFunction<SourceTable, ?> col1, SFunction<TargetTable, ?> col2, ConditionEnum sqlKeyword) {
            TableColumn<SourceTable> tableColumn1 = new TableColumn<>(col1);
            TableColumn<TargetTable> tableColumn2 = new TableColumn<>(col2);
            ColumnCondition condition = new ColumnCondition(sqlKeyword, tableColumn1, tableColumn2);
            condition.setSqlKeyword(sqlKeyword);
            conditions.add(condition);
            return this;
        }

        public Join<SourceTable, TargetTable> on(SFunction<SourceTable, ?> col1, Column col2, ConditionEnum sqlKeyword) {
            ColumnCondition condition = new ColumnCondition(sqlKeyword, new TableColumn<>(col1), col2);
            condition.setSqlKeyword(sqlKeyword);
            conditions.add(condition);
            return this;
        }

        public Join<SourceTable, TargetTable> on(SFunction<SourceTable, ?> col1, Serializable value, ConditionEnum sqlKeyword) {
            ValueCondition condition = new ValueCondition(sqlKeyword,
                    new ConstColumn(SFuncUtils.getColumnNameWithTable(col1)),
                    value, conditions.size(), index);
            condition.setSqlKeyword(sqlKeyword);
            conditions.add(condition);
            return this;
        }


        public Join<SourceTable, TargetTable> on(SFunction<TargetTable, ?> col1, ConditionEnum sqlKeyword, Serializable value) {
            ValueCondition condition = new ValueCondition(sqlKeyword, new TableColumn<>(col1), value, conditions.size(), index);
            condition.setSqlKeyword(sqlKeyword);
            conditions.add(condition);
            return this;
        }

        @Override
        public String toString() {
            return getSql();
        }

        public String getSql() {
            String s = conditions.stream().map(Object::toString)
                    .collect(Collectors.joining(" " + SqlKeyword.AND.getSqlSegment() + " "));
            return joinType + " JOIN " + tableName + " ON " + s;
        }

        @SuppressWarnings("unchecked")
        public <M, N> ExistWrapper<M, N> wrapper() {
            return (ExistWrapper<M, N>) ExistWrapper.this;
        }
    }
}
