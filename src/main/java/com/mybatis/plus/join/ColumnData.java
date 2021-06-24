package com.mybatis.plus.join;


import lombok.Data;

/**
 * 存放拼接sql时必要的上下文参数
 *
 * @author by zhaojin
 * @since 2021/6/21 12:22
 */
@Data
public class ColumnData {

    private JoinWrapper<?> wrapper;

    public ColumnData(JoinWrapper<?> wrapper) {
        this.wrapper = wrapper;
    }
}
