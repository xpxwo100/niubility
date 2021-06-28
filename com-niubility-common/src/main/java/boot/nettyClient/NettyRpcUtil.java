package boot.nettyClient;

import boot.nettyRpcModel.*;
import boot.zookeeper.ServiceRegistryAndDiscovery;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.util.concurrent.*;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * netty工具类
 * @author hasee
 *
 */
public class NettyRpcUtil {

	private  static ServiceRegistryAndDiscovery zookeeperDiscovery;
	private static volatile IRpcClient rpcClient;
	private static volatile ListeningExecutorService threadPoolExecutor;
	private static volatile DiscoveryClient discoveryClient;
	/**
	 * rpc远程调用后端
	 *
	 * @param SERVICE_BEAN_NAME SERVICE类名
	 * @param methodName
	 * @param param
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public  static ObjectDto load(Boolean isSyn,String service_name, String SERVICE_BEAN_NAME, String methodName, Object... param)  {
		if(service_name == null || "".equals(service_name)  ){
			throw new AppRunTimeException("service_name is null！");
		}
		if(SERVICE_BEAN_NAME == null || methodName == null ){
			throw new AppRunTimeException("service_name is null！");
		}
		ParamsData paramsData = bulidParamsData(SERVICE_BEAN_NAME,methodName,param);
		ObjectDto objectDto = null;
		try{
			System.out.println("zookeeperDiscovery"+zookeeperDiscovery);
			String p = paramsData.toString();
			String SERVER_IP ="";
			int SERVER_PORT = 0;
			//nacos 上获取注册列表
			List<ServiceInstance> instances = discoveryClient.getInstances(service_name);
			if(instances != null){
				Random random = new Random();
				ServiceInstance instance = instances.get(random.nextInt(instances.size()));
				SERVER_IP = instance.getHost();
				SERVER_PORT = instance.getPort()+10;
			}else {
				throw new AppRunTimeException("找不到服务器！");
			}
			/*String serverNodeName = zookeeperDiscovery.getService(service_name,p);
			System.out.println("serverNodeName"+serverNodeName);
			if(serverNodeName == null){
				throw new AppRunTimeException("找不到服务器！");
			}
			System.out.println(Thread.currentThread().getName()+serverNodeName);
			String[] a = serverNodeName.split(":");

			 SERVER_IP = a[0];
			 SERVER_PORT = Usual.f_getInteger(a[1]);*/




			if (rpcClient == null) {
				synchronized (NettyRpcUtil.class) {
					if (rpcClient == null) {
						rpcClient = new ClientPool();
					}
				}
			}
			if (threadPoolExecutor == null) {
				synchronized (NettyRpcClient.class) {
					if (threadPoolExecutor == null) {
						threadPoolExecutor = MoreExecutors.listeningDecorator((ThreadPoolExecutor) RpcThreadPool.getExecutor(16, -1));
					}
				}
			}

			rpcClient.setNameAndHostAndPort(service_name,SERVER_IP,SERVER_PORT);
			if(isSyn){
				//同步
				NettyRpcLoad nettyRpcLoad = NettyRpcProxy.get(isSyn, service_name,NettyRpcLoad.class,zookeeperDiscovery,rpcClient);
				RpcResponse rpcResponse = (RpcResponse)nettyRpcLoad.load(paramsData);
				if(rpcResponse != null){
					if(rpcResponse.getError() == null){
						if(rpcResponse.getResult()!= null){
							objectDto = (ObjectDto)rpcResponse.getResult();
						}
					}else{
						objectDto = new ObjectDto();
						objectDto.setObj(rpcResponse.getError().getMessage());
						//throw new AppRunTimeException((Exception) rpcResponse.getError());
					}
				}else{
					throw new AppRunTimeException("rpcResponse 为空，与服务器失去连接！");
				}
			}else{
				//异步调用
				Task task = new Task(isSyn,service_name, paramsData, zookeeperDiscovery, rpcClient);
				ListenableFuture<ObjectDto>  listenableFuture = threadPoolExecutor.submit(task);
				CallBackAsyn callBackAsyn = new CallBackAsyn();
				Futures.addCallback(listenableFuture, new FutureCallback<ObjectDto>() {
					@Override
					public void onSuccess(ObjectDto result) {
						callBackAsyn.setResult(result);
						System.out.println("callBackAsyn setResult"+result);
					}
					@Override
					public void onFailure(Throwable t) {
						throw new AppRunTimeException(t.getMessage());
					}
				});
				threadPoolExecutor.execute(new Runnable() {
					@Override
					public void run() {
						ObjectDto objectDto = callBackAsyn.getResult();
						System.out.println("得到ObjectDto 进行处理："+objectDto);
					}
				});
				objectDto = new ObjectDto();
				System.out.println("ObjectDto"+objectDto);
				objectDto.setObj("你的请求已提交，请稍后在查看！");

			/*	CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
					System.out.println("ObjectDto");
					return 2;
				}, threadPoolExecutor);
				integerCompletableFuture.get();*/

			}
		}catch (AppRunTimeException | InterruptedException e){
			throw new AppRunTimeException(e.getMessage());
		}finally {
			return objectDto;
		}
	}


	public static void setZookeeperDiscovery(ServiceRegistryAndDiscovery zookeeperDiscovery) {
		NettyRpcUtil.zookeeperDiscovery = zookeeperDiscovery;
	}


	public static void setDiscoveryClient(DiscoveryClient discoveryClient){
		NettyRpcUtil.discoveryClient = discoveryClient;
	}
	/**
	 * 构建DataWrapper
	 * @param SERVICE_BEAN_NAME
	 * @param methodName
	 * @param inData
	 * @return
	 */
	private static ParamsData bulidParamsData(String SERVICE_BEAN_NAME,String methodName, Object... inData){
		ParamsData paramsData = new ParamsData();
		paramsData.setBeanName(SERVICE_BEAN_NAME);
		paramsData.setMethodName(methodName);
		paramsData.setParams(inData);
		paramsData.setId(UUID.randomUUID().toString());
		return paramsData;
	}
	/** 
     * byte(字节)根据长度转成kb(千字节)和mb(兆字节) 
     *  
     * @param bytes 
     * @return 
     */  
    public static String bytes2kb(long bytes) {  
        BigDecimal filesize = new BigDecimal(bytes);  
        BigDecimal megabyte = new BigDecimal(1024 * 1024);  
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)  
                .floatValue();  
        if (returnValue > 1)  
            return (returnValue + "MB");  
        BigDecimal kilobyte = new BigDecimal(1024);  
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)  
                .floatValue();  
        return (returnValue + "KB");  
    }  
}
