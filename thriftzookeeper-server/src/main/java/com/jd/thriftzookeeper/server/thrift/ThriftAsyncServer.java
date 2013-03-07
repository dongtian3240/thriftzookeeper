package com.jd.thriftzookeeper.server.thrift;

import com.jd.thriftzookeeper.register.ServerAddress;
import com.jd.thriftzookeeper.server.config.ProcessorConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.*;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TNonblockingServerSocket;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: lishuai
 * Date: 13-1-14
 * Time: 下午5:09
 * To change this template use File | Settings | File Templates.
 */
public class ThriftAsyncServer<I> extends ThriftServer<I>{
    private static final Log logger = LogFactory.getLog(ThriftAsyncServer.class);

    private TThreadedSelectorServer _server;

    private TProcessor processor;
    private ServerAddress serverAddress;
    public ThriftAsyncServer(TProcessor processor,ServerAddress serverAddress){
        this.processor=processor;
        this.serverAddress=serverAddress;
    }


    @Override
    public void start() throws Exception {
        logger.info("Server config..............");
        String protocol = serverAddress.getProtocol();
        int port = serverAddress.getPort();

        TProtocolFactory protoFactory = null;

        if(TBinaryProtocol.class.getSimpleName().equals(protocol)){
            protoFactory = new TBinaryProtocol.Factory(true, true);
        }else if(TCompactProtocol.class.getSimpleName().equals(protocol)){
            protoFactory = new TCompactProtocol.Factory();
        }else if(TTupleProtocol.class.getSimpleName().equals(protocol)){
            protoFactory = new TTupleProtocol.Factory();
        }
        _server = new TThreadedSelectorServer(
                new TThreadedSelectorServer.Args(new TNonblockingServerSocket(port))
                .processor(processor)
                .protocolFactory(protoFactory)
                .selectorThreads(selectorThreads)
                .acceptQueueSizePerThread(acceptQueueSizePerThread)
                .stopTimeoutUnit(timeUnit)
                .stopTimeoutVal(timeoutVal)
                .executorService(threadPool));

        //必须启动一个线程,使zookeeper注册成功。
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                _server.serve();
            }
        });
        Thread.sleep(1*1000);
        logger.info("Server starting .......");
    }

    @Override
    public void stop() {
        //todo 关闭服务
        _server.stop();

    }

    @Override
    public ServerAddress getServerAddress() {
        return serverAddress;
    }


}
