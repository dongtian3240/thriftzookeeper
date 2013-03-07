package com.jd.thriftzookeeper.pool;
import com.jd.thriftzookeeper.client.impl.RealClient;
import com.jd.thriftzookeeper.register.ServerAddress;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.thrift.transport.TSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-15
 * Time: 下午5:43
 * To change this template use File | Settings | File Templates.
 */
public class ThriftPoolableObjectFactory<T> implements PoolableObjectFactory<T>{
    /** 日志记录器 */
    public static final Logger logger = LoggerFactory.getLogger(ThriftPoolableObjectFactory.class);
    private ServerAddress serverAddress;
    /** 超时设置 */
    private int timeOut;
    private Class ifaceClass;
    /**
     *
     * @param timeOut
     */
    public ThriftPoolableObjectFactory(Class ifaceClass,ServerAddress serverAddress,int timeOut){
        this.ifaceClass = ifaceClass;
        this.serverAddress = serverAddress;
        this.timeOut = timeOut;
    }
    @Override
    public void destroyObject(Object arg0) throws Exception
    {
        if (arg0 instanceof TSocket)
        {
            TSocket socket = (TSocket) arg0;
            if (socket.isOpen())
            {
                socket.close();
            }
        }
    }
    /**
     *
     */
    @Override
    public T makeObject() throws Exception
    {
        try
        {
          T t =  (new RealClient<T>(ifaceClass,serverAddress.getIp(),serverAddress.getPort(),serverAddress.getProtocol())).getRealClient();
          return t;
        }
        catch (Exception e)
        {
            logger.error("error ThriftPoolableObjectFactory()", e);
            throw new RuntimeException(e);
        }
    }
    //检验对象是否可以由pool安全返回
    @Override
    public boolean validateObject(Object arg0)
    {
        try
        {
            if (arg0 !=null)return true;
            else return false;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    //用来将对象作为idle状态
    @Override
    public void passivateObject(Object arg0) throws Exception
    {
        // DO NOTHING
    }

    /**
     * 激活对象,令对象可用
     * @param arg0
     * @throws Exception
     */
    @Override
    public void activateObject(Object arg0) throws Exception
    {
        // DO NOTHING
    }

    public int getTimeOut()
    {
        return timeOut;
    }
    public void setTimeOut(int timeOut)
    {
        this.timeOut = timeOut;
    }
}