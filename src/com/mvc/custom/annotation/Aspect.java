package com.mvc.custom.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)//保留时间长短
@Target(value = {ElementType.TYPE})//使用范围 接口、类、枚举�?�注�?
public @interface Aspect {
}
