package com.jd.thriftzookeeper.cluster;

import com.jd.thriftzookeeper.client.impl.ConnectionPoolClient;
import com.jd.thriftzookeeper.client.impl.PoolExpandInfo;
import com.jd.thriftzookeeper.pool.ConnectionManager;
import com.jd.thriftzookeeper.pool.impl.GenericConnectionProvider;
import com.jd.thriftzookeeper.register.ServerAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@SuppressWarnings("hiding")
public abstract class AbstractPool<T> extends Pool<T> {

	protected Logger logger = LoggerFactory.getLogger(getClass().getName());

	/*
	 * 是否已初始化
	 */
	private boolean inited = false;

	/*
	 * 客户端实例
	 */
	protected ConcurrentMap<String, ConnectionPoolClient> map = new ConcurrentHashMap<String, ConnectionPoolClient>();
	/*
	 * 配置信息
	 */
	protected ConcurrentMap<String, ServerAddress> configMap = new ConcurrentHashMap<String, ServerAddress>();


    protected AbstractPool(){}
	/*
	 * 初始化，启动定时任务
	 */
	public void init() {

        //创建链接
		buildMap();

        //监听变化
		starttimer();

		inited = true;
	}

    /**
     * 启动一个线程，进行监控
     */
	protected void starttimer() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                 doCheck();
            }
        });

	}

	/*
	 * 监听目录变化
	 */
	public abstract void doCheck();

    /*
	 * 根据配置信息把客户端都给创建出来
	 */
    protected synchronized void buildMap() {
        List<ServerAddress> configs = findConfigs();
        buildMap(configs);
    }

    protected synchronized void buildMap(List<ServerAddress> serverAddresses){
        List<ServerAddress> configs = serverAddresses;
        if (configs == null) {
            configs = new ArrayList<ServerAddress>();
        }
        configMap.clear();
        //将推送过来的新的服务,加入服务列表中
        for (ServerAddress config : configs) {
            configMap.put(config.key(), config);
            //不包含此服务
            if (!map.containsKey(config.key())) {
                //服务正常对外提供服务
                if(config.getStatus() == 1 ){
                    logger.debug("find server-" + config.key());
                    configMap.putIfAbsent(config.key(), config);
                    ConnectionPoolClient client = getConnectionPoolClient(config);
                    if (client != null) {
                        logger.debug("创建客户端成功-{}", config.key());
                        map.putIfAbsent(config.key(), client);
                    }
                }else{
                    //不对外提供服务
                }

            }else{
                //包含此服务
                if(config.getStatus() == 1){//服务正常,让服务变成健康的
                    ConnectionPoolClient client = map.get(config.key());
                    client.toHealthy();
                }else{

                }
            }
        }
        /**
         * 不再删除没有推送过来的服务列表,因为可能server可能正常工作,但跟zookeeper之间网络不通,此时要让server正常工作
         *当client跟server不通,但server跟zookeeper通，client也跟server通时,也要根据将权规则删除服务
         */
// 清掉服务列表里没有的
//        Iterator<Map.Entry<String, ConnectionManager>> it = map.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry<String, ConnectionManager> entry = it.next();
//            if (!configMap.containsKey(entry.getKey())) {
//                logger.debug("服务列表里没有了-{}", entry.getKey());
//                it.remove();
//            }
//        }
    }

    protected synchronized  void buildMap(ServerAddress serverAddress){
        if(serverAddress!=null){
            switch (serverAddress.getStatus()){
                case 0:
                    if(map.containsKey(serverAddress.key())){
                        map.remove(serverAddress.key());
                    }
                    break;
                case 1:
                    if(!map.containsKey(serverAddress.key())){
                        ConnectionPoolClient client = getConnectionPoolClient(serverAddress);
                        if (client != null) {
                            logger.debug("创建客户端成功-{}", serverAddress.key());
                            map.putIfAbsent(serverAddress.key(), client);
                        }
                    }
                    break;
                default:
                    System.out.println("server status should is 1 or 0 ");
            }
        }

    }
    protected ConnectionManager getConnectionManager(ServerAddress config){
        GenericConnectionProvider<T>  connectionProvider = getGenericConnectionProvider(config);

        ConnectionManager<T>  connectionManager = new ConnectionManager<T>();
        connectionManager.setConnectionProvider(connectionProvider);
        return connectionManager;
    }
    protected ConnectionPoolClient getConnectionPoolClient(ServerAddress config){
        //得到链接管理者
        ConnectionManager connectionManager = getConnectionManager(config);
        //得到PoolExpandInfo
        PoolExpandInfo expandInfo = new PoolExpandInfo(0,config);
        ConnectionPoolClient client = new ConnectionPoolClient(connectionManager,expandInfo);
        return client;
    }
    /**
     * thrift 连接池
     * @param config
     * @return
     */
    protected abstract GenericConnectionProvider<T> getGenericConnectionProvider(ServerAddress config);

    protected abstract List<ServerAddress> findConfigs();

}
