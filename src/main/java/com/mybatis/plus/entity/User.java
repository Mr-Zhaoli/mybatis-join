package com.mybatis.plus.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_user",autoResultMap = true)
public class User {
    @TableId
    private String id;
    private String name;
    private String createTime;
    private String age;
    private String title;
}
