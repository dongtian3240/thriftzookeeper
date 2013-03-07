package com.jd.thriftzookeeper.policy.support;

import com.jd.thriftzookeeper.client.impl.ConnectionPoolClient;
import com.jd.thriftzookeeper.loadbalance.LoadBalancer;
import com.jd.thriftzookeeper.policy.InvokerPolicy;
import com.jd.thriftzookeeper.pool.ConnectionManager;
import org.apache.thrift.transport.TTransportException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-2-4
 * Time: 下午6:59
 * To change this template use File | Settings | File Templates.
 */

/**
 * 具体执行放在子类中,选择那个池执行放在此类中,注意负责均衡和失败策略的区分
 * @param <T>
 */
public abstract class AbstractClusterPolicy<T> implements InvokerPolicy<T> {

    /**
     * 使用loadbalance选择线程池</br>
     */
    protected ConnectionPoolClient select(LoadBalancer loadBalancer,ConcurrentMap<String, ConnectionPoolClient> map) throws Exception{

        return loadBalancer.select(new ArrayList<ConnectionPoolClient>(map.values()));
    }

    /**
     * 具体业务调用
     * @return
     * @throws Exception
     */
    public abstract Object invoke(LoadBalancer loadBalancer,ConcurrentMap<String, ConnectionPoolClient> map,Method method, Object[] args) throws Exception;

    protected  Object doInvoke(ConnectionManager connectionManager,Method method, Object[] args)throws Exception{
        return connectionManager.invoke(method,args);
    }
}
