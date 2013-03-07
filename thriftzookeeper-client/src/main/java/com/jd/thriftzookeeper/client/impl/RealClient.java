package com.jd.thriftzookeeper.client.impl;

import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.*;
import org.apache.thrift.transport.*;

import java.lang.reflect.Constructor;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-15
 * Time: 下午5:59
 * To change this template use File | Settings | File Templates.
 */
public class RealClient<T> {
    
    private String ip;
    private int port;
    private String protocolName;
    private final int TIMEOUT =10 * 1000;
    private Class ifaceClass;
    
    public RealClient(Class ifaceClass,String ip,int port,String protocolName){
        this.ip = ip;
        this.port=port;
        this.protocolName=protocolName;
        this.ifaceClass=ifaceClass;
    }

    /**
     * 获取真实client
     * @return
     * @throws org.apache.thrift.TException
     */
    public T getRealClient()throws org.apache.thrift.TException{
        try{
            System.out.println("ip= "+ip+" port= "+port+" protocol= "+protocolName);
            boolean sync =  true;
            //判断是同步调用还是异步
            Constructor[] constructors = ifaceClass.getConstructors();
            if(constructors.length==1&&constructors[0].getParameterTypes().length==3){
               sync=false;
            }
            if(sync){
                //调用c++有问题
                TTransport transport = new TFramedTransport(new TSocket(ip,port,TIMEOUT));
                TProtocol protocol = null;
                //根据协议名,实例化不同的协议
                if(TBinaryProtocol.class.getSimpleName().equals(protocolName)){
                    protocol = new TBinaryProtocol(transport);
                }else if(TCompactProtocol.class.getSimpleName().equals(protocolName)){
                    protocol = new TCompactProtocol(transport);
                }else if(TTupleProtocol.class.getSimpleName().equals(protocolName)){
                    protocol = new TTupleProtocol(transport);
                }

                transport.open();

                //根据接口找到实现类
                Constructor constructor = ifaceClass.getConstructor(TProtocol.class);
                constructor.setAccessible(true);
                Object o = constructor.newInstance(protocol);
                return (T)o;
            }else{
                TAsyncClientManager clientManager = new TAsyncClientManager();
                TNonblockingTransport transport = new TNonblockingSocket(ip, port, 5*1000);

                TProtocolFactory protocol = null;

                if(TBinaryProtocol.class.getSimpleName().equals(protocolName)){
                    protocol = new TBinaryProtocol.Factory();
                }else if(TCompactProtocol.class.getSimpleName().equals(protocolName)){
                    protocol = new TCompactProtocol.Factory();
                }else if(TTupleProtocol.class.getSimpleName().equals(protocolName)){
                    protocol = new TTupleProtocol.Factory();
                }

                Constructor constructor = ifaceClass.getConstructor(TProtocolFactory.class,TAsyncClientManager.class,TNonblockingTransport.class);
                constructor.setAccessible(true);
                Object o = constructor.newInstance(protocol,clientManager,transport);

                return (T)o;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
