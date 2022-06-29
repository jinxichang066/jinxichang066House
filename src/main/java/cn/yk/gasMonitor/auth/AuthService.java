package cn.yk.gasMonitor.auth;

import cn.yk.gasMonitor.common.MessageConstant;
import cn.yk.gasMonitor.common.PageResult;
import cn.yk.gasMonitor.common.StatusCode;
import cn.yk.gasMonitor.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 校验权限
 *
 * @author Jin Xichang
 * @date 2022/6/28
 **/
@Slf4j
@Aspect
@Component
public class AuthService {

    @Resource
    private TokenService tokenService;

    @Pointcut("@annotation(Auth)")
    private void cut() {
    }

    /**
     * <h5>功能:前置通知</h5>
     */
    @Around("cut()")
    public PageResult around(ProceedingJoinPoint pjp) {
        PageResult result = new PageResult();

        log.info("[验证token]自定义注解开始执行");
        Object[] args = pjp.getArgs();
        if (args.length == 0) {
            //return RequestResult.failure();
        }

        // 获取请求头Header中的gasToken
        HttpServletRequest request = (HttpServletRequest) args[0];
        String gasToken = request.getHeader("gasToken");
        log.info("gasToken:{}", gasToken);

        // 调用验证逻辑
        boolean success = tokenService.validToken(gasToken);

        if (!success) {
            result.setCode(StatusCode.LOGIN_EXPIRED);
            result.setMessage(MessageConstant.LOGIN_EXPIRED);
        } else {
            try {
                result = (PageResult) pjp.proceed(pjp.getArgs());
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
            }
        }

        return result;
    }
}
