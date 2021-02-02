package org.bingjian.enums;

/**
 * 对象存储服务商
 * @author fanglong
 */
public enum OssTypeEnum {
    //华为
    HUIWEI("huawei"),
    //MINIO
    MINIO("minio");

    private OssTypeEnum(String ossType) {
        this.ossType = ossType;
    }

    private final String ossType;

    public String getCode() {
        return this.name();
    }
}
