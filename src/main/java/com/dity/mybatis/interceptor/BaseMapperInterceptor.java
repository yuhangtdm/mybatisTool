package com.dity.mybatis.interceptor;

import com.dity.mybatis.annotation.RegisterDto;
import com.dity.mybatis.mapper.BaseMapper;
import com.dity.mybatis.utils.DtoUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Intercepts({@Signature(type = Executor.class,method = "query",
        args = {MappedStatement.class,Object.class,RowBounds.class,ResultHandler.class}),
        @Signature(type = Executor.class,method = "update",
                args = {MappedStatement.class,Object.class})
})
public class BaseMapperInterceptor implements Interceptor {
    //用以存放符合的类名以及对应的Class
    private Map<String,Class<?>> classMap=new ConcurrentHashMap<>();
    private Lock lock=new ReentrantLock();
    private static final Method[] methods=BaseMapper.class.getDeclaredMethods();

    /**
     * 拦截mybatis的运行 改变运行时的参数
     * @param invocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println("init intercept...");
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object param=args[1];
        String id = ms.getId();
        String className = id.substring(0,id.lastIndexOf("."));
        Class currentClass=this.getClass(className);
        String[] split = id.split("\\.");
        String methodId=split[split.length-1];
        if(!this.isBaseMethod(currentClass,methodId)){
            return invocation.proceed();
        }else {
            String[] keyProperties = ms.getKeyProperties();
            String[] keyColumns = ms.getKeyColumns();
            this.setReturnId(ms,param,keyProperties,keyColumns);
            this.setObject(currentClass,args,param,methodId);
            return invocation.proceed();
        }

    }



    public static void main(String[] args) {
        String[] split = "com.atguigu.mybatis.UserMapper.queryById".split("\\.");
        System.out.println(split.length);
        System.out.println(split[split.length-2]);
        System.out.println(split[split.length-1]);
    }


    @Override
    public Object plugin(Object o) {
        System.out.println("拦截的对象："+o);
        return Plugin.wrap(o,this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    /**
     * 获取当前类名对应Class 并且缓存到map中
     * @param className
     * @return
     */
    private Class getClass(String className) throws ClassNotFoundException {
        Class<?> currentClass=classMap.get(className);
        if(currentClass!=null){
            return currentClass;
        }else {
            currentClass= Class.forName(className);
            Class<?>[] interfaces = currentClass.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                if(anInterface.equals(BaseMapper.class)){
                    if(!classMap.containsKey(className)){
                        lock.lock();;
                        classMap.put(className,currentClass);
                        lock.unlock();
                    }
                }
            }
            return currentClass;
        }
    }

    /**
     * 判断传来的方法是否
     * @param currentClass
     * @param methodId
     * @return
     */
    private boolean isBaseMethod(Class currentClass, String methodId) {
        Boolean isBase=Boolean.FALSE;
        for (Method method : methods) {
            if(methodId.equals(method.getName())){
                isBase=Boolean.TRUE;
            }
        }
        if(isBase.booleanValue()){
            Class<?>[] interfaces = currentClass.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                if(anInterface.equals(BaseMapper.class)){
                   return true;
                }
            }
        }
        return false;
    }

    /**
     * 用于insert操作 获取插入后的id
     * @param ms
     * @param param
     * @param keyProperties
     * @param keyColumns
     */
    private void setReturnId(MappedStatement ms, Object param, String[] keyProperties, String[] keyColumns) {
        if(ms.getSqlCommandType().equals(SqlCommandType.INSERT)){
          Field field= DtoUtil.idField((Serializable)param);
            String name = field.getName();
            if(keyProperties==null){
                keyProperties=new String[1];
            }
            keyProperties[0]=name;
            String idColumn=DtoUtil.id((Serializable)param);
            if(keyColumns==null){
                keyColumns=new String[1];
            }
            keyColumns[0]=idColumn;
        }
    }

    /**
     * 对于根据id查询的将对象封装给方法
     * @param currentClass
     * @param param
     * @param o
     * @param methodId
     */
    private void setObject(Class currentClass, Object[] args, Object o, String methodId) throws IllegalAccessException, InstantiationException {
        if(methodId.equals("queryById")) {
            Annotation annotation = currentClass.getAnnotation(RegisterDto.class);
            if (annotation == null) {
                throw new PluginException("继承BaseMapper的接口未加@RegisterDto注解");
            }
            Class<?> clazz = ((RegisterDto) annotation).value();
            Serializable obj = (Serializable) clazz.newInstance();
            Field field = DtoUtil.idField(obj);
            field.setAccessible(true);
            field.set(obj,o);
            field.setAccessible(false);
            o = obj;
            args[1]=obj;
        }
    }

}
