package combookservice.service.config.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * 基于Zookeeper的分布式锁
 */
public class ZookeeperLock {

    public static   int a = 0;

    public static void main(String[] args) throws Exception {
        //创建zookeeper的客户端

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

        CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy);

        client.start();


        //创建分布式锁 临时锁, 锁空间的根节点路径为/curator/lock

        final InterProcessMutex mutex = new InterProcessMutex(client, "/curator/lock2");

        for(int i=0;i<10;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                try {
                    mutex.acquire();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                    //获得了锁, 进行业务流程
                    a++;
                    System.out.println(Thread.currentThread().getName()+":"+a);
                try {
                    mutex.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                  /*  if(a > 0){
                        a -= 1;
                    }*/
                 /* synchronized (ZookeeperLock.class){
                      a++;
                      //获得了锁, 进行业务流程
                      System.out.println(Thread.currentThread().getName()+":"+a);

                  }*/

                }
            }).start();
        }
        Thread.sleep(5000);
        System.out.println("a:" +a);
        //关闭客户端
        client.close();
    }

    public void rasad() throws Exception {
        //创建zookeeper的客户端

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

        CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy);

        client.start();

//创建分布式锁, 锁空间的根节点路径为/curator/lock

        InterProcessMutex mutex = new InterProcessMutex(client, "/curator2/lock");

        mutex.acquire();

//获得了锁, 进行业务流程

        System.out.println("Enter mutex");

//完成业务流程, 释放锁

        mutex.release();

//关闭客户端

        client.close();
    }
}
