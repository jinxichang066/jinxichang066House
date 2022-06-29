package cn.yk.gasMonitor.exception;

import cn.yk.gasMonitor.common.MessageConstant;
import cn.yk.gasMonitor.common.PageResult;
import cn.yk.gasMonitor.common.StatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public PageResult error(Exception exception) {
        exception.printStackTrace();
        return new PageResult(false, StatusCode.ERROR, MessageConstant.SYSTEM_BUSY);
    }
}
