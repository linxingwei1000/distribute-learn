package cn.lxw.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author xingweilin@clubfactory.com
 * @date 2021/2/2 7:16 下午
 */
public class ZookeeperClusterTest {

    private static final String ZK_ADDRESS = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183,127.0.0.1:2184";
    private static final int SESSION_TIMEOUT = 5000;
    private static ZooKeeper zooKeeper;

    private static final String CONFIG_NODE = "/zookeeper/config";

    public static void main(String[] args) throws InterruptedException, IOException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        zooKeeper = new ZooKeeper(ZK_ADDRESS, SESSION_TIMEOUT, watchClusterChange(countDownLatch));
        System.out.println("连中。。。。。");
        countDownLatch.await();

        tryConnectTimeout();

        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }

    /**
     * zookeeper 原生代码会进行重连，不过会有一段时间超时
     */
    private static void tryConnectTimeout() {
        while (true) {
            try {
                Stat stat = new Stat();
                byte[] bytes = zooKeeper.getData("/zookeeper/config", false, stat);
                System.out.println("config data: " + new String(bytes));
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();

                System.out.println("开始重连");

                while (true) {
                    System.out.println("zookeeper status:  " + zooKeeper.getState().name());
                    if (zooKeeper.getState().isConnected()) {
                        break;
                    }
                }
            }

        }
    }

    /**
     * 动态监听集群变化，更新集群列表
     *
     * 配合指令：reconfig -remove 1  && reconfig -add server.1==127.0.0.1:2001:3001:participant;127.0.0.1:2181
     * @param countDownLatch
     * @return
     */
    private static Watcher watchClusterChange(CountDownLatch countDownLatch) {
        return new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getType() == Event.EventType.None && event.getState() == Event.KeeperState.SyncConnected) {
                    countDownLatch.countDown();
                    System.out.println(" 连接成功");

                    System.out.println("开始监听：" + CONFIG_NODE);
                    try {
                        zooKeeper.getConfig(true, null);
                    } catch (KeeperException | InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (event.getPath() != null && event.getPath().equals(CONFIG_NODE)) {
                    try {
                        byte[] config = zooKeeper.getConfig(this, null);
                        String clientConfigInfo = new String(config);
                        System.out.println("配置发生变化：" + clientConfigInfo);

                        zooKeeper.updateServerList(clientConfigInfo.split(" ")[1]);
                    } catch (KeeperException | InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };


    }
}
