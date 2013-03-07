package com.jd.thriftzookeeper.pool;

import com.jd.thriftzookeeper.cluster.ClusterAbleClient;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-15
 * Time: 下午5:43
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionManager<T>{
    /** 日志记录器 */
    public Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
    /** 保存local对象 */
    ThreadLocal<T> socketThreadSafe = new ThreadLocal<T>();
    /** 连接提供池 */
    public ConnectionProvider connectionProvider;

    public Object invoke(Method method, Object[] args) throws Exception
    {
        T client = null;
        Object result = null;
        try
        {
            client = (T)(connectionProvider.getConnection());
            socketThreadSafe.set(client);
            result = method.invoke(this.getClient(),args);
        }catch (Exception e)
        {
            throw e;
        }
        finally
        {
            connectionProvider.returnConnection(client);
            socketThreadSafe.remove();
        }
        return result;
    }
    /**
     * 取socket
     *
     * @return
     */
    public T getClient()
    {
        return socketThreadSafe.get();
    }
    public ConnectionProvider getConnectionProvider()
    {
        return connectionProvider;
    }
    public void setConnectionProvider(ConnectionProvider connectionProvider)
    {
        this.connectionProvider = connectionProvider;
    }
}
