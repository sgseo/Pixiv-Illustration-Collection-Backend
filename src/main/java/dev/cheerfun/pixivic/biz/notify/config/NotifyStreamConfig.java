package dev.cheerfun.pixivic.biz.notify.config;

import dev.cheerfun.pixivic.biz.notify.po.NotifyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import javax.annotation.PostConstruct;
import java.time.Duration;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 19-12-16 下午9:39
 * @description NotifyStreamConfig
 */
@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotifyStreamConfig {
    private final StreamListener streamListener;
    private final RedisConnectionFactory redisConnectionFactory;
    private final StringRedisTemplate stringRedisTemplate;
    private final static String NOTIFYEVENTSTREAMKEY = "n:e";
    private final static String NOTIFYEVENTSTREAMEMAILGROUP = "email";

    @PostConstruct
    public void init() {
        //stringRedisTemplate.opsForStream().createGroup(NOTIFYEVENTSTREAMKEY,"email");
        initNotifySetting();
        listenerNotifyEvent();
    }

    private void listenerNotifyEvent() {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, NotifyEvent>> containerOptions = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()/*.objectMapper(new Jackson2HashMapper(false))*/.targetType(NotifyEvent.class).pollTimeout(Duration.ofMillis(100)).build();
        StreamMessageListenerContainer<String, ObjectRecord<String, NotifyEvent>> container = StreamMessageListenerContainer.create(redisConnectionFactory,
                containerOptions);
        Subscription receive = container.receive(Consumer.from(NOTIFYEVENTSTREAMEMAILGROUP, NOTIFYEVENTSTREAMEMAILGROUP), StreamOffset.create(NOTIFYEVENTSTREAMKEY, ReadOffset.from(">")), streamListener);
        container.start();
    }

    private void initNotifySetting() {
        //从配置文件或数据库读取通知设定

    }

}
