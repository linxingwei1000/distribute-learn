package cn.lxw.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author xingweilin@clubfactory.com
 * @date 2021/2/2 8:15 下午
 */
public class ZookeeperBean {

    private volatile static ZookeeperBean zookeeperBean;


    private String ZK_ADDRESS = "127.0.0.1:2181";
    private int SESSION_TIMEOUT = 10000;
    private ZooKeeper zooKeeper;

    private static final String WRITE_SING = "write-";

    private static final String READ_SING = "read-";

    private static final Object SYNC_SIGN = new Object();

    public static ZookeeperBean getInstance() {
        if (zookeeperBean == null) {
            synchronized (SYNC_SIGN) {
                if (zookeeperBean == null) {
                    zookeeperBean = new ZookeeperBean();
                }
            }
        }
        return zookeeperBean;
    }

    public ZookeeperBean() {
        try {
            init();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private void init() throws InterruptedException, IOException {
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
    }


    /**
     * 判断是否有节点
     */
    public Boolean isExistNode(String nodePath) {
        boolean result = false;
        try {
            Stat stat = zooKeeper.exists(nodePath, false);
            result = stat != null;
        } catch (KeeperException | InterruptedException ignored) {
        }
        return result;
    }

    /**
     * 创建节点
     */
    public Boolean createNode(String nodePath) {
        boolean result = false;
        try {
            String path = zooKeeper.create(nodePath, "data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("create path:" + path);
            result = true;
        } catch (KeeperException | InterruptedException ignored) {
        }
        return result;
    }

    /**
     * 创建临时节点
     */
    public Boolean createEphemeralNode(String nodePath) {
        boolean result = false;
        try {
            String path = zooKeeper.create(nodePath, "data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("create path:" + path);
            result = true;
        } catch (KeeperException | InterruptedException ignored) {
        }
        return result;
    }

    /**
     * 创建容器节点
     */
    public Boolean createContainerNode(String nodePath) {
        boolean result = false;
        try {
            String path = zooKeeper.create(nodePath, "data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.CONTAINER);
            System.out.println("create path:" + path);
            result = true;
        } catch (KeeperException | InterruptedException ignored) {
        }
        return result;
    }

    /**
     * 创建临时顺序子节点
     */
    public String createEphemeralSequentialNode(String nodePath) {
        String result = null;
        try {
            String path = zooKeeper.create(nodePath, "data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println("create path:" + path);
            result = path;
        } catch (KeeperException | InterruptedException ignored) {
        }
        return result;
    }

    public Boolean deleteNode(String nodePath) {
        boolean result = false;
        try {
            Stat stat = new Stat();
            zooKeeper.getData(nodePath, false, stat);
            System.out.println("delete node:" + nodePath);
            zooKeeper.delete(nodePath, stat.getVersion());
            result = true;
        } catch (KeeperException | InterruptedException ignored) {
        }
        return result;
    }

    public void createUnFairDistributeLock(String nodePath, Integer timeoutSecond) {
        while (true) {
            if (isExistNode(nodePath)) {
                final CountDownLatch latch = new CountDownLatch(1);
                Watcher watcher = event -> {
                    System.out.println("非公平分布式锁被释放，event=" + event.getType().name());
                    latch.countDown();
                };

                try {
                    zooKeeper.getData(nodePath, watcher, new Stat());
                    if (timeoutSecond == null || timeoutSecond == 0) {
                        latch.await();
                    } else {
                        latch.await(timeoutSecond, TimeUnit.SECONDS);
                    }
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                //创建锁失败，再次争夺锁资源
                if (createEphemeralNode(nodePath)) {
                    break;
                }
            }
        }
    }


    public void createFairDistributeLock(String nodePath, Integer timeoutSecond) {
        //不存在，创建容器节点
        if (!isExistNode(nodePath)) {
            createContainerNode(nodePath);
        }

        final CountDownLatch latch = new CountDownLatch(1);
        Watcher watcher = event -> {
            System.out.println("公平分布式锁被释放，event=" + event.getType().name());
            latch.countDown();
        };

        String parentNode = nodePath + "/";
        String curNode = createEphemeralSequentialNode(parentNode);

        try {
            List<String> list = zooKeeper.getChildren(nodePath, false, new Stat());
            list.sort(String::compareTo);

            String childName = curNode.substring(curNode.lastIndexOf("/") + 1);
            int seat = list.indexOf(childName);
            //获取锁
            if (seat != 0) {
                String preNode = list.get(seat - 1);
                zooKeeper.getData(nodePath + "/" + preNode, watcher, new Stat());

                if (timeoutSecond == null || timeoutSecond == 0) {
                    latch.await();
                } else {
                    latch.await(timeoutSecond, TimeUnit.SECONDS);
                }
            }
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void releaseFairDistributeLock(String nodePath) {
        try {
            List<String> list = zooKeeper.getChildren(nodePath, false, new Stat());
            list.sort(String::compareTo);
            String preNode = list.get(0);
            deleteNode(nodePath + "/" + preNode);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createReadDistributeLock(String nodePath, Integer timeoutSecond) {
        //不存在，创建容器节点
        if (!isExistNode(nodePath)) {
            createContainerNode(nodePath);
        }

        final CountDownLatch latch = new CountDownLatch(1);
        Watcher watcher = event -> {
            System.out.println("分布式锁写锁释放，event=" + event.getType().name());
            latch.countDown();
        };

        String parentNode = nodePath + "/" + READ_SING;
        String curNode = createEphemeralSequentialNode(parentNode);

        try {
            List<String> list = zooKeeper.getChildren(nodePath, false, new Stat());
            List<SequentialStr> sequentialList = dealResource(list);

            boolean needLock = false;
            String lockPreNode = null;
            boolean isFindSelf = false;

            String childName = curNode.substring(parentNode.length());
            for (SequentialStr sequentialStr : sequentialList) {
                if (sequentialStr.getSequential().equals(childName)) {
                    isFindSelf = true;
                } else {
                    if (sequentialStr.getResourceStr().contains(WRITE_SING) && !isFindSelf) {
                        needLock = true;
                        lockPreNode = sequentialStr.getResourceStr();
                    }
                }
            }

            //获取锁
            if (needLock) {
                zooKeeper.getData(nodePath + "/" + lockPreNode, watcher, new Stat());

                if (timeoutSecond == null || timeoutSecond == 0) {
                    latch.await();
                } else {
                    latch.await(timeoutSecond, TimeUnit.SECONDS);
                }
            }

            System.out.println("执行读锁逻辑：" + curNode);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createWriteDistributeLock(String nodePath, Integer timeoutSecond) {
        //不存在，创建容器节点
        if (!isExistNode(nodePath)) {
            createContainerNode(nodePath);
        }

        final CountDownLatch latch = new CountDownLatch(1);
        Watcher watcher = event -> {
            System.out.println("分布式锁被释放，event=" + event.getType().name() + " path=" + event.getPath());
            latch.countDown();
        };

        String parentNode = nodePath + "/" + WRITE_SING;
        String curNode = createEphemeralSequentialNode(parentNode);

        try {
            List<String> list = zooKeeper.getChildren(nodePath, false, new Stat());
            List<SequentialStr> sequentialList = dealResource(list);

            boolean needLock = false;
            String lockPreNode = null;

            String childName = curNode.substring(parentNode.length());
            for (int seat = 0; seat < sequentialList.size(); seat++) {
                SequentialStr sequentialStr = sequentialList.get(seat);
                if (sequentialStr.getSequential().equals(childName)) {
                    if (seat != 0) {
                        needLock = true;
                        lockPreNode = sequentialList.get(seat - 1).getResourceStr();
                        break;
                    }
                }
            }
            //获取锁
            if (needLock) {
                zooKeeper.getData(nodePath + "/" + lockPreNode, watcher, new Stat());

                if (timeoutSecond == null || timeoutSecond == 0) {
                    latch.await();
                } else {
                    latch.await(timeoutSecond, TimeUnit.SECONDS);
                }
            }
            System.out.println("执行写锁逻辑：" + curNode);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void releaseWriteReadDistributeLock(String nodePath){
        try {
            while(true){
                List<String> list = zooKeeper.getChildren(nodePath, false, new Stat());
                List<SequentialStr> sequentialList = dealResource(list);
                String preNode = sequentialList.get(0).getResourceStr();
                boolean deleteResult = deleteNode(nodePath + "/" + preNode);
                if(deleteResult){
                    break;
                }
            }
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<SequentialStr> dealResource(List<String> list) {
        return list.stream().map(name -> {
            SequentialStr sequentialStr = new SequentialStr();
            sequentialStr.setResourceStr(name);
            if (name.contains(WRITE_SING)) {
                sequentialStr.setSequential(name.substring(WRITE_SING.length()));
            } else {
                sequentialStr.setSequential(name.substring(READ_SING.length()));
            }
            return sequentialStr;
        }).sorted(Comparator.comparing(SequentialStr::getSequential)).collect(Collectors.toList());
    }

    static class SequentialStr {

        private String sequential;
        private String resourceStr;

        public String getSequential() {
            return sequential;
        }

        public void setSequential(String sequential) {
            this.sequential = sequential;
        }

        public String getResourceStr() {
            return resourceStr;
        }

        public void setResourceStr(String resourceStr) {
            this.resourceStr = resourceStr;
        }
    }
}
