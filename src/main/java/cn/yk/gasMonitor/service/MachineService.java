package cn.yk.gasMonitor.service;

import cn.yk.gasMonitor.common.MessageConstant;
import cn.yk.gasMonitor.common.PageResult;
import cn.yk.gasMonitor.common.StatusCode;
import cn.yk.gasMonitor.dao.MachineMapper;
import cn.yk.gasMonitor.domain.Machine;
import cn.yk.gasMonitor.dto.MachineSearchDTO;
import cn.yk.gasMonitor.websocket.WebSocketHandler;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
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
    @Resource
    private WebSocketHandler webSocketHandler;

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

    public PageResult findById(String id) {
        Machine machine = machineMapper.selectById(id);
        return new PageResult(true, StatusCode.OK, MessageConstant.MACHINE_FIND_BY_ID_SUCCESS, machine);

    }

    public PageResult add(Machine machine) {
        deleteWhiteSpace(machine);

        if (!validMachineUrl(machine.getMachineUrl())) {
            return new PageResult(true, StatusCode.OK, MessageConstant.MACHINE_URL_NOT_MATCH);
        }
        if (!validMachineUrl(machine.getDatabaseIp())) {
            return new PageResult(true, StatusCode.OK, MessageConstant.MACHINE_DATABASE_IP_NOT_MATCH);
        }

        LambdaQueryWrapper<Machine> wrapper = Wrappers.lambdaQuery(Machine.class).eq(Machine::getMachineId, machine.getMachineId());
        if (machineMapper.selectCount(wrapper) > 0) {
            return new PageResult(true, StatusCode.OK, MessageConstant.MACHINE_DUP_MACHINE_ID);
        } else {
            machine.setState(Machine.State.OFFLINE);
            machine.setCreateTime(new Date());
            machine.setMachineUrl(machine.getMachineUrl().trim());

            machineMapper.insert(machine);
            return new PageResult(true, StatusCode.OK, MessageConstant.MACHINE_ADD_SUCCESS);
        }
    }

    public PageResult update(Machine machine) {
        deleteWhiteSpace(machine);

        if (!validMachineUrl(machine.getMachineUrl())) {
            return new PageResult(true, StatusCode.OK, MessageConstant.MACHINE_URL_NOT_MATCH);
        }
        if (!validMachineUrl(machine.getDatabaseIp())) {
            return new PageResult(true, StatusCode.OK, MessageConstant.MACHINE_DATABASE_IP_NOT_MATCH);
        }

        LambdaQueryWrapper<Machine> wrapper = Wrappers.lambdaQuery(Machine.class).eq(Machine::getMachineId, machine.getMachineId()).ne(Machine::getId, machine.getId());
        if (machineMapper.selectCount(wrapper) > 0) {
            return new PageResult(true, StatusCode.OK, MessageConstant.MACHINE_DUP_MACHINE_ID_UPDATE);
        } else {
            machine.setModifyTime(new Date());
            machine.setMachineUrl(machine.getMachineUrl().trim());

            machineMapper.updateById(machine);
            return new PageResult(true, StatusCode.OK, MessageConstant.MACHINE_UPDATE_SUCCESS);
        }
    }

    private void deleteWhiteSpace(Machine machine) {
        machine.setMachineId(machine.getMachineId().trim());
        machine.setMachineUrl(machine.getMachineUrl().trim());
        machine.setDatabaseIp(machine.getDatabaseIp().trim());
        machine.setDatabaseName(machine.getDatabaseName().trim());
        machine.setDatabaseUser(machine.getDatabaseUser().trim());
        machine.setDatabasePassword(machine.getDatabasePassword().trim());
    }

    // 判断是否ip地址格式
    private boolean validMachineUrl(String url) {
        String regex = "^((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}$";

        return Pattern.matches(regex, url);
    }

    public PageResult delete(List<String> ids) {
        machineMapper.deleteBatchIds(ids);
        return new PageResult(true, StatusCode.OK, MessageConstant.MACHINE_DELETE_SUCCESS);
    }

    public List<Machine> loadAllMachine() {
        return machineMapper.selectList(Wrappers.lambdaQuery(Machine.class).orderByAsc(Machine::getCreateTime));
    }

    public Machine getMachine(String id) {
        return machineMapper.selectById(id);
    }

    public void updateMachine(Machine machine) {
        machineMapper.updateById(machine);
    }

    // 30秒check一次
    //@Scheduled(cron = "0/30 0/1 * * * ? ")
    //public void checkMachineState() {
    //    log.info("更新设备状态定时任务开始");
    //    List<Machine> machineList = loadAllMachine();
    //    if (CollectionUtil.isNotEmpty(machineList)) {
    //        String messageHead = "监测到设备状态变更:\n";
    //        String messageTail = "请刷新页面获取最新设备状态!";
    //        String message = "";
    //        for (Machine machine : machineList) {
    //            // 上线
    //            //boolean open = MachineStatusWebSocketClient.valid(machine.getMachineUrl());
    //            boolean open = false;
    //            if (open && machine.getState().equals(Machine.State.OFFLINE)) {
    //                machine.setState(Machine.State.ONLINE);
    //                machineMapper.updateById(machine);
    //                message += machine.getMachineName() + ":" + "离线->" + "在线" + "\n";
    //            } else if (!open && machine.getState().equals(Machine.State.ONLINE)) { // 下线
    //                machine.setState(Machine.State.OFFLINE);
    //                machineMapper.updateById(machine);
    //                message += machine.getMachineName() + ":" + "在线->" + "离线" + "\n";
    //            }
    //        }
    //        if (!StringUtils.isEmpty(message)) {
    //            log.info("有设备状态变更消息");
    //            webSocketHandler.sendToAll(messageHead + message + messageTail);
    //        } else {
    //            log.info("没有设备状态变更消息");
    //        }
    //    }
    //
    //    log.info("更新设备状态定时任务结束");
    //}

}
