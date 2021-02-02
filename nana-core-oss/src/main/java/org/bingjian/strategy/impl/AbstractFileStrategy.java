package org.bingjian.strategy.impl;


import org.bingjian.config.properties.OssProperties;
import org.bingjian.strategy.OssStrategy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 对象存储抽象策略 处理类
 * @author fanglong
 */
public abstract class AbstractFileStrategy implements OssStrategy {

    @Autowired
    protected OssProperties fileProperties;
}
