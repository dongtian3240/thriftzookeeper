package com.jd.thriftzookeeper.proxy.impl;

import com.jd.thriftzookeeper.client.impl.ConnectionPoolClient;
import com.jd.thriftzookeeper.loadbalance.LoadBalancer;
import com.jd.thriftzookeeper.policy.InvokerPolicy;
import com.jd.thriftzookeeper.pool.ConnectionManager;
import com.jd.thriftzookeeper.proxy.ProxyFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-23
 * Time: 下午2:20
 * To change this template use File | Settings | File Templates.
 */

/**
 * 具体业务调用类
 * @param <T>
 */
public class JdkProxyFactory<T> implements ProxyFactory<T> {

    private InvokerPolicy<T> invoker;
    public JdkProxyFactory(InvokerPolicy<T> invoker){
        this.invoker=invoker;
    }
    /**
     * 需要有失败重试功能
     * @param loadBalancer
     * @param map
     * @param ifaces
     * @return
     */
    public T getProxy(final LoadBalancer loadBalancer, final ConcurrentMap<String, ConnectionPoolClient> map,Class[] ifaces){


       return  (T)Proxy.newProxyInstance(this.getClass().getClassLoader(), ifaces, new InvocationHandler() {
           @Override
           public Object invoke(Object proxy, Method method, Object[] args)throws Exception {

              return invoker.invoke(loadBalancer,map,method,args);

           }
       });
    }
}
