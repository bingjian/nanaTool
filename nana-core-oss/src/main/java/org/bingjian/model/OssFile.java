package org.bingjian.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bingjian.enums.StorageClassEnum;

import java.util.Date;

/**
 * 对象文件参数
 *
 * @author fanglong
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OssFile {
    /**
     * 文件存储桶名称
     */
    private String bucketName;

    /**
     * 文件对象名称
     */
    private String objectName;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 存储类型
     */
    private StorageClassEnum storageClassEnum;

    /**
     * 文件最后修改时间
     */
    private Date lastModifiedDate;

    /**
     * 文件contentType
     */
    private String contentType;

}