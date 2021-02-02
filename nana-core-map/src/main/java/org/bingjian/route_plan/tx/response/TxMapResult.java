package org.bingjian.route_plan.tx.response;

import lombok.Data;

/**
 * 腾讯地图返回对象
 *
 * @author fanglong
 */
@Data
public class TxMapResult {
    /**
     * 状态码
     */
    public String status;

    /**
     * 返回内容
     */
    public String message;

    /**
     * 数据对象
     */
    public Object result;

    public TxMapResult_X_Limit txMapResult_X_Limit;

    public boolean isStatus() {
        return this.status == "0";
    }
}
