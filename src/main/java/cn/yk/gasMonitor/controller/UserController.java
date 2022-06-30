package cn.yk.gasMonitor.controller;

import cn.yk.gasMonitor.auth.Auth;
import cn.yk.gasMonitor.common.MessageConstant;
import cn.yk.gasMonitor.common.PageResult;
import cn.yk.gasMonitor.common.StatusCode;
import cn.yk.gasMonitor.domain.User;
import cn.yk.gasMonitor.dto.UserDTO;
import cn.yk.gasMonitor.dto.UserSearchDTO;
import cn.yk.gasMonitor.service.UserService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Jin Xichang
 * @date 2022/4/20
 **/
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @RequestMapping("/searchCondition")
    @Auth
    public PageResult searchCondition(HttpServletRequest request, @RequestBody UserSearchDTO userSearchDTO) {
        IPage<User> page = userService.searchCondition(userSearchDTO);
        return new PageResult(true, StatusCode.OK, MessageConstant.USER_SEARCH_SUCCESS, page.getRecords(), page.getTotal());
    }

    @RequestMapping("/findById")
    public PageResult findById(String id) {
        return userService.findById(id);
    }

    @RequestMapping("/add")
    public PageResult add(@RequestBody User user) {
        return userService.add(user);
    }

    @RequestMapping("/update")
    public PageResult update(@RequestBody User user) {
        return userService.update(user);
    }

    @RequestMapping("/del")
    public PageResult del(@RequestBody List<String> ids) {
        return userService.delete(ids);
    }

    @RequestMapping("/setSelf")
    public PageResult setSelf(@RequestBody UserDTO userDTO) {
        return userService.setSelf(userDTO);
    }

    @RequestMapping("/revertPassword")
    public PageResult revertPassword(@RequestBody JSONObject jsonObject) {
        return userService.revertPassword(jsonObject);
    }

}
