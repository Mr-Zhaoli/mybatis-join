package com.mybatis.plus.service.impl;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mybatis.plus.join.ConditionEnum;
import com.mybatis.plus.join.ExistWrapper;
import com.mybatis.plus.join.column.*;
import com.mybatis.plus.mapper.ParentMapper;
import com.mybatis.plus.service.IBaseService;

import java.io.Serializable;
import java.util.function.Consumer;

public class BaseService<M extends ParentMapper<T>, T> extends ServiceImpl<M, T> implements IBaseService<T> {

    public static <T, K> ExistColumn exists(Consumer<ExistWrapper<T, K>> consumer) {
        ExistColumn existColumn = new ExistColumn();
        existColumn.setConsumer((Consumer) consumer);
        return existColumn;
    }

    public static <T> JSONColumn json(Column... column) {
        return new JSONColumn(column);
    }

    public static <T> Column col(Serializable column) {
        return new ConstColumn(column);
    }

    public static <T> Column col(SFunction<T, ?> column) {
        return new TableColumn<>(column);
    }

    public static <T> SumColumn sum(SFunction<T, ?> column) {
        return new SumColumn(new TableColumn<>(column));
    }

    public static <T> MinColumn min(SFunction<T, ?> column) {
        return new MinColumn(new TableColumn<>(column));
    }

    public static <T> MaxColumn max(SFunction<T, ?> column) {
        return new MaxColumn(new TableColumn<>(column));
    }

    public static <T> IfColumn If(Column column1, Serializable trueValue, Serializable falseValue) {
        return new IfColumn(column1, null, null, new ConstColumn(trueValue), new ConstColumn(falseValue));
    }

    public static <T> IfColumn If(SFunction<T, ?> column1, ConditionEnum condition, Serializable column2, SFunction<T, ?> trueValue, SFunction<T, ?> falseValue) {
        return new IfColumn(new TableColumn<>(column1), condition, new ConstColumn(column2), new TableColumn<>(trueValue), new TableColumn<>(falseValue));
    }

    public static <T> IfColumn If(SFunction<T, ?> column1, ConditionEnum condition, Serializable column2, Serializable trueValue, Serializable falseValue) {
        return new IfColumn(new TableColumn<>(column1), condition, new ConstColumn(column2), new ConstColumn(trueValue), new ConstColumn(falseValue));
    }

    public static <T> IfNullColumn ifNull(SFunction<T, ?> column1, Column trueValue, Column falseValue) {
        return new IfNullColumn(new TableColumn<>(column1), trueValue, falseValue);
    }

    public static <T> IfNullColumn ifNull(SFunction<T, ?> column1, Serializable trueValue, Serializable falseValue) {
        return new IfNullColumn(new TableColumn<>(column1), new ConstColumn(trueValue), new ConstColumn(falseValue));
    }

    public static <T> MonthColumn month(SFunction<T, ?> column) {
        return new MonthColumn(new TableColumn<>(column));
    }

    public static <T> MonthColumn month(String dateFormat) {
        return new MonthColumn(new ConstColumn(dateFormat));
    }

    public static <T> YearColumn year(SFunction<T, ?> column) {
        return new YearColumn(new TableColumn<>(column));
    }

    public static <T> YearColumn year(String dateFormat) {
        return new YearColumn(new ConstColumn(dateFormat));
    }
}
