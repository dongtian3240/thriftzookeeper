package com.jd.thriftzookeeper.main.client;

import com.jd.thriftzookeeper.register.impl.RegisterClientImpl;
import com.jd.thriftzookeeper.tutorial.test.Calculator;
import com.jd.thriftzookeeper.client.PooledClient;
import com.jd.thriftzookeeper.client.impl.PooledClientImpl;
import com.jd.thriftzookeeper.loadbalance.impl.RoundLoadBalancer;
import com.jd.thriftzookeeper.register.RegisterClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.async.AsyncMethodCallback;


/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-16
 * Time: 下午5:04
 * To change this template use File | Settings | File Templates.
 */
public class TestPooledClient {
    private static final Log logger = LogFactory.getLog(TestPooledClient.class);
    public static void main(String[] args)throws Exception{
        String connectString = "10.12.147.103:2181";
        if(args!=null&&args.length==1){
            connectString=args[0];
        }

        //1.new zkclient
        RegisterClient zkClient =new RegisterClientImpl(connectString);

        //2.new PooledClient
        PooledClient<Calculator.Iface> pooledClient = new PooledClientImpl(zkClient,"/jd/search/suggest");

        //3.获取业务处理类
        final Calculator.Iface myservice = pooledClient.get(new RoundLoadBalancer(),Calculator.Client.class);

        //4.具体业务处理
        while(true){
            try{
                Thread.sleep(1*1000);
                logger.error(myservice.add(1,2));
//
//              myservice.ping();
//              myservice.zip();

            }catch (Exception e){
              e.printStackTrace();
            }
        }
    }

    public static void main1(String[] args)throws Exception{

        RegisterClient zkClient = new RegisterClientImpl("10.12.147.103:2181");

        PooledClient<Calculator.AsyncClient> pooledClient = new PooledClientImpl(zkClient,"/jd/search/suggest");

        Calculator.AsyncIface myservice = pooledClient.get(new RoundLoadBalancer(),Calculator.AsyncClient.class);


        while(true){
            myservice.add(1,2,new AsyncMethodCallback<Calculator.AsyncClient.add_call>(){

                @Override
                public void onComplete(Calculator.AsyncClient.add_call response) {
                    try{
                        System.out.println(response.getResult());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception exception) {
                    exception.printStackTrace();
                }
            });

            Thread.sleep(1*1000);
        }
    }
}