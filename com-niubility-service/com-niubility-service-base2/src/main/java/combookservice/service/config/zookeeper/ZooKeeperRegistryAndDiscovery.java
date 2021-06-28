package combookservice.service.config.zookeeper;


import boot.zookeeper.*;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


/**
 * 基于 ZooKeeper 的服务中心实现
 *
 * @author jsj
 * @date 2018-10-14
 */
public class ZooKeeperRegistryAndDiscovery implements ServiceRegistryAndDiscovery {
    /**
     * 连接最大重试次数
     */
    private final static int MAX_RETRYS = 3;
    private final static int BASE_SLEEP_TIMEOUT = 1000;
    /**
     * ZooKeeper连接的重试策略
     */
    private final static RetryPolicy RETRY_POLICY = new ExponentialBackoffRetry(BASE_SLEEP_TIMEOUT, MAX_RETRYS);
    /**
     * 会话超时参数
     */
    private final static int ZK_SESSION_TIMEOUT = 60000;
    /**
     * 连接超时参数
     */
    private final static int ZK_CONNECTION_TIMEOUT = 5000;
    /**
     * rpc注册根节点的命名空间
     */
    private final static String ZK_REGISTRY_PATH = "/server";
    private static final String SEPARATOR = ":";
    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperRegistryAndDiscovery.class);
    private final String address;
    private CuratorFramework client;
    private LoadBalance loadBalance = null;
    private List<String> serviceList = null;
    private  static Map<String,ConsistentHash<String>> serviceHashMap = new HashMap<>();
    private  static Map<String,List<String>> serviceMap = new HashMap<>();
    private final String balance;
    public ZooKeeperRegistryAndDiscovery(String ip, int port, String balance) {
        this.address = ip + SEPARATOR + port;
        this.balance = balance;
        init();
    }
    public void init() {
        if(client == null){
            client = createCuratorFramework();
            client.start();
        }
        try {
            registryWatcher("/server");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CuratorFramework getClient() {
        return client;
    }

    @Override
    public void stop() {
        if(client != null){
            client.close();
        }
    }


    private CuratorFramework createCuratorFramework() {
        // 创建 ZooKeeper 客户端
        return CuratorFrameworkFactory.builder()
                .connectString(address)
                .sessionTimeoutMs(ZK_SESSION_TIMEOUT)
                .connectionTimeoutMs(ZK_CONNECTION_TIMEOUT)
                .retryPolicy(RETRY_POLICY)
                .build();
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        try {
            LOGGER.info("Zookeeper 已连接！");
            // 检查 registry 节点和 service 节点（持久）
            this.checkNodeExists(client, serviceName);
            String servicePath = ZK_REGISTRY_PATH + "/" + serviceName;
            // 创建 address 节点（临时），只有停止服务后才能断开连接
            String addressPath = servicePath + "/address-";
            String addressNode = client.create()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(addressPath, serviceAddress.getBytes());
            LOGGER.info("创建 address 节点: {}", addressNode);
        } catch (Exception e) {
            throw new RuntimeException("RPC server 注册时发生异常");
        }
    }

    @Override
    public void createNode0(String serverName, String path, String data) {
        try {
            Stat stat1 = client.checkExists().forPath(ZK_REGISTRY_PATH);
            boolean haveServer = stat1 != null ? true : false;
            if(haveServer){
                Stat stat2 = client.checkExists().forPath(ZK_REGISTRY_PATH+"/"+serverName+"#"+path);
                boolean s = stat2 != null ? true : false;
                if(!s){
                    //不存在
                    //创建临时节点
                    client.create().withMode(CreateMode.EPHEMERAL)
                            .forPath(ZK_REGISTRY_PATH+"/"+serverName+"#"+path,data.getBytes());
                }
            }else{
                //创建永久节点
                client.create().forPath(ZK_REGISTRY_PATH,"服务注册的节点".getBytes());
            }
        } catch (Exception e) {
            throw new RuntimeException("RPC "+ZK_REGISTRY_PATH+" 注册时发生异常");
        }
    }

    @Override
    public void deleteNode0(String path) {
        try {
            Stat stat1 = client.checkExists().forPath(path);
            boolean s = stat1 != null ? true : false;
            if(s){
                //级联删除子节点
                client.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
            }
        } catch (Exception e) {
            throw new RuntimeException("RPC server 注册时发生异常");
        }
    }



    /**
     * 检查 registry 节点和 service 节点是否已经创建
     *
     * @param serviceName
     */
    private void checkNodeExists(CuratorFramework client, String serviceName) throws Exception {
        // 检查 registry 节点（持久）
        checkRegistryNodeExists(client);
        // 检查 service 节点（持久）
        String servicePath = ZK_REGISTRY_PATH + "/" + serviceName;
        Stat stat = client.checkExists().forPath(servicePath);
        if (stat == null) {
            client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath);
            LOGGER.info("创建 service 节点: {}", servicePath);
        }
    }

    /**
     * 选取 serviceAddress 节点
     *
     * @param addressList serviceAddress 节点列表
     * @return
     */
    private String chooseServiceAddress(List<String> addressList) {
        if (addressList == null || addressList.isEmpty()) {
            return null;
        }
        String address;
        int size = addressList.size();
        if (size == 1) {
            // 若只有一个地址，则获取该地址
            address = addressList.get(0);
            LOGGER.debug("选取任意唯一节点: {}", address);
        } else {
            //todo 若存在多个地址，则随机获取一个地址，可以考虑利用zooKeeper做更好的负载均衡
            address = addressList.get(ThreadLocalRandom.current().nextInt(size));
            LOGGER.debug("选取任意一个节点: {}", address);
        }
        return address;
    }

    /**
     * 检查 registry 节点是否已经创建
     *
     * @param client
     */
    private void checkRegistryNodeExists(CuratorFramework client) {
        try {
            // 创建 registry 节点（持久）
            Stat stat = client.checkExists().forPath(ZK_REGISTRY_PATH);
            if (stat == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(ZK_REGISTRY_PATH);
                LOGGER.info("创建 registry 节点: {}", ZK_REGISTRY_PATH);
            }
        } catch (Exception e) {
            LOGGER.debug("创建 registry 节点时发生异常");
            throw new RuntimeException("创建 registry 节点时发生异常");
        }
    }


    /**
     * 监听事件 服务发现 然后放到serviceHashMap
     * @param path
     * @throws Exception
     */
    private void registryWatcher(String path) throws Exception {

        PathChildrenCache pathChildrenCache = new PathChildrenCache(getClient(),path,true);
        pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            public void childEvent(CuratorFramework ient, PathChildrenCacheEvent event) throws Exception {
                if(event.getType().equals(PathChildrenCacheEvent.Type.INITIALIZED)){
                    System.out.println("子节点初始化成功");
                }else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)){
                    System.out.println("添加子节点路径:"+event.getData().getPath());
                    System.out.println("子节点数据:"+new String(event.getData().getData()));
                    serviceList =   ient.getChildren().forPath(path);
                    buildServiceMap(serviceList);
                }else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)){
                    System.out.println("删除子节点:"+event.getData().getPath());
                    serviceList = ient.getChildren().forPath(path);
                    buildServiceMap(serviceList);
                }else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)){
                    System.out.println("修改子节点路径:"+event.getData().getPath());
                    System.out.println("修改子节点数据:"+new String(event.getData().getData()));
                }
            }
        });
    }

    /**
     * 构建服务Map集合
     * @param service
     */
    private void buildServiceMap(List<String> service ){
        Set<String> set = new HashSet();
        if(serviceHashMap.size() > 0){
            serviceHashMap.clear();
        }
        for(String s : service){
            String[] strArr = s.split("#");
            set.add(strArr[0]);
        }
        for(String set1 : set){
            HashSet<String> serverNode = new HashSet<String>();
            List<String> list = new ArrayList<>();
            for(String s : service){
                String[] strArr = s.split("#");
                if(set1.equals(strArr[0])){
                    serverNode.add(strArr[1]);
                    list.add(strArr[1]);
                }
            }
            ConsistentHash<String> consistentHash =  new ConsistentHash<String>( 200, serverNode);
            serviceHashMap.put(set1,consistentHash);
            serviceMap.put(set1,list);
        }
    }

    @Override
    public String getService(String service_name,String param) {
        if("hash".equals(balance) ){
            loadBalance = new HashBalance(service_name,serviceHashMap);
        }else{
            loadBalance = new RandomBalance(service_name);
        }
        String serverNodeName = loadBalance.selectHost(service_name,param,serviceMap);
        return serverNodeName;
    }
}