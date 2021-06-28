package boot.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@SuppressWarnings("unchecked")
public class FSTUtil {

	private final static  Logger logger = LoggerFactory.getLogger(FSTUtil.class);

	//序列化函数
	/**
	 * 基础序列化函数
	 * @param obj
	 * @param cls
	 * @return
	 */
	public static byte[] encodeFst(Object obj,Class<?> cls){
		return encodeFST(obj,cls);
	}
	/**
	 * 基础序列化函数
	 * @param obj
	 * @param cls
	 * @return
	 */
	public static byte[] encodeFst(Object obj){
		return encodeFST(obj,null);
	}
	/**
	 * 序列化函数.ProtoStuff格式
	 * @param obj
	 * @param cls
	 * @return
	 */
	public static byte[] encodeBuf(Object obj,Class<?> cls){
		byte[] mBytes= Usual.mEmptyBytes;
		if(obj==null){
			return mBytes;
		}
		Schema<Object> mSchema= (Schema<Object>) ProtostuffUtil.getClassSchema(cls);
		LinkedBuffer mBuffer=getApplicationBuffer();
		try {
			if(mSchema!=null)
			{
				mBytes=ProtostuffIOUtil.toByteArray(obj, mSchema, mBuffer);
			}
		} catch (Exception e) {
			
		}finally{
			mBuffer.clear();
		}
		
		return mBytes;
	}
	/**
	 * 序列化函数.JSON格式
	 * @param obj
	 * @param cls
	 * @return
	 */
	public static byte[] encodeJson(Object obj,Class<?> cls){
		byte[] mBytes= Usual.mEmptyBytes;
		if(obj==null){
			return mBytes;
		}
		ObjectMapper mMapper=new ObjectMapper();
		mMapper.setDateFormat(Usual.mfAll);
		//无法使用Include.NON_NULL
		mMapper.setSerializationInclusion(Include.NON_NULL);
		try {
			mBytes=mMapper.writeValueAsBytes(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return mBytes;
	}
	/**
	 * 序列化函数.Java FST格式
	 * @param obj
	 * @return
	 */
	private static byte[] encodeFST(Object obj,Class<?> cls){
		byte[] mBytes= Usual.mEmptyBytes;
		if(obj==null){
			return mBytes;
		}
		FSTObjectOutput fstOut=null;
		try {
			fstOut= new FSTObjectOutput();
			if(cls!=null){
				fstOut.writeObject(obj,cls);
			}else{
				fstOut.writeObject(obj);
			}
			//不产生byte[]数组拷贝
			mBytes=fstOut.getBuffer();
//			mBytes=fstOut.getCopyOfWrittenBuffer();
			fstOut.flush();
			fstOut.close();
		} catch (Exception exp) {
			logger.error(exp.getMessage(),exp);
			mBytes= Usual.mEmptyBytes;
		}
		finally{
			if(fstOut!=null){
				fstOut=null;
			}
		}
		return mBytes;
	}

	//反序列化函数
	/**
	 * 基础反序列化函数
	 * @param data
	 * @param cls
	 * @return
	 */
	public static <T> T decodeFst(byte[] data,Class<?> cls){
		return FSTUtil.<T>decodeFST(data,cls);
	}
	/**
	 * 基础反序列化函数
	 * @param data
	 * @param cls
	 * @return
	 */
	public static <T> T decodeFst(byte[] data){
		return FSTUtil.<T>decodeFST(data,null);
	}
	/**
	 * 反序列化函数.ProtoStuff格式
	 * @param data
	 * @param cls
	 * @return
	 */
	public static Object decodeBuf(byte[] data,Class<?> cls){
		Object obj=null;
		if(data==null || data.length==0){
			return obj;
		}
		try {
			Schema<Object> mSchema = (Schema<Object>) ProtostuffUtil.getClassSchema(cls);
			obj=cls.newInstance();
			ProtostuffIOUtil.mergeFrom(data,obj , mSchema);
		} catch (Exception exp) {
			logger.error(exp.getMessage(),exp);
		}
		finally{
			data= Usual.mEmptyBytes;
		}
		return obj;
	}
	/**
	 * 反序列化函数.JSON格式
	 * @param data
	 * @param cls
	 * @return
	 */
	public static Object decodeJson(byte[] data,Class<?> cls){
		Object obj=null;
		if(data==null || data.length==0){
			return obj;
		}
		ObjectMapper mMapper=new ObjectMapper();
		mMapper.setDateFormat(Usual.mfAll);
		//无法使用Include.NON_NULL
		mMapper.setSerializationInclusion(Include.NON_NULL);
		try {
			obj=mMapper.readValue(data,cls);
		} catch (Exception exp) {
			logger.error(exp.getMessage(),exp);
		}
		finally{
			data= Usual.mEmptyBytes;
		}
		return obj;
	}
	/**
	 * 反序列化函数.Java FST格式
	 * @param data
	 * @return
	 */
	private static <T> T decodeFST(byte[] data,Class<?> cls){
		T obj=null;
		if(data==null || data.length==0){
			return obj;
		}
		ByteArrayInputStream inStream=null;
		FSTObjectInput fstIn=null;
		try {
			inStream=new ByteArrayInputStream(data);
			fstIn=new FSTObjectInput(inStream);
			//修改为ByteArrayInputStream方式
//			fstIn= new FSTObjectInput();
//			fstIn.resetForReuseUseArray(data);
			if(cls!=null){
				obj= (T)fstIn.readObject(cls);
			}else{
				obj= (T)fstIn.readObject();
			}
			inStream.close();
     		fstIn.close();
		} catch (Exception exp) {
			logger.error(exp.getMessage(),exp);
		}
		finally{
			data= Usual.mEmptyBytes;
			if(inStream!=null){
				inStream=null;
			}
			if(fstIn!=null){
				fstIn=null;
			}
			
		}
		return obj;
	}
	/**
	 * 反序列化函数.Java FST格式
	 * @param data
	 * @return
	 */
	public static <T> T decodeFST(InputStream inStream,Class<?> cls){
		T obj=null;
		FSTObjectInput fstIn=null;
		if(inStream==null){
			return obj;
		}
		try {
			fstIn= new FSTObjectInput(inStream);
			if(cls!=null){
				obj= (T)fstIn.readObject(cls);
			}else{
				obj= (T)fstIn.readObject();
			}
			inStream.close();
     		fstIn.close();
		} catch (Exception exp) {
			logger.error(exp.getMessage(),exp);
		}
		finally{
			if(inStream!=null){
				inStream=null;
			}
			if(fstIn!=null){
				fstIn=null;
			}
		}
		return obj;
	}
	
	//自定义函数
	/**
	 * 获取ProtoStuff LinkBuffer
	 * @return
	 */
	public static LinkedBuffer getApplicationBuffer()
	{
		return LinkedBuffer.allocate(Usual.mByteBaseSize);
	}
}
