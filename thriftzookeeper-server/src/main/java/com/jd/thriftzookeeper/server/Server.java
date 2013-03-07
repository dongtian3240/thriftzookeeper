package com.jd.thriftzookeeper.server;

import com.jd.thriftzookeeper.register.ServerAddress;

/**
 * Created with IntelliJ IDEA.
 * User: lishuai
 * Date: 13-1-14
 * Time: 下午5:04
 * To change this template use File | Settings | File Templates.
 */
public interface Server<I> {
    /**
     * 启动服务
     * @throws Exception
     */
    public void start() throws Exception;

    /**
     * 关闭服务
     * @throws Exception
     */

    public void stop() throws Exception;

    /**
     * 得到服务地址和协议
     * @return
     */
    public ServerAddress getServerAddress();

}
