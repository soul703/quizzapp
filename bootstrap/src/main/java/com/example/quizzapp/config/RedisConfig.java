package com.example.quizzapp.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.example.quizzapp.infrastructure.redis.AuthenticationEventListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * Bean này cấu hình RedisTemplate để sử dụng JSON serializer thay vì mặc định.
     * Điều này giúp dữ liệu trong Redis dễ đọc và tương thích giữa các hệ thống.
     * @param connectionFactory Spring Boot sẽ tự động cung cấp bean này từ application.yml
     * @return một RedisTemplate được cấu hình đầy đủ.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Sử dụng String serializer cho keys
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Sử dụng Jackson JSON serializer cho values
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    /**
     * Cung cấp một ObjectMapper bean để serialize/deserialize các sự kiện.
     * Thêm JavaTimeModule để xử lý đúng các kiểu dữ liệu thời gian của Java 8+ (Instant, LocalDateTime).
     */
    @Bean
    @Primary // Vẫn nên giữ @Primary để đảm bảo đây là ObjectMapper mặc định
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Chỉ cần đăng ký module để xử lý các kiểu dữ liệu thời gian
        mapper.registerModule(new JavaTimeModule());

        // KHÔNG CÓ CẤU HÌNH activateDefaultTyping NỮA.
        // Điều này làm cho ObjectMapper trở nên đơn giản và dễ đoán.

        return mapper;
    }


    // --- Cấu hình cho Pub/Sub ---

    /**
     * Định nghĩa topic (channel) cho các sự kiện xác thực.
     */
    @Bean
    ChannelTopic authenticationEventsTopic() {
        return new ChannelTopic("authentication-events");
    }

    /**
     * Adapter để kết nối Listener của chúng ta với Redis.
     * Nó chỉ định rằng khi có message, hãy gọi phương thức "handleAuthEvent" trên bean "listener".
     * @param listener Bean AuthenticationEventListener sẽ được inject vào đây.
     */
    @Bean
    MessageListenerAdapter authEventListenerAdapter(AuthenticationEventListener listener) {
        return new MessageListenerAdapter(listener, "handleAuthEvent");
    }

    /**
     * Container chính để quản lý việc lắng nghe các message từ Redis.
     * Nó sẽ chạy ở một luồng nền riêng biệt.
     * @param connectionFactory
     * @param authEventListenerAdapter
     * @return
     */
    @Bean
    RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
                                                 MessageListenerAdapter authEventListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // Đăng ký listener adapter để lắng nghe trên topic đã định nghĩa
        container.addMessageListener(authEventListenerAdapter, authenticationEventsTopic());
        // Có thể thêm các listener khác cho các topic khác ở đây
        // container.addMessageListener(anotherAdapter, anotherTopic());
        return container;
    }
}