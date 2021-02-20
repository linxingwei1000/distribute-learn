package cn.lxw.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author xingweilin@clubfactory.com
 * @date 2021/2/12 10:32 上午
 */
public class CommonConnect {

    public static final String SIMPLE_QUEUE = "lxw.simple";

    public static final String WORK_QUEUE = "lxw.work.queue";

    public static final String EXCHANGE_QUEUE_ONE = "exchange.queue.one";

    public static final String EXCHANGE_QUEUE_TWO = "exchange.queue.two";

    public static final String EXCHANGE_WEATHER = "weather";

    public static final String EXCHANGE_WEATHER_ROUTING = "weather_route";

    public static final String EXCHANGE_WEATHER_TOPIC = "weather_topic";



    public static Connection createConnect() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("60.190.138.133");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");

        //创建连接
        return connectionFactory.newConnection();
    }
}
