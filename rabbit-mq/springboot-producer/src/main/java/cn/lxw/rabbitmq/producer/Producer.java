package cn.lxw.rabbitmq.producer;

import cn.lxw.rabbitmq.config.RabbitMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author xingweilin@clubfactory.com
 * @date 2021/2/18 3:33 下午
 */
@Slf4j
//@Component
public class Producer implements InitializingBean {

    /**
     * 注入调用方法
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            log.info("---------confirm方法被执行了。。。。。。");

            if (ack) {
                log.info("---------接收消息成功：" + cause);
            } else {
                log.info("---------接收消息失败：" + cause);
                //做一些处理，比如消息的重发
            }
        });
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME + 123, "boot123.hhhhh", "boot mq............");

        Thread.sleep(5000);

        //设置该参数=true，消息才会回退
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.info("---------return方法被执行了。。。。。。");

            log.info("---------message:{}", message);
            log.info("---------replyCode:{}", replyCode);
            log.info("---------replyText:{}", replyText);
            log.info("---------exchange:{}", exchange);
            log.info("---------routingKey:{}", routingKey);

            //做一些处理，比如消息的重发
        });

        //测试消息投递失败
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, "boot123.hhhhh", "boot mq............");
    }
}
