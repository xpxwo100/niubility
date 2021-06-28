package combookservice.service.nettyRpcServer.constraint;

/**
 * 方法调用返回类型
 * 
 */
public class MethodInvokeReturnType {
	/**
	 * 返回基础类型(int,short,char,boolean,byte)
	 */
	public static final int IRETURN = 172;
	/**
	 * 返回基础类型（long)
	 */
	public static final int LRETURN = 173;
	/**
	 * 返回基础类型（float)
	 */
	public static final int FRETURN = 174;
	/**
	 * 返回基础类型（double)
	 */
	public static final int DRETURN = 175;
	/**
	 * 返回所有对象类型
	 */
	public static final int ARETURN = 176;
	/**
	 * void调用
	 */
	public static final int VOID = 177;
}