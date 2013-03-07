package com.jd.thriftzookeeper.policy.impl;

import com.jd.thriftzookeeper.client.impl.ConnectionPoolClient;
import com.jd.thriftzookeeper.client.impl.PoolExpandInfo;
import com.jd.thriftzookeeper.loadbalance.LoadBalancer;
import com.jd.thriftzookeeper.policy.support.AbstractClusterPolicy;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

import java.lang.reflect.Method;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-2-4
 * Time: 下午7:00
 * To change this template use File | Settings | File Templates.
 */
public class FailoverClusterPolicy<T> extends AbstractClusterPolicy<T> {

    /**
     * 失败最大转移次数
     */
    private static int FAILOVERTIMES = 3;

    /**
     * 需要检查链接是否健康,如果不健康,则继续选择，如果判断已经挂掉,则直接删除链接
     * @param loadBalancer
     * @param map
     * @param method
     * @param args
     * @return
     * @throws Exception
     */
    public  Object invoke(LoadBalancer loadBalancer,ConcurrentMap<String, ConnectionPoolClient> map,Method method, Object[] args) throws Exception{
        Exception exception =null;
        return invoke(0,loadBalancer,map,method,args,exception);
    }

    
    private Object invoke(int invokeTimes,LoadBalancer loadBalancer,ConcurrentMap<String, ConnectionPoolClient> map,Method method, Object[] args,Exception exception)throws Exception{
        //1.调用
        if(invokeTimes>=FailoverClusterPolicy.FAILOVERTIMES){
           throw new Exception(exception);
        }
        ConnectionPoolClient poolClient = this.select(loadBalancer,map);
        if(poolClient!=null){
            //需要先调用连接池是否健康
            if(poolClient.isHealthy()){
                Object o = null;
                try{
                   o = this.doInvoke(poolClient.getConnectionManager(),method,args);
                   //调通之后,需要把服务失败次数改为0
                   poolClient.toHealthy();
                }catch (Exception e){
                    if(e.getCause() instanceof TException){
                        //失败
                        exception = e;
                        poolClient.illHealthy();
                        o = invoke(invokeTimes+1,loadBalancer,map,method,args,exception);
                    }else{
                        throw e;
                    }
                }
                return o;
            }else {
                //服务关闭，则直接删除服务
                if(poolClient.getPoolExpandInfo().getFailTimes()>= PoolExpandInfo.MAXFAILTIME){
                    map.remove(poolClient.getPoolExpandInfo().getServerAddress().key());
                }
                if(invokeTimes<FailoverClusterPolicy.FAILOVERTIMES)return invoke(invokeTimes+1,loadBalancer,map,method,args,exception);
            }
        }else{
            Thread.sleep(1*1000);
            throw  new RuntimeException("sorry!server is down ,please check it .");
        }
        return null;
    }
}
