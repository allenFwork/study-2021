package com.study.service;

import com.study.util.R;

public interface UserService {
    public R getUser();
    public R getUserByPage(int page, int limit);
}
