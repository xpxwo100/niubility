package boot.zookeeper;

/**
 * Service服务中心
 *
 * @author jsj
 * @date 2018-10-8
 */
public interface ServiceRegistryAndDiscovery {

    /**
     * 服务注册
     *
     * @param serviceName    服务名称
     * @param serviceAddress 服务地址
     */
    void register(String serviceName, String serviceAddress);

    void createNode0(String serverName, String path, String data);

    void deleteNode0(String path);

    void stop();
    public  String getService(String service_name,String param);
}
