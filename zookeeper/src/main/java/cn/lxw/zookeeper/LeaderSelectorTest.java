package cn.lxw.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author xingweilin@clubfactory.com
 * @date 2021/2/4 7:49 下午
 *
 * 利用curator分布式锁
 */
public class LeaderSelectorTest {

    private static final String ZK_ADDRESS = "127.0.0.1:2181";

    private static final int SESSION_TIMEOUT = 5000;

    private static final int CONNECT_TIMEOUT = 10000;

    private static final String ZK_NODE = "/zk‐node-leader";

    public static void main(String[] args) {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(ZK_ADDRESS)
                .sessionTimeoutMs(SESSION_TIMEOUT)
                .connectionTimeoutMs(CONNECT_TIMEOUT)
                .retryPolicy(new ExponentialBackoffRetry(1000, 5))
                .canBeReadOnly(true)
                .build();
        client.start();

        LeaderSelectorListener listener = new LeaderSelectorListenerAdapter() {
            @Override
            public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                System.out.println("I am the leader!");

                Thread.sleep(5000);
            }
        };


        LeaderSelector selector = new LeaderSelector(client, ZK_NODE, listener);
        selector.autoRequeue();
        selector.start();
    }
}
