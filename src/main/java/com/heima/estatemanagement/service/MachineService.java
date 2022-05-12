package com.heima.estatemanagement.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.estatemanagement.common.MessageConstant;
import com.heima.estatemanagement.common.Result;
import com.heima.estatemanagement.common.StatusCode;
import com.heima.estatemanagement.dao.MachineMapper;
import com.heima.estatemanagement.domain.Machine;
import com.heima.estatemanagement.dto.MachineSearchDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 描述
 *
 * @author Jin Xichang
 * @date 2022/4/20
 **/
@Service
public class MachineService {

    @Resource
    private MachineMapper machineMapper;

    public IPage<Machine> search(JSONObject jsonObject) {
        int pageNum = (int) jsonObject.get("pageNum");
        int pageSize = (int) jsonObject.get("pageSize");

        Page<Machine> page = new Page<>(pageNum, pageSize);
        return machineMapper.selectPage(page, Wrappers.lambdaQuery(Machine.class).orderByDesc(Machine::getState).orderByAsc(Machine::getCreateTime));
    }

    public IPage<Machine> searchCondition(MachineSearchDTO machineSearchDTO) {
        Page<Machine> page = new Page<>(machineSearchDTO.getPageNum(), machineSearchDTO.getPageSize());
        LambdaQueryWrapper<Machine> wrapper = Wrappers.lambdaQuery(Machine.class)
                .gt(machineSearchDTO.getStartTime() != null, Machine::getCreateTime, machineSearchDTO.getStartTime())
                .lt(machineSearchDTO.getEndTime() != null, Machine::getCreateTime, machineSearchDTO.getEndTime())
                .like(!StringUtils.isEmpty(machineSearchDTO.getName().trim()), Machine::getMachineName, machineSearchDTO.getName())
                .orderByDesc(Machine::getState)
                .orderByAsc(Machine::getCreateTime);
        return machineMapper.selectPage(page, wrapper);
    }

    public List<Machine> searchAll() {
        return machineMapper.selectList(Wrappers.lambdaQuery(Machine.class).orderByDesc(Machine::getState).orderByAsc(Machine::getCreateTime));
    }

    public List<Machine> selectByIds(List<String> ids) {
        return machineMapper.selectList(Wrappers.lambdaQuery(Machine.class).in(Machine::getId, ids).orderByDesc(Machine::getState).orderByAsc(Machine::getCreateTime));
    }

    public Result findById(String id) {
        Machine machine = machineMapper.selectById(id);
        return new Result(true, StatusCode.OK, MessageConstant.MACHINE_FIND_BY_ID_SUCCESS, machine);

    }

    public Result add(Machine machine) {
        LambdaQueryWrapper<Machine> wrapper = Wrappers.lambdaQuery(Machine.class).eq(Machine::getMachineId, machine.getMachineId());
        if (machineMapper.selectCount(wrapper) > 0) {
            return new Result(true, StatusCode.OK, MessageConstant.MACHINE_DUP_MACHINE_ID);
        } else {
            machine.setState(Machine.State.OFFLINE);
            machine.setCreateTime(new Date());

            machineMapper.insert(machine);
            return new Result(true, StatusCode.OK, MessageConstant.MACHINE_ADD_SUCCESS);
        }
    }

    public Result update(Machine machine) {
        LambdaQueryWrapper<Machine> wrapper = Wrappers.lambdaQuery(Machine.class).eq(Machine::getMachineId, machine.getMachineId()).ne(Machine::getId, machine.getId());
        if (machineMapper.selectCount(wrapper) > 0) {
            return new Result(true, StatusCode.OK, MessageConstant.MACHINE_DUP_MACHINE_ID_UPDATE);
        } else {
            machine.setModifyTime(new Date());

            machineMapper.updateById(machine);
            return new Result(true, StatusCode.OK, MessageConstant.MACHINE_UPDATE_SUCCESS);
        }


    }

    public Result delete(List<String> ids) {
        machineMapper.deleteBatchIds(ids);
        return new Result(true, StatusCode.OK, MessageConstant.MACHINE_DELETE_SUCCESS);
    }

}
