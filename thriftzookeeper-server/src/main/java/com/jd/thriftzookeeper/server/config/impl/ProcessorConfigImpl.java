package com.jd.thriftzookeeper.server.config.impl;

import com.jd.thriftzookeeper.server.config.ProcessorConfig;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-21
 * Time: 上午11:55
 * To change this template use File | Settings | File Templates.
 */
public class ProcessorConfigImpl implements ProcessorConfig{

    //线程池大小
   private int threadPoolSize = 200;
    //工作队列的最大长度
   private int workQueueSize = 10000;

   private long keepAliveTime = 6000;

   private int timeoutVal = 2*1000;

   private int selectorThreads = 4;

   public ProcessorConfigImpl(){}

    public ProcessorConfigImpl(int threadPoolSize, int workQueueSize, long keepAliveTime, int timeoutVal, int selectorThreads){
        this.threadPoolSize=threadPoolSize;
        this.workQueueSize=workQueueSize;
        this.keepAliveTime=keepAliveTime;
        this.timeoutVal=timeoutVal;
        this.selectorThreads=selectorThreads;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public int getWorkQueueSize() {
        return workQueueSize;
    }

    public void setWorkQueueSize(int workQueueSize) {
        this.workQueueSize = workQueueSize;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public int getTimeoutVal() {
        return timeoutVal;
    }

    public void setTimeoutVal(int timeoutVal) {
        this.timeoutVal = timeoutVal;
    }

    public int getSelectorThreads() {
        return selectorThreads;
    }

    public void setSelectorThreads(int selectorThreads) {
        this.selectorThreads = selectorThreads;
    }
}
