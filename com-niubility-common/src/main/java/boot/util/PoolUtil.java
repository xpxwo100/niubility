package boot.util;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lvlianqi
 * @description 对象池工具类
 * @createDate 2020/4/28
 */
public class PoolUtil{

    private static final Logger LOGGER = LoggerFactory.getLogger(PoolUtil.class);

    private static final int maxTotal = 10;// 最大线程数
    private static final int maxIdle = 5;// 最大闲置线程
    private static final int minIdle = 5;// 最小闲置线程
    /**
     * 对象池配置
     */
    private static GenericObjectPoolConfig objectPoolConfig = new GenericObjectPoolConfig();

    static {
        objectPoolConfig.setMaxTotal(maxTotal);// 最大线程
        objectPoolConfig.setMaxIdle(maxIdle);// 最大闲置线程
        objectPoolConfig.setMinIdle(minIdle);// 最小闲置线程
    }

    /**
     * apache 封装的线程池
     */
    private static Map<String, ObjectPool<PoolObject>> objectPools = new HashMap<>();

    /**
     * apache自己封装的对象池
     * @return
     */
    private static ObjectPool getApacheObjectPool (Class<?> clazz){
        String className = clazz.getName();
        ObjectPool<PoolObject> objectPool = null;
        if(!objectPools.containsKey(className)){
            synchronized (objectPools.getClass()) {
                if(!objectPools.containsKey(className)){
                    PoolFactory poolFactory = new PoolFactory(clazz);
                    objectPool = new GenericObjectPool<PoolObject>(poolFactory, objectPoolConfig);
                    objectPools.put(className, objectPool);
                } else {
                    objectPool = objectPools.get(className);
                }
            }
        }else{
            objectPool = objectPools.get(className);
        }
        return objectPool;
    }

    /**
     * 从对象池获取对象
     * @param clazz 对象类型
     * @param <T>
     * @return
     */
    public static <T extends PoolObject> T getObject(Class<T> clazz){
        ObjectPool<PoolObject> objectPool = getApacheObjectPool(clazz);
        PoolObject poolObject = null;
        try {
            poolObject = objectPool.borrowObject();// 从线程池获取对象，
            //System.out.println("---------------------获取对象成功,对象编码："+threadPoolHandler.getCode());
            //LOGGER.info("---------------------获取对象成功,对象名：{}对象编码：{}-----------------------------------", poolObject.getClass().getSimpleName(), poolObject.getCode());
            poolObject.setObjectPool(objectPool);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return (T) poolObject;
    }
}
