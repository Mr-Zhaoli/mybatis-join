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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mybatis.plus.join.column.Column;
import com.mybatis.plus.join.column.ConstColumn;
import com.mybatis.plus.join.column.SubQueryColumn;
import com.mybatis.plus.join.column.TableColumn;
import com.mybatis.plus.mapper.ParentMapper;
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

public class JoinWrapper<T> extends AbstractLambdaWrapper<T, JoinWrapper<T>> {

    protected final List<Join<?, ?>> joinList = new ArrayList<>();
    protected final Map<String, List<Column>> selectList = new HashMap<>();
    protected String tableName;
    protected ParentMapper<T> baseMapper;

    JoinWrapper(T entity, Class<T> cl, AtomicInteger paramNameSeq,
                Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments,
                SharedString lastSql, SharedString sqlComment) {
        super.setEntity(entity);
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
        this.lastSql = lastSql;
        this.sqlComment = sqlComment;
        this.entityClass = cl;
        tableName = TableInfoHelper.getTableInfo(cl).getTableName();
    }

    public JoinWrapper(Class<T> cl) {
        super.initNeed();
        super.initEntityClass();
        this.entityClass = cl;
        tableName = TableInfoHelper.getTableInfo(cl).getTableName();
    }

    public static String getTableName(Class<?> cl) {
        return TableInfoHelper.getTableInfo(cl).getTableName();
    }

    @Override
    public Map<String, Object> getParamNameValuePairs() {
        return paramNameValuePairs;
    }

