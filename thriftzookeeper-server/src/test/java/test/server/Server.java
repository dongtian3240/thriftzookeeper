package test.server;

import com.jd.thriftzookeeper.log.service.LogService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: lishuai
 * Date: 13-1-11
 * Time: 下午2:31
 * To change this template use File | Settings | File Templates.
 */
public class Server implements LogService.Iface {
    private static final Log logger = LogFactory.getLog(Server.class);
    @Override
    public String log(String type, String context) throws TException {
        logger.info("receive type = ["+type+"], context = ["+context+"].");
        return null;
    }
    public void startup() throws TTransportException {
        logger.info("Server is running..............");
        //服务的端口号
        int port = 8081;
        //线程池大小
        int threadPoolSize = 200;
        //工作队列的最大长度
        int workQueueSize = 100 * 10000;
        //selector线程数，建议为cpu核数
        int selectorThreads = 2 * 4;
        //每个selectorThreads支持的通道数，即多少个连接。
        int acceptQueueSizePerThread = 2000;
//        TBinaryProtocol.Factory protoFactory = new TBinaryProtocol.Factory(true, true);
        TCompactProtocol.Factory protoFactory = new TCompactProtocol.Factory();//.Factory(true, true);

        LogService.Processor<LogService.Iface> processor = new LogService.Processor<LogService.Iface>(
                this);

        TNonblockingServerTransport nioTransport = new TNonblockingServerSocket(port);

        ThreadPoolExecutor execservice = new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 6000L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(workQueueSize),
                new ThreadPoolExecutor.DiscardPolicy() {
                    public void rejectedExecution(Runnable r,
                                                  ThreadPoolExecutor e) {
                        logger.error("超出系统处理极限，忽略掉任务，工作队列长度-1000000");
                    }
                });
        TThreadedSelectorServer tr_server = new TThreadedSelectorServer(new TThreadedSelectorServer.Args(nioTransport)
                .processor(processor).protocolFactory(protoFactory)
                .selectorThreads(selectorThreads).acceptQueueSizePerThread(acceptQueueSizePerThread)
                .executorService(execservice));
        tr_server.serve();
    }

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.startup();
        } catch (TTransportException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
