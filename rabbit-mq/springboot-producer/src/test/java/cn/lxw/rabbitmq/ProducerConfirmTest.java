package cn.lxw.rabbitmq;

import cn.lxw.rabbitmq.config.RabbitMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author xingweilin@clubfactory.com
 * @date 2021/2/18 2:45 下午
 */
@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)
public class ProducerConfirmTest {

    /**
     * 注入调用方法
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testConfirm() {
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            log.info("confirm方法被执行了。。。。。。");

            if (ack) {
                log.info("接收消息成功：" + cause);
            } else {
                log.info("接收消息失败：" + cause);
                //做一些处理，比如消息的重发
            }
        });

        //测试消息投递失败
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME + "123", "boot.hhhhh", "boot mq............");

        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReturn() {

        //设置该参数=true，消息才会回退
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.info("return方法被执行了。。。。。。");

            log.info("message:{}", message);
            log.info("replyCode:{}", replyCode);
            log.info("replyText:{}", replyText);
            log.info("exchange:{}", exchange);
            log.info("routingKey:{}", routingKey);

            //做一些处理，比如消息的重发
        });

        //测试消息投递失败
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, "boot123.hhhhh", "boot mq............");

        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
