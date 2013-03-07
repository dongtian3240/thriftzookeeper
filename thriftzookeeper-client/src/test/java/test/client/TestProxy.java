package test.client;

import com.jd.thriftzookeeper.log.service.LogService;

/**
 * Created with IntelliJ IDEA.
 * User: lishuai
 * Date: 13-1-11
 * Time: 下午4:00
 * To change this template use File | Settings | File Templates.
 */
public class TestProxy {
    public static void main(String[] args) {
        Class<LogService.Client> fClass = LogService.Client.class;

        System.out.println(fClass.getName());



    }
}
