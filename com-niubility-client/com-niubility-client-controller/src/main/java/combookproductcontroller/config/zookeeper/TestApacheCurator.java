package combookproductcontroller.config.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestApacheCurator {

    //会话超时时间
    private final int SESSION_TIMEOUT = 30 * 1000;

    //连接超时时间
    private final int CONNECTION_TIMEOUT = 3 * 1000;

    //ZooKeeper服务地址
    private static final String SERVER = "localhost:2181";

    //创建连接实例
    private CuratorFramework client = null;

    /**
     * baseSleepTimeMs：初始的重试等待时间
     * maxRetries：最多重试次数
     */
    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    public void init(){
        //创建 CuratorFrameworkImpl实例
        client = CuratorFrameworkFactory.newClient(SERVER, SESSION_TIMEOUT, CONNECTION_TIMEOUT, retryPolicy);

        //启动
        client.start();
    }

    /**
     * 测试创建节点
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception{
        init();
       /* //创建永久节点
        client.create().forPath("/curator","/curator data".getBytes());

        //创建永久有序节点
        client.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath("/curator_sequential","/curator_sequential data".getBytes());

        //创建临时节点
        client.create().withMode(CreateMode.EPHEMERAL)
                .forPath("/curator/ephemeral","/curator/ephemeral data".getBytes());

        //创建临时有序节点
        client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath("/curator/ephemeral_path1","/curator/ephemeral_path1 data".getBytes());

        client.create().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath("/curator/ephemeral_path2","/curator/ephemeral_path2 data".getBytes());*/


        List<String> addressList = client.getChildren().forPath("/");
        System.out.println(addressList);

        Stat stat1 = client.checkExists().forPath("/server");
        boolean s = stat1 != null ? true : false;
        System.out.println(s);
        //创建永久节点
        //client.create().forPath("/curator","/curator data".getBytes());
      /*  client.create().withMode(CreateMode.EPHEMERAL)
                .forPath("/curator/ephemeral","/curator/ephemeral data".getBytes());*/

        //级联删除子节点
        //client.delete().guaranteed().deletingChildrenIfNeeded().forPath("/127.1.1.4:9090");
        //client.delete().guaranteed().deletingChildrenIfNeeded().forPath("/localhost:9899");
        //client.delete().guaranteed().deletingChildrenIfNeeded().forPath("/localhost+:9899");

        List<String> addressList2 = client.getChildren().forPath("/server");
        System.out.println(addressList2);
        client.close();
    }

}