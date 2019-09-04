package com.mvc.custom.proxy;




import java.lang.reflect.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.mvc.custom.annotation.Delete;
import com.mvc.custom.annotation.Insert;
import com.mvc.custom.annotation.Param;
import com.mvc.custom.annotation.Query;
import com.mvc.custom.annotation.Update;
import com.mvc.custom.util.DBCPUtils;
import com.mvc.custom.util.SQLUtils;

public class UserMapperInvocationHandler implements InvocationHandler {

    private Class userMapperClazz;

    public UserMapperInvocationHandler(Class userMapperClazz) {
        this.userMapperClazz = userMapperClazz;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Query query =  method.getDeclaredAnnotation(Query.class);
        if (null != query) {
            return getResult(method, query, args);
        }
        Insert insert = method.getDeclaredAnnotation(Insert.class);
        if (null != insert) {
            String insertSql = insert.value();
            String[] insertParam = SQLUtils.getInsertParams(insertSql);
            
            ConcurrentHashMap<String, Object> paramMap = getMethodParam(method, args);
            List<Object> paramValueList = addParamToList(insertParam, paramMap);

            insertSql = SQLUtils.replaceParam(insertSql, insertParam);
            return DBCPUtils.insert(insertSql, false, paramValueList);
        }
        Update update = method.getDeclaredAnnotation(Update.class);
        if(null != update) {
        	String updateSql = update.value();
        	String[] updateParam = SQLUtils.getUpdateParams(updateSql);
        	ConcurrentHashMap<String, Object> paramMap = getMethodParam(method, args);
        	List<Object> paramValueList = addParamToList(updateParam, paramMap);
        	updateSql = SQLUtils.replaceParam(updateSql, updateParam);
        	return DBCPUtils.update(updateSql, false, paramValueList);
        }
        Delete delete = method.getDeclaredAnnotation(Delete.class);
        if(null != delete) {
        	String deleteSql = delete.value();
        	String[] deleteParam = SQLUtils.getDeleteParams(deleteSql);
        	ConcurrentHashMap<String, Object> paramMap = getMethodParam(method, args);
        	List<Object> paramValueList = addParamToList(deleteParam, paramMap);
        	deleteSql = SQLUtils.replaceParam(deleteSql, deleteParam);
        	return DBCPUtils.delete(deleteSql, false, paramValueList);
        }
        return null;
    }


    private Object getResult(Method method, Query query, Object[] args) throws SQLException, IllegalAccessException, InstantiationException {
        String querySql = query.value();
        List<Object> paramList = SQLUtils.getSelectParams(querySql);
        querySql = SQLUtils.replaceParam(querySql, paramList);
        ConcurrentHashMap<String, Object> paramMap = getMethodParam(method, args);

        List<Object> paramValueList = new ArrayList<>();
        for (Object param : paramList) {
            Object paramValue = paramMap.get(param);
            paramValueList.add(paramValue);
        }


        ResultSet rs = DBCPUtils.query(querySql, paramValueList);
        if (!rs.next()) {return null;}

        Class<?> returnTypeClazz = method.getReturnType();
        Object obj = returnTypeClazz.newInstance();
        rs.previous();
        while (rs.next()) {
            Field[] fields = returnTypeClazz.getDeclaredFields();
            for (Field field : fields) {
                String fieldName = field.getName();
                Object fieldValue = rs.getObject(fieldName);
                field.setAccessible(true);    
                field.set(obj, fieldValue);
            }
        }
        return obj;
    }

    private ConcurrentHashMap getMethodParam(Method method, Object[] args) {
        ConcurrentHashMap paramMap = new ConcurrentHashMap();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Param gdzParam = parameters[i].getAnnotation(Param.class);
            if (null == gdzParam) {continue;}          
            String paramName = gdzParam.value();
            Object paramValue = args[i];
            paramMap.put(paramName, paramValue);
        }
        return paramMap;
    }


    private List<Object> addParamToList(String[] insertParam, ConcurrentHashMap<String, Object> paramMap) {
        List<Object> paramValueList = new ArrayList<>();
        for (String param : insertParam) {
            Object paramValue = paramMap.get(param.trim());
            paramValueList.add(paramValue);
        }
        return paramValueList;
    }

}
