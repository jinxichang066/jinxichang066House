package cn.yk.gasMonitor.controller;

import cn.yk.gasMonitor.auth.Auth;
import cn.yk.gasMonitor.common.MessageConstant;
import cn.yk.gasMonitor.common.PageResult;
import cn.yk.gasMonitor.common.StatusCode;
import cn.yk.gasMonitor.domain.Machine;
import cn.yk.gasMonitor.dto.MachineSearchDTO;
import cn.yk.gasMonitor.service.MachineService;
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
@RequestMapping("/machine")
public class MachineController {

    @Resource
    private MachineService machineService;

    @RequestMapping("/search")
    @Auth
    public PageResult search(HttpServletRequest request, @RequestBody JSONObject jsonObject) {
        IPage<Machine> page = machineService.search(jsonObject);
        return new PageResult(true, StatusCode.OK, MessageConstant.MACHINE_SEARCH_SUCCESS, page.getRecords(), page.getTotal());
    }

    @RequestMapping("/searchOneList")
    @Auth
    public PageResult searchOneList(HttpServletRequest request, @RequestBody JSONObject jsonObject) {
        IPage<Machine> page = machineService.searchOneList(jsonObject);
        return new PageResult(true, StatusCode.OK, MessageConstant.MACHINE_SEARCH_SUCCESS, page.getRecords(), page.getTotal());
    }

    @RequestMapping("/searchCondition")
    @Auth
    public PageResult searchCondition(HttpServletRequest request, @RequestBody MachineSearchDTO machineSearchDTO) {
        IPage<Machine> page = machineService.searchCondition(machineSearchDTO);
        return new PageResult(true, StatusCode.OK, MessageConstant.MACHINE_SEARCH_SUCCESS, page.getRecords(), page.getTotal());
    }

    @RequestMapping("/searchAll")
    @Auth
    public PageResult searchAll(HttpServletRequest request) {
        List<Machine> machineList = machineService.searchAll();
        return new PageResult(true, StatusCode.OK, MessageConstant.MACHINE_SEARCH_SUCCESS, machineList);
    }

    @RequestMapping("/selectByIds")
    public PageResult selectByIds(@RequestBody List<String> ids) {
        List<Machine> machineList = machineService.selectByIds(ids);
        return new PageResult(true, StatusCode.OK, MessageConstant.MACHINE_SEARCH_SUCCESS, machineList);
    }

    @RequestMapping("/findById")
    public PageResult findById(String id) {
        return machineService.findById(id);
    }

    @RequestMapping("/add")
    public PageResult add(@RequestBody Machine machine) {
        return machineService.add(machine);
    }

    @RequestMapping("/update")
    public PageResult update(@RequestBody Machine machine) {
        return machineService.update(machine);
    }

    @RequestMapping("/del")
    public PageResult del(@RequestBody List<String> ids) {
        return machineService.delete(ids);
    }

}
