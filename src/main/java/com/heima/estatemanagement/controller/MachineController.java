package com.heima.estatemanagement.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.heima.estatemanagement.common.MessageConstant;
import com.heima.estatemanagement.common.PageResult;
import com.heima.estatemanagement.common.Result;
import com.heima.estatemanagement.common.StatusCode;
import com.heima.estatemanagement.domain.Machine;
import com.heima.estatemanagement.dto.MachineSearchDTO;
import com.heima.estatemanagement.service.MachineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 描述
 *
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
    public PageResult search(@RequestBody JSONObject jsonObject) {
        IPage<Machine> page = machineService.search(jsonObject);
        return new PageResult(true, StatusCode.OK, MessageConstant.MACHINE_SEARCH_SUCCESS, page.getRecords(), page.getTotal());
    }

    @RequestMapping("/searchCondition")
    public PageResult searchCondition(@RequestBody MachineSearchDTO machineSearchDTO) {
        IPage<Machine> page = machineService.searchCondition(machineSearchDTO);
        return new PageResult(true, StatusCode.OK, MessageConstant.MACHINE_SEARCH_SUCCESS, page.getRecords(), page.getTotal());
    }

    @RequestMapping("/searchAll")
    public Result searchAll() {
        List<Machine> machineList = machineService.searchAll();
        return new Result(true, StatusCode.OK, MessageConstant.MACHINE_SEARCH_SUCCESS, machineList);
    }

    @RequestMapping("/selectByIds")
    public Result selectByIds(@RequestBody List<String> ids) {
        List<Machine> machineList = machineService.selectByIds(ids);
        return new Result(true, StatusCode.OK, MessageConstant.MACHINE_SEARCH_SUCCESS, machineList);
    }

    @RequestMapping("/findById")
    public Result findById(String id) {
        return machineService.findById(id);
    }

    @RequestMapping("/add")
    public Result add(@RequestBody Machine machine) {
        return machineService.add(machine);
    }

    @RequestMapping("/update")
    public Result update(@RequestBody Machine machine) {
        return machineService.update(machine);
    }

    @RequestMapping("/del")
    public Result del(@RequestBody List<String> ids) {
        return machineService.delete(ids);
    }

}
