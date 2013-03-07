package com.jd.thriftzookeeper.zkclient.test;

import com.github.zkclient.ZkClient;
import com.github.zkclient.ZkConnection;
import com.jd.thriftzookeeper.client.ChildListener;
import com.jd.thriftzookeeper.client.StateListener;
import com.jd.thriftzookeeper.client.ZookeeperClient;
import com.jd.thriftzookeeper.client.impl.ZKClientZookeeperClient;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-30
 * Time: 下午4:29
 * To change this template use File | Settings | File Templates.
 */
public class ZKClientTest {
    private static final int TIMEOUT = 60 * 1000;

    public static void main1(String[] args)throws Exception{
        String connectString = "10.12.147.196:2181,10.12.147.195:2181,10.12.147.197:2181";
        String namespace = "/jdns";

        ZooKeeper zk = new ZooKeeper(connectString, TIMEOUT, new Watcher() {

            @Override
            public void process(WatchedEvent event) {
            }
        });

        zk.sync("/", null, null);

        Stat s = zk.exists(namespace, false);
        if (s == null) {
            zk.create(namespace, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT);
        }



        ZkConnection zkConnection = new ZkConnection("10.12.147.103:2181");

        ZkClient zkClient = new ZkClient(zkConnection, 2000);

        Thread.sleep(10*1000);
        System.out.println("---------1-------------");
        zkClient.getChildren("/");
        System.out.println("---------2-------------");


        Object o = new Object();
        synchronized (o){
            o.wait();
        }
    }

    public static void main(String[] args)throws Exception{
        String connectString = "10.12.147.103:2181";
        ZookeeperClient zookeeperClient = new ZKClientZookeeperClient(connectString);

        zookeeperClient.addChildListener("/jdns/jd/search/suggest/rpc",new ChildListener(){

            @Override
            public void childChanged(String path, List<String> children) {
                System.out.println(path+"         "+children+"-----childChanged------");
            }
        });

        zookeeperClient.addStateListener(new StateListener(){

            @Override
            public void stateChanged(int connected) {
                System.out.println(connected+"----StateListener------");
            }
        });

        Object o = new Object();
        synchronized (o){
            o.wait();
        }
    }

}
