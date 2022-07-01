package cn.yk.gasMonitor.controller;

import cn.yk.gasMonitor.common.PageResult;
import cn.yk.gasMonitor.service.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Jin Xichang
 * @date 2022/4/20
 **/
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/system")
public class SystemController {

    @Resource
    private SystemService systemService;

    @RequestMapping("/getImage")
    public void logout(HttpServletResponse response) {
        systemService.getImage(response);
    }

    @RequestMapping("/uploadImage")
    private PageResult uploadImage(@RequestParam MultipartFile file) throws IOException {
        return systemService.uploadImage(file);
    }

}
