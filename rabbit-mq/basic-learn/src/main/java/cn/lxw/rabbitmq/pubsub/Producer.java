package cn.lxw.rabbitmq.pubsub;

import cn.lxw.rabbitmq.CommonConnect;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author xingweilin@clubfactory.com
 * @date 2021/2/12 10:32 上午
 */
public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = CommonConnect.createConnect();
        Channel channel = connection.createChannel();

        channel.basicPublish(CommonConnect.EXCHANGE_WEATHER, "", null, "苍南今天天气真好".getBytes());

        channel.close();
        connection.close();
    }
}
