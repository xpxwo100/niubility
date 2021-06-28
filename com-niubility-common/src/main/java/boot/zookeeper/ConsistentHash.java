package boot.zookeeper;


import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

/**
 * 一致性hash算法
 * @param <T>
 */
public class ConsistentHash<T> implements Serializable {
    private static final long serialVersionUID = 20365543230L;
    /**
     * 虚拟节点数 ， 越大分布越均衡，但越大，在初始化和变更的时候效率差一点。 测试中，设置200基本就均衡了。
     */
    private  int numberOfReplicas;

    /**
     * 环形Hash空间
     */
    public final SortedMap<Integer, T> circle = new TreeMap<Integer, T>();

    public ConsistentHash(int numberOfReplicas) {
        this.numberOfReplicas = numberOfReplicas;
    }
    public ConsistentHash() {
    }
    /**
     * @param
     *            ，哈希函数
     * @param numberOfReplicas
     *            ，虚拟服务器系数
     * @param nodes
     *            ，服务器节点
     */
    public ConsistentHash( int numberOfReplicas,
                          Collection<T> nodes) {
        this.numberOfReplicas = numberOfReplicas;
        for (T node : nodes) {
            this.addNode(node);
        }
    }

    /**
     * 添加物理节点，每个node 会产生numberOfReplicas个虚拟节点，这些虚拟节点对应的实际节点是node
     */
    public void addNode(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            int hashValue = hash(node.toString() + i);
            circle.put(hashValue, node);
        }
    }

    /**移除物理节点，将node产生的numberOfReplicas个虚拟节点全部移除
     * @param node
     */
    public void removeNode(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            int hashValue = hash(node.toString() + i);
            circle.remove(hashValue);
        }
    }

    /**
     * 得到映射的物理节点
     *
     * @param key
     * @return
     */
    public T getNode(Object key) {
        if (circle.isEmpty()) {
            return null;
        }
        int hashValue = hash(key);
//		System.out.println("key---" + key + " : hash---" + hash);
        if (!circle.containsKey(hashValue)) {
            // 返回键大于或等于hash的node，即沿环的顺时针找到一个虚拟节点
            SortedMap<Integer, T> tailMap = circle.tailMap(hashValue);
            // System.out.println(tailMap);tailMap
            // System.out.println(circle.firstKey());
            hashValue = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
//		System.out.println("hash---: " + hash);
        return circle.get(hashValue);
    }


    public int hash(Object key) {
        ByteBuffer buf = ByteBuffer.wrap(key.toString().getBytes());
        int seed = 0x1234ABCD;

        ByteOrder byteOrder = buf.order();
        buf.order(ByteOrder.LITTLE_ENDIAN);

        long m = 0xc6a4a7935bd1e995L;
        int r = 47;

        long h = seed ^ (buf.remaining() * m);

        long k;
        while (buf.remaining() >= 8) {
            k = buf.getLong();

            k *= m;
            k ^= k >>> r;
            k *= m;

            h ^= k;
            h *= m;
        }

        if (buf.remaining() > 0) {
            ByteBuffer finish = ByteBuffer.allocate(8).order(
                    ByteOrder.LITTLE_ENDIAN);
            finish.put(buf).rewind();
            h ^= finish.getLong();
            h *= m;
        }

        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;
        buf.order(byteOrder);
        return (int) h;
    }
    public static void main(String[] args) throws Exception {/*

        ZooKeeperRegistry zooKeeperRegistry = new ZooKeeperRegistry("localhost", 2181);

        List<String> addressList = zooKeeperRegistry.getClient().getChildren().forPath("/server");
        System.out.println(addressList);
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zooKeeperRegistry.getClient(),"/server",true);
        pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            public void childEvent(CuratorFramework ient, PathChildrenCacheEvent event) throws Exception {
                if(event.getType().equals(PathChildrenCacheEvent.Type.INITIALIZED)){
                    System.out.println("子节点初始化成功");
                }else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)){
                    System.out.println("添加子节点路径:"+event.getData().getPath());
                    System.out.println("子节点数据:"+new String(event.getData().getData()));


                }else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)){
                    System.out.println("删除子节点:"+event.getData().getPath());
                }else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)){
                    System.out.println("修改子节点路径:"+event.getData().getPath());
                    System.out.println("修改子节点数据:"+new String(event.getData().getData()));
                }
            }
        });*/
       // System.out.println(sad);
    }
    public void getServer(Object key) throws Exception {
        BaseZookeeper zookeeper = new BaseZookeeper();
        zookeeper.connectZookeeper("localhost:2181");
        /*zookeeper.createNode("127.1.1.1:9090","1");
        zookeeper.createNode("127.1.1.2:9090","1");
        zookeeper.createNode("127.1.1.3:9090","1");
        zookeeper.createNode("127.1.1.4:9090","1");*/


        HashSet<String> serverNode = new HashSet<String>();
        List<String> z = zookeeper.getChildren("/");
        System.out.println(z);
        for(String s : z){
            serverNode.add(s);
        }
      /*  ConsistentHash<String> consistentHash = new ConsistentHash<String>(
                new HashFunction(), 200, serverNode);
        String serverNodeName = consistentHash.getNode(key);*/

    }

    public int getNumberOfReplicas() {
        return numberOfReplicas;
    }

    public SortedMap<Integer, T> getCircle() {
        return circle;
    }

}