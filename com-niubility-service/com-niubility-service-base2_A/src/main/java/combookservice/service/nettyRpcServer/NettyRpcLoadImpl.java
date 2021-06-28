package combookservice.service.nettyRpcServer;

import boot.nettyRpcModel.*;
import com.esotericsoftware.reflectasm.MethodAccess;
import combookservice.service.spring.SpringContextUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RpcService(NettyRpcLoad.class)
public class NettyRpcLoadImpl implements NettyRpcLoad {
	private static ConcurrentHashMap<String, MethodAccess> funcMap = new ConcurrentHashMap<String, MethodAccess>();
	private static final Log logger = LogFactory.getLog(NettyRpcLoadImpl.class);
	public NettyRpcLoadImpl() {
	}

	@Override
	public RpcResponse load(ParamsData pojo) {
		String methodName = pojo.getMethodName();
		String beanName = pojo.getBeanName();
		RpcResponse value = null;
		try {
			//参数解析
			Object[] map = pojo.getParams();
			/*Object[] arg = null;
			if(map != null){
				 arg = new Object[]{map};
			}*/
			ObjectDto objectDto = execute(methodName, beanName, map);
			value = new RpcResponse();
			if(objectDto != null){
				value.setResult(objectDto);
				value.setReturnNotNull(true);
			}else{
				value.setResult(null);
				value.setReturnNotNull(false);
			}
			
		} catch (Exception e) {
			//ErrorMsgException err=new ErrorMsgException(e.getMessage());
			RpcResponse result = new RpcResponse();
			result.setError(e);
			return result;
		}
		return value;
	}
	
	/**
	 * 序列化异常数据对象
	 * @param mExp
	 */
	private RpcResponse returnExpBean(ErrorMsgException mExp){
		RpcResponse value = new RpcResponse();
		value.setResult(mExp);
		return value;
	}
	/**
	 * 调用service
	 * @param methodName
	 * @param beanName 类名
	 * @param arg
	 * @return
	 * @throws
	 */
	private ObjectDto execute(String methodName,String beanName,Object[] arg) throws Exception {
		ObjectDto objectDto = null;
		try {
			//调用服务
			//Object obj = MethodUtils.invokeMethod(SpringContextUtil.getBean(beanName), methodName,arg);
			//IBaseService baseService = (IBaseService) SpringContextUtil.getBean(beanName);
			//Object obj = asmCall(arg, null, baseService, methodName,beanName);
			Object bean = SpringContextUtil.getBean(beanName);
			String key = beanName;
			MethodAccess access;
			if (!funcMap.containsKey(key)) {
				 access = MethodAccess.get(bean.getClass());
				 funcMap.put(key,access);
			}else{
				access = funcMap.get(key);
			}
			//int index = access.getIndex(methodName);
			int index = access.getIndex(methodName,arg.length);
			//int index2 = access.getIndex(methodName,arg[0].getClass(),arg[1].getClass());
			Object obj = access.invoke(bean, index, arg);
			// 返回数据
			if (obj != null) {
				 objectDto = new ObjectDto();
				 objectDto.setObj(obj);
			}
		} catch (Exception e) {
			logger.equals(e);
			throw new Exception(e.getMessage());
		}
		return objectDto;
	}
	/**
	 * asm字节码调用
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	/*private Object asmCall(Object[] args,Class<?>[] argClz,
			IBaseService baseService,String methodName,String beanName) throws Exception {
		// long mBegin=System.currentTimeMillis();

		Object obj = null;
		try {
			// Asm字节码调用
			if (!ObjectUtils.isEmpty(args)) {
				if (ObjectUtils.isEmpty(argClz) ){
					int argsLength = args.length;
					argClz = new Class[argsLength];
					for (int i = 0; i < argsLength; i++) {
						argClz[i] = args[i].getClass(); // 存放参数类型
					}
				} 
			} else {
				args = null;
			}

			obj = AsmMethodRefUtil.invokeAsmFunc
					(
							baseService.getClass(),
							methodName,
							args,
							argClz,
							baseService
					);
		} catch (Exception e) {
			logger.error("NotDefinedException,BeanName:" + beanName+ ",MethodName:" + methodName, e);
			throw e;
		}
		return obj;
	}*/


	public static void main(String[] args) throws InterruptedException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		/*Object test = new TestSend();
		//Object sda = MethodUtils.invokeMethod(test, "send","sss2222s");

		MethodAccess access = MethodAccess.get(test.getClass());
		int index = access.getIndex("send","sddddd".getClass());
		Object[] abg =  new Object[1];
		abg[0] = "sddddd";
		//Object sda = access.invoke(test, "send",abg );
		Object sda = access.invoke(test, index,  abg);
		System.out.println(sda);
*/
		/*UserService target = new UserService();
		MethodAccess access = MethodAccess.get(UserService.class);//生成字节码的方式创建UserServiceMethodAccess
		long start = System.currentTimeMillis();
		access.invoke(target, "update", 1, "zhangsan");
		long end = System.currentTimeMillis();
		System.out.println("timeout=" + (end - start));//523 382 415 489 482*/
	}
	}
