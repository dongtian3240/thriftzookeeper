package com.jd.thriftzookeeper.register.monitor;

import com.jd.thriftzookeeper.register.ServerAddress;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-21
 * Time: 下午4:22
 * To change this template use File | Settings | File Templates.
 */
public interface Watcher {
    /**
     * 注册监听器,监听节点变化名称,获取path下的子节点可服务列表
     * @param path
     * @param serverAddresses
     */
    public void processChildNodeName(String path,List<ServerAddress> serverAddresses);

    /**
     * 获取path结点的value值
     * @param path
     * @param serverAddress
     */
    public void processNodeValue(String path,ServerAddress serverAddress);
}
