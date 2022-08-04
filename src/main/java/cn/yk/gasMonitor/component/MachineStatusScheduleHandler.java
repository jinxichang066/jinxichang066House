package cn.yk.gasMonitor.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author Jin Xichang
 * @date 2022/8/4
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class MachineStatusScheduleHandler {

    private final MachineStatusCommandLineRunner machineStatusCommandLineRunner;

    private final MachineStatusHandler machineStatusHandler;

    // 设备端已关闭，或者设备端未关闭但是由于某些原因消息已经停止发送
    // 导致web端已经接收不到消息，但是状态还是在线
    // 作为websocket客户端，定时进行重连
    // 因为设备端假如断电后重启的话，是不会主动与客户端建立连接的
    @Scheduled(cron = "30 0/1 * * * ?")
    public void checkOldOnline() {
        log.info("检测设备脏在线定时任务开启");

        // web端在线，websocket也能连接，但是接收不到信息
        machineStatusHandler.checkOnlineWithoutMessage();

        // websocket重连
        machineStatusCommandLineRunner.clear();
        machineStatusCommandLineRunner.run();

        log.info("检测设备脏在线定时任务关闭");
    }

}
