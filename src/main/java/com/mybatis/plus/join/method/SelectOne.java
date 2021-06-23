package com.mybatis.plus.join.method;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

public class SelectOne extends AbstractMethod {
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String sql = String.format("<script>\nSELECT %s FROM %s %s %s %s\n</script>",
                sqlSelectColumns(tableInfo, true),
                tableInfo.getTableName(),
                sqlJoinWrapper(),
                sqlWhereEntityWrapper(true, tableInfo),
                sqlComment());
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addSelectMappedStatementForTable(mapperClass, "selectOneJoin", sqlSource, tableInfo);
    }

    protected String sqlJoinWrapper() {
        return SqlScriptUtils.convertIf(String.format(" ${%s}", "ew.joins"),
                String.format("%s != null and !%s.isEmpty()", "ew.joinList", "ew.joinList"), true);
    }
}