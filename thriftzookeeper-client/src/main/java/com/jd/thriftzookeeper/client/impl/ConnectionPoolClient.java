package com.jd.thriftzookeeper.client.impl;

import com.jd.thriftzookeeper.cluster.ClusterAbleClient;
import com.jd.thriftzookeeper.pool.ConnectionManager;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-2-4
 * Time: 下午8:31
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionPoolClient<T> implements ClusterAbleClient{

    private ConnectionManager<T> connectionManager;
    private PoolExpandInfo poolExpandInfo;

    public ConnectionPoolClient(ConnectionManager connectionManager,PoolExpandInfo poolExpandInfo){
       this.connectionManager = connectionManager;
        this.poolExpandInfo = poolExpandInfo;
    }


    public ConnectionManager<T> getConnectionManager() {
        return connectionManager;
    }

    public PoolExpandInfo getPoolExpandInfo() {
        return poolExpandInfo;
    }

    public void illHealthy(){
        poolExpandInfo.incrementFailTimes();
        poolExpandInfo.setLastFailTime((new Date()).getTime());
    }
    public void toHealthy(){
        poolExpandInfo.toHealthy();
    }
    /**
     *  计算此连接池是否是健康的
     * @return
     */
    public boolean isHealthy(){
        //失败的次数
        int failTime = poolExpandInfo.getFailTimes();
        //最后一次失败的时间
        long lastFailTime =poolExpandInfo.getLastFailTime();

        long now =(new Date()).getTime();

        long num = now-lastFailTime-failTime*PoolExpandInfo.FREEZETIME*failTime;
        return num>0?true:false;
    }
}
