package combookservice.service.startListener;

import boot.nettyClient.NettyRpcUtil;
import boot.nettyRpcModel.IBootStart;
import boot.nettyRpcModel.RpcService;
import boot.nettyServer.NettyRpcServer;
import boot.zookeeper.ServiceRegistryAndDiscovery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by lzhijun on 2019/9/29.
 */
@Component
public class NettyStart implements IBootStart {
    @Autowired
    public NettyRpcServer nettyRpcSnerver;
    @Autowired
    public ServiceRegistryAndDiscovery zookeeperDiscovery;
    @Override
    public void doBootStart(ContextRefreshedEvent contextRefreshedEvent) {
    }

    @Override
    public void doRegisteredStart(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        for (Map.Entry<String, Object> entry : serviceBeanMap.entrySet()) {
            nettyRpcSnerver.registerService(entry.getKey(), entry.getValue());
        }
        nettyRpcSnerver.registerZookeeperNode();
        nettyRpcSnerver.start();
        NettyRpcUtil.setZookeeperDiscovery(zookeeperDiscovery);
    }
}
