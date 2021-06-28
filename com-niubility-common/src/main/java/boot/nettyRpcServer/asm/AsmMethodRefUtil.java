package boot.nettyRpcServer.asm;


import jodd.util.StringUtil;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 通过Asm做函数的反射调用
 * 
 * @author Administrator
 * 
 */
public class AsmMethodRefUtil {

	/**
	 * 保存动态执行函数的静态Class对象锁
	 */
	// func锁
	private static Lock funcLock = new ReentrantLock();
	private static HashMap<String, AsmFunctionInvokerImpl> funcMap = new HashMap<String, AsmFunctionInvokerImpl>();
	private static HashMap<String, Object> funcInvokeInstance = new HashMap<String, Object>();
	// action锁
	private static Lock actionLock = new ReentrantLock();
	private static HashMap<String, AsmActionInvokerImpl> actionMap = new HashMap<String, AsmActionInvokerImpl>();
	private static HashMap<String, Object> actInvokeInstance = new HashMap<String, Object>();
	private static final String mSplitStr = "_";

	/**
	 * 动态执行非静态函数,返回函数执行结果
	 * 
	 * @param cls
	 * @param methodName
	 * @param types
	 * @param objs
	 * @return 执行函数后返回对象
	 * @throws Exception
	 */
	public static Object invokeAsmFunc(String className, String methodName,
			Object[] args, Class<?>[] argsTypes) throws Exception {
		Class<?> cls = Class.forName(className);
		return invokeAsmFunc(cls, methodName, args, argsTypes, null);
	}

	/**
	 * 动态执行非静态函数,返回函数执行结果
	 * 
	 * @param cls
	 * @param methodName
	 * @param types
	 * @param objs
	 * @return 执行函数后返回对象
	 * @throws Exception
	 */
	public static Object invokeAsmFunc(String className, String methodName,
			Object[] args, Class<?>[] argsTypes, Object instance)
			throws Exception {
		Class<?> cls = Class.forName(className);
		return invokeAsmFunc(cls, methodName, args, argsTypes, instance);
	}

	/**
	 * 动态执行非静态函数,返回函数执行结果
	 * 
	 * @param cls
	 * @param methodName
	 * @param types
	 * @param objs
	 * @return 执行函数后返回对象
	 * @throws Exception
	 */
	public static Object invokeAsmFunc(Class<?> cls, String methodName,
			Object[] args, Class<?>[] argsTypes) throws Exception {
		return invokeAsmFunc(cls, methodName, args, argsTypes, null);
	}

	/**
	 * 动态执行非静态函数,返回函数执行结果
	 * 
	 * @param cls
	 * @param methodName
	 * @param types
	 * @param objs
	 * @return 执行函数后返回对象
	 * @throws Exception
	 */
	public static Object invokeAsmFunc(Class<?> cls, String methodName,
			Object[] args, Class<?>[] argsTypes, Object instance)
			throws Exception {
		String targetName = getClassName(cls.getName());
		String key = targetName + mSplitStr + methodName;
		AsmFunctionInvokerImpl impl;
		if (!funcMap.containsKey(key)) {
			funcLock.lock();
			try {
				if (!funcMap.containsKey(key)) {
					impl = new AsmByteBuilder().createASMFunctionInvoker(key,
							cls, methodName, argsTypes);
					funcMap.put(key, impl);
					funcInvokeInstance.put(key, cls.newInstance());
				}
			} finally {
				funcLock.unlock();
			}
		}
		impl = funcMap.get(key);
		if (instance == null) {
			instance = funcInvokeInstance.get(key);
		}
		return impl.invokeFunction(instance, args);
	}

	/**
	 * 动态执行非静态函数,无返回值
	 * 
	 * @param clsName
	 * @param methodName
	 * @param types
	 * @param objs
	 * @return 执行函数后返回对象
	 * @throws Exception
	 */
	public static void invokeAsmAction(String className, String methodName,
			Object[] args, Class<?>[] argsTypes, Object instance)
			throws Exception {
		Class<?> cls = Class.forName(className);
		invokeAsmAction(cls, methodName, args, argsTypes, instance);
	}

	/**
	 * 动态执行非静态函数,无返回值
	 * 
	 * @param clsName
	 * @param methodName
	 * @param types
	 * @param objs
	 * @return 执行函数后返回对象
	 * @throws Exception
	 */
	public static void invokeAsmAction(String className, String methodName,
			Object[] args, Class<?>[] argsTypes) throws Exception {
		Class<?> cls = Class.forName(className);
		invokeAsmAction(cls, methodName, args, argsTypes, null);
	}

	/**
	 * 动态执行非静态函数,无返回值
	 * 
	 * @param clsName
	 * @param methodName
	 * @param types
	 * @param objs
	 * @return 执行函数后返回对象
	 * @throws Exception
	 */
	public static void invokeAsmAction(Class<?> cls, String methodName,
			Object[] args, Class<?>[] argsTypes) throws Exception {
		invokeAsmAction(cls, methodName, args, argsTypes, null);
	}

	/**
	 * 动态执行非静态函数,无返回值
	 * 
	 * @param clsName
	 * @param methodName
	 * @param types
	 * @param objs
	 * @return 执行函数后返回对象
	 * @throws Exception
	 */
	public static void invokeAsmAction(Class<?> cls, String methodName,
			Object[] args, Class<?>[] argsTypes, Object instance)
			throws Exception {
		String targetName = getClassName(cls.getName());
		String key = targetName + mSplitStr + methodName;
		AsmActionInvokerImpl impl;
		if (!actionMap.containsKey(key)) {
			actionLock.lock();
			try {
				if (!actionMap.containsKey(key)) {
					impl = new AsmByteBuilder().createASMActionInvokerImpl(key,
							cls, methodName, argsTypes);
					actionMap.put(key, impl);
					actInvokeInstance.put(key, cls.newInstance());
				}
			} finally {
				actionLock.unlock();
			}
		}
		impl = actionMap.get(key);
		if (instance == null) {
			instance = actInvokeInstance.get(key);
		}
		impl.invokeAction(instance, args);
	}

	/**
	 * 获取Class全名
	 * 
	 * @param clsName
	 * @return
	 */
	private static String getClassName(String clsName) {
		String targetName = null;
		if (clsName.contains(".")) {
			targetName = StringUtil.replace(clsName, ".", "");
		} else {
			targetName = StringUtil.replace(clsName, "/", "");
		}
		return targetName;
	}
}
