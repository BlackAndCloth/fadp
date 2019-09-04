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
    	// ��ȡ��ջ��Ϣ
        StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
        StackTraceElement caller = null;
        if (null == callStack) {
        }
        else {
            // ��־������
            String logClassName = MyLog.class.getName();
            // ѭ����������־���ʶ
            boolean isEachLogClass = false;
 
            // ������ջ��Ϣ����ȡ����ԭʼ�����õķ�����Ϣ
            for (StackTraceElement s : callStack) {
            	// ��������־��
                if (logClassName.equals(s.getClassName())) {
                    isEachLogClass = true;
                }
             // ��һ������־��Ķ�ջ��������ԭʼ�����õķ���
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
