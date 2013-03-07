package com.jd.thriftzookeeper.server.thrift;

import com.jd.thriftzookeeper.register.RegisterClient;
import com.jd.thriftzookeeper.register.ServerAddress;
import com.jd.thriftzookeeper.server.Server;
import com.jd.thriftzookeeper.server.ServerCluster;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-29
 * Time: 下午3:14
 * To change this template use File | Settings | File Templates.
 */
public class ServerClusterImpl implements ServerCluster{
    private RegisterClient zkClient;
    private String directory;

    public ServerClusterImpl(RegisterClient zkClient,String directory){
        this.zkClient = zkClient;
        this.directory=directory;
    }

    /**
     * 加入监控
     * @param server
     * @throws Exception
     */
    @Override
    public void join(Server server)throws Exception{
        //1.获取ServerAddress
        ServerAddress serverAddress = server.getServerAddress();
        //2.向zookeeper中写入信息
        zkClient.setDirectory(directory);
        zkClient.registerServer(serverAddress);
        zkClient.registerMoniter( serverAddress,new byte[0]);

    }
    public void leave(Server server)throws Exception{
        //1.注销到server注册信息
        zkClient.cancelServer(server.getServerAddress());
        //2.注销掉server监控信息
        zkClient.cancelMoniter(server.getServerAddress());
    }
}
