package com.mvc.custom.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class MyLog {

	private static StackTraceElement caller = getCaller(); 
    private static Logger log = LoggerFactory.getLogger(caller.getClassName() + "." + caller.getMethodName() + "() Line: " + caller.getLineNumber());
 
    public static void debug(String message) {
        if (log.isDebugEnabled()) {
            log.debug(message);
        }
    }
 
    public static void debug(String message, Exception e) {
        if (log.isDebugEnabled()) {
            log.debug(message, e);
        }
    }
 
    public static void info(String message) {
        if (log.isInfoEnabled()) {
            log.info(message);
        }
    }
 
    public static void info(String message, Exception e) {
        if (log.isInfoEnabled()) {
            log.info(message, e);
        }
    }
 
    public static void warn(String message) {
        log.warn(message);
    }
 
    public static void warn(String message, Exception e) {
        log.warn(message, e);
    }
 
    public static void error(String message) {
        log.error(message);
    }
 
    public static void error(String message, Exception e) {
        log.error(message, e);
    }
 
    public static StackTraceElement getCaller() {
    	// 获取堆栈信息
        StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
        StackTraceElement caller = null;
        if (null == callStack) {
        }
        else {
            // 日志类名称
            String logClassName = MyLog.class.getName();
            // 循环遍历到日志类标识
            boolean isEachLogClass = false;
 
            // 遍历堆栈信息，获取出最原始被调用的方法信息
            for (StackTraceElement s : callStack) {
            	// 遍历到日志类
                if (logClassName.equals(s.getClassName())) {
                    isEachLogClass = true;
                }
             // 下一个非日志类的堆栈，就是最原始被调用的方法
                if (isEachLogClass) {
                    if (!logClassName.equals(s.getClassName())) {
                        isEachLogClass = false;
                        caller = s;
                        break;
                    }
                }
            }
        }
        return caller;
    }
}
