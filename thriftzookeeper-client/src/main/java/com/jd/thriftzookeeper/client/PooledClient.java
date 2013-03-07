package com.jd.thriftzookeeper.client;

import com.jd.thriftzookeeper.loadbalance.LoadBalancer;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-15
 * Time: 下午5:43
 * To change this template use File | Settings | File Templates.
 */
public interface PooledClient<T>{

    /**
     * 失败
     * @param loadBalance
     * @param client
     * @return
     */
    public T get(LoadBalancer loadBalance,Class client);

}
