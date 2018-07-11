package com.dity.mybatis.bean;

import com.dity.mybatis.annotation.Column;
import com.dity.mybatis.annotation.TableId;
import com.dity.mybatis.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = -5236810568067654941L;
    @TableId(value = "tid")
    private Integer tid;

    @Column("user_name")
    private String name;

    private Integer age;

    @Column(type = false)
    private String userPhone;

    private Date createTime;

    private Date updateTime;

    public Integer getTid() {
        return tid;
    }

    public void setTid(Integer tid) {
        this.tid = tid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + tid +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", userPhone='" + userPhone + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
