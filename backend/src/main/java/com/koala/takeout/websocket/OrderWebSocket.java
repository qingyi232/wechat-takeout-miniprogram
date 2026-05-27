package com.koala.takeout.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/ws/order/{merchantId}")
public class OrderWebSocket {

    private static final ConcurrentHashMap<Long, Session> merchantSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("merchantId") Long merchantId) {
        merchantSessions.put(merchantId, session);
        log.info("商家[{}]WebSocket连接已建立", merchantId);
    }

    @OnClose
    public void onClose(@PathParam("merchantId") Long merchantId) {
        merchantSessions.remove(merchantId);
        log.info("商家[{}]WebSocket连接已关闭", merchantId);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket错误: {}", error.getMessage());
    }

    @OnMessage
    public void onMessage(String message, @PathParam("merchantId") Long merchantId) {
        log.info("收到商家[{}]消息: {}", merchantId, message);
    }

    public static void sendToMerchant(Long merchantId, String message) {
        Session session = merchantSessions.get(merchantId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
                log.info("向商家[{}]推送消息: {}", merchantId, message);
            } catch (IOException e) {
                log.error("消息推送失败: {}", e.getMessage());
            }
        }
    }
}
