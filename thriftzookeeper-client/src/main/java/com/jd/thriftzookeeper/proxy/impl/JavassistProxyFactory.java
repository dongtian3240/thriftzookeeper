package com.jd.thriftzookeeper.proxy.impl;

import com.jd.thriftzookeeper.client.impl.ConnectionPoolClient;
import com.jd.thriftzookeeper.loadbalance.LoadBalancer;
import com.jd.thriftzookeeper.pool.ConnectionManager;
import com.jd.thriftzookeeper.proxy.ProxyFactory;

import java.util.concurrent.ConcurrentMap;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-23
 * Time: 下午2:20
 * To change this template use File | Settings | File Templates.
 */
public class JavassistProxyFactory<T> implements ProxyFactory<T> {

    @Override
    public T getProxy(final LoadBalancer loadBalance,final ConcurrentMap<String, ConnectionPoolClient> map,Class[] ifaces){
        return null;
    }
}
