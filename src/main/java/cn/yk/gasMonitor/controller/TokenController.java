package cn.yk.gasMonitor.controller;

import cn.yk.gasMonitor.common.MessageConstant;
import cn.yk.gasMonitor.common.PageResult;
import cn.yk.gasMonitor.common.StatusCode;
import cn.yk.gasMonitor.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Jin Xichang
 * @date 2022/4/20
 **/
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/token")
public class TokenController {

    @Resource
    private TokenService tokenService;

    @RequestMapping("/valid")
    public PageResult valid(@RequestParam String token) {
        boolean success = tokenService.validToken(token);
        if (success) {
            return new PageResult(true, StatusCode.OK, MessageConstant.LOGIN_SUCCESS);
        } else {
            return new PageResult(true, StatusCode.LOGIN_EXPIRED, MessageConstant.LOGIN_EXPIRED);
        }
    }


}
