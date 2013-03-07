package test.client;

import com.jd.thriftzookeeper.log.service.LogService;

import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-15
 * Time: 下午6:47
 * To change this template use File | Settings | File Templates.
 */
public class TestRealClient {
    public static void main(String[] args)throws Exception{
        System.out.println(LogService.Client.class.getName());
        System.out.println(LogService.Client.class.getInterfaces());
        
        Class c = LogService.Client.class.getInterfaces()[0];
        Method[] methods = c.getMethods();
        
        for(Method m:methods){
           System.out.println(m.getName());
        }
        
//        RealClient<LogService.Iface> realClient = new RealClient<LogService.Iface>(LogService.Client.class,"10.12.212.19",8081, TCompactProtocol.class.getSimpleName());
//
//        realClient.getRealClient().log("----","--------");
    }
}
