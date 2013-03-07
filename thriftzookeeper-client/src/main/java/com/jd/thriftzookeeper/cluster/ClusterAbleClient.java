package com.jd.thriftzookeeper.cluster;

/*
 * 可集群标识
 */
public interface ClusterAbleClient {
	/*
	 * 此链接池是否可用，计算规则,加分降券,当分数加到十分时，直接认为链接死掉,然后删除
	 */
	public boolean isHealthy();

}