    public AtomicInteger getParamNameSeq() {
        return paramNameSeq;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public void setBaseMapper(ParentMapper<T> baseMapper) {
        this.baseMapper = baseMapper;
    }

    // ******************以下方法用于执行sql**********************
    public T first() {
        List<T> list = page(new Page<>(1, 1)).getRecords();
        return list.isEmpty() ? null : list.get(0);
    }

    public T one() {
        return baseMapper.selectOneJoin(this);
    }

    @SuppressWarnings("unchecked")
    public <K> K obj(Class<K> cl) {
        return (K) baseMapper.selectObjJoin(this);
    }

    public Integer count() {
        return baseMapper.selectCountJoin(this);
    }

    public List<T> list() {
        return baseMapper.selectListJoin(this);
    }

    @SuppressWarnings("unchecked")
    public <K> List<K> listObjs(Class<K> cl) {
        return (List<K>) baseMapper.selectObjsJoin(this);
    }

    public IPage<T> page(IPage<T> page) {
        return baseMapper.selectPageJoin(page, this);
    }

    // ******************以上方法用于执行sql**********************

    @Override
    public String getSqlSelect() {
        if (selectList.isEmpty()) {
            return "*";
        }
        return selectList.values().stream().flatMap(Collection::stream)
                .map(Column::selectColumn)
                .collect(Collectors.joining(","));
    }

    protected ISqlSegment inExpression(Collection<?> value) {
        return () -> value.stream().map(i -> formatSql("{0}", i))
                .collect(joining(StringPool.COMMA, StringPool.LEFT_BRACKET, StringPool.RIGHT_BRACKET));
    }

    private void addConditionWithTable(boolean condition, SFunction<?, ?> column, SqlKeyword sqlKeyword, Object val) {
        doIt(condition, () -> SFuncUtils.getColumnNameWithTable(column), sqlKeyword, () -> formatSql("{0}", val));
    }

    @Override
    protected JoinWrapper<T> instance() {
        JoinWrapper<T> wrapper = new JoinWrapper<>(entity, entityClass, paramNameSeq, paramNameValuePairs, new MergeSegments(),
                SharedString.emptyString(), SharedString.emptyString());
        wrapper.setBaseMapper(baseMapper);
        return wrapper;
    }

    @Override
    public String columnToString(SFunction<T, ?> column) {
        return SFuncUtils.getColumnNameWithTable(column);
    }

    public final String fmtSql(Object param) {
        return formatSqlIfNeed(true, "{0}", param);
    }


    // ************************以下方法用于判断是否含有join操作,以及拼接join的相关sql*************************
    public List<Join<?, ?>> getJoinList() {
        return joinList;
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
    // *************************以上方法用于判断是否含有join操作,以及拼接join的相关sql**************************


    // ***************************以下是补充的拼接sql方法**************************************


    public JoinWrapper<T> notIn(boolean condition, SFunction<T, ?> col, SubQueryColumn column) {
        return not(condition).in(true, col, column);
    }

    public JoinWrapper<T> notIn(SFunction<T, ?> col, SubQueryColumn column) {
        return notIn(true, col, column);
    }

    public JoinWrapper<T> in(boolean condition, SFunction<T, ?> col, SubQueryColumn column) {
        TableColumn<T> tableColumn = new TableColumn<>(col);
        ColumnData join = new ColumnData(this);
        column.fillData(join);
        tableColumn.fillData(join);
        return doIt(condition, tableColumn::selectColumn, IN, column::selectColumn);
    }

    public JoinWrapper<T> in(SFunction<T, ?> col, SubQueryColumn column) {
        return in(true, col, column);
    }

    public JoinWrapper<T> where(Column col1, ConditionEnum conditionEnum, SFunction<T, ?> col2) {
        return where(true, col1, conditionEnum, new TableColumn<>(col2));
    }

    public JoinWrapper<T> where(Column col1, ConditionEnum conditionEnum, Object col2) {
        return where(true, col1, conditionEnum, new ConstColumn(col2));
    }

    public JoinWrapper<T> where(SFunction<T, ?> col1, ConditionEnum conditionEnum, Column col2) {
        return where(true, new TableColumn<>(col1), conditionEnum, col2);
    }

    public <K> JoinWrapper<T> where(SFunction<T, ?> col1, ConditionEnum conditionEnum, SFunction<K, ?> col2) {
        return where(true, new TableColumn<>(col1), conditionEnum, new TableColumn<>(col2));
    }

    public JoinWrapper<T> where(Column col1, ConditionEnum conditionEnum, Column col2) {
        return where(true, col1, conditionEnum, col2);
    }

    public JoinWrapper<T> where(boolean condition, Column col1, ConditionEnum conditionEnum, Column col2) {
        ColumnData join = new ColumnData(this);
        col1.fillData(join);
        col2.fillData(join);
        return doIt(condition, col1::selectColumn, conditionEnum::getSqlSegment, col2::selectColumn);
    }

    public <K> JoinWrapper<T> exists(Consumer<SubQueryWrapper<T>> consumer) {
        final SubQueryWrapper<T> instance = new SubQueryWrapper<>(entity, entityClass, paramNameSeq,
                paramNameValuePairs, new MergeSegments(),
                SharedString.emptyString(), SharedString.emptyString());
        consumer.accept(instance);
        doIt(true, EXISTS, LEFT_BRACKET, instance, RIGHT_BRACKET);
        return this;
    }

    public JoinWrapper<T> select(Column column, String asName) {
        column.fillData(new ColumnData(this));
        column.setAsName(asName);
        List<Column> columns = selectList.get(tableName);
        if (columns == null) {
            columns = new ArrayList<>();
        }
        columns.add(column);
        selectList.put(tableName, columns);
        return this;
    }


    public JoinWrapper<T> select(SFunction<T, ?> column, String asName) {
        TableColumn<T> tableColumn = new TableColumn<>(column);
        return select(tableColumn, asName);
    }

    @SuppressWarnings("unchecked")
    public final JoinWrapper<T> select(SFunction<T, ?> column) {
        return select(new SFunction[]{column});
    }

    @SafeVarargs
    public final JoinWrapper<T> select(SFunction<T, ?>... columns) {
        List<Column> newList = Arrays.stream(columns)
                .map(TableColumn::new).collect(Collectors.toList());
        List<Column> oldList = selectList.get(tableName);
        if (oldList == null) {
            oldList = newList;
        } else {
            oldList.addAll(newList);
        }
        selectList.put(tableName, oldList);
        return this;
    }

    public <K> Join<T, K> innerJoin(Class<K> targetTable) {
        Join<T, K> innerJoin = this.new Join<>("INNER", targetTable, joinList.size());
        joinList.add(innerJoin);
        return innerJoin;
    }

    public <K> Join<T, K> leftJoin(Class<K> targetTable) {
        Join<T, K> innerJoin = this.new Join<>("LEFT", targetTable, joinList.size());
        joinList.add(innerJoin);
        return innerJoin;
    }

    public <K> Join<T, K> rightJoin(Class<K> targetTable) {
        Join<T, K> innerJoin = this.new Join<>("RIGHT", targetTable, joinList.size());
        joinList.add(innerJoin);
        return innerJoin;
    }

    // ****************************以上是补充的拼接sql方法*************************************


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
            tableName = getTableName(t);
        }

        public T first() {
            List<T> list = list();
            return list.isEmpty() ? null : list.get(0);
        }

        public T one() {
            return baseMapper.selectOneJoin(JoinWrapper.this);
        }

        @SuppressWarnings("unchecked")
        public <K> K obj(Class<K> cl) {
            return (K) baseMapper.selectObjJoin(JoinWrapper.this);
        }

        public Integer count() {
            return baseMapper.selectCountJoin(JoinWrapper.this);
        }

        public List<T> list() {
            return baseMapper.selectListJoin(JoinWrapper.this);
        }

        @SuppressWarnings("unchecked")
        public <K> List<K> listObjs(Class<K> cl) {
            return (List<K>) baseMapper.selectObjsJoin(JoinWrapper.this);
        }

        public IPage<T> page(IPage<T> page) {
            return baseMapper.selectPageJoin(page, JoinWrapper.this);
        }

        public <K> Join<T, K> innerJoin(Class<K> targetTable) {
            return JoinWrapper.this.innerJoin(targetTable);
        }

        public <K> Join<T, K> leftJoin(Class<K> targetTable) {
            return JoinWrapper.this.leftJoin(targetTable);
        }

        public <K> Join<T, K> rightJoin(Class<K> targetTable) {
            return JoinWrapper.this.rightJoin(targetTable);
        }


        public Join<SourceTable, TargetTable> where(Column col1, ConditionEnum conditionEnum, SFunction<TargetTable, ?> col2) {
            return where(true, col1, conditionEnum, new TableColumn<>(col2));
        }

        public Join<SourceTable, TargetTable> where(Column col1, ConditionEnum conditionEnum, Object col2) {
            return where(true, col1, conditionEnum, new ConstColumn(col2));
        }

        public Join<SourceTable, TargetTable> where(SFunction<TargetTable, ?> col1, ConditionEnum conditionEnum, Column col2) {
            return where(true, new TableColumn<>(col1), conditionEnum, col2);
        }

        public Join<SourceTable, TargetTable> where(Column col1, ConditionEnum conditionEnum, Column col2) {
            return where(true, col1, conditionEnum, col2);
        }

        public Join<SourceTable, TargetTable> where(boolean condition, Column col1, ConditionEnum conditionEnum, Column col2) {
            ColumnData join = new ColumnData(JoinWrapper.this);
            col1.fillData(join);
            col2.fillData(join);
            doIt(condition, col1::selectColumn, conditionEnum::getSqlSegment, col2::selectColumn);
            return this;
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
                JoinWrapper.this.lastSql.setStringValue(StringPool.SPACE + lastSql);
            }
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> comment(boolean condition, String comment) {
            if (condition) {
                JoinWrapper.this.sqlComment.setStringValue(comment);
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
            final JoinWrapper<T> instance = instance();
            Join<SourceTable, TargetTable> join = instance.new Join<>(joinType, (Class<TargetTable>) t, index);
            consumer.accept(join);
            doIt(condition, LEFT_BRACKET, join, RIGHT_BRACKET);
            return this;
        }

        @Override
        public Join<SourceTable, TargetTable> and(boolean condition, Consumer<Join<SourceTable, TargetTable>> consumer) {
            JoinWrapper.this.and(condition);
            return addNestedConditionJoin(condition, consumer);
        }

        @Override
        public Join<SourceTable, TargetTable> or(boolean condition, Consumer<Join<SourceTable, TargetTable>> consumer) {
            JoinWrapper.this.or(condition);
            return addNestedConditionJoin(condition, consumer);
        }

        @Override
        public Join<SourceTable, TargetTable> nested(boolean condition, Consumer<Join<SourceTable, TargetTable>> consumer) {
            return addNestedConditionJoin(condition, consumer);
        }


        public Join<SourceTable, TargetTable> select(Column column, String asName) {
            column.fillData(new ColumnData(JoinWrapper.this));
            List<Column> columns = selectList.get(tableName);
            column.setAsName(asName);
            if (columns == null) {
                columns = new ArrayList<>();
            }
            columns.add(column);
            selectList.put(tableName, columns);
            return this;
        }

        public Join<SourceTable, TargetTable> select(SFunction<TargetTable, ?> column, String asName) {
            TableColumn<TargetTable> tableColumn = new TableColumn<>(column);
            return select(tableColumn, asName);
        }


        public final Join<SourceTable, TargetTable> select(SFunction<TargetTable, ?> column) {
            return select(new SFunction[]{column});
        }

        @SafeVarargs
        public final Join<SourceTable, TargetTable> select(SFunction<TargetTable, ?>... columns) {
            List<Column> newList = Arrays.stream(columns).map(TableColumn::new).collect(Collectors.toList());
            List<Column> oldList = selectList.get(tableName);
            if (oldList == null) {
                oldList = newList;
            } else {
                oldList.addAll(newList);
            }
            selectList.put(tableName, oldList);
            return this;
        }


        public Join<SourceTable, TargetTable> on(SFunction<SourceTable, ?> col1, SFunction<TargetTable, ?> col2, ConditionEnum sqlKeyword) {
            TableColumn<SourceTable> tableColumn1 = new TableColumn<>(col1);
            TableColumn<TargetTable> tableColumn2 = new TableColumn<>(col2);
            ColumnCondition condition = new ColumnCondition(sqlKeyword, tableColumn1, tableColumn2);
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
        public <K> JoinWrapper<K> wrapper() {
            return (JoinWrapper<K>) JoinWrapper.this;
        }
    }

}