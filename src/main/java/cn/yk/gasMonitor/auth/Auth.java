package cn.yk.gasMonitor.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，校验token
 * <p>
 * 将需要校验的Controller接口加上该注解即可
 * 注意加上该注解的方法参数，第一个要是HttpServletRequest，因为前端请求将gasToken信息放入了请求头Header中，后端要用request对象去取
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Auth {

}
