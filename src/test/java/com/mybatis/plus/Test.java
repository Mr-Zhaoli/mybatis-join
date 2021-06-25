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

/**
 * 运行前需要 set @@global_mode=''
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {

    @Autowired
    private IUserService userService;

    /**
     * 测试子查询,获取子查询的结果,不能将子查询当成临时表来查询.
     */
    @org.junit.Test
    public void testSubQuery() {
        List<User> userList = userService.query(User.class)
                .select(QUERY(q -> q.query(Score.class)
                                .select(Score::getId)
                                .where(Score::getId, ConditionEnum.EQ, User::getId)
                                .eq(Score::getId, "1")),
                        "id")
                .list();
        System.out.println(userList);

        userList = userService.query(User.class)
                .notIN(User::getId, QUERY(q -> q.query(User.class)
                        .select(User::getId)))
                .list();
        System.out.println(userList);
    }


    @org.junit.Test
    public void testCaseWhenColumn() {
        List<User> userList = userService.query(User.class)
                .select(CASE(
                        User::getId,
                        COL(1000),
                        WHEN(ConditionEnum.EQ, "1", 100),
                        WHEN(ConditionEnum.EQ, "1", "200"),
                        WHEN(ConditionEnum.EQ, "1", 300),
                        WHEN(ConditionEnum.EQ, "1", "400")
                ), "id")
                .list();
        System.out.println(userList);


        userList = userService.query(User.class)
                .select(CASE(null,
                        WHEN(User::getId, ConditionEnum.EQ, "1", 100),
                        WHEN(User::getId, ConditionEnum.EQ, "1", "200"),
                        WHEN(User::getId, ConditionEnum.EQ, "1", 300),
                        WHEN(User::getId, ConditionEnum.EQ, "1", "400")
                ), "id")
                .list();
        System.out.println(userList);
    }

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
     * 查询一下名字为老李的有没有参加比赛2和比赛3
     */
    @org.junit.Test
    public void testLAOLIExistScore() {
        User user = userService.query(User.class)
                .select(JSON(
                        COL("是否参加了比赛2"),
                        IF(EXISTS(q -> q.query(Score.class)
                                .eq(Score::getExamId, "3")
                                .where(Score::getUserId, ConditionEnum.EQ, User::getId)), true, false),
                        COL("是否参加了比赛3"),
                        IF(EXISTS(q -> q.query(Score.class).eq(Score::getExamId, "3")
                                .where(Score::getUserId, ConditionEnum.EQ, User::getId)), true, false)
                        )
                        , "name")
                .eq(User::getName, "老李")
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
                        .where(Score::getUserId, ConditionEnum.EQ, User::getId)
                )
                .list();
        System.out.println(users);
    }

}
