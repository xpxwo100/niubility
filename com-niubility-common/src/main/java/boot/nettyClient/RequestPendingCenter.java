package boot.nettyClient;


import boot.nettyRpcModel.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xpx
 * @description 请求等待结果返回中心
 * @createDate 2020/4/26
 */
public class RequestPendingCenter {

    private static volatile Map<String, MessageCallBack> map = new ConcurrentHashMap<String, MessageCallBack>();
    public void add(String streamId, MessageCallBack future){
        this.map.put(streamId, future);
    }

    public void set(String streamId, RpcResponse response){
        MessageCallBack operationResultFuture = this.map.get(streamId);

       /* if (operationResultFuture != null) {
            operationResultFuture.setSuccess(response);
            this.map.remove(streamId);
        }*/
        if (operationResultFuture != null) {
            this.map.remove(response.getMessageId());
            System.out.println(Thread.currentThread().getName()+"获得response"+response);
            operationResultFuture.over(response);
            //operationResultFuture.done(response);
        }
     }
}
