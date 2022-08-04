package cn.yk.gasMonitor.service;

import cn.yk.gasMonitor.component.MachineStatusHandler;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Jin Xichang
 * @date 2022/5/13
 **/
@Slf4j
public class MachineStatusWebSocketClient extends org.java_websocket.client.WebSocketClient {

    private final static String URL_TEMPLATE = "ws://ip:7250/";

    private final String machineId;

    private final MachineStatusHandler machineStatusHandler;

    public MachineStatusWebSocketClient(String url, String machineId, MachineStatusHandler machineStatusHandler) throws URISyntaxException {
        super(new URI(URL_TEMPLATE.replace("ip", url)));
        this.machineId = machineId;
        this.machineStatusHandler = machineStatusHandler;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info(">>>>>>>>>>>websocket open");
        send("create_subscriber$DeviceStatus$G2021001${\"Msg\":{\"TopicName\":\"WarningMsg\",\"TopicDomain\":\"1001\",\"TopicSource\":\"G2021001\"},\"TimeStamp\":\"20210220214742\"}");
    }

    @Override
    public void onMessage(String message) {
        log.info("设备状态message：{}", message);
        machineStatusHandler.changeStatus(message, machineId);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        log.info(">>>>>>>>>>>websocket close");
    }

    @Override
    public void onError(Exception e) {
        log.error(">>>>>>>>>websocket error {}", e.getMessage());
    }

}