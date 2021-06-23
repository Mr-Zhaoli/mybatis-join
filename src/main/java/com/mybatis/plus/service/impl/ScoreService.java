package com.mybatis.plus.service.impl;

import com.mybatis.plus.entity.Score;
import com.mybatis.plus.mapper.ScoreMapper;
import com.mybatis.plus.service.IScoreService;
import org.springframework.stereotype.Service;

@Service
public class ScoreService extends BaseService<ScoreMapper, Score> implements IScoreService {
}
