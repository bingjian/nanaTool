package org.bingjian.route_plan.tx.enums;

/**
 * api版本号
 */
public enum ApiEnum {
    V1("v1"),
    V2("v2");

    private String api;

    ApiEnum(String api) {
        this.api = api;
    }

    public String getApi() {
        return api;
    }
}
