package cn.lxw.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author xingweilin@clubfactory.com
 * @date 2021/1/28 6:25 下午
 */
public class ZookeeperClientTest {

    private static final String ZK_ADDRESS = "127.0.0.1:2181";
    private static final int SESSION_TIMEOUT = 5000;
    private static ZooKeeper zooKeeper;
    private static final String ZK_NODE = "/zk‐node-java";
    private static final String ZK_NODE_ASYNC = "/zk‐node-java-async";

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        zooKeeper = new ZooKeeper(ZK_ADDRESS, SESSION_TIMEOUT, event -> {
            if (event.getState() == Watcher.Event.KeeperState.SyncConnected
                    && event.getType() == Watcher.Event.EventType.None) {
                countDownLatch.countDown();
                System.out.println("连接成功");
            }
        });
        System.out.println("连中。。。。。");
        countDownLatch.await();

        createNode();
        createAsyncNode();
        setData();

        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }

    private static void createNode() throws KeeperException, InterruptedException {
        String path = zooKeeper.create(ZK_NODE, "data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("create path:" + path);
    }

    //异步创建节点
    private static void createAsyncNode() {
        zooKeeper.create(ZK_NODE_ASYNC, "data_async".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
                (rc, path, ctx, name) -> {
                    Thread thread = Thread.currentThread();
                    System.out.println(String.format("thread name:%s rc %s,path %s,ctx %s,name %s, ", thread.getName(), rc, path, ctx, name));
                },
                "context");
        System.out.println("finish async");
    }

    private static void setData() throws KeeperException, InterruptedException {
        Stat stat = new Stat();
        byte[] data = zooKeeper.getData(ZK_NODE, false, stat);
        System.out.println("修改前：" + new String(data));

        zooKeeper.setData(ZK_NODE, "changed!".getBytes(), stat.getVersion());
        byte[] dataAfter = zooKeeper.getData(ZK_NODE, false, stat);
        System.out.println("修改后：" + new String(dataAfter));
    }
}
