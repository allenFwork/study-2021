package com.study.mapper;

import com.study.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserMapper {

    public List<User> selectAll();

    public List<User> selectAllByPage(Map map);

}
