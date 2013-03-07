package com.jd.thriftzookeeper.main.server;

import com.jd.thriftzookeeper.register.impl.RegisterClientImpl;
import com.jd.thriftzookeeper.tutorial.test.Calculator;
import com.jd.thriftzookeeper.tutorial.test.InvalidOperation;
import com.jd.thriftzookeeper.tutorial.test.SharedStruct;
import com.jd.thriftzookeeper.tutorial.test.Work;
import com.jd.thriftzookeeper.common.NetUtils;
import com.jd.thriftzookeeper.register.RegisterClient;
import com.jd.thriftzookeeper.register.ServerAddress;
import com.jd.thriftzookeeper.server.Server;
import com.jd.thriftzookeeper.server.ServerCluster;
import com.jd.thriftzookeeper.server.thrift.ServerClusterImpl;
import com.jd.thriftzookeeper.server.thrift.ThriftAsyncServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.TException;


/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-16
 * Time: 下午4:31
 * To change this template use File | Settings | File Templates.
 */
public class TestServerCluster {
    private static final Log logger = LogFactory.getLog(TestServerCluster.class);
    public static void main(String[] args)throws Exception{

        String connectString = "10.12.147.103:2181";
        if(args!=null&&args.length==1){
           connectString=args[0];
        }

        //1.具体业务处理
        final Calculator.Processor<Calculator.Iface> service = new Calculator.Processor<Calculator.Iface>(
                new Calculator.Iface() {

                    @Override
                    public void ping() throws TException {
                        logger.info("----------ping()-----------");
                    }

                    @Override
                    public int add(int num1, int num2) throws TException {
                        logger.info("add("+num1+","+num2+")");
                        return num1+num2;  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public int calculate(int logid, Work w) throws InvalidOperation, TException {
                        logger.info(logid+"----"+w);
                        return 0;  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public void zip() throws TException {
                        logger.info("------zip()---------------");
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public SharedStruct getStruct(int key) throws TException {
                        logger.info("-------------"+key+"----------------");
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }
                });

        //2.new zkClient
        RegisterClient zkClient = new RegisterClientImpl(connectString);

        //3.获取服务地址和端口
        ServerAddress serverAddress = new ServerAddress(NetUtils.getLocalHost(),NetUtils.getAvailablePort(),"TBinaryProtocol");

        //4.启动服务 && 加入监控
        final ServerCluster cluster = new ServerClusterImpl(zkClient,"/jd/search/suggest");
        final Server<Calculator.Processor> server = new ThriftAsyncServer<Calculator.Processor>(service,serverAddress);
        server.start();

        //5.将服务加入到集群中
        cluster.join(server);

        //6.但服务正常退出时,主动删除服务注册信息和停止信息
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                try{
                    //1.删除服务注册信息
                    cluster.leave(server);
                    Thread.sleep(3*1000);
                    //2.停止服务
                    server.stop();
                }catch (Exception e){

                }

            }
        });
    }
}