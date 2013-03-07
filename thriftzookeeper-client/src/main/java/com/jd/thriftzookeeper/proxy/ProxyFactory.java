package com.jd.thriftzookeeper.proxy;

import com.jd.thriftzookeeper.client.impl.ConnectionPoolClient;
import com.jd.thriftzookeeper.loadbalance.LoadBalancer;

import java.util.concurrent.ConcurrentMap;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-23
 * Time: 下午2:16
 * To change this template use File | Settings | File Templates.
 */
public interface ProxyFactory<T> {
    /**
     * create proxy.
     *
     * @param invoker
     * @return proxy
     */
     T getProxy(LoadBalancer invoker,ConcurrentMap<String, ConnectionPoolClient> map,Class[] ifaces);
}
