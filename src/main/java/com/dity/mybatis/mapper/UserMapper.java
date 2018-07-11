package com.dity.mybatis.mapper;

import com.dity.mybatis.annotation.RegisterDto;
import com.dity.mybatis.bean.User;

@RegisterDto(User.class)
public interface UserMapper extends BaseMapper<User> {
}
