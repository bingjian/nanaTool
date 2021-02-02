import cn.hutool.core.lang.Console;
import org.bingjian.route_plan.tx.TXMapUtil;
import org.bingjian.route_plan.tx.response.TxMapResult;

public class Test {

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
