package cn.lxw.rabbitmq.routing;

import cn.lxw.rabbitmq.CommonConnect;
import com.google.common.collect.Maps;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author xingweilin@clubfactory.com
 * @date 2021/2/12 10:32 上午
 */
public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = CommonConnect.createConnect();
        Channel channel = connection.createChannel();

        Map<String, String> messageMap = Maps.newHashMap();
        messageMap.put("zj.cangnan.0214", "苍南天气0214天气晴");
        messageMap.put("zj.wenzhou.0214", "温州天气0214天气晴");
        messageMap.put("zj.ruian.0214", "瑞安天气0214天气晴");
        messageMap.put("fj.xiamen.0214", "厦门天气0214天气晴");

        messageMap.put("zj.cangnan.0215", "苍南天气0215天气晴");
        messageMap.put("zj.wenzhou.0215", "温州天气0215天气晴");
        messageMap.put("zj.ruian.0215", "瑞安天气0215天气晴");
        messageMap.put("fj.xiamen.0215", "厦门天气0215天气晴");
        messageMap.forEach((key, value) -> {
            try {
                //路由模型下需要指定route key
                channel.basicPublish(CommonConnect.EXCHANGE_WEATHER_ROUTING, key, null, value.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });



        channel.close();
        connection.close();
    }
}
