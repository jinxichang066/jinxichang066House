package cn.yk.gasMonitor.controller;

import cn.yk.gasMonitor.auth.Auth;
import cn.yk.gasMonitor.common.MessageConstant;
import cn.yk.gasMonitor.common.PageResult;
import cn.yk.gasMonitor.common.Result;
import cn.yk.gasMonitor.common.StatusCode;
import cn.yk.gasMonitor.domain.Machine;
import cn.yk.gasMonitor.domain.User;
import cn.yk.gasMonitor.dto.MachineSearchDTO;
import cn.yk.gasMonitor.dto.UserSearchDTO;
import cn.yk.gasMonitor.service.MachineService;
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
    public PageResult searchCondition(@RequestBody UserSearchDTO userSearchDTO) {
        IPage<User> page = userService.searchCondition(userSearchDTO);
        return new PageResult(true, StatusCode.OK, MessageConstant.USER_SEARCH_SUCCESS, page.getRecords(), page.getTotal());
    }

    //@RequestMapping("/searchAll")
    //@Auth
    //public Result searchAll(HttpServletRequest request) {
    //    List<Machine> machineList = machineService.searchAll();
    //    return new Result(true, StatusCode.OK, MessageConstant.USER_SEARCH_SUCCESS, machineList);
    //}
    //
    //@RequestMapping("/selectByIds")
    //public Result selectByIds(@RequestBody List<String> ids) {
    //    List<Machine> machineList = machineService.selectByIds(ids);
    //    return new Result(true, StatusCode.OK, MessageConstant.MACHINE_SEARCH_SUCCESS, machineList);
    //}
    //
    //@RequestMapping("/findById")
    //public Result findById(String id) {
    //    return machineService.findById(id);
    //}
    //
    //@RequestMapping("/add")
    //public Result add(@RequestBody Machine machine) {
    //    return machineService.add(machine);
    //}
    //
    //@RequestMapping("/update")
    //public Result update(@RequestBody Machine machine) {
    //    return machineService.update(machine);
    //}
    //
    //@RequestMapping("/del")
    //public Result del(@RequestBody List<String> ids) {
    //    return machineService.delete(ids);
    //}

}
