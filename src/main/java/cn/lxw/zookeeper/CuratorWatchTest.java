package cn.lxw.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

/**
 * @author xingweilin@clubfactory.com
 * @date 2021/2/2 2:19 下午
 */
public class CuratorWatchTest {

    private static final String ZK_ADDRESS = "127.0.0.1:2181";

    private static final int SESSION_TIMEOUT = 5000;

    private static final int CONNECT_TIMEOUT = 10000;

    private static final String ZK_NODE = "/zk‐node-watch";

    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(ZK_ADDRESS)
                .sessionTimeoutMs(SESSION_TIMEOUT)
                .connectionTimeoutMs(CONNECT_TIMEOUT)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .canBeReadOnly(true)
                .build();

        client.getConnectionStateListenable().addListener((client1, newState)->{
            if(newState == ConnectionState.CONNECTED){
                System.out.println("链接成功！");
            }
        });

        System.out.println("连接中。。。。");
        client.start();

        byte[] bytes = client.getData().forPath(ZK_NODE);
        System.out.println(String.format("path %s init: %s", ZK_NODE, new String(bytes)));

        //监控节点数据变化
        nodeCache(client);

        pathChildrenCache(client);

        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }

    private static void nodeCache(CuratorFramework client) throws Exception {
        NodeCache nodeCache = new NodeCache(client, ZK_NODE);
        nodeCache.getListenable().addListener(() -> {
            byte[] bytes = client.getData().forPath(ZK_NODE);
            System.out.println(String.format("path %s init: %s", ZK_NODE, new String(bytes)));
        });

        nodeCache.start();
    }

    private static void pathChildrenCache(CuratorFramework client) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, ZK_NODE, true);
        pathChildrenCache.getListenable().addListener((client1, event) -> System.out.println(String.format("path %s child change %s", ZK_NODE, event.getType().name())));
        pathChildrenCache.start();
    }

    private static void treeCache(CuratorFramework client) throws Exception {
        TreeCache treeCache = new TreeCache(client, ZK_NODE);
        treeCache.getListenable().addListener((client1, event) -> System.out.println(String.format("path %s child change %s", ZK_NODE, event.getType().name())));
        treeCache.start();
    }
}
