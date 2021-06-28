package boot.nettyClient;

import boot.zookeeper.ServiceRegistryAndDiscovery;

/**
 * 注册中心
 */
public class RegistrationCenter {


    private ServiceRegistryAndDiscovery zookeeperDiscovery;

    public RegistrationCenter(ServiceRegistryAndDiscovery zookeeperDiscovery) {
        this.zookeeperDiscovery = zookeeperDiscovery;
    }



}
