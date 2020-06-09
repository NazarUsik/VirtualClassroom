package com.infostroy.usik.listener;

import com.infostroy.usik.modal.entity.ChatMessage;
import com.infostroy.usik.modal.entity.Student;
import com.infostroy.usik.modal.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;


/**
 * The WebSocket Event Listener class is used when creating a Socket connection and when it is disconnected.
 *
 * @author N. Usik
 */
@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private StudentService service;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    /**
     * Processing after creating a Socket connection.
     *
     * <p>Logs connection information</p>
     *
     * @param event socket connection event
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
    }

    /**
     * Processing before a Socket disconnection.
     *
     * <p>Logs disconnection information and
     * notification about the user who leaves the class, and about the remaining users</p>
     *
     * @param event socket disconnection event
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username != null) {
            logger.info("User Disconnected : " + username);

            StringBuilder builder = new StringBuilder();
            String delimiter = "";

            for (Student student : service.listAll()) {
                builder.append(delimiter).append(student.getName());
                delimiter = "|";
            }

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(username);

            messagingTemplate.convertAndSend("/topic/publicChatRoom", chatMessage);

            chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.USERS);
            chatMessage.setContent(builder.toString());

            messagingTemplate.convertAndSend("/topic/publicChatRoom", chatMessage);
        }
    }

}