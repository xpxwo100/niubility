package combookservice.service.nettyRpcServer.asm;

import combookservice.service.nettyRpcServer.reflect.MethodUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


/**
 * ASM ClassWriter初始化参数修改为自动计算MaxStack和MaxLocals 避免出现Arguments can't fit into
 * locals in class file 错误信息
 */
public class AsmByteBuilder extends ClassLoader implements Opcodes {

	// ASM基础数据类型字符串
	public final static String mAsmObjectStr = "Ljava/lang/Object;";
	public final static String mAsmArrayListStr = "Ljava/util/ArrayList;";
	public final static String mAsmStringStr = "Ljava/lang/String;";
	public final static String mAsmBytesStr = "[B";
	public final static String mAsmHashMapStr = "Ljava/util/HashMap;";
	// ASM当前定义字符串
	private final static String mAsmFunObject = "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;";
	private final static String mAsmFunVoid = "(Ljava/lang/Object;[Ljava/lang/Object;)V";
	private final static String mAsmVoid = "()V";
	private final static String mAsmInit = "<init>";
	private final static byte[] emptyBytes = new byte[0];

	private final static String mIntegerClassName = "java/lang/Integer";
	private final static String mIntegerValueOfMethodName = "valueOf";
	private final static String mIntegerValueOfMethodDes = "(I)Ljava/lang/Integer;";

	private final static String mFloatClassName = "java/lang/Float";
	private final static String mFloatValueOfMethodName = "valueOf";
	private final static String mFloatValueOfMethodDes = "(F)Ljava/lang/Float;";

	private final static String mDoubleClassName = "java/lang/Double";
	private final static String mDoubleValueOfMethodName = "valueOf";
	private final static String mDoubleValueOfMethodDes = "(D)Ljava/lang/Double;";

	private final static String mLongClassName = "java/lang/Long";
	private final static String mLongValueOfMethodName = "valueOf";
	private final static String mLongValueOfMethodDes = "(L)Ljava/lang/Long;";

	// ASM对象类型
	private final static String mAsmObjectBeanStr = "java/lang/Object";

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
	// /**
	// * 非静态函数调用时对象属性名称
	// */
	// private final static String mSpecialFieldName = "target";

	public AsmByteBuilder() {
		super(Thread.currentThread().getContextClassLoader());
	}

	/**
	 * 生成 ASMFunctionInvokerImpl接口对应调用实现
	 * 
	 *            要生成的类的名称
	 *            被调用的对象名称
	 * @param method
	 *            被调用的对象方法名
	 * @param argsTypes
	 *            被调用的方法的参数类型
	 * @return
	 * @throws ClassFormatError
	 * @throws IllegalAccessException
	 * @throws Exception
	 */
	public AsmFunctionInvokerImpl createASMFunctionInvoker(
			String generateClassName, Class<?> invokeClass, String method,
			Class<?>[] argsTypes) throws Exception {
		String[] implsStrings = new String[] { Type
				.getInternalName(AsmFunctionInvokerImpl.class) };
		byte[] bts = createAsmByte(generateClassName, invokeClass,
				implsStrings, "invokeFunction", mAsmFunObject, method,
				argsTypes);
		try {
			Class<?> mClass = defineClass(generateClassName, bts, 0, bts.length);
			return (AsmFunctionInvokerImpl) mClass.newInstance();
		} finally {
			bts = emptyBytes;
		}
	}

	/**
	 * 生成 ASMFunctionInvokerImpl接口对应调用实现
	 * 
	 *            要生成的类的名称
	 *            被调用的对象名称
	 * @param method
	 *            被调用的对象方法名
	 * @param argsTypes
	 *            被调用的方法的参数类型
	 * @return
	 * @throws ClassFormatError
	 * @throws IllegalAccessException
	 * @throws Exception
	 */
	public AsmActionInvokerImpl createASMActionInvokerImpl(
			String generateClassName, Class<?> invokeClass, String method,
			Class<?>[] argsTypes) throws Exception {
		String[] implsStrings = new String[] { Type
				.getInternalName(AsmActionInvokerImpl.class) };
		byte[] bts = createAsmByte(generateClassName, invokeClass,
				implsStrings, "invokeAction", mAsmFunVoid, method, argsTypes);
		try {
			Class<?> mClass = defineClass(generateClassName, bts, 0, bts.length);
			return (AsmActionInvokerImpl) mClass.newInstance();
		} finally {
			bts = emptyBytes;
		}
	}

