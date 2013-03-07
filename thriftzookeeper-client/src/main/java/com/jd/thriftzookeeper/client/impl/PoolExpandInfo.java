package com.jd.thriftzookeeper.client.impl;

import com.jd.thriftzookeeper.register.ServerAddress;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-2-4
 * Time: 下午8:45
 * To change this template use File | Settings | File Templates.
 */

/**
 * 链接池扩展信息,后续可以添加权重等
 * 根据failTime和lastFailTime算出吃链接池现在是否是健康的
 */
public class PoolExpandInfo {

    public static int MAXFAILTIME = 5;
    /**
     * 失败冻结的时间,但失败failTime次时,应该冻结failTime*failTime毫秒
     */
    public static long FREEZETIME = 1000L;
    
    private int failTimes;

    private long lastFailTime;

    private ServerAddress serverAddress;

    public PoolExpandInfo(int failTimes,ServerAddress serverAddress){
        this.failTimes = failTimes;
        this.serverAddress=serverAddress;
    }

    public ServerAddress getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(ServerAddress serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getFailTimes() {
        return failTimes;
    }

    public void incrementFailTimes() {
        this.failTimes++;
    }

    public void toHealthy(){
        this.failTimes = 0;
    }

    public long getLastFailTime() {
        return lastFailTime;
    }

    public void setLastFailTime(long lastFailTime) {
        this.lastFailTime = lastFailTime;
    }
}
