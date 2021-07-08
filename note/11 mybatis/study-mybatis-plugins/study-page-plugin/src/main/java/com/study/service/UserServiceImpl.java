package com.study.service;

import com.study.mapper.UserMapper;
import com.study.util.PageUtil;
import com.study.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public R getUser() {
        return R.success().data(userMapper.selectAll());
    }

    public R getUserByPage(int page, int limit) {
        Map<String, Object> map = new HashMap<>();
        PageUtil pageUtil = new PageUtil(page, limit);
        map.put("page", pageUtil);
        return R.success().data(userMapper.selectAllByPage(map)).set("count", pageUtil.getCount());
    }

}
