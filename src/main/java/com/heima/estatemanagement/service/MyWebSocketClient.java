package com.heima.estatemanagement.service;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 描述
 *
 * @author Jin Xichang
 * @date 2022/5/13
 **/
@Slf4j
public class MyWebSocketClient extends WebSocketClient {

    private final static String URL_TEMPLATE = "ws://ip:7250/";

    public MyWebSocketClient(String url) throws URISyntaxException {
        super(new URI(URL_TEMPLATE.replace("ip", url)));
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info(">>>>>>>>>>>websocket open");
    }

    @Override
    public void onMessage(String s) {
        log.info(">>>>>>>>>> websocket message");
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        log.info(">>>>>>>>>>>websocket close");
    }

    @Override
    public void onError(Exception e) {
        log.error(">>>>>>>>>websocket error {}", e.getMessage());
    }


    public static boolean valid(String url) {
        MyWebSocketClient myClient = null;
        boolean open = false;
        try {
            myClient = new MyWebSocketClient(url);
            myClient.connect();
            Thread.sleep(2000);
            open = myClient.getReadyState().equals(READYSTATE.OPEN);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (myClient != null) {
                try {
                    myClient.close();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        return open;
    }
}