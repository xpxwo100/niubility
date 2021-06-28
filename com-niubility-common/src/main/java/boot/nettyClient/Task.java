package boot.nettyClient;


import boot.nettyRpcModel.*;
import boot.zookeeper.ServiceRegistryAndDiscovery;

import java.util.concurrent.Callable;

public class Task implements Callable {
    private String service_name;
    private ParamsData paramsData;
    private Boolean isSyn;
    private ServiceRegistryAndDiscovery zookeeperDiscovery;
    private IRpcClient rpcClient;
    @Override
    public ObjectDto call() throws Exception {
        ObjectDto objectDto = null;
        NettyRpcLoad nettyRpcLoad = NettyRpcProxy.get(isSyn,service_name, NettyRpcLoad.class, zookeeperDiscovery, rpcClient);
        RpcResponse rpcResponse = (RpcResponse) nettyRpcLoad.load(paramsData);
        try {
            if (rpcResponse != null) {
                if (rpcResponse.getError() == null) {
                    if (rpcResponse.getResult() != null) {
                        objectDto = (ObjectDto) rpcResponse.getResult();
                    }
                } else {
                    objectDto = new ObjectDto();
                    objectDto.setObj(rpcResponse.getError().getMessage());
                    //throw new AppRunTimeException((Exception) rpcResponse.getError());
                }
            } else {
                throw new AppRunTimeException("rpcResponse 为空，与服务器失去连接！");
            }
            return objectDto;
        } catch (AppRunTimeException e) {
            throw new AppRunTimeException(e.getMessage());
        }
    }
    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public void setParamsData(ParamsData paramsData) {
        this.paramsData = paramsData;
    }

    public void setZookeeperDiscovery(ServiceRegistryAndDiscovery zookeeperDiscovery) {
        this.zookeeperDiscovery = zookeeperDiscovery;
    }

    public void setRpcClient(IRpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    public Task(Boolean isSyn, String service_name, ParamsData paramsData, ServiceRegistryAndDiscovery zookeeperDiscovery, IRpcClient rpcClient) {
        this.isSyn = isSyn;
        this.service_name = service_name;
        this.paramsData = paramsData;
        this.zookeeperDiscovery = zookeeperDiscovery;
        this.rpcClient = rpcClient;
    }
}