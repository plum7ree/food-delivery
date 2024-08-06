package com.example.websocketserver.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/queue", "/topic");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // websocket 은 gateway 가 아닌 자체적dls cors 체크
        registry.addEndpoint("/ws")
            .setAllowedOrigins("http://localhost:5173");
        registry.addEndpoint("/sockjs")
            .setAllowedOrigins("http://localhost:5173")
            .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                log.info("InboundChannel: {}", message.getHeaders());
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    log.info("websocket InboundChannel trying connect!");
                    //note: oauth2 sub 이 아니라 user db 의 uuid primary key
                    // String userId = accessor.getFirstNativeHeader("X-Auth-User-Id");
                    // log.info("userId : {}", userId);
                    // accessor.setUser(() -> userId);
                }
                return message;
            }
        });
    }
// 성공 했을 시 로그
//{simpMessageType=CONNECT, stompCommand=CONNECT,
// nativeHeaders={accept-version=[1.2,1.1,1.0], heart-beat=[10000,10000]},
// simpSessionAttributes={}, simpHeartbeat=[J@2d341aa5, simpSessionId=yez3g10l}
// websocket InboundChannel trying connect!
//InboundChannel: {simpMessageType=SUBSCRIBE, stompCommand=SUBSCRIBE,
// nativeHeaders={id=[sub-0], destination=[/user/queue/notifications]},
// simpSessionAttributes={}, simpHeartbeat=[J@4f546ed1, simpSubscriptionId=sub-0,
// simpSessionId=yez3g10l, simpDestination=/user/queue/notifications}
//InboundChannel: {simpMessageType=SUBSCRIBE, stompCommand=SUBSCRIBE,
// nativeHeaders={id=[sub-1], destination=[/user/topic/heartbeat]},
// simpSessionAttributes={}, simpHeartbeat=[J@3164d254, simpSubscriptionId=sub-1,
// simpSessionId=yez3g10l, simpDestination=/user/topic/heartbeat}
//WebSocketSession[1 current WS(1)-HttpStream(0)-HttpPoll(0), 1 total,
// 0 closed abnormally (0 connect failure, 0 send limit, 0 transport error)],
// stompSubProtocol[processed CONNECT(1)-CONNECTED(1)-DISCONNECT(0)],
// stompBrokerRelay[null], inboundChannel[pool size = 9, active threads = 0,
// queued tasks = 0, completed tasks = 9], outboundChannel[pool size = 1,
// active threads = 0, queued tasks = 0, completed tasks = 1],
// sockJsScheduler[pool size = 6, active threads = 1, queued tasks = 2,
// completed tasks = 2]


}