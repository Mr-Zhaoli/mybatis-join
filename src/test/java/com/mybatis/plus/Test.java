package com.mybatis.plus;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mybatis.plus.entity.Score;
import com.mybatis.plus.entity.User;
import com.mybatis.plus.join.ConditionEnum;
import com.mybatis.plus.service.IUserService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.mybatis.plus.service.impl.BaseService.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {

    @Autowired
    private IUserService userService;


    @org.junit.Test
    public void testConstColumn() {
        if (StringUtils.isNotEmpty("")) {
            return;
        }
        List<User> list = userService.query(User.class)
                .select(IFNULL(User::getId, 100), "id")
                .innerJoin(Score.class)
                .on(User::getId, Score::getUserId, ConditionEnum.EQ)
                .select(Score::getUserId, Score::getScore, Score::getExamId)
                .select(IFNULL(Score::getId, 100), "id")
                .where(Score::getId, ConditionEnum.GE, COL("10001"))
                .list();
        System.out.println(list);
    }

    @org.junit.Test
    public void testWhere() {
        List<User> list = userService.query(User.class)
                .where(YEAR(User::getCreateTime), ConditionEnum.LE, "2021")
                .list();
        System.out.println(list);
    }

    /**
     * 查询在考试1中成绩最高的人名字
     */
    @org.junit.Test
    public void testMaxScore() {
        User user = userService.query(User.class)
                .select(User::getName)
                .innerJoin(Score.class)
                .on(User::getId, Score::getUserId, ConditionEnum.EQ)
                .eq(Score::getExamId, "1")
                .select(MAX(Score::getScore), "id")
                .one();
        System.out.println(user);
    }


    /**
     * 查询一下名字为赵小莉的有没有参加比赛2和比赛3
     */
    @org.junit.Test
    public void testZXLExistScore() {
        User user = userService.query(User.class)
                .select(JSON(
                        COL("是否参加了比赛2"),
                        IF(EXISTS(q -> q.query(Score.class).eq(Score::getExamId, "3")
                                .on(Score::getUserId, User::getId, ConditionEnum.EQ)), true, false),
                        COL("是否参加了比赛3"),
                        IF(EXISTS(q -> q.query(Score.class).eq(Score::getExamId, "3")
                                .on(Score::getUserId, User::getId, ConditionEnum.EQ)), true, false)
                        )
                        , "name")
                .eq(User::getName, "赵小莉")
                .one();
        System.out.println(user.getName());
    }

    /**
     * 查询参加比赛2的人员名单
     */
    @org.junit.Test
    public void testExistScoreInExam2() {
        List<User> users = userService.query(User.class)
                .exists(q -> q.query(Score.class).eq(Score::getExamId, "2")
                        .on(Score::getUserId, User::getId, ConditionEnum.EQ)
                )
                .list();
        System.out.println(users);
    }

}
