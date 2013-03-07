package com.jd.thriftzookeeper.server.config;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-21
 * Time: 上午11:55
 * To change this template use File | Settings | File Templates.
 */
public interface ProcessorConfig {

    public int getThreadPoolSize();

    public int getWorkQueueSize();

    public long getKeepAliveTime();

    public int getTimeoutVal();

    public int getSelectorThreads();

}
