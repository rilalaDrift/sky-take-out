package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类，用于创建AliOssUtil对象,为AliOssUtil对象赋值
 */

@Slf4j
@Configuration
public class OssConfiguration {
//参数注入，直接把对象注入进来     AliOssProperties读取配置文件里的配置项
    //不加bean不会被调用， 加上项目启动就会调用方法，创建后交给容器
    @Bean
    @ConditionalOnMissingBean       //没有这个bean时才创建，不需要这么多这个bean
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
        log.info("开始创建上传工具类对象:{}",aliOssProperties);
     return    new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }
}
