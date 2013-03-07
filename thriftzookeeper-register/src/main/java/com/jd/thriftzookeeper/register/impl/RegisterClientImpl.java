package com.jd.thriftzookeeper.register.impl;

import com.jd.thriftzookeeper.client.ZookeeperClient;
import com.jd.thriftzookeeper.client.impl.ZKClientZookeeperClient;
import com.jd.thriftzookeeper.register.support.AbstractRegisterClient;
import org.apache.zookeeper.ZooKeeper;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-30
 * Time: 下午7:10
 * To change this template use File | Settings | File Templates.
 */
public class RegisterClientImpl extends AbstractRegisterClient {

    private String connectString;
    private ZookeeperClient zkClient;
    public RegisterClientImpl(String connectString){
        this.connectString=connectString;
    }

    @Override
    public ZookeeperClient getZookeeperClient() {
        zkClient = new ZKClientZookeeperClient(connectString);
        return zkClient;
    }

    public ZooKeeper getZookeeper(){
        return zkClient.getZookeeper();
    }
}
