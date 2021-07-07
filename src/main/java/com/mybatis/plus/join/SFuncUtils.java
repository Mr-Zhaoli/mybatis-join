package com.mybatis.plus.join;


import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.mybatis.plus.entity.Score;
import com.mybatis.plus.entity.User;
import org.apache.ibatis.reflection.property.PropertyNamer;

import java.util.Map;
import java.util.function.Function;

/**
 * 用SFunction获取表名+"."+列名
 *
 * @author by zhaojin
 * @since 2021/6/23 11:21
 */
public class SFuncUtils {

    public static String getColumnNameWithTable(SFunction<?, ?> sFunction) throws MybatisPlusException {
        SerializedLambda lambda = LambdaUtils.resolve(sFunction);
        String fieldName = PropertyNamer.methodToProperty(lambda.getImplMethodName());
        Class<?> aClass = lambda.getInstantiatedMethodType();
        Map<String, ColumnCache> columnMap = LambdaUtils.getColumnMap(aClass);
        ColumnCache columnCache = columnMap.get(LambdaUtils.formatKey(fieldName));
        Assert.notNull(columnCache, "can not find lambda cache for this property [%s] of entity [%s]",
                fieldName, aClass.getName());
        return TableInfoHelper.getTableInfo(aClass).getTableName() + ".`" + columnCache.getColumn() + "`";
    }


    public static<T> String getColumnName(SFunction<T, ?> sFunction) throws MybatisPlusException {
        SerializedLambda lambda = LambdaUtils.resolve(sFunction);
        return PropertyNamer.methodToProperty(lambda.getImplMethodName());
    }
}
