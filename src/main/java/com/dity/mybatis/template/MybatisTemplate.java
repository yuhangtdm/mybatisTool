package com.dity.mybatis.template;

import com.dity.mybatis.utils.DtoUtil;
import org.apache.ibatis.jdbc.SQL;

import java.io.Serializable;

public class MybatisTemplate<T> {
    private SQL sql;


    public String insert(T t) throws Exception {
        this.sql=new SQL();
        sql.INSERT_INTO(DtoUtil.getTableName(t));
        DtoUtil.calculateColumn(t.getClass());
        sql.VALUES(DtoUtil.getColumns(t),DtoUtil.getValues(t));
        return sql.toString();
    }

    public String queryById(Serializable obj)throws Exception{
        this.sql=new SQL();
        DtoUtil.calculateColumn(obj.getClass());
        sql.SELECT(DtoUtil.getSelectColumns(obj.getClass()));
        sql.FROM(DtoUtil.getTableName(obj));
        sql.WHERE(DtoUtil.whereId(obj));
        return sql.toString();
    }
}
