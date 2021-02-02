package org.bingjian.route_plan.tx.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author fanglong
 */
@Data
//@Builder
public class TxMapResult_X_Limit {
    /**
     * 当前每秒并发量
     */
    public String currentQps;

    /**
     * 每秒并发配额
     */
    public String limitQps;

    /**
     * 今日调用量
     */
    public String currentPv;

    /**
     * 日请求量配额
     */
    public String limitPv;
}
