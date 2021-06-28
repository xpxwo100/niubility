package combookproductcontroller.config;

import org.springframework.context.annotation.Configuration;


@Configuration
public class ZooKeeperConfig {
    /*@Value("${spring.application.name}")
    private  String serverName;
    @Value("${zookeeper.ip}")
    private  String zookeeper_ip;
    @Value("${zookeeper.port}")
    private  int zookeeper_port;
    @Value("${zookeeper.balance}")
    private String balance;



    public String getZookeeper_ip() {
        return zookeeper_ip;
    }

    public void setZookeeper_ip(String zookeeper_ip) {
        this.zookeeper_ip = zookeeper_ip;
    }

    public int getZookeeper_port() {
        return zookeeper_port;
    }

    public void setZookeeper_port(int zookeeper_port) {
        this.zookeeper_port = zookeeper_port;
    }


    //@Bean
    public ServiceRegistryAndDiscovery serviceRegistry() {
        return (ServiceRegistryAndDiscovery) new ZooKeeperRegistryAndDiscovery(zookeeper_ip, zookeeper_port,balance);
    }*/
}
