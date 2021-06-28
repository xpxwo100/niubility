package boot.nettyClient;

import boot.nettyRpcModel.RpcRequest;
import boot.nettyRpcModel.RpcResponse;

import java.util.concurrent.ExecutionException;

/**
 * @author xpx
 * @description
 * @createDate 2020/5/5
 */
public interface IRpcClient {

    /**
     * 设置地址
     * @param host
     * @param port
     */
    void setNameAndHostAndPort(String service_name,String host, int port) throws InterruptedException;

    /**
     *  通过 RPC 客户端发送 RPC 请求并获取 RPC 响应
     *
     * @param isSyn
     * @param requestBody 发送请求参数
     * @return RpcResponseBody
     * @throws InterruptedException
     * @throws ExecutionException
     */
    RpcResponse send(Boolean isSyn, RpcRequest requestBody) throws Exception;
}