	/**
	 * 生成函数调用字节码
	 * 
	 * 
	 * 
	 * @param generateClassName
	 *            集成的接口列表
	 * @param methodDes
	 * @param argsTypes
	 *            是否调用静态方法
	 * 
	 * 
	 * @return
	 */
	private byte[] createAsmByte(String generateClassName,
			Class<?> invokeClass, String[] impls, String implMethodName,
			String implMethodDes, String methodDes, Class<?>[] argsTypes)
			throws Exception {
		Method method = invokeClass.getMethod(methodDes, argsTypes);
		boolean isStatic = Modifier.isStatic(method.getModifiers());
		int returnType = MethodUtil.getMethodReturnType(method.getReturnType());
		generateClassName = generateClassName.replace(".", "/");
		// 自动计算MaxStack和MaxLocals
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		// ClassWriter cw = new ClassWriter(0);
		createConstructor(generateClassName, invokeClass, impls, cw, isStatic);
		// creates a MethodWriter for the (implicit) constructor
		if (isStatic) {
			createStaticInvoke(invokeClass, implMethodName, implMethodDes,
					methodDes, argsTypes, returnType, cw);
		} else {
			createInvoke(invokeClass, generateClassName, implMethodName,
					implMethodDes, methodDes, argsTypes, returnType, cw);
		}
		byte[] code = cw.toByteArray();
		cw = null;
		return code;
	}

	/**
	 * 创建构造函数
	 * 
	 * @param generateClassName
	 * @param impls
	 * @param cw
	 * @throws ClassNotFoundException
	 */
	private void createConstructor(String generateClassName,
			Class<?> invokeClass, String[] impls, ClassWriter cw,
			boolean isStatic) throws Exception {
		cw.visit(V1_1, ACC_PUBLIC, generateClassName, null, mAsmObjectBeanStr,
				impls);
		// creates a MethodWriter for the (implicit) constructor
		MethodVisitor mw = cw.visitMethod(ACC_PUBLIC, mAsmInit, mAsmVoid, null,
				null);
		// pushes the 'this' variable
		mw.visitVarInsn(ALOAD, 0);
		// invokes the super class constructor
		mw.visitMethodInsn(INVOKESPECIAL, mAsmObjectBeanStr, mAsmInit,
				mAsmVoid);
		// mw = handleUnStaticField(mw, cw, isStatic, invokeClass,
		// generateClassName);

		mw.visitInsn(RETURN);
		// this code uses a maximum of one stack element and one local variable
		mw.visitMaxs(0, 0);
		mw.visitEnd();
	}

	/**
	 * <ul>
	 * <li>创建静态函数action/function invoke</li>
	 * <li>继承接口方法 invoke(instance,args)</li>
	 * <li>生成调用代码 Target.statticInvokeMethod(args);</li>
	 * <li>instance参数在此无意义，仅为接口一致性保留</li>
	 * </ul>
	 * 
	 * @param implMethodName
	 * @param implMethodDes
	 * @param method
	 * @param cw
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	private void createStaticInvoke(Class<?> invokeClass,
			String implMethodName, String implMethodDes, String method,
			Class<?>[] argsTypes, int returnType, ClassWriter cw)
			throws Exception {
		// 调用静态方法
		MethodVisitor mw = cw.visitMethod(ACC_PUBLIC, implMethodName,
				implMethodDes, null, null);
		int argsCount = 0;
		if (argsTypes != null) {
			argsCount = argsTypes.length;
			for (int i = 0; i < argsCount; i++) {
				mw.visitVarInsn(ALOAD, 2);
				int opCode = getOpcode(i);
				if (opCode == BIPUSH) {
					mw.visitIntInsn(opCode, i);
				} else {
					mw.visitInsn(opCode);
				}
				mw.visitInsn(AALOAD);
				mw.visitTypeInsn(CHECKCAST, Type.getInternalName(argsTypes[i]));
			}
		}
		String invokeClassFormat = invokeClass.getName().replace('.', '/');
		Method meth = invokeClass.getMethod(method, argsTypes);
		String methodDes = Type.getMethodDescriptor(meth);
		mw.visitMethodInsn(INVOKESTATIC, invokeClassFormat, method, methodDes);
		mw = handleReturn(mw, returnType);
		// 设置为ASM自动计算，但是还是需要调用该方法
		mw.visitMaxs(0, 0);
		// mw.visitMaxs(argsCount * 2 + 2, argsCount + 2);
		mw.visitEnd();
	}

	/**
	 * <ul>
	 * <li>创建非静态函数action/function invoke</li>
	 * <li>继承接口方法 invoke(instance,args)</li>
	 * <li>生成调用代码 (T)instance.invokeMethod(args);</li>
	 * </ul>
	 * 
	 * @param implMethodName
	 * @param implMethodDes
	 * @param method
	 * @param cw
	 */
	private void createInvoke(Class<?> invokeClass, String generateClassName,
			String implMethodName, String implMethodDes, String method,
			Class<?>[] argsTypes, int returnType, ClassWriter cw)
			throws Exception {
		// 调用非静态方法
		MethodVisitor mw = cw.visitMethod(ACC_PUBLIC, implMethodName,
				implMethodDes, null, null);
		mw.visitVarInsn(ALOAD, 1);
		mw.visitTypeInsn(CHECKCAST, Type.getInternalName(invokeClass));
		mw.visitVarInsn(ASTORE, 3);
		mw.visitVarInsn(ALOAD, 3);
		int argsCount = 0;
		if (argsTypes != null) {
			argsCount = argsTypes.length;
			for (int i = 0; i < argsCount; i++) {
				mw.visitVarInsn(ALOAD, 2);
				int opCode = getOpcode(i);
				if (opCode == BIPUSH) {
					mw.visitIntInsn(opCode, i);
				} else {
					mw.visitInsn(opCode);
				}
				mw.visitInsn(AALOAD);
				mw.visitTypeInsn(CHECKCAST, Type.getInternalName(argsTypes[i]));
			}
		}
		String invokeClassFormat = invokeClass.getName().replace('.', '/');
		Method meth = invokeClass.getMethod(method, argsTypes);
		String methodDes = Type.getMethodDescriptor(meth);
		mw.visitMethodInsn(INVOKEVIRTUAL, invokeClassFormat, method, methodDes);
		mw = handleReturn(mw, returnType);
		// maximum stack size of the method.
		// maximum number of local variables for the method.
		mw.visitMaxs(0, 0);
		// mw.visitMaxs(argsCount * 2 + 2, argsCount + 2);
		mw.visitEnd();
	}

