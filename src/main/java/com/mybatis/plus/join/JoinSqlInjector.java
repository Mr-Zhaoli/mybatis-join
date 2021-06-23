package com.mybatis.plus.join;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.mybatis.plus.join.method.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class JoinSqlInjector extends DefaultSqlInjector implements Constants {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
        methodList.addAll(Arrays.asList(
                new SelectPage(),
                new SelectCount(),
                new SelectObj(),
                new SelectOne(),
                new SelectList(),
                new SelectObjs()
        ));
        return methodList;
    }
}