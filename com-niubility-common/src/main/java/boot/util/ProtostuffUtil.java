package boot.util;

import io.protostuff.LinkedBuffer;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Protostuff序列化基础库
 */
public class ProtostuffUtil
{
	/**
	 * 保存动态执行函数的静态Class对象锁
	 */
	private static Lock cht_stLock = new ReentrantLock();
	private static HashMap<String,Schema<?>> mStuffMap=new HashMap<String,Schema<?>>();
	/**
	 * 获取LinkedBuffer
	 * @return
	 */
	public static LinkedBuffer getApplicationBuffer()
	{
		return LinkedBuffer.allocate(4096);
	}
	/**
	 * 获取指定Class Schema
	 * @param mClass
	 * @return
	 */
	public static Schema<?> getClassSchema(Class<?> mClass)
	{
		String mClassName=mClass.getName();
		boolean mKeyState=false;
		Schema<?> mSchema = null;
		cht_stLock.lock();
		try
		{
			mKeyState=mStuffMap.containsKey(mClassName);
		}
		finally
		{
			cht_stLock.unlock();
		}
		//判断缓存中是否已经存在对应Schema
		if(mKeyState==false)
		{
			mSchema = RuntimeSchema.getSchema(mClass);
			cht_stLock.lock();
			try
			{
				mStuffMap.put(mClassName,mSchema);
			}
			finally
			{
				cht_stLock.unlock();
			}
		}
		else
		{
			mSchema=mStuffMap.get(mClassName);
		}
		return mSchema;
	}
	/**
	 * 增加指定Class Schema
	 * @param mClassName
	 * @param mSchema
	 */
	public static void addClassSchema(String mClassName,Schema<?> mSchema)
	{
		if(mClassName!=null && mClassName.trim().length()>0)
		{
			return;
		}
		cht_stLock.lock();
		try
		{
			if(!mStuffMap.containsKey(mClassName))
			{
				mStuffMap.put(mClassName,mSchema);
			}
		}
		finally
		{
			cht_stLock.unlock();
		}
	}
}
