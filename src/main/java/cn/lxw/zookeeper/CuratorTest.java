package cn.lxw.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author xingweilin@clubfactory.com
 * @date 2021/1/28 7:45 下午
 */
public class CuratorTest {

    private static final String ZK_ADDRESS = "127.0.0.1:2181";

    private static final int SESSION_TIMEOUT = 5000;

    private static final int CONNECT_TIMEOUT = 10000;

    private static final String ZK_NODE = "/zk‐node-java";

    private static final String ZK_NODE_PROTECTION = "/zk‐node-java-protection";

    private static final String ZK_PARENT_NODE = "/parent/zk-node-java";

    private static final String ZK_NODE_ASYNC = "/zk‐node-java-async";

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

        createNode(client);

        createNodeWithProtection(client);

        createNodeWithParent(client);

        getData(client);

        changeData(client);

        deleteNode(client);

        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }

    private static void createNode(CuratorFramework client) throws Exception {
        String path = client.create().withMode(CreateMode.EPHEMERAL).forPath(ZK_NODE);
        System.out.println("curator create node: " + path);
    }

    /**
     *  protection模式，防止由于异常原因，导致僵尸节点存在，创建节点：/_c_f614f9ba-e45a-4b40-9f6e-a09ce3e4d900-zk‐node-java-protection0000000003
     *
     *  网络问题导致客户端未收到服务器创建成功回调，客户端重试创建节点
     *  客户端判断之前uuid是否已有节点存在，如果不存在，则创建，存在，直接返回成功
     */
    private static void createNodeWithProtection(CuratorFramework client) throws Exception {
        String path = client.create().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(ZK_NODE_PROTECTION, "protection".getBytes());
        System.out.println("curator create node: " + path);
    }

    private static void createNodeWithParent(CuratorFramework client) throws Exception {
        String path = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(ZK_PARENT_NODE);
        System.out.println("curator create path node: " + path);
    }

    private static void getData(CuratorFramework client) throws Exception {
        byte[] bytes = client.getData().forPath(ZK_NODE);
        System.out.println("get node data: " + new String(bytes));
    }

    private static void changeData(CuratorFramework client) throws Exception {
        byte[] bytes = client.getData().forPath(ZK_NODE);
        System.out.println("change node data before: " + new String(bytes));

        client.setData().forPath(ZK_NODE, "test data".getBytes());

        bytes = client.getData().forPath(ZK_NODE);
        System.out.println("change node data after: " + new String(bytes));
    }

    //guaranteed：只要客户端不断链接，curator会在后台继续发起删除请求，直到该数据节点被zookeeper删除
    //deletingChildrenIfNeeded：指定了该函数后，系统在删除该数据节点的时候会以递归的方式 直接删除其子节点，以及子节点的子节点
    private static void deleteNode(CuratorFramework client) throws Exception {
        client.delete().guaranteed().deletingChildrenIfNeeded().forPath(ZK_PARENT_NODE);
    }

    //异步处理默认在EventThread中执行
    public void testAsyncDealDefault(CuratorFramework client) throws Exception {
        client.getData().inBackground((item1, item2)-> System.out.println("background: " + item2)).forPath(ZK_NODE);
    }

    //异步处理指定线程池
    public void testAsyncDealThreadPool(CuratorFramework client) throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        client.getData().inBackground((item1, item2)-> System.out.println("background: " + item2), executorService).forPath(ZK_NODE);
    }


}
