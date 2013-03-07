package com.jd.thriftzookeeper.policy;

import com.jd.thriftzookeeper.client.impl.ConnectionPoolClient;
import com.jd.thriftzookeeper.loadbalance.LoadBalancer;
import com.jd.thriftzookeeper.pool.ConnectionManager;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-2-4
 * Time: 下午6:55
 * To change this template use File | Settings | File Templates.
 */
public interface InvokerPolicy<T> {
    Object invoke(LoadBalancer loadBalancer,ConcurrentMap<String, ConnectionPoolClient> map,Method method, Object[] args) throws Exception;
}
