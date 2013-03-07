package test.server;

import com.jd.thriftzookeeper.log.service.LogService;
import com.jd.thriftzookeeper.common.NetUtils;
import com.jd.thriftzookeeper.register.ServerAddress;
import com.jd.thriftzookeeper.server.thrift.ThriftAsyncServer;
import com.jd.thriftzookeeper.server.thrift.ThriftServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.TException;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: lishuai
 * Date: 13-1-14
 * Time: 下午6:23
 * To change this template use File | Settings | File Templates.
 */
public class SOAServer {
    private static final Log logger = LogFactory.getLog(SOAServer.class);
    public static void main(String[] args) throws Exception {

        //MyServiceImpl server
        LogService.Processor<LogService.Iface> service = new LogService.Processor<LogService.Iface>(
                new LogService.Iface() {
                    @Override
                    public String log(String type, String context) throws TException {
                        logger.info("SOAServer receive type = ["+type+"], context = ["+context+"].");
                        return null;
                    }
                });

        //线程池大小
        int threadPoolSize = 200;
        //工作队列的最大长度
        int workQueueSize = 100 * 10000;
        // MyThreadPool
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 6000L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(workQueueSize),
                new ThreadPoolExecutor.DiscardPolicy() {
                    public void rejectedExecution(Runnable r,
                                                  ThreadPoolExecutor e) {
                        logger.error("超出系统处理极限，忽略掉任务，工作队列长度-1000000");
                    }
                });

        ServerAddress serverAddress = new ServerAddress(NetUtils.getLocalHost(),NetUtils.getAvailablePort(),"TBinaryProtocol");
        ThriftServer<LogService.Iface> server = new ThriftAsyncServer<LogService.Iface>(service,serverAddress);
        server.setProcessor(service);
        server.setPort(8081);
        server.setThreadPool(threadPool);
        server.setTimeoutVal(6000);
        server.setTimeUnit(TimeUnit.MILLISECONDS);
        server.setSelectorThreads(4);
//      server.setAcceptQueueSizePerThread();
        server.start();
    }
}
