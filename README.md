对mybatis-plus的补充

支持关联查询

支持mysql中的各种函数,不完整,但是可以后续慢慢补充


使用说明:
替换BaseMapper为com.mybatis.plus.mapper.ParentMapper
替换IService为com.mybatis.plus.service.IBaseService
替换ServiceImpl为com.mybatis.plus.service.impl.BaseService

举例:有用户表和成绩表

User

|  id   | name  |
|  ----  | ----  |
| 10001  | 老李 |
| 10002  | 老王 |

Score

|  id    |  userId | score | examId(考试id) |
|  ----  | ----  |  ----  | ----  |
| 1  | 10001 | 100  |   1   |
| 2  | 10002 |  50  |   1   |
| 3  | 10001 | 100  |   2   |
| 4  | 10002 |  60  |   2   |
| 5  | 10001 | 100  |   3   |
| 6  | 10002 |  70  |   3   |

##1.查询在考试'1'中成绩最高的人名字

````java
import com.mybatis.plus.entity.Score;
import com.mybatis.plus.entity.User;
import com.mybatis.plus.join.ConditionEnum;
import com.mybatis.plus.service.IUserService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static com.mybatis.plus.service.impl.BaseService.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {
    @Autowired
    private IUserService userService;

    @org.junit.Test
    public void testMaxScore() {
        User user = userService.query(User.class)
                .select(User::getName)
                .innerJoin(Score.class)
                .on(User::getId, Score::getUserId, ConditionEnum.EQ)
                .eq(Score::getExamId, "1")
                .select(max(Score::getScore), "id")
                .groupBy(Score::getExamId)
                .one();
        System.out.println(user);
    }

}
````
打印日志为:
```log
2021-06-23 18:04:01.634 DEBUG 8260 --- [           main] c.m.p.mapper.UserMapper.selectOneJoin    : ==>  Preparing: SELECT t_user.`name`,max(t_score.`score`) as id FROM t_user INNER JOIN t_score ON t_user.`id`=t_score.`user_id` WHERE (t_score.`exam_id` = ?) GROUP BY t_score.`exam_id` 
2021-06-23 18:04:01.653 DEBUG 8260 --- [           main] c.m.p.mapper.UserMapper.selectOneJoin    : ==> Parameters: 1(String)
2021-06-23 18:04:01.669 DEBUG 8260 --- [           main] c.m.p.mapper.UserMapper.selectOneJoin    : <==      Total: 1
User(id=100, name=老王)
```

##2.查询一下名字为老李的有没有参加比赛'3'

```java
package com.mybatis.plus;


import com.mybatis.plus.entity.Score;
import com.mybatis.plus.entity.User;
import com.mybatis.plus.join.ConditionEnum;
import com.mybatis.plus.service.IUserService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;

import static com.mybatis.plus.service.impl.BaseService.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {

    @Autowired
    private IUserService userService;
    /**
     * 查询一下名字为老李的有没有参加比赛2和比赛3
     */
    @org.junit.Test
    public void testZXLExistScore() {
        User user = userService.query(User.class)
                .select(json(
                        col("是否参加了比赛2"),
                        If(exists(q -> q.query(Score.class)
                                .eq(Score::getExamId, "2")
                                .on(Score::getUserId, User::getId, ConditionEnum.EQ)), true, false),
                        col("是否参加了比赛3"),
                        If(exists(q -> q.query(Score.class)
                                .eq(Score::getExamId, "3")
                                .on(Score::getUserId, User::getId, ConditionEnum.EQ)), true, false))
                        , "name")
                .eq(User::getName, "老李")
                .one();
        System.out.println(user.getName());
    }

}

```
```log
2021-06-23 18:42:58.931 DEBUG 22404 --- [           main] c.m.p.mapper.UserMapper.selectOneJoin    : ==>  Preparing: SELECT JSON_OBJECT('是否参加了比赛2',IF(EXISTS(SELECT 1 FROM t_score WHERE t_score.`user_id`=t_user.`id` AND (t_score.`exam_id` = ?)),true,false),'是否参加了比赛3',IF(EXISTS(SELECT 1 FROM t_score WHERE t_score.`user_id`=t_user.`id` AND (t_score.`exam_id` = ?)),true,false)) as name FROM t_user WHERE (t_user.`name` = ?) 
2021-06-23 18:42:58.948 DEBUG 22404 --- [           main] c.m.p.mapper.UserMapper.selectOneJoin    : ==> Parameters: 2(String), 3(String), 老李(String)
2021-06-23 18:42:58.964 DEBUG 22404 --- [           main] c.m.p.mapper.UserMapper.selectOneJoin    : <==      Total: 1
{"是否参加了比赛2": 1, "是否参加了比赛3": 1}
```
##3.查询参加比赛'2'的人员名单
```java
package com.mybatis.plus;


import com.mybatis.plus.entity.Score;
import com.mybatis.plus.entity.User;
import com.mybatis.plus.join.ConditionEnum;
import com.mybatis.plus.service.IUserService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Objects;

import static com.mybatis.plus.service.impl.BaseService.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {

    @Autowired
    private IUserService userService;

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

```
```log
2021-06-23 18:31:24.885 DEBUG 28780 --- [           main] c.m.p.mapper.UserMapper.selectListJoin   : ==>  Preparing: SELECT * FROM t_user WHERE ( EXISTS(SELECT 1 FROM t_score WHERE t_score.`user_id`=t_user.`id` AND (t_score.`exam_id` = ?)) ) 
2021-06-23 18:31:24.903 DEBUG 28780 --- [           main] c.m.p.mapper.UserMapper.selectListJoin   : ==> Parameters: 2(String)
2021-06-23 18:31:24.919 DEBUG 28780 --- [           main] c.m.p.mapper.UserMapper.selectListJoin   : <==      Total: 2
[User(id=10001, name=老王), User(id=10002, name=老李)]
```