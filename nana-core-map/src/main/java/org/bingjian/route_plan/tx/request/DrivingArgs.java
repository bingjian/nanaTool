package org.bingjian.route_plan.tx.request;

import lombok.Builder;
import lombok.Data;

/**
 * 驾车请求参数对象
 * @author fanglong
 */
@Data
@Builder
public class DrivingArgs {
    /**
     * 起点座标
     */
    private String from;

    /**
     * 开发key
     */
    private String key;
    /**
     * 起点POI ID，传入后，优先级高于from（坐标）
     */
    private String fromPoi;

    private String heading;

    private String speed;

    private String accuracy;

    private String roadType;

    private String fromTrack;

    private String to;

    private String toPoi;

    private String waypoints;

    private String policy;

    private String avoidpolygons;

    private String plateNumber;

    private String cartype;

    private String getMp;

    private String noStep;

    private String callback;

    private String output;

    private String departureTime;
}
