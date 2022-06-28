package cn.yk.gasMonitor.controller;

import cn.yk.gasMonitor.common.Result;
import cn.yk.gasMonitor.dto.LoginDTO;
import cn.yk.gasMonitor.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Jin Xichang
 * @date 2022/4/20
 **/
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/login")
public class LoginController {

    @Resource
    private LoginService loginService;

    @RequestMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        return loginService.login(loginDTO);
    }


}