	// /**
	// * 适配非静态方法时，创建对象时初始化一个调用对象,生成后如下 class A { public Target target=new
	// * Target(); public Object invokeFunction(Object[] args){
	// * target.invokeMethod(args); } }
	// *
	// * @param mw
	// * @param cw
	// * @param isStatic
	// * @param invokeClass
	// * @param generateClassName
	// * @return
	// */
	// private MethodVisitor handleUnStaticField(MethodVisitor mw, ClassWriter
	// cw,
	// boolean isStatic, Class<?> invokeClass, String generateClassName) {
	// if (!isStatic) {
	// String filedDes = Type.getDescriptor(invokeClass);
	// String invokeClassName = Type.getInternalName(invokeClass);
	// cw.visitField(Opcodes.ACC_PUBLIC, mSpecialFieldName, filedDes,
	// null, null);
	// mw.visitVarInsn(ALOAD, 0);
	// mw.visitTypeInsn(NEW, invokeClassName);
	// // new address
	// mw.visitInsn(DUP);
	// // a=new ClassA();
	// mw.visitMethodInsn(INVOKESPECIAL, invokeClassName, mAsmInit,
	// mAsmVoid, false);
	// mw.visitFieldInsn(Opcodes.PUTFIELD, generateClassName,
	// mSpecialFieldName, filedDes);
	// }
	// return mw;
	// }

	/**
	 * 处理返回 基础类型采用对象类型的valueOf方法进行装箱
	 * 
	 * @param mw
	 * @param returnType
	 * @return
	 */
	private MethodVisitor handleReturn(MethodVisitor mw, int returnType) {
		if (returnType ==   VOID) {
			mw.visitInsn(RETURN);
		} else {

			switch (returnType) {
			case   DRETURN:
				mw.visitMethodInsn(INVOKESTATIC, mDoubleClassName,
						mDoubleValueOfMethodName, mDoubleValueOfMethodDes);
				break;
			case   FRETURN:
				mw.visitMethodInsn(INVOKESTATIC, mFloatClassName,
						mFloatValueOfMethodName, mFloatValueOfMethodDes);
				break;
			case   IRETURN:
				mw.visitMethodInsn(INVOKESTATIC, mIntegerClassName,
						mIntegerValueOfMethodName, mIntegerValueOfMethodDes);
				break;
			case   LRETURN:
				mw.visitMethodInsn(INVOKESTATIC, mLongClassName,
						mLongValueOfMethodName, mLongValueOfMethodDes);
				break;
			case   ARETURN:
				break;
			}
			mw.visitInsn(ARETURN);
		}
		return mw;
	}

	/**
	 * 获取要调用的函数的参数索引地址
	 * 
	 * @param i
	 * @return
	 */
	private int getOpcode(int i) {
		switch (i) {
		case 0:
			return ICONST_0;
		case 1:
			return ICONST_1;
		case 2:
			return ICONST_2;
		case 3:
			return ICONST_3;
		case 4:
			return ICONST_4;
		case 5:
			return ICONST_5;
		default:
			return BIPUSH;
		}
	}
}
