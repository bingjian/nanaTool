package org.bingjian.model;

import lombok.Builder;
import lombok.Data;
import org.bingjian.enums.StorageClassEnum;

import java.util.Date;

/**
 * 对象元数据
 *
 * @author fanglong
 */
@Data
@Builder
public class NaNaObjectMetadata {

    /**
     * 桶名称
     */
    private String bucketName;
    /**
     * 对象名称
     */
    private String objectName;
    /**
     * 最后修改时间
     */
    private Date lastModified;
    /**
     * 对象大小
     */
    private Long contentLength;
    /**
     * 对象类型
     */
    private String contentType;
    /**
     * 内容编码
     */
    private String contentEncoding;
    /**
     * 标签
     */
    private String etag;
    private String contentMd5;
    /**
     * 存储类型
     */
    private StorageClassEnum storageClass;
    private String webSiteRedirectLocation;
    private long nextPosition = -1L;
    private boolean appendable;
}
