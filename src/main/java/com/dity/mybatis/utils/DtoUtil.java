package com.dity.mybatis.utils;

import com.dity.mybatis.annotation.Column;
import com.dity.mybatis.annotation.TableId;
import com.dity.mybatis.annotation.TableName;
import com.dity.mybatis.bean.User;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class DtoUtil {

    private static Map<String,Map<String,String>> objColumnMap=new HashMap<>();
    private static final String SERIAL_VERSION_UID="serialVersionUID";

    public static void main(String[] args) throws Exception {
        User user=new User();
        user.setName("王琴");
        user.setAge(24);

        System.out.println(getTableName(user));
        calculateColumn(User.class);
        System.out.println(getColumns(user));
        System.out.println(getValues(user));
        System.out.println(convert("UserName"));

    }

    //得到对象的表名
    public static <T> String getTableName(T t) {
        Class<?> aClass = t.getClass();
        TableName annotation = aClass.getAnnotation(TableName.class);
        if (annotation != null) {
            return annotation.value();
        }
        String simpleName = aClass.getSimpleName();
        return convert(simpleName);
    }



    //将对象的属性名和表名的映射缓存到map中
    public static void calculateColumn(Class<?> aClass) {
        String className = aClass.getSimpleName();
        if(!objColumnMap.containsKey(className)){
            Map<String,String> columnMap=new HashMap<>();
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                String fieldName=declaredField.getName();
                if(!fieldName.equals(SERIAL_VERSION_UID)){
                    TableId tableId = declaredField.getDeclaredAnnotation(TableId.class);
                    if(tableId!=null){
                        columnMap.put(fieldName,tableId.value());
                        continue;
                    }
                    Column column = declaredField.getDeclaredAnnotation(Column.class);
                    if(column!=null && column.type() ){
                        columnMap.put(fieldName,column.value());
                    }else{
                        columnMap.put(fieldName,convert(fieldName));
                    }
                }
            }
            objColumnMap.put(className,columnMap);
        }

    }


    public static <T> String getColumns(T t) throws Exception {
        StringBuffer sb=new StringBuffer();
        Class<?> aClass = t.getClass();
        Map<String, String> columnMap = objColumnMap.get(aClass.getSimpleName());
        if(columnMap==null)return null;

        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            String fieldName=declaredField.getName();
            if(fieldName.equals(SERIAL_VERSION_UID))continue;
            Column column = declaredField.getDeclaredAnnotation(Column.class);
            if(column!=null && !column.type())continue;
            declaredField.setAccessible(true);
            Object field = declaredField.get(t);
            if(field!=null || fieldName.equals("createTime") || fieldName.equals("updateTime")){
                sb.append(columnMap.get(fieldName)).append(",");
            }
            declaredField.setAccessible(false);
        }
        return sb.substring(0,sb.length()-1);

    }

    public static <T> String getValues(T t) throws Exception {
        StringBuffer sb=new StringBuffer();
        Class<?> aClass = t.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            String fieldName=declaredField.getName();
            if(fieldName.equals(SERIAL_VERSION_UID))continue;
            Column column = declaredField.getDeclaredAnnotation(Column.class);
            if(column!=null && !column.type())continue;
            declaredField.setAccessible(true);
            Object field = declaredField.get(t);
            if(field!=null && !fieldName.equals("createTime") && !fieldName.equals("updateTime")){
                sb.append("#{"+fieldName+"}").append(",");
            }else if(fieldName.equals("createTime") || fieldName.equals("updateTime")){
                sb.append("now()").append(",");
            }
            declaredField.setAccessible(false);
        }
        return sb.substring(0,sb.length()-1);
    }

    private static String convert(String source){
        String sr=source.substring(0,1).toUpperCase()+source.substring(1);
       return sr.replaceAll("([A-Z])", "_$1").replaceFirst("_", "").toLowerCase();
    }


    public static Field idField(Serializable param) {
        boolean flag=false;
        Class<? extends Serializable> aClass = param.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            TableId annotation = declaredField.getAnnotation(TableId.class);
            if(annotation!=null){
                return declaredField;
            }
            if(declaredField.getName().equals("id")){
                flag=true;
            }

        }

        if(flag){
            try {
                return param.getClass().getDeclaredField("id");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("未定义主键字段");
            }
        }else {
            throw new RuntimeException("未加注解TableId且没有主键字段");
        }
    }


    public static String id(Serializable param) {
        Field[] declaredFields = param.getClass().getDeclaredFields();
        boolean flag=false;
        for (Field declaredField : declaredFields) {
            TableId annotation = declaredField.getAnnotation(TableId.class);
            if(annotation!=null){
                return annotation.value();
            }
            if(declaredField.getName().equals("id")){
                flag=true;
            }
        }
        if(flag){
            return "id";
        }else {
            throw new RuntimeException("未定义主键");
        }

    }


    public static String getSelectColumns(Class<? extends Serializable> aClass) {
        StringBuffer sb=new StringBuffer();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            String fieldName=declaredField.getName();
            if(fieldName.equals(SERIAL_VERSION_UID))continue;
            TableId tableId = declaredField.getDeclaredAnnotation(TableId.class);
            if(tableId!=null){
                sb.append(tableId.value()).append(" as ").append(fieldName).append(",");
                continue;
            }
            Column column = declaredField.getDeclaredAnnotation(Column.class);
            if(column!=null){
                if(!column.type())continue;
                sb.append(column.value()).append(" as ").append(fieldName).append(",");
            } else {
                sb.append(convert(fieldName)).append(" as ").append(fieldName).append(",");
            }

        }
        return sb.substring(0,sb.length()-1);
    }

    public static String whereId(Serializable obj) {
        Field field = idField(obj);
        String id = id(obj);
        return id+"=#{"+field.getName()+"}";
    }
}
