package com.infostroy.usik.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * The HttpHandshakeInterceptor class is used to resolve events immediately before
 * and after WebSocket shook hands with HTTP.
 *
 * <p>The existing infrastructure can create restrictions for deploying WebSocket, usually HTTP uses port 80 & 443,
 * so WebSocket must use other ports, with almost all Firewall preventing ports other than 80 & 443,
 * using Proxy (authorized) different problems also occur. Therefore, to be easily deployed,
 * WebSocket uses HTTP Handshake to upgrade.
 * This means the first time the client sends an HTTP-based request to the server,
 * tells the server that it is not HTTP, update it to WebSocket, so they form a connection.</p>
 *
 * @author N. Usik
 */
@Component
public class HttpHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(HttpHandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        logger.info("Call beforeHandshake");

        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession();
            attributes.put("sessionId", session.getId());
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception ex) {
        logger.info("Call afterHandshake");
    }

}