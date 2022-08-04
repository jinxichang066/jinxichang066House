package cn.yk.gasMonitor.websocket;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class WebSocketHandler extends AbstractWebSocketHandler {

    private final Map<String, WebSocketSession> webSocketMap = new ConcurrentHashMap<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.info(message.toString());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        if (session.getUri() == null) {
            log.warn("session uri is empty");
            try {
                session.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            return;
        }
        String userName = StrUtil.subAfter(session.getUri().getPath(), "/", true);
        webSocketMap.put(userName, session);
        log.info("userName [{}] is online,current online size is {}", userName, webSocketMap.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userName = StrUtil.subAfter(Objects.requireNonNull(session.getUri()).toString(), "/", true);
        webSocketMap.remove(userName);
        log.info("userName [{}] is offline,current online size is {}", userName, webSocketMap.size());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WebSocketService Error:{}", exception.getMessage(), exception);
    }

    /**
     * 向当前在线的所有用户发送消息
     *
     * @param message 消息
     */
    public void sendToAll(String message) {
        try {
            for (String key : webSocketMap.keySet()) {
                try {
                    WebSocketSession session = webSocketMap.get(key);
                    if (session != null) {
                        session.sendMessage(new TextMessage(message));
                        log.info("send to " + key + " message:" + message);
                    } else {
                        log.info(key + ":不在线！");
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("webSocket消息发送异常", e);
        }
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void heartCheck() {
        log.info("web端设备状态websocket连接心跳检测开始");
        sendToAll("ping");

        log.info("web端设备状态websocket连接心跳检测结束");
    }

}
