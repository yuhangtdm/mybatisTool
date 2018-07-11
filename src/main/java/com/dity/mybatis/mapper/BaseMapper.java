package com.dity.mybatis.mapper;

import com.dity.mybatis.template.MybatisTemplate;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;

public interface BaseMapper<T> {

    @InsertProvider(type = MybatisTemplate.class,method = "insert")
    @Options(useGeneratedKeys = true)
    Integer  insert(T t);

    @SelectProvider(type = MybatisTemplate.class,method = "queryById")
    T queryById(Serializable id);
}
