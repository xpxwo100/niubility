package boot.nettyClient;

import boot.nettyRpcModel.RpcRequest;
import boot.nettyRpcModel.RpcResponse;
import boot.zookeeper.ServiceRegistryAndDiscovery;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 动态代理
 * @author hasee
 *
 */
@Component
public class NettyRpcProxy {
	private String service_name;
	private ServiceRegistryAndDiscovery zookeeperDiscovery;
	public static AtomicInteger atomicInteger = new AtomicInteger(0);
	public NettyRpcProxy() {
	}

	public NettyRpcProxy(String service_name,ServiceRegistryAndDiscovery zookeeperDiscovery) {
		this.service_name = service_name;
		this.zookeeperDiscovery = zookeeperDiscovery;
	}
	public synchronized static <T> T get(Boolean isSyn, String service_name, Class<?> interfaceClass, ServiceRegistryAndDiscovery zookeeperDiscovery, IRpcClient rpcClient) {
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
				new Class<?>[]{interfaceClass},
				//new NettyRpcProxy(service_name,zookeeperDiscovery)
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						RpcRequest request = new RpcRequest();
						request.setMessageId(UUID.randomUUID().toString());
						//request.setMessageId(atomicInteger.incrementAndGet()+"");
						request.setClassName(method.getDeclaringClass().getName());
						request.setMethodName(method.getName());
						request.setTypeParameters(method.getParameterTypes());
						request.setParametersVal(args);
						/*System.out.println("zookeeperDiscovery"+zookeeperDiscovery);
						String serverNodeName = zookeeperDiscovery.getService(service_name);
						System.out.println("serverNodeName"+serverNodeName);
						if(serverNodeName == null){
							throw new AppRunTimeException("找不到服务器！");
						}
						System.out.println(Thread.currentThread().getName()+serverNodeName);
						String[] a = serverNodeName.split(":");

						String SERVER_IP = a[0];
						int SERVER_PORT = Usual.f_getInteger(a[1]);*/

						//NettyRpcClient nettyRpcClient = NettyRpcClient.getInstance().getConnect(service_name,SERVER_IP, SERVER_PORT);


						RpcResponse rpcResponse = rpcClient.send(isSyn,request);


						/*System.out.println(SERVER_PORT+"a nettyRpcClient:"+nettyRpcClient);
						ClientHandler clientHandler = nettyRpcClient.getClientHandler();
						System.out.println(SERVER_PORT+"a clientHandler:"+clientHandler);
						if(clientHandler == null){
							throw new AppRunTimeException("连接失败！");
						}
						MessageCallBack callBack = clientHandler.getCallBack(request);
						if(callBack == null ){
							throw new AppRunTimeException("连接失败！");
						}
						return  callBack.start(request,clientHandler);*/
						return rpcResponse;
					}
				}
		);
	}

	/*@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws InterruptedException {
		*//*if(zookeeperDiscovery == null){
			synchronized (NettyRpcProxy.class){
				if(zookeeperDiscovery == null){
					zookeeperDiscovery = (ZookeeperDiscovery) SpringContextUtil.getBean("zookeeperDiscovery");
				}
			}
		}*//*
		RpcRequest request = new RpcRequest();
		//request.setMessageId(UUID.randomUUID().toString());
		request.setMessageId(atomicInteger.incrementAndGet()+"");
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setTypeParameters(method.getParameterTypes());
        request.setParametersVal(args);
		System.out.println("zookeeperDiscovery"+zookeeperDiscovery);
		String serverNodeName = zookeeperDiscovery.getService(service_name);
		System.out.println("serverNodeName"+serverNodeName);
	     if(serverNodeName == null){
	    	throw new AppRunTimeException("找不到服务器！");
		}
		System.out.println(Thread.currentThread().getName()+serverNodeName);
		String[] a = serverNodeName.split(":");

		String SERVER_IP = a[0];
		int SERVER_PORT = Usual.f_getInteger(a[1]);

		NettyRpcClient nettyRpcClient = NettyRpcClient.getInstance().getConnect(service_name,SERVER_IP, SERVER_PORT);
		System.out.println(SERVER_PORT+"a nettyRpcClient:"+nettyRpcClient);
		ClientHandler clientHandler = nettyRpcClient.getClientHandler();
		System.out.println(SERVER_PORT+"a clientHandler:"+clientHandler);
		if(clientHandler == null){
			throw new AppRunTimeException("连接失败！");
		}
		MessageCallBack callBack = clientHandler.getCallBack(request);
		if(callBack == null ){
			throw new AppRunTimeException("连接失败！");
		}
		return  callBack.start(request,clientHandler);
	}*/
}
