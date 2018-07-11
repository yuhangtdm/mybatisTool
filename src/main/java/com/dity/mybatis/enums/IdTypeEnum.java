package com.dity.mybatis.enums;

public enum  IdTypeEnum {
    INTERGER("INTERGER","主键是整型"),
    VARCHAR("VARCHAR","主键是字符串型");
    private final String name;
    private final String msg;
    private IdTypeEnum(String name,String msg){
        this.name=name;
        this.msg=msg;
    }

    public String getName() {
        return name;
    }

    public String getMsg() {
        return msg;
    }
}
