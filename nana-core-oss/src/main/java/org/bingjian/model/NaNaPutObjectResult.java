package org.bingjian.model;

import lombok.Builder;
import lombok.Data;
import org.bingjian.enums.StorageClassEnum;

/**
 * 上传对象返回结果
 *
 * @author fanglong
 */
@Data
@Builder
public class NaNaPutObjectResult {
    private int statusCode;
    private String bucketName;
    private String objectKey;
    private String etag;
    private String versionId;
    private StorageClassEnum storageClass;
    private String objectUrl;
}
