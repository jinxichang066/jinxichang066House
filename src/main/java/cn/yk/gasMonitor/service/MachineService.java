package cn.yk.gasMonitor.service;

import cn.yk.gasMonitor.common.Result;
import cn.yk.gasMonitor.dao.MachineMapper;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.yk.gasMonitor.common.MessageConstant;
import cn.yk.gasMonitor.common.StatusCode;
import cn.yk.gasMonitor.domain.Machine;
import cn.yk.gasMonitor.dto.MachineSearchDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Jin Xichang
 * @date 2022/4/20
 **/
@Slf4j
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

    public IPage<Machine> searchOneList(JSONObject jsonObject) {
        IPage<Machine> list = new Page<>();

        Integer total = machineMapper.selectCount(Wrappers.lambdaQuery(Machine.class));
        list.setTotal(total);

        Machine machine = machineMapper.selectById(jsonObject.getString("id"));
        if (Objects.isNull(machine)) {
            list.setRecords(new ArrayList<>());
        } else {
            list.setRecords(Collections.singletonList(machine));
        }

        return list;
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
        if (!validMachineUrl(machine.getMachineUrl())) {
            return new Result(true, StatusCode.OK, MessageConstant.MACHINE_URL_NOT_MATCH);
        }

        LambdaQueryWrapper<Machine> wrapper = Wrappers.lambdaQuery(Machine.class).eq(Machine::getMachineId, machine.getMachineId());
        if (machineMapper.selectCount(wrapper) > 0) {
            return new Result(true, StatusCode.OK, MessageConstant.MACHINE_DUP_MACHINE_ID);
        } else {
            machine.setState(Machine.State.OFFLINE);
            machine.setCreateTime(new Date());
            machine.setMachineUrl(machine.getMachineUrl().trim());

            machineMapper.insert(machine);
            return new Result(true, StatusCode.OK, MessageConstant.MACHINE_ADD_SUCCESS);
        }
    }

    public Result update(Machine machine) {
        if (!validMachineUrl(machine.getMachineUrl())) {
            return new Result(true, StatusCode.OK, MessageConstant.MACHINE_URL_NOT_MATCH);
        }

        LambdaQueryWrapper<Machine> wrapper = Wrappers.lambdaQuery(Machine.class).eq(Machine::getMachineId, machine.getMachineId()).ne(Machine::getId, machine.getId());
        if (machineMapper.selectCount(wrapper) > 0) {
            return new Result(true, StatusCode.OK, MessageConstant.MACHINE_DUP_MACHINE_ID_UPDATE);
        } else {
            machine.setModifyTime(new Date());
            machine.setMachineUrl(machine.getMachineUrl().trim());

            machineMapper.updateById(machine);
            return new Result(true, StatusCode.OK, MessageConstant.MACHINE_UPDATE_SUCCESS);
        }
    }

    // 设备url判断是否ip地址格式
    private boolean validMachineUrl(String url) {
        String regex = "^((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}$";

        return Pattern.matches(regex, url);
    }

    public Result delete(List<String> ids) {
        machineMapper.deleteBatchIds(ids);
        return new Result(true, StatusCode.OK, MessageConstant.MACHINE_DELETE_SUCCESS);
    }

    public List<Machine> loadAllMachine() {
        return machineMapper.selectList(Wrappers.lambdaQuery(Machine.class).orderByAsc(Machine::getCreateTime));
    }

    @Scheduled(cron = "0 0/3 * * * ?")
    public void checkMachineState() {
        log.info("更新设备状态定时任务开始");
        List<Machine> machineList = loadAllMachine();
        if (CollectionUtil.isNotEmpty(machineList)) {
            for (Machine machine : machineList) {
                // 上线
                boolean open = MyWebSocketClient.valid(machine.getMachineUrl());
                if (open && machine.getState().equals(Machine.State.OFFLINE)) {
                    machine.setState(Machine.State.ONLINE);
                    machineMapper.updateById(machine);
                } else if (!open && machine.getState().equals(Machine.State.ONLINE)) { // 下线
                    machine.setState(Machine.State.OFFLINE);
                    machineMapper.updateById(machine);
                }
            }
        }
        log.info("更新设备状态定时任务结束");
    }

}
