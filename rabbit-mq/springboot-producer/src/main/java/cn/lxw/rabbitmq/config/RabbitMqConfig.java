package cn.lxw.rabbitmq.config;

import com.google.common.collect.Maps;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author xingweilin@clubfactory.com
 * @date 2021/2/18 11:50 上午
 */
@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE_NAME = "boot_topic_exchange";

    public static final String QUEUE_NAME = "boot_queue";

    public static final String TTL_EXCHANGE_NAME = "ttl_exchange";

    public static final String TTL_QUEUE_NAME = "ttl_queue";

    public static final String DLX_SOURCE_EXCHANGE_NAME = "dlx_source_exchange";

    public static final String DLX_SOURCE_QUEUE_NAME = "dlx_source_queue";

    public static final String DLX_EXCHANGE_NAME = "dlx_exchange";

    public static final String DLX_QUEUE_NAME = "dlx_queue";

    public static final String DELAY_SOURCE_EXCHANGE_NAME = "delay_source_exchange";

    public static final String DELAY_SOURCE_QUEUE_NAME = "delay_source_queue";

    public static final String DELAY_EXCHANGE_NAME = "delay_exchange";

    public static final String DELAY_QUEUE_NAME = "delay_queue";

    /**
     * 声明交换机
     */
    @Bean("bootExchange")
    public Exchange bootExchange(){
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME).durable(true).build();
    }

    /**
     * 声明队列
     */
    @Bean("bootQueue")
    public Queue bootQueue(){
        return QueueBuilder.durable(QUEUE_NAME).build();
    }


    /**
     * 队列与交换机绑定
     */
    @Bean
    public Binding bindQueueExchange(@Qualifier("bootQueue") Queue queue, @Qualifier("bootExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("boot.#").noargs();
    }

    /**
     * 声明ttl交换机
     */
    @Bean("ttlExchange")
    public Exchange ttlExchange(){
        return ExchangeBuilder.topicExchange(TTL_EXCHANGE_NAME).durable(true).build();
    }

    /**
     * 声明ttl队列
     */
    @Bean("ttlQueue")
    public Queue ttlQueue(){
        Map<String, Object> map = Maps.newHashMap();
        map.put("x-max-length", 3000);
        map.put("x-message-ttl", 50 * 1000);
        return QueueBuilder.durable(TTL_QUEUE_NAME).withArguments(map).build();
    }

    /**
     * ttl队列与ttl交换机绑定
     */
    @Bean("ttlBinding")
    public Binding ttlBindQueueExchange(@Qualifier("ttlQueue") Queue queue, @Qualifier("ttlExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("ttl.#").noargs();
    }


    /**
     * 声明死信原始交换机
     */
    @Bean("dlxSourceExchange")
    public Exchange dlxSourceExchange(){
        return ExchangeBuilder.topicExchange(DLX_SOURCE_EXCHANGE_NAME).durable(true).build();
    }

    /**
     * 声明死信交换机
     */
    @Bean("dlxExchange")
    public Exchange dlxExchange(){
        return ExchangeBuilder.directExchange(DLX_EXCHANGE_NAME).durable(true).build();
    }

    /**
     * 声明死信原始队列
     */
    @Bean("dlxSourceQueue")
    public Queue dlxSourceQueue(){
        Map<String, Object> map = Maps.newHashMap();
        map.put("x-max-length", 2);
        map.put("x-message-ttl", 10 * 1000);

        //设置死信队列
        map.put("x-dead-letter-exchange", DLX_EXCHANGE_NAME);
        //正常队列路由到死信队列时到route key
        map.put("x-dead-letter-routing-key", "dlx.message");
        return QueueBuilder.durable(DLX_SOURCE_QUEUE_NAME).withArguments(map).build();
    }

    /**
     * 声明死信队列
     */
    @Bean("dlxQueue")
    public Queue dlxQueue(){
        return QueueBuilder.durable(DLX_QUEUE_NAME).build();
    }

    /**
     * 死信原始队列与死信原始交换机绑定
     */
    @Bean("dlxSourceBinding")
    public Binding dlxSourceBindQueueExchange(@Qualifier("dlxSourceQueue") Queue queue, @Qualifier("dlxSourceExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("dlx.#").noargs();
    }

    /**
     * 死信原始队列与死信原始交换机绑定
     */
    @Bean("dlxBinding")
    public Binding dlxBindQueueExchange(@Qualifier("dlxQueue") Queue queue, @Qualifier("dlxExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("dlx.message").noargs();
    }

    /**
     * 声明延时原始交换机
     */
    @Bean("delaySourceExchange")
    public Exchange delaySourceExchange(){
        return ExchangeBuilder.topicExchange(DELAY_SOURCE_EXCHANGE_NAME).durable(true).build();
    }

    /**
     * 声明死信交换机
     */
    @Bean("delayExchange")
    public Exchange delayExchange(){
        return ExchangeBuilder.directExchange(DELAY_EXCHANGE_NAME).durable(true).build();
    }

    /**
     * 声明死信原始队列
     */
    @Bean("delaySourceQueue")
    public Queue delaySourceQueue(){
        Map<String, Object> map = Maps.newHashMap();
        //设置死信队列
        map.put("x-dead-letter-exchange", DELAY_EXCHANGE_NAME);
        //正常队列路由到死信队列时到route key
        map.put("x-dead-letter-routing-key", "delay.message");
        return QueueBuilder.durable(DELAY_SOURCE_QUEUE_NAME).withArguments(map).build();
    }

    /**
     * 声明死信队列
     */
    @Bean("delayQueue")
    public Queue delayQueue(){
        return QueueBuilder.durable(DELAY_QUEUE_NAME).build();
    }

    /**
     * 死信原始队列与死信原始交换机绑定
     */
    @Bean("delaySourceBinding")
    public Binding delaySourceBindQueueExchange(@Qualifier("delaySourceQueue") Queue queue, @Qualifier("delaySourceExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("delay.#").noargs();
    }

    /**
     * 死信原始队列与死信原始交换机绑定
     */
    @Bean("delayBinding")
    public Binding delayBindQueueExchange(@Qualifier("delayQueue") Queue queue, @Qualifier("delayExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("delay.message").noargs();
    }

}
