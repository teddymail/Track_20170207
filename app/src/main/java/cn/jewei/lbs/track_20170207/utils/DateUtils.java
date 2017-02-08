package cn.jewei.lbs.track_20170207.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类
 * Created by teddy on 2017/2/7.
 */

public class DateUtils {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String toDate(Date date){
        return sdf.format(date);
    }
}
