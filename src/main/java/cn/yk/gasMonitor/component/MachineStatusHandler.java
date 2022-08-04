package cn.yk.gasMonitor.component;

import cn.yk.gasMonitor.domain.Machine;
import cn.yk.gasMonitor.service.MachineService;
import cn.yk.gasMonitor.websocket.WebSocketHandler;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备状态缓存
 *
 * @author Jin Xichang
 * @date 2022/8/4
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class MachineStatusHandler {

    private final String messageHead = "监测到设备状态变更:\n";
    private final String messageTail = "请刷新页面获取最新设备状态!";

    private final MachineService machineService;
    private final WebSocketHandler webSocketHandler;

    // 设备状态缓存
    private final Map<String, JSONObject> statusMap = new HashMap<>();

    public void loadAll() {
        List<Machine> machineList = machineService.loadAllMachine();
        statusMap.clear();
        for (Machine machine : machineList) {
            statusMap.put(machine.getId(), new JSONObject().fluentPut("time", System.currentTimeMillis()).fluentPut("status", machine.getState()));
        }
    }

    public void changeStatus(String message, String machineId) {
        String[] split = message.split("\\$");
        if (split.length == 4 && split[1].equals("devicestatus") && split[0].equals("read_data")) {
            JSONObject msgJson = JSONObject.parseObject(split[3]);
            JSONObject msg = msgJson.getJSONObject("Msg");
            Integer connected = msg.getInteger("Connected");
            Integer ptz = msg.getInteger("PTZ");

            JSONObject jsonObject = statusMap.get(machineId);
            Long time = jsonObject.getLong("time");
            Machine.State status = jsonObject.getObject("status", Machine.State.class);

            if (connected == 1 && ptz == 1) {
                if (status.equals(Machine.State.OFFLINE)) {
                    Machine machine = machineService.getMachine(machineId);
                    // 更新DB
                    machine.setState(Machine.State.ONLINE);
                    machine.setModifyTime(new Date());
                    machineService.updateMachine(machine);

                    // 更新缓存
                    statusMap.put(machineId, new JSONObject().fluentPut("time", System.currentTimeMillis()).fluentPut("status", Machine.State.ONLINE));

                    // 发送前端
                    String str = machine.getMachineName() + ":" + "离线->" + "在线" + "\n";
                    webSocketHandler.sendToAll(messageHead + str + messageTail);
                } else {
                    // 如果之前就是在线，则只更新时间
                    jsonObject.fluentPut("time", System.currentTimeMillis());
                    statusMap.put(machineId, jsonObject);
                }
            } else {
                if (status.equals(Machine.State.ONLINE)) {
                    Machine machine = machineService.getMachine(machineId);
                    // 更新DB
                    machine.setState(Machine.State.OFFLINE);
                    machine.setModifyTime(new Date());
                    machineService.updateMachine(machine);

                    // 更新缓存
                    statusMap.put(machineId, new JSONObject().fluentPut("time", System.currentTimeMillis()).fluentPut("status", Machine.State.OFFLINE));

                    // 发送前端
                    String str = machine.getMachineName() + ":" + "在线->" + "离线" + "\n";
                    webSocketHandler.sendToAll(messageHead + str + messageTail);
                }
            }
        }
    }

    public void offLine(String machineId) {
        JSONObject jsonObject = statusMap.get(machineId);
        Long time = jsonObject.getLong("time");
        Machine.State status = jsonObject.getObject("status", Machine.State.class);
        if (!status.equals(Machine.State.OFFLINE)) {
            Machine machine = machineService.getMachine(machineId);

            // 更新DB
            machine.setState(Machine.State.OFFLINE);
            machine.setModifyTime(new Date());
            machineService.updateMachine(machine);

            // 更新缓存
            statusMap.put(machineId, new JSONObject().fluentPut("time", System.currentTimeMillis()).fluentPut("status", Machine.State.OFFLINE));

            // 发送前端
            String str = machine.getMachineName() + ":" + "在线->" + "离线" + "\n";
            webSocketHandler.sendToAll(messageHead + str + messageTail);
        }
    }

    public void checkOnlineWithoutMessage() {
        for (Map.Entry<String, JSONObject> entry : statusMap.entrySet()) {
            String machineId = entry.getKey();
            JSONObject jsonObject = entry.getValue();
            Long time = jsonObject.getLong("time");
            Machine.State status = jsonObject.getObject("status", Machine.State.class);
            if (status.equals(Machine.State.ONLINE)) {
                // 如果30秒中设备在线状态时间都没有更新，即没有收到新的设备在线消息
                if (System.currentTimeMillis() - time > 30000) {
                    Machine machine = machineService.getMachine(machineId);
                    // 更新DB
                    machine.setState(Machine.State.OFFLINE);
                    machine.setModifyTime(new Date());
                    machineService.updateMachine(machine);

                    // 更新缓存
                    statusMap.put(machineId, new JSONObject().fluentPut("time", System.currentTimeMillis()).fluentPut("status", Machine.State.OFFLINE));

                    // 发送前端
                    String str = machine.getMachineName() + ":" + "在线->" + "离线" + "\n";
                    webSocketHandler.sendToAll(messageHead + str + messageTail);
                }
            }
        }
    }


}
