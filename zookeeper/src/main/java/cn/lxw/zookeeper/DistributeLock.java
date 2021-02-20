package cn.lxw.zookeeper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author xingweilin@clubfactory.com
 * @date 2021/2/2 3:51 下午
 * zookeeper实现的分布式锁
 */
public class DistributeLock {

    private static Integer stock = 5;

    private static Integer threadNum = 10;

    private static String nodeNamePre = "/product-1";

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

        //初始化连接
        ZookeeperBean.getInstance();
//        do {
//            executorService.execute(DistributeLock::businessDeal);
//            threadNum--;
//        } while (threadNum >= 0);

        //读写锁
        executorService.execute(DistributeLock::businessReadDeal);
        executorService.execute(DistributeLock::businessReadDeal);
        executorService.execute(DistributeLock::businessWriteDeal);
        executorService.execute(DistributeLock::businessReadDeal);
        executorService.execute(DistributeLock::businessReadDeal);
        executorService.execute(DistributeLock::businessWriteDeal);
        executorService.execute(DistributeLock::businessWriteDeal);
        executorService.execute(DistributeLock::businessReadDeal);
        executorService.execute(DistributeLock::businessWriteDeal);
    }

    /**
     * 模拟多服务调用
     */
    private static void businessDeal() {
        //加非公平分布式锁
        ZookeeperBean.getInstance().createUnFairDistributeLock(nodeNamePre, 0);
        //公平分布式锁
        //ZookeeperBean.getInstance().createFairDistributeLock(nodeNamePre, 0);

        int stock = getStockNum();

        System.out.println("cur stock:" + stock);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (stock <= 0) {
            //释放分布式锁
            ZookeeperBean.getInstance().deleteNode(nodeNamePre);
            //ZookeeperBean.getInstance().releaseFairDistributeLock(nodeNamePre);
            throw new RuntimeException("stock is zero!!!");
        }
        stock--;
        System.out.println("stock after business:" + stock);
        updateStockNum(stock);

        //释放分布式锁
        ZookeeperBean.getInstance().deleteNode(nodeNamePre);
        //ZookeeperBean.getInstance().releaseFairDistributeLock(nodeNamePre);
    }

    /**
     * 模拟多服务调用
     */
    private static void businessWriteDeal() {
        //加分布式锁写锁
        ZookeeperBean.getInstance().createWriteDistributeLock(nodeNamePre, 0);

        int stock = getStockNum();

        System.out.println("write stock:" + stock);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (stock <= 0) {
            //释放分布式锁
            ZookeeperBean.getInstance().releaseWriteReadDistributeLock(nodeNamePre);
            throw new RuntimeException("stock is zero!!!");
        }
        stock--;
        System.out.println("stock after business:" + stock);
        updateStockNum(stock);

        //释放分布式锁
        ZookeeperBean.getInstance().releaseWriteReadDistributeLock(nodeNamePre);
    }

    /**
     * 模拟多服务调用
     */
    private static void businessReadDeal() {
        //加分布式锁读锁
        ZookeeperBean.getInstance().createReadDistributeLock(nodeNamePre, 0);

        int stock = getStockNum();
        System.out.println("read stock:" + stock);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //释放分布式锁
        ZookeeperBean.getInstance().releaseWriteReadDistributeLock(nodeNamePre);
    }


    /**
     * 模拟数据库获取
     *
     * @return
     */
    private static Integer getStockNum() {
        return stock;
    }

    /**
     * 模拟数据库更新
     *
     * @return
     */
    private static void updateStockNum(Integer tmpStock) {
        stock = tmpStock;
    }
}
