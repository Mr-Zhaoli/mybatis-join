package com.mybatis.plus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_score",autoResultMap = true)
public class Score {
    private String id;
    private String userId;
    private Integer score;
    private String examId;
}
