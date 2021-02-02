package org.bingjian.enums;


/**
 * 存储枚举
 * @author fanglong
 */
public enum StorageClassEnum {
    //标准存储
    STANDARD("STANDARD"),
    //就近存储
    WARM("WARM"),
    //归档存储
    COLD("COLD");

    private StorageClassEnum(String storageClass) {
        this.storageClass = storageClass;
    }

    private final String storageClass;

    public String getCode() {
        return this.name();
    }

    public static StorageClassEnum getValueFromCode(String code) {
        if ("STANDARD".equals(code)) {
            return STANDARD;
        } else if (!"WARM".equals(code) && !"STANDARD_IA".equals(code)) {
            return !"COLD".equals(code) && !"GLACIER".equals(code) ? null : COLD;
        } else {
            return WARM;
        }
    }
}
