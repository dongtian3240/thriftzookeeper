package com.jd.thriftzookeeper.server;

import com.jd.thriftzookeeper.register.RegisterClient;
import com.jd.thriftzookeeper.server.config.ProcessorConfig;
import com.jd.thriftzookeeper.server.thrift.ThriftAsyncServer;
import com.jd.thriftzookeeper.server.thrift.ThriftServer;
import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.TProcessor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-16
 * Time: 下午3:57
 * To change this template use File | Settings | File Templates.
 */
public interface ServerCluster {

    /**
     * 加入监控
     * @param server
     * @throws Exception
     */
     public void join(Server server)throws Exception;

    /**
     * 去掉监控信息
     */
    public void leave(Server server)throws Exception;
}
