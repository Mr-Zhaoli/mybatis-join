package com.mybatis.plus.dto;


import lombok.Data;

import java.sql.Timestamp;

/**
 *
 *
 * @author by zhaojin
 * @since 2021/7/5 9:38
 */
@Data
public class UserDTO {

    private String name;
    private Timestamp createTime;
}
