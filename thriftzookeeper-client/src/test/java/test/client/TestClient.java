package test.client;

import com.jd.thriftzookeeper.log.service.LogService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.*;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: lishuai
 * Date: 13-1-11
 * Time: 下午2:56
 * To change this template use File | Settings | File Templates.
 */
public class TestClient {
    private static final Log logger = LogFactory.getLog(TestClient.class);
    public static void main1(String[] args) throws TException, InterruptedException,Exception {
        String ip = "10.12.212.19";
        int port = 8081;
        int timeout = 3*1000;

        TTransport transport = new TFramedTransport(new TSocket(ip,port,timeout));
        transport.open();
//        TProtocol protocol = new TBinaryProtocol(transport);
        TProtocol protocol = new TCompactProtocol(transport);
//        TProtocol protocol = new TTupleProtocol(transport);
        LogService.Iface service = new LogService.Client(protocol);

        TAsyncClientManager clientManager = new TAsyncClientManager();
//        LogService.AsyncIface service= new LogService.AsyncClient(protocol,clientManager,transport);


        long init = 1;
        long processNS = 0;
        while (true){
            long start = System.nanoTime();
            service.log("1","2");
            long currentns = System.nanoTime()-start;
            if(init==1){
                processNS+=currentns;
                ++init;
            }else {
                processNS = (processNS+currentns)/2;
                logger.info("send time avg ns = "+processNS);
            }
//            logger.info("send time ns = "+currentns);
        }
//        transport.close();
    }
    
    public static void main(String[] args)throws Exception{
        TAsyncClientManager clientManager = new TAsyncClientManager();
        TNonblockingTransport transport = new TNonblockingSocket("10.12.147.103", 38540, 2*1000);
//        TProtocolFactory protocol = new TCompactProtocol.Factory();
        TProtocolFactory protocol = new TBinaryProtocol.Factory();
        LogService.AsyncClient asyncClient = new LogService.AsyncClient(protocol, clientManager, transport);
        System.out.println("Client calls .....");


        asyncClient.log("kevin", "verygood", new AsyncMethodCallback<LogService.AsyncClient.log_call>() {
            @Override
            public void onComplete(LogService.AsyncClient.log_call response) {
                try{
                    System.out.println(response.getResult());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Exception exception) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        while (true) {
            Thread.sleep(1);
        }
    }
}
