package com.mybatis.plus;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mybatis.plus.dto.UserDTO;
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
     * 测试map转对象
     */
    @org.junit.Test
    public void testMap() {
        List<UserDTO> userList = userService.query(User.class)
                .select(User::getName,User::getId,User::getCreateTime)
                .list(UserDTO.class);
        System.out.println(userList);
    }

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
                .notIn(User::getId, QUERY(q -> q.query(User.class)
                        .select(User::getId)))
                .list();
        System.out.println(userList);
    }


    @org.junit.Test
    public void testCaseWhenColumn() {
        List<User> userList = userService.query(User.class)
                .select(CASE().when(User::getId, ConditionEnum.EQ, "1", 100)
                        .when(User::getId, ConditionEnum.EQ, "2", 100)
                        .when(User::getId, ConditionEnum.EQ, "3", 100)
                        .when(User::getId, ConditionEnum.EQ, "4", 100)
                        .el(100), "id")
                .list();
        System.out.println(userList);


        userList = userService.query(User.class)
                .select(CASE(User::getId).when("1", "1000")
                        .when("2", "2000")
                        .when("3", "3000")
                        .when("4", "4000")
                        .el(100), "id")
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
                .on(User::getId, ConditionEnum.EQ, Score::getUserId)
                .select(Score::getUserId, Score::getScore, Score::getExamId)
                .select(IFNULL(Score::getId, 100), "id")
                .where(Score::getId, ConditionEnum.GE, COL("10001"))
                .list();
        System.out.println(list);
    }

    /**
     * test where
     */
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
                .on(User::getId, ConditionEnum.EQ, Score::getUserId)
                .on(User::getId, ConditionEnum.EQ,"1")
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
                        KV("是否参加了比赛2", IF(EXISTS(q -> q.query(Score.class)
                                        .eq(Score::getExamId, "2")
                                        .where(Score::getUserId, ConditionEnum.EQ, User::getId)),
                                true, false)),
                        KV("是否参加了比赛3", IF(EXISTS(q -> q.query(Score.class)
                                        .eq(Score::getExamId, "3")
                                        .where(Score::getUserId, ConditionEnum.EQ, User::getId)),
                                true, false))
                ), "name")
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
