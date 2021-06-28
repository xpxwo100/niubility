package boot.util;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @author lvlianqi
 * @description 对象池工厂
 * @createDate 2020/4/28
 */
public class PoolFactory<T> extends BasePooledObjectFactory<T> {

    private Class<T> clazz;

    public PoolFactory(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 创建对象
     * @return
     * @throws Exception
     */
    @Override
    public T create() throws Exception {
        return clazz.newInstance();
    }
    /**
     * 包装对象
     * @param obj
     * @return
     */
    @Override
    public PooledObject<T> wrap(T obj) {
        return new DefaultPooledObject<T>(obj);
    }
}
