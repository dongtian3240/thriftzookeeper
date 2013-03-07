package com.jd.thriftzookeeper.client.impl;

import com.jd.thriftzookeeper.client.PooledClient;
import com.jd.thriftzookeeper.cluster.AbstractPool;
import com.jd.thriftzookeeper.loadbalance.LoadBalancer;
import com.jd.thriftzookeeper.policy.InvokerPolicy;
import com.jd.thriftzookeeper.policy.impl.FailoverClusterPolicy;
import com.jd.thriftzookeeper.pool.impl.GenericConnectionProvider;
import com.jd.thriftzookeeper.pool.ThriftPoolConfig;
import com.jd.thriftzookeeper.proxy.ProxyFactory;
import com.jd.thriftzookeeper.proxy.impl.JdkProxyFactory;
import com.jd.thriftzookeeper.register.RegisterClient;
import com.jd.thriftzookeeper.register.ServerAddress;
import com.jd.thriftzookeeper.register.monitor.Watcher;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-29
 * Time: 下午2:16
 * To change this template use File | Settings | File Templates.
 */
public class PooledClientImpl<T> extends AbstractPool<T> implements PooledClient<T>{
    private RegisterClient zkClient;
    private String directory;
    private Class ifaceClass;
    private InvokerPolicy invokerPolicy = null;

    public PooledClientImpl(RegisterClient zkClient,String directory){
        this.zkClient = zkClient;
        //监控directory,当节点变化时,及时更新节点变化

        this.directory=directory;

        zkClient.setDirectory(directory);

        invokerPolicy = new FailoverClusterPolicy();

    }
    /**
     * 获取 javassist生成的代理client
     */
    @Override
    public T get(LoadBalancer loadBalance,Class ifaceClass){
        this.ifaceClass=ifaceClass;

        //新建连接池
        init();
        //1.获取所有可用client
        ConcurrentMap<String, ConnectionPoolClient> map = this.map;
        //2.得到代理类的接口
        Class<?>[] ifaces = ifaceClass.getInterfaces();
        if(ifaces.length==1){
            //3.封装带到代理client
            ProxyFactory<T> proxyFactory = new JdkProxyFactory<T>(invokerPolicy);
            return proxyFactory.getProxy(loadBalance,map,ifaces);
        }
        return null;
    }

    /**
     * 监控服务变化
     */
    @Override
    public void doCheck(){
        try{
            String path = RegisterClient.NAMESPACE+directory+"/rpc";
            //监听节点名变化
            zkClient.registerWatcher(new Watcher() {
                @Override
                public void processChildNodeName(String path, List<ServerAddress> serverAddresses) {
                    buildMap();
                }

                @Override
                public void processNodeValue(String path, ServerAddress serverAddress) {
                    //判断serverAddress 状态,如果为1则添加到服务列表中. 为0则要立即删除
                    buildMap(serverAddress);
                }
            },path,true);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected List<ServerAddress> findConfigs() {
        //只获取nodevalue 的status:1的服务列表
        return zkClient.getServers();
    }

    @Override
    protected GenericConnectionProvider getGenericConnectionProvider(ServerAddress serverAddress){
        try{
            ThriftPoolConfig poolConfig = new ThriftPoolConfig();
            poolConfig.setServerAddress(serverAddress);
            poolConfig.setMaxActive(10);
            poolConfig.setMaxIdle(10);

            //为true 则报错
            poolConfig.setTestOnBorrow(false);
            poolConfig.setTestOnReturn(true);
            poolConfig.setTestWhileIdle(true);
            poolConfig.setConTimeOut(2*1000);
            poolConfig.setIfaceClass(ifaceClass);
            GenericConnectionProvider provider = new GenericConnectionProvider(ifaceClass, poolConfig);

            return provider;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
