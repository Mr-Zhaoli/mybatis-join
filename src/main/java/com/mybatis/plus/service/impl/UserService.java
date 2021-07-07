package com.mybatis.plus.service.impl;

import com.mybatis.plus.entity.Score;
import com.mybatis.plus.entity.User;
import com.mybatis.plus.join.ConditionEnum;
import com.mybatis.plus.mapper.UserMapper;
import com.mybatis.plus.service.IUserService;
import org.springframework.stereotype.Service;


@Service
public class UserService extends BaseService<UserMapper, User> implements IUserService {

    public void main() {
        User score = query(User.class).select(User::getName)
                .innerJoin(Score.class)
                .on(Score::getUserId, ConditionEnum.EQ, User::getId)
                .eq(Score::getExamId, 1)
                .select(MAX(Score::getScore), "name")
                .select(Score::getUserId)
                .one();
        System.out.println(score);
    }
}
