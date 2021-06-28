package boot.nettyRpcServer.reflect;


import boot.nettyRpcServer.constraint.MethodInvokeReturnType;

import java.util.HashMap;
import java.util.Map;

public class MethodUtil {
	public static Map<String, Integer> returnTypeMap = new HashMap<String, Integer>();
	static {
		returnTypeMap.put("void", MethodInvokeReturnType.VOID);
		returnTypeMap.put("int", MethodInvokeReturnType.IRETURN);
		returnTypeMap.put("byte", MethodInvokeReturnType.IRETURN);
		returnTypeMap.put("short", MethodInvokeReturnType.IRETURN);
		returnTypeMap.put("boolean", MethodInvokeReturnType.IRETURN);
		returnTypeMap.put("char", MethodInvokeReturnType.IRETURN);
		returnTypeMap.put("double", MethodInvokeReturnType.DRETURN);
		returnTypeMap.put("float", MethodInvokeReturnType.FRETURN);
		returnTypeMap.put("long", MethodInvokeReturnType.LRETURN);
	}

	public static int getMethodReturnType(Class<?> returnType) {
		String name = returnType.getName();
		if (returnTypeMap.containsKey(name)) {
			return returnTypeMap.get(name);
		}
		return MethodInvokeReturnType.ARETURN;
	}
}
