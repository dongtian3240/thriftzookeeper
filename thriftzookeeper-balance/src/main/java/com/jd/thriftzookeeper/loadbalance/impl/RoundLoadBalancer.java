/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jd.thriftzookeeper.loadbalance.impl;

import com.jd.thriftzookeeper.loadbalance.support.AbstractLoadBalancer;
import com.jd.thriftzookeeper.loadbalance.utils.AtomicPositiveInteger;

import java.util.List;

/**
 * Round robin load balance.
 *
 * @author qian.lei
 * @author william.liangf
 */
public class RoundLoadBalancer extends AbstractLoadBalancer {

    public static final String NAME = "roundrobin"; 
    
    private final AtomicPositiveInteger  atomicPositiveInteger = new AtomicPositiveInteger();


    protected <T> T doSelect(List<T> invokers) {
        int length = invokers.size();
        if(length==1){
             return (T)invokers.get(0);
        }
        // 取模轮循
        return (T)invokers.get(atomicPositiveInteger.getAndIncrement() % length);
    }
}