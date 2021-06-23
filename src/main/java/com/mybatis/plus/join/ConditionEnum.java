package com.mybatis.plus.join;

public enum ConditionEnum {
    LIKE("LIKE"),
    IN("IN"),
    EQ("="),
    NE("<>"),
    GT(">"),
    GE(">="),
    LT("<"),
    LE("<="),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL"),
    ;
    private final String keyword;

    private ConditionEnum(final String keyword) {
        this.keyword = keyword;
    }

    public String getSqlSegment() {
        return this.keyword;
    }
}
