package com.jd.thriftzookeeper.pool.impl;
import com.jd.thriftzookeeper.pool.ConnectionProvider;
import com.jd.thriftzookeeper.pool.ThriftPoolConfig;
import com.jd.thriftzookeeper.pool.ThriftPoolableObjectFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-15
 * Time: 下午5:43
 * To change this template use File | Settings | File Templates.
 */
public class GenericConnectionProvider<T> implements ConnectionProvider<T> {

    private static final Log logger = LogFactory.getLog(GenericConnectionProvider.class);
    /** 对象缓存池 */
    private ObjectPool objectPool = null;
    private ThriftPoolConfig poolConfig;
    private Class ifaceClass;

    public GenericConnectionProvider(Class ifaceClass, ThriftPoolConfig poolConfig){
        this.ifaceClass=ifaceClass;
        this.poolConfig = poolConfig;

        try{
            this.afterPropertiesSet();
        }catch (Exception  e){
            e.printStackTrace();
        }
    }
    /**
     *
     */
    public void afterPropertiesSet() throws Exception
    {
        // 对象池
        objectPool = new GenericObjectPool();
        //
        ((GenericObjectPool) objectPool).setMaxActive(poolConfig.getMaxActive());
        ((GenericObjectPool) objectPool).setMaxIdle(poolConfig.getMaxIdle());
        ((GenericObjectPool) objectPool).setMinIdle(poolConfig.getMinIdle());
        ((GenericObjectPool) objectPool).setMaxWait(poolConfig.getMaxWait());
        ((GenericObjectPool) objectPool).setTestOnBorrow(poolConfig.isTestOnBorrow());
        ((GenericObjectPool) objectPool).setTestOnReturn(poolConfig.isTestOnReturn());
        ((GenericObjectPool) objectPool).setTestWhileIdle(poolConfig.isTestWhileIdle());
        ((GenericObjectPool) objectPool).setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
        // 设置factory
        ThriftPoolableObjectFactory<T> thriftPoolableObjectFactory = new ThriftPoolableObjectFactory<T>(ifaceClass,poolConfig.getServerAddress(), poolConfig.getConTimeOut());
        objectPool.setFactory(thriftPoolableObjectFactory);
    }

    public void destroy()
    {
        try
        {
            objectPool.close();
        }
        catch (Exception e)
        {
            throw new RuntimeException("erorr destroy()", e);
        }
    }
    @Override
    public T getConnection()
    {
        try
        {
            T client = (T) (objectPool.borrowObject());
            logger.debug("NumActive=  "+objectPool.getNumActive()+"      NumIdle = "+objectPool.getNumIdle()+"      -------->getConnection   ");
            return client;
        }
        catch (Exception e)
        {
            throw new RuntimeException("error getConnection()", e);
        }
    }
    @Override
    public void returnConnection(T client)
    {
        try
        {
            objectPool.returnObject(client);
            logger.debug("NumActive=  " + objectPool.getNumActive() + "      NumIdle = " + objectPool.getNumIdle() + "      -------->returnConnection   ");
        }
        catch (Exception e)
        {
            throw new RuntimeException("error returnCon()", e);
        }
    }
    public ObjectPool getObjectPool()
    {
        return objectPool;
    }
    public void setObjectPool(ObjectPool objectPool)
    {
        this.objectPool = objectPool;
    }
}
