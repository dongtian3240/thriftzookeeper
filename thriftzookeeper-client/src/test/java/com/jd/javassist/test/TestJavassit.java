package com.jd.javassist.test;

import com.jd.thriftzookeeper.log.service.LogService;
import com.jd.thriftzookeeper.loadbalance.LoadBalancer;
import com.jd.thriftzookeeper.loadbalance.impl.RandomLoadBalancer;
import javassist.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-22
 * Time: 上午11:48
 * To change this template use File | Settings | File Templates.
 */
public class TestJavassit<T> {
    public static void main(String[] args)throws Exception{
        TestJavassit<LogService.Iface> testJavassit = new TestJavassit<LogService.Iface>();

        ConcurrentMap<String, LogService.Iface> map = new ConcurrentHashMap<String, LogService.Iface>();

        testJavassit.createJavassistProxy(new RandomLoadBalancer(), map,LogService.Client.class);



    }

    
    public T createJavassistProxy(LoadBalancer loadBalance,ConcurrentMap<String, T> map,Class ifaces)throws Exception{

        Class<?>[] interfaces =  ifaces.getInterfaces();
        
        if(interfaces.length==1){
            ClassPool mPool = new ClassPool(true);
            CtClass ctClass =  mPool.get(interfaces[0].getName());

            //新建代理类
            CtClass mCtc = mPool.makeClass(ifaces.getName()+"$JavassistProxy");
            mCtc.setSuperclass(ctClass);

            for(CtMethod method:ctClass.getDeclaredMethods()){
                System.out.println(method.getName());

//                CtMethod m = new CtMethod(method.getReturnType(), ,method.getParameterTypes(), mCtc);
//                cc.addMethod(m);
//                m.setBody("{ x += $1; }");

                mCtc.addMethod(method);
//                method.setBody("");

            }
//            mCtc.debugWriteFile("/home/liguojun");

            return null;
        }else {
            return null;
        }
    }
    
    
    //用javassit得到动态代理
    public T createJavassistBytecodeDynamicProxy(LoadBalancer loadBalance,ConcurrentMap<String, T> map,Class ifaces){
        try{
            ClassPool mPool = new ClassPool(true);
            CtClass mCtc = mPool.makeClass(ifaces.getName() + "JavaassistProxy");
            mCtc.addInterface(mPool.get(ifaces.getName()));
            mCtc.addConstructor(CtNewConstructor.defaultConstructor(mCtc));

            mCtc.addField(CtField.make("public " + loadBalance.getClass().getName() + " sub;", mCtc));
            mCtc.addField(CtField.make("public " + map.getClass().getName() + " map;", mCtc));
//            mCtc.addField(CtField.make("public " + ArrayList.class.getName() + " list;", mCtc));

            mCtc.addMethod(CtNewMethod.make("public Object getRealClient() { return (Object)sub.select(new " + ArrayList.class.getName() + "(map.values())); }", mCtc));

            //获取接口的方法
            for(Method method: ifaces.getMethods()){
                Class returnType = method.getReturnType();
                String modifiers =  "public";
                if(Modifier.PUBLIC==method.getModifiers()){
                    modifiers="public";
                }else if(Modifier.PROTECTED==method.getModifiers()){
                    modifiers="protected";
                }else if(Modifier.PRIVATE==method.getModifiers()){
                    modifiers="private";
                }
                Class<?>[] parameter = method.getParameterTypes();

                String params = "";
                String ps = "";
                for(Class param:parameter){
                    params += param.getName()+" "+param.getName()+",";
                    ps+=param.getName()+",";
                }
                if(params.equals("")){
                    params = "";
                    ps="";
                }else{
                    params=params.substring(0,params.length());
                    ps=ps.substring(0,ps.length());
                }

                mCtc.addMethod(CtNewMethod.make(modifiers +" void "+method.getName()+"(String a,String b){ Object t=this.getRealClient(); return (("+ifaces.getName()+")t)."+method.getName()+"(a,b) ;}", mCtc));
//                mCtc.addMethod(CtNewMethod.make("public int count() { return delegate.count(); }", mCtc));
            }


            Class<?> pc = mCtc.toClass();

            mCtc.debugWriteFile("/home/liguojun");
            mCtc.writeFile("/home/liguojun");

            T bytecodeProxy = (T) pc.newInstance();
            Field filed = bytecodeProxy.getClass().getField("sub");
            filed.set(bytecodeProxy, loadBalance);

            Field filed1 = bytecodeProxy.getClass().getField("map");
            filed1.set(bytecodeProxy, map);

            return bytecodeProxy;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
