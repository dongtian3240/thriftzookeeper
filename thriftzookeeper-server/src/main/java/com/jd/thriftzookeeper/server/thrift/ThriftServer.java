package com.jd.thriftzookeeper.server.thrift;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.jd.thriftzookeeper.server.Server;

/**
 * Created with IntelliJ IDEA.
 * User: lishuai
 * Date: 13-1-11
 * Time: 下午6:45
 * To change this template use File | Settings | File Templates.
 */
public abstract class ThriftServer<I> implements Server<I> {
    private static final Log logger = LogFactory.getLog(ThriftServer.class);
    //端口
    protected int port;
    //selector线程数，建议为cpu核数
    protected int selectorThreads = 2 * 4;
    //每个selectorThreads支持的通道数，即多少个连接。
    protected int acceptQueueSizePerThread = 2000;

    protected int timeoutVal = 1*1000;

    protected TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    protected ThreadPoolExecutor threadPool;

    protected TBaseProcessor<I> processor;
    /**
     * 启动服务
     * @throws Exception
     */
    public abstract void start() throws Exception;

    /**
     * 关闭服务
     * @throws Exception
     */

    public abstract void stop() throws Exception;


    public void setPort(int port) {
        this.port = port;
    }
    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public void setTimeoutVal(int timeoutVal) {
        this.timeoutVal = timeoutVal;
    }

    public void setSelectorThreads(int selectorThreads) {
        this.selectorThreads = selectorThreads;
    }

    public void setAcceptQueueSizePerThread(int acceptQueueSizePerThread) {
        this.acceptQueueSizePerThread = acceptQueueSizePerThread;
    }

    public void setThreadPool(ThreadPoolExecutor threadPool) {
        this.threadPool = threadPool;
    }

    public void setProcessor(TBaseProcessor<I> processor) {
        this.processor = processor;
    }
}
