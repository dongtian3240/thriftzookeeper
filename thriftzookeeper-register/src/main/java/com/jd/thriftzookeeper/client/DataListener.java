package com.jd.thriftzookeeper.client;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-2-5
 * Time: 下午3:02
 * To change this template use File | Settings | File Templates.
 */
public interface DataListener {
    public void handleDataChange(String dataPath, Object data) throws Exception;

    public void handleDataDeleted(String dataPath) throws Exception;
}
