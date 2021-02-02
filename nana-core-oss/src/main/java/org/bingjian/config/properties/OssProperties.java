package org.bingjian.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 对象存储配置
 *
 * @author fanglong
 */
@Setter
@Getter
@ConfigurationProperties(prefix = OssProperties.PREFIX)
public class OssProperties {
    public static final String PREFIX = "oss";

    /**
     * 指定不同的自动化配置
     * huawei：华为云oss
     * minio：私有云oss
     */
    private String type;
    /**
     * 对象存储服务的URL
     */
    private String endpoint;
    /**
     * Access key ID就像用户ID，可以唯一标识你的账户
     */
    private String accessKeyId;
    /**
     * Access key Secret是你账户的密码
     */
    private String accessKeySecret;

    /**
     * 默认存储桶
     */
    private String bucketName;
}
