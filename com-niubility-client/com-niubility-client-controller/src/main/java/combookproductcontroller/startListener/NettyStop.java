package combookproductcontroller.startListener;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
public class NettyStop implements ApplicationListener<ContextClosedEvent> {
   /* @Autowired
    private ServiceRegistryAndDiscovery zooKeeperRegistry;*/
    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        /*System.out.println("zooKeeperRegistry:"+zooKeeperRegistry);
        String ip =  nettyConfig.getIp();
        int port =  nettyConfig.getPort();
        ApplicationContext applicationContext = contextClosedEvent.getApplicationContext();
        System.out.println(redisUtils);
        ConsistentHash<String> consistentHash = zooKeeperRegistry.setConsistentHash();
        boolean b = redisUtils.set("consistentHash",consistentHash);
        System.out.println("consistentHash： redisUtils del"+consistentHash);*/
        //zooKeeperRegistry.stop();
    }
}
