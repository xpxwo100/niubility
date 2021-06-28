package boot.nettyRpcServer.asm;

/**
 * 通过ASM调用有返回值函数接口 函数传入参数类型为Object
 * 
 */
public interface AsmFunctionInvokerImpl {

	/**
	 * 传入参数类型为Object[],返回参数类型为Object
	 * 
	 * @param args
	 * @return
	 */
	public Object invokeFunction(Object instance, Object[] args);
}