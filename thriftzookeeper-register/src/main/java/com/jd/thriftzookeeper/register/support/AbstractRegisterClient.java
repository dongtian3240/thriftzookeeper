package com.jd.thriftzookeeper.register.support;


import com.jd.thriftzookeeper.client.ChildListener;
import com.jd.thriftzookeeper.client.DataListener;
import com.jd.thriftzookeeper.client.StateListener;
import com.jd.thriftzookeeper.client.ZookeeperClient;
import com.jd.thriftzookeeper.register.MonitorMessage;
import com.jd.thriftzookeeper.register.RegisterClient;
import com.jd.thriftzookeeper.register.ServerAddress;
import com.jd.thriftzookeeper.register.monitor.Watcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.ZooKeeper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-30
 * Time: 下午7:24
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractRegisterClient implements RegisterClient {

    private static final Log logger = LogFactory.getLog(AbstractRegisterClient.class);
    protected String directory;

    private ZookeeperClient zkClient;

    @Override
    public void setDirectory(String directory) {
        this.directory=directory;
    }

    @Override
    public void registerWatcher(final Watcher watcher,final String path, boolean wakeRepeat) {
        final ZookeeperClient zookeeperClient = getClient();

        //对子结点变化进行监听
        zookeeperClient.addChildListener(path,new ChildListener() {
            @Override
            public void childChanged(String pathStr, List<String> children) {
                if(pathStr.equals(path)){
                    List<ServerAddress> serverAddresses = getServerAddress(children);
                    watcher.processChildNodeName(path, serverAddresses);

                    //当节点变化时,需要重新监听字节点变化
                    monitorChildNodeValueChange(zookeeperClient,path,watcher);
                }
            }
        });
        //对zookeeper的链接状态进行监听
        zookeeperClient.addStateListener(new StateListener() {
            @Override
            public void stateChanged(int connected) {
                //重新链接上
                if(StateListener.CONNECTED==connected){
                    zookeeperClient.addChildListener(path,new ChildListener() {
                        @Override
                        public void childChanged(String pathStr, List<String> children) {
                            if(pathStr.equals(path)){
                                List<ServerAddress> serverAddresses = getServerAddress(children);
                                watcher.processChildNodeName(path, serverAddresses);
                            }
                        }
                    });
                }
                if(StateListener.DISCONNECTED==connected){
                    logger.error("zookeeper connection lose.");
                }
                if(StateListener.RECONNECTED==connected){
                    logger.error("zookeeper reconnection.");
                }
            }
        });

        //对path下的所有孩子节点值进行监听
       monitorChildNodeValueChange(zookeeperClient,path,watcher);
        

    }

    private void monitorChildNodeValueChange(ZookeeperClient zookeeperClient, String path,final Watcher watcher){
        //得到所有子结点name
        List<String> children = zookeeperClient.getChildren(path);
        //监控所有子节点
        for(String nodeName:children){
           zookeeperClient.addDataChanges(path+"/"+nodeName,new DataListener() {
               @Override
               public void handleDataChange(String dataPath, Object data) throws Exception {
                   byte[] nodeValue = (byte[])data;
                   ServerAddress serverAddress = new ServerAddress(new String(nodeValue));
                   watcher.processNodeValue(dataPath,serverAddress);
               }

               @Override
               public void handleDataDeleted(String dataPath) throws Exception {

               }
           });
        }

    }
    
    @Override
    public void registerServer(final ServerAddress serverAddress) {
        final String path = NAMESPACE+this.directory+"/rpc"+"/"+serverAddress.toString();
        final ZookeeperClient zookeeperClient = getClient();
        //1.创建node,并且需要设置节点值
        zookeeperClient.create(path,serverAddress.toValue().getBytes(),true);
        //2.注册watcher,当zookeeper挂掉重启后,可以重新注册上服务。
        zookeeperClient.addStateListener(new StateListener() {
            @Override
            public void stateChanged(int connected) {
                //重新链接上
                if(StateListener.CONNECTED==connected){
                    zookeeperClient.create(path,serverAddress.toString().getBytes(),true);
                }
                if(StateListener.DISCONNECTED==connected){
                    logger.error("zookeeper connection lose.");
                }
                if(StateListener.RECONNECTED==connected){
                    logger.error("zookeeper reconnection.");
                }
            }
        });
    }

    @Override
    public void registerMoniter(final ServerAddress serverAddress, byte[] bytes) {
        final String path = NAMESPACE+this.directory+"/monitors/"+serverAddress.key();
        final ZookeeperClient zookeeperClient = getClient();
        //1.创建node,并且需要设置节点值
        zookeeperClient.create(path,serverAddress.toString().getBytes(),true);
        //2.注册watcher,当zookeeper挂掉重启后,可以重新注册上服务。
        zookeeperClient.addStateListener(new StateListener() {
            @Override
            public void stateChanged(int connected) {
                //重新链接上
                if(StateListener.CONNECTED==connected){
                    zookeeperClient.create(path,serverAddress.toString().getBytes(),true);
                }
                if(StateListener.DISCONNECTED==connected){
                    logger.error("zookeeper connection lose.");
                }
                if(StateListener.RECONNECTED==connected){
                    logger.error("zookeeper reconnection.");
                }
            }
        });
    }

    public void cancelServer(ServerAddress serverAddress){
        String path = NAMESPACE+this.directory+"/rpc/"+serverAddress.toString();
        ZookeeperClient zookeeperClient = getClient();
        zookeeperClient.delete(path);
    }

    public void cancelMoniter(ServerAddress serverAddress){
        String path = NAMESPACE+this.directory+"/monitors/"+serverAddress.key();
        ZookeeperClient zookeeperClient = getClient();
        zookeeperClient.delete(path);
    }

    public ZookeeperClient getClient(){
        if(this.zkClient==null){
            synchronized (AbstractRegisterClient.class){
                if(this.zkClient==null){
                    this.zkClient = getZookeeperClient();
                }
            }
        }
        return this.zkClient;
    }

    @Override
    public List<ServerAddress> getServers() {
        String path = NAMESPACE+directory+"/rpc";
        List<String> children = getClient().getChildren(path);

        /**
         * 只获取那些status为1的服务
         */
        List<ServerAddress> addresses = new ArrayList<ServerAddress>();
        for(String nodeName: children){
           ServerAddress server = getNodeValue(path+"/"+nodeName);
           if(server!=null)addresses.add(server);
        }
        return addresses;
    }

    public ServerAddress getNodeValue(String path){
        byte[] nodeValue = getClient().getNodeValue(path);
        ServerAddress serverAddress = new ServerAddress(new String(nodeValue));
        if(serverAddress.getStatus()==1)return serverAddress;
        else return null;
    }
    
    private List<ServerAddress> getServerAddress(List<String> children){
        if(children!=null&&children.size()>0){
            List<ServerAddress> serverAddresses = new ArrayList<ServerAddress>(children.size());
            for(String str:children){
                serverAddresses.add(new ServerAddress(str));
            }
            return serverAddresses;
        }
        return null;
    }
    @Override
    public List<MonitorMessage> getMoniters() {
        throw new RuntimeException("not support now .");
    }

    @Override
    public abstract ZookeeperClient getZookeeperClient();

    @Override
    public abstract ZooKeeper getZookeeper();



}
