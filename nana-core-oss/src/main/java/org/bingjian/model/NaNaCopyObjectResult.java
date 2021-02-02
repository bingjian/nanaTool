package org.bingjian.model;


import lombok.Builder;
import lombok.Data;
import org.bingjian.enums.StorageClassEnum;

import java.util.Date;

/**
 * 复制对象返回结果
 *
 * @author fanglong
 */
@Data
@Builder
public class NaNaCopyObjectResult {
    /**
     * 标签
     */
    private String etag;
    /**
     * 最后修改时间
     */
    private Date lastModified;
    private String versionId;
    private String copySourceVersionId;
    /**
     * 存储类型
     */
    private StorageClassEnum storageClass;

}