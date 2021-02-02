package org.bingjian.route_plan.tx.enums;

/**
 * 路线类型
 * @author fanglong
 */
public enum RouteEnum {
    /**
     * 驾车
     */
    DRIVING("driving"),
    /**
     * 步行
     */
    WALKING("walking"),
    /**
     * 骑行
     */
    BICYCLING("bicycling"),
    /**
     *  公交
     */
    TRANSIT("transit");

    private String direction;

    RouteEnum(String direction) {
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }
}
