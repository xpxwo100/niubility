package boot.util;

import org.apache.commons.pool2.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * @author lvlianqi
 * @description 抽象类对象
 * @createDate 2020/4/28
 */
public abstract class PoolObject  {



    private static final Logger LOGGER = LoggerFactory.getLogger(PoolObject.class);
    /**
     * 对象编码，没啥用，只是为了测试是不是创建了新的对象
     */
    private String code = UUID.randomUUID().toString();
    /**
     * 对象池
     */
    private ObjectPool objectPool;

    public void setObjectPool(ObjectPool objectPool){
        this.objectPool = objectPool;
    }

    public String getCode() {
        return code;
    }

    /**
     * 归还对象
     */
    public void returnObject(){
        try {
            objectPool.returnObject(this);
            //System.out.println("---------------------归还对象成功,对象编码："+code);
            //LOGGER.info("---------------------归还对象成功,对象名：{} 对象编码：{}-----------------------------------", this.getClass().getSimpleName(), code);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
