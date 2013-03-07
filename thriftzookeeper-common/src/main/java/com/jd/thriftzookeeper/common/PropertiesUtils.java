package com.jd.thriftzookeeper.common;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 12-11-22
 * Time: 下午3:52
 * To change this template use File | Settings | File Templates.
 */
public class PropertiesUtils {
    private PropertiesUtils(){}

    public synchronized static Map<String,String> getFieldsValue(String configFile){

        Map<String ,String > fieldValueMap = new HashMap<String, String>();
        Properties properties = new Properties();
        try {
            properties.load(PropertiesUtils.class.getClassLoader().getResourceAsStream(configFile));

            Enumeration enumeration =  properties.keys();
            while(enumeration.hasMoreElements()){
                String fullKey = (String)enumeration.nextElement();

                int num = fullKey.lastIndexOf(".");

                String key = fullKey;

                if(num!=-1)
                    key = key.substring(num+1,key.length());
                fieldValueMap.put(key, (String) properties.get(fullKey));
            }

        } catch (IOException e) {
            throw new RuntimeException("无法加载配置文件:" + configFile, e);
        }
       return fieldValueMap;
    }
}
