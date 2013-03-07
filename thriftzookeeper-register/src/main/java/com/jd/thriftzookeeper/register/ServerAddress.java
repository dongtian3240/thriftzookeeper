package com.jd.thriftzookeeper.register;

import com.alibaba.fastjson.JSON;



/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-15
 * Time: 下午4:29
 * To change this template use File | Settings | File Templates.
 */
public class ServerAddress {
    private String addressInform;

    private String ip;
    private int port;
    private String protocol;
    //服务状态, 0服务正常启动，但不对外提供服务 1:服务正常对外提供服务
    private int status = 1;


    public ServerAddress(){}
    public ServerAddress(String ip,int port,String protocol){
        this.ip =ip;
        this.port = port;
        this.protocol = protocol;
    }
    /**
     * addressInform json
     * @param addressInform
     */
    public ServerAddress(String addressInform){
        ServerAddress serverAddress = JSON.parseObject(addressInform,ServerAddress.class);
        this.ip =serverAddress.getIp();
        this.port = serverAddress.getPort();
        this.protocol = serverAddress.getProtocol();
        this.status = serverAddress.getStatus();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String key(){
        return this.getIp()+":"+this.getPort();
    }

    @Override
    public String toString() {
        String json = "{\"ip\":\""+ip+"\",\"port\":"+port+",\"protocol\":\""+protocol+"\"}";
        return json;
    }
    public String toValue(){
        return JSON.toJSONString(this);
    }
    public static void main(String[] args){
        ServerAddress serverAddress = new ServerAddress("11111",111,"1111");
        System.out.println(serverAddress.toString());
        System.out.println(JSON.toJSONString(serverAddress)  );

    }
}
