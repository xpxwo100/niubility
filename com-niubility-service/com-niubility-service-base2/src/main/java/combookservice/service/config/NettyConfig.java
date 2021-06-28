package combookservice.service.config;

import boot.nettyServer.NettyRpcServer;
import boot.zookeeper.ServiceRegistryAndDiscovery;
import combookservice.service.config.zookeeper.ZooKeeperRegistryAndDiscovery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class NettyConfig {
    @Value("${nettyrpc.ip}")
    private  String ip;
    @Value("${nettyrpc.port}")
    private  int port;
    @Value("${spring.application.name}")
    private  String serverName;
    @Value("${zookeeper.ip}")
    private  String zookeeper_ip;
    @Value("${zookeeper.port}")
    private  int zookeeper_port;
    @Value("${zookeeper.balance}")
    private String balance;


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

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

    @ConditionalOnProperty(prefix = "zookeeper.ip")
    @Bean
    public ServiceRegistryAndDiscovery serviceRegistry() {
        return (ServiceRegistryAndDiscovery) new ZooKeeperRegistryAndDiscovery(zookeeper_ip, zookeeper_port,balance);
    }
    @ConditionalOnProperty(prefix = "nettyrpc.ip")
    @Bean
    public NettyRpcServer initRpcServer(@Autowired ServiceRegistryAndDiscovery serviceRegistryAndDiscovery) {
        return new NettyRpcServer(serverName,ip,port, serviceRegistryAndDiscovery);
    }
}
