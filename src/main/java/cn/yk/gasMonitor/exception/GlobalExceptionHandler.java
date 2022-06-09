package cn.yk.gasMonitor.exception;

import cn.yk.gasMonitor.common.MessageConstant;
import cn.yk.gasMonitor.common.Result;
import cn.yk.gasMonitor.common.StatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result error(Exception exception) {
        exception.printStackTrace();
        return new Result(false, StatusCode.ERROR, MessageConstant.SYSTEM_BUSY);
    }
}
