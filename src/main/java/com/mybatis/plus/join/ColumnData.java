package com.mybatis.plus.join;


import lombok.Data;

/**
 * TODO
 *
 * @author by zhaojin
 * @since 2021/6/21 12:22
 */
@Data
public class ColumnData {

    private JoinWrapper<?> wrapper;

    public ColumnData() {
    }

    public ColumnData(JoinWrapper<?> wrapper) {
        this.wrapper = wrapper;
    }
}
