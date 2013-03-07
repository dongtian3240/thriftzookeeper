package com.jd.thriftzookeeper.pool;

/**
 * Created by IntelliJ IDEA.
 * User: liguojun
 * Date: 13-1-15
 * Time: 下午5:43
 * To change this template use File | Settings | File Templates.
 */
public interface ConnectionProvider<T>
{
    /**
     * 取链接池中的一个链接
     *
     * @return
     */
    public T getConnection();
    /**
     * 返回链接
     *
     * @param client
     */
    public void returnConnection(T client);
}
