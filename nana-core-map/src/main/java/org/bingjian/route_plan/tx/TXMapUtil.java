package org.bingjian.route_plan.tx;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import org.bingjian.route_plan.tx.request.DrivingArgs;
import org.bingjian.route_plan.tx.enums.*;
import org.bingjian.route_plan.tx.response.TxMapResult;

import java.util.List;
import java.util.Map;

/**
 * 腾讯地图工具类
 *
 * @author fanglong
 */
public class TXMapUtil {

    private static String urlString = "https://apis.map.qq.com/ws/direction/";

    private String key;

    private static final String X_LIMIT = "X-LIMIT";

    private static TXMapUtil instance;

    public TXMapUtil(String key) {
        this.key = key;
    }

    public static TXMapUtil instance(String key) {
        if (instance == null) {
            instance = new TXMapUtil(key);
        }
        return instance;
    }

    public static TXMapUtil getInstance() {
        return instance;
    }

    /**
     * 驾车路线规划简单调用
     *
     * @param from      起点位置坐标
     * @param to        终点位置坐标
     * @param waypoints 途经点，格式：lat1,lng1;lat2,lng2;… 最大支持16个
     * @param output    返回值类型：json、jsonp
     * @param callback  回调函数
     * @return
     */
    public TxMapResult getDrivingDirection(String from, String to, String waypoints, String output, String callback) {
        return getDrivingDirection(urlString, DrivingArgs.builder()
                .from(from)
                .to(to)
                .output(output)
                .waypoints(waypoints)
                .key(getKey())
                .callback(callback)
                .build());
    }

    /**
     * 驾车路线规划, 默认版本v1
     *
     * @param urlString
     * @param drivingArgs
     * @return
     */
    public static TxMapResult getDrivingDirection(String urlString, DrivingArgs drivingArgs) {
        return getDirection(urlString, RouteEnum.DRIVING, ApiEnum.V1, drivingArgs);
    }

    /**
     * 驾车路线规划, 可选择其他版本
     *
     * @param urlString
     * @param apiEnum
     * @param drivingArgs
     * @return
     */
    public static TxMapResult getDrivingDirection(String urlString, ApiEnum apiEnum, DrivingArgs drivingArgs) {
        return getDirection(urlString, RouteEnum.DRIVING, ApiEnum.V1, drivingArgs);
    }

    /**
     * 路线规划请求
     *
     * @param urlString   请求地址
     * @param routeEnum   交通方式
     * @param apiEnum     版本
     * @param drivingArgs 请求参数
     * @return
     */
    public static TxMapResult getDirection(String urlString, RouteEnum routeEnum, ApiEnum apiEnum, DrivingArgs drivingArgs) {
        //除了驾车有V2，其他类型只有版本V1
        if (routeEnum.equals(RouteEnum.BICYCLING) || routeEnum.equals(RouteEnum.WALKING) || routeEnum.equals(RouteEnum.TRANSIT)) {
            apiEnum = ApiEnum.V1;
        }
        urlString = urlString.endsWith("/") ? urlString.substring(0, urlString.length() - 1) : urlString;
        urlString = StrUtil.join("/" , urlString, apiEnum.getApi(), routeEnum.getDirection());
        System.out.println(urlString);
        //请求体转成Map
        Map<String, Object> map = BeanUtil.beanToMap(drivingArgs, true, true);
        return getDirection(urlString, map);
    }

    /**
     * 路线规划请求
     *
     * @param urlString
     * @param map
     */
    public static TxMapResult getDirection(String urlString, Map<String, Object> map) {
        //Assert.isTrue(map.containsKey("from"), "请求参数不能有null值");
        HttpResponse httpResponse = HttpRequest.get(urlString).form(map).execute();
        String body = httpResponse.body();
        Map<String, List<String>> headers = httpResponse.headers();
        TxMapResult txMapResult = JSON.toJavaObject(JSON.parseObject(body), TxMapResult.class);
        if (headers.containsKey(X_LIMIT)) {
            List<String> lines = headers.get("X-LIMIT");
            lines.stream().forEach(l -> Console.log(l));
        }
        return txMapResult;
    }

    public String getKey() {
        return this.key;
    }

    public static void main(String[] args) {
        String urlString = "https://apis.map.qq.com/ws/direction/";
        String key = "OB4BZ-D4W3U-B7VVO-4PJWW-6TKDJ-WPB77";
        String from = "39.915285,116.403857";
        String to = "39.915285,116.803857";
        String waypoints = "39.111,116.112;39.112,116.113";
        String output = "json";
        String callback = "cb";
        TxMapResult txMapResult = TXMapUtil.instance(key).getDrivingDirection(from, to, waypoints, output, callback);
        Console.log(txMapResult);
    }
}
