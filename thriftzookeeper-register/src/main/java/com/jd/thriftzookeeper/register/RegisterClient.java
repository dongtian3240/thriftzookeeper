package com.jd.thriftzookeeper.register;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-15
 * Time: 上午10:56
 * To change this template use File | Settings | File Templates.
 */

import com.jd.thriftzookeeper.client.ZookeeperClient;
import com.jd.thriftzookeeper.register.monitor.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;

/**
 * 向zookeeper中提供注册信息
 * 1.在目录xxx下注册零时节点 serers & monitor
 * 2.但零时节点删除后,自动提醒
 * 3.当网络中断后，server 必须尝试重新注册自己
 */
public interface RegisterClient {

    static String NAMESPACE="/jdns";
    /**
     *
     * @param watcher
     * @param wakeRepeat true 多次唤醒,false 只唤醒一次
     */
    void registerWatcher(final Watcher watcher,final String path,final boolean wakeRepeat);

    /**
     * 当服务已经死掉，但zookeeper还没有清除掉临时节点,服务端又注册相同端口,会报错
     * @param serverAddress
     */
    void registerServer(ServerAddress serverAddress);

    /**
     * 注册监控信息
     * @param serverAddress
     * @param bytes
     */
    void registerMoniter(ServerAddress serverAddress,byte[] bytes);

    /**
     * 注销服务注册信息
     * @param serverAddress
     */
    void cancelServer(ServerAddress serverAddress);

    /**
     * 注销服务监控信息
     * @param serverAddress
     */
    void cancelMoniter(ServerAddress serverAddress);
    /**
     * 得到可用服务列表
     * @return
     */
    List<ServerAddress> getServers();

    /**
     * 得到监控列表
     * @return
     */
    List<MonitorMessage> getMoniters();

    /**
     * 得到zookeeperclient
     * @return
     */
    ZookeeperClient getZookeeperClient();

    /**
     * 得到原生态zookeeper
     * @return
     */
    ZooKeeper getZookeeper();

    /**
     * 设置服务放置路径
     * @param directory
     */
    void setDirectory(String directory);

}
