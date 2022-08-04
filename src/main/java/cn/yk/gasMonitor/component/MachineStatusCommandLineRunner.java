package cn.yk.gasMonitor.component;

import cn.yk.gasMonitor.domain.Machine;
import cn.yk.gasMonitor.service.MachineService;
import cn.yk.gasMonitor.service.MachineStatusWebSocketClient;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * spring boot 启动完成之后执行
 */
@Slf4j
@Component
public class MachineStatusCommandLineRunner implements CommandLineRunner {

    @Autowired
    private MachineService machineService;
    @Autowired
    private MachineStatusHandler machineStatusHandler;

    private final List<MachineStatusWebSocketClient> clientList = new ArrayList<>();

    @Override
    public void run(String... args) {
        machineStatusHandler.loadAll();

        List<Machine> machineList = machineService.loadAllMachine();
        for (Machine machine : machineList) {
            try {
                MachineStatusWebSocketClient myClient = new MachineStatusWebSocketClient(machine.getMachineUrl(), machine.getId(), machineStatusHandler);
                myClient.connect();
                Thread.sleep(1000);
                boolean open = myClient.getReadyState().equals(WebSocket.READYSTATE.OPEN);
                if (!open) {
                    machineStatusHandler.offLine(machine.getId());
                } else {
                    clientList.add(myClient);
                }
            } catch (URISyntaxException | InterruptedException e) {
                log.error(e.getMessage(), e);
            }

        }
    }

    public void clear() {
        for (MachineStatusWebSocketClient client : clientList) {
            try {
                client.close();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        clientList.clear();
    }

}