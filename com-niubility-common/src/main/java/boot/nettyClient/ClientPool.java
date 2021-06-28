package boot.nettyClient;


import boot.nettyRpcModel.RpcRequest;
import boot.nettyRpcModel.RpcResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * @author xpx 这个对象会被并发访问
 * @description 客户端测试，用对象池
 * @createDate 2020/5/5
 */
public class ClientPool implements IRpcClient {
    private static final Log logger = LogFactory.getLog(ClientPool.class);

    private List<NettyRpcClient> rpcClientInits;
    private int currentIndex;
    private int totalServer;
    private String service_name;
    private String host;
    private int port;
    private NettyRpcClient nettyRpcClient;
    public void initRound() throws InterruptedException {
         nettyRpcClient = NettyRpcClient.getInstance().getConnect(host,port,service_name);
    }
    private static volatile RpcClientUser client = new RpcClientUser();
    @Override
    public void setNameAndHostAndPort(String service_name, String host, int port) throws InterruptedException {
        this.service_name = service_name;
        this.host = host;
        this.port = port;
        initRound();
    }

    /**
     * 这个方法会被并发访问
     *
     * @param isSyn
     * @param requestBody 发送请求参数
     * @return
     * @throws Exception
     */
    @Override
    public RpcResponse send(Boolean isSyn, RpcRequest requestBody) throws Exception {
        //RpcClientUser client = PoolUtil.getObject(RpcClientUser.class);

        RpcResponse response = null;
        if(nettyRpcClient != null){
            response = client.send(isSyn,nettyRpcClient,requestBody,this);
           // client.returnObject(); //归还对象到对象池
        }/*{
            initRound();
            response = client.send(nettyRpcClient,requestBody,this);
            client.returnObject(); //归还对象到对象池
        }*/
        return response;
    }
}
