package com.jd.thriftzookeeper.pool;

import com.jd.thriftzookeeper.register.ServerAddress;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-24
 * Time: 下午8:47
 * To change this template use File | Settings | File Templates.
 */
public class ThriftPoolConfig {
    public ServerAddress serverAddress;
    private Class ifaceClass;
    /** 连接超时配置 */
    private int conTimeOut;
    /** 可以从缓存池中分配对象的最大数量 */
    private int maxActive = GenericObjectPool.DEFAULT_MAX_ACTIVE;
    /** 缓存池中最大空闲对象数量 */
    private int maxIdle = GenericObjectPool.DEFAULT_MAX_IDLE;
    /** 缓存池中最小空闲对象数量 */
    private int minIdle = GenericObjectPool.DEFAULT_MIN_IDLE;
    /** 阻塞的最大数量 */
    private long maxWait = GenericObjectPool.DEFAULT_MAX_WAIT;
    /** 从缓存池中分配对象，是否执行PoolableObjectFactory.validateObject方法 */
    private boolean testOnBorrow = GenericObjectPool.DEFAULT_TEST_ON_BORROW;
    private boolean testOnReturn = GenericObjectPool.DEFAULT_TEST_ON_RETURN;
    private boolean testWhileIdle = GenericObjectPool.DEFAULT_TEST_WHILE_IDLE;


    public ServerAddress getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(ServerAddress serverAddress) {
        this.serverAddress = serverAddress;
    }

    public Class getIfaceClass() {
        return ifaceClass;
    }

    public void setIfaceClass(Class ifaceClass) {
        this.ifaceClass = ifaceClass;
    }

    public int getConTimeOut() {
        return conTimeOut;
    }

    public void setConTimeOut(int conTimeOut) {
        this.conTimeOut = conTimeOut;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public long getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }
}
