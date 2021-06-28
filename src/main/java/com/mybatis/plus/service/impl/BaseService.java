package com.mybatis.plus.service.impl;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mybatis.plus.join.ConditionEnum;
import com.mybatis.plus.join.SubQueryWrapper;
import com.mybatis.plus.join.column.*;
import com.mybatis.plus.mapper.ParentMapper;
import com.mybatis.plus.service.IBaseService;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Arrays;
import java.util.function.Consumer;

public class BaseService<M extends ParentMapper<T>, T> extends ServiceImpl<M, T> implements IBaseService<T> {


    public  static<T> CaseWhenColumn CASE(Column column) {
        CaseWhenColumn caseWhenColumn = new CaseWhenColumn();
        caseWhenColumn.setConditionColumn(column);
        return caseWhenColumn;
    }

    public  static<T> CaseWhenColumn CASE(SFunction<T,?> column) {
        CaseWhenColumn caseWhenColumn = new CaseWhenColumn();
        caseWhenColumn.setConditionColumn(new TableColumn<>(column));
        return caseWhenColumn;
    }


    public static CaseWhenColumn CASE() {
        return new CaseWhenColumn();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> SubQueryColumn QUERY(Consumer<SubQueryWrapper<T>> consumer) {
        SubQueryColumn subQueryColumn = new SubQueryColumn();
        subQueryColumn.setConsumer((Consumer) consumer);
        return subQueryColumn;
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> ExistColumn EXISTS(Consumer<SubQueryWrapper<T>> consumer) {
        SubQueryColumn subQueryColumn = new SubQueryColumn();
        subQueryColumn.setConsumer((Consumer) consumer);
        return new ExistColumn(subQueryColumn);
    }

    /**
     * 构建key value 键值对
     */
    public static JSONColumn.Entry KV(String key, Column value) {
        return new JSONColumn.Entry(new ConstColumn(key), value);
    }

    /**
     * 构建key value 键值对
     */
    public static <T> JSONColumn.Entry KV(String key, SFunction<T, ?> value) {
        return new JSONColumn.Entry(new ConstColumn(key), new TableColumn<>(value));
    }

    public static JSONColumn JSON(JSONColumn.Entry... column) {
        return new JSONColumn(column);
    }

    /**
     * 转化为常量列
     */
    public static <T> Column COL(Serializable column) {
        return new ConstColumn(column);
    }

    /**
     * 转化为表的列
     */
    public static <T> Column COL(SFunction<T, ?> column) {
        return new TableColumn<>(column);
    }

    public static <T> SumColumn SUM(SFunction<T, ?> column) {
        return new SumColumn(new TableColumn<>(column));
    }

    public static <T> MinColumn MIN(SFunction<T, ?> column) {
        return new MinColumn(new TableColumn<>(column));
    }

    public static <T> MaxColumn MAX(SFunction<T, ?> column) {
        return new MaxColumn(new TableColumn<>(column));
    }

    public static <T> IfColumn IF(ExistColumn column1, Serializable trueValue, Serializable falseValue) {
        return new IfColumn(column1, null, null, new ConstColumn(trueValue), new ConstColumn(falseValue));
    }

    public static <T> IfColumn IF(SFunction<T, ?> column1, ConditionEnum condition, Serializable column2, SFunction<T, ?> trueValue, SFunction<T, ?> falseValue) {
        return new IfColumn(new TableColumn<>(column1), condition, new ConstColumn(column2), new TableColumn<>(trueValue), new TableColumn<>(falseValue));
    }

    public static <T> IfColumn IF(SFunction<T, ?> column1, ConditionEnum condition, Serializable column2, Serializable trueValue, Serializable falseValue) {
        return new IfColumn(new TableColumn<>(column1), condition, new ConstColumn(column2), new ConstColumn(trueValue), new ConstColumn(falseValue));
    }

    public static <T> IfNullColumn IFNULL(SFunction<T, ?> column1, Column otherValue) {
        return new IfNullColumn(new TableColumn<>(column1), otherValue);
    }

    public static <T> IfNullColumn IFNULL(SFunction<T, ?> column1, Serializable otherValue) {
        return new IfNullColumn(new TableColumn<>(column1), new ConstColumn(otherValue));
    }

    public static <T> MonthColumn MONTH(SFunction<T, ?> column) {
        return new MonthColumn(new TableColumn<>(column));
    }

    public static <T> MonthColumn MONTH(String dateFormat) {
        return new MonthColumn(new ConstColumn(dateFormat));
    }

    public static <T> YearColumn YEAR(SFunction<T, ?> column) {
        return new YearColumn(new TableColumn<>(column));
    }

    public static <T> YearColumn YEAR(String dateFormat) {
        return new YearColumn(new ConstColumn(dateFormat));
    }
}
