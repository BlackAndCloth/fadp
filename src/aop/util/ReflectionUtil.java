package aop.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.mvc.custom.util.Scanner;


public class ReflectionUtil {
	/**
	 * ����ʵ��
	 */
	public static Object newInstance(Class<?> cls) {
		Object instance;

		try {
			instance = cls.newInstance();
		} catch (Exception e) {
			throw new RuntimeException();
		}

		return instance;
	}

	/**
	 * ����ʵ�� ������������
	 */
	public static Object newInstance(String className) {
		Class<?> cls = Scanner.loadClass(className);

		return newInstance(cls);
	}

	/**
	 * ���÷���
	 */
	public static Object invokeMethod(Object obj, Method method, Object... args) {
		Object result;

		try {
			method.setAccessible(true);
			result = method.invoke(obj, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	/**
	 * ���ó�Ա������ֵ
	 */
	public static void setField(Object obj, Field field, Object value) {
		try {
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
