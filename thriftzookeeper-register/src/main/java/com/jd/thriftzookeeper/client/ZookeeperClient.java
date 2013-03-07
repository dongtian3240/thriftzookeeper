package com.jd.thriftzookeeper.client;

import com.github.zkclient.ZkClient;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-30
 * Time: 下午5:48
 * To change this template use File | Settings | File Templates.
 */
public interface ZookeeperClient {

    public ZkClient getZkClient();

    ZooKeeper getZookeeper();

    void create(String path,byte[] bytes,boolean ephemeral);

    void delete(String path);

    List<String> getChildren(String path);

    List<String> addChildListener(String path, ChildListener listener);

    void removeChildListener(String path, ChildListener listener);

    void addStateListener(StateListener listener);

    void removeStateListener(StateListener listener);

    void addDataChanges(String path,DataListener dataListener);

    void removeDataChanges(String path,DataListener dataListener);

    boolean isConnected();

    void close();

    byte[] getNodeValue(String path);

}
