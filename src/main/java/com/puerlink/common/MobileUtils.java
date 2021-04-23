package com.puerlink.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangxm on 2016/11/11.
 */
public class MobileUtils {

    /**
     * 判断是否中国手机号码
     * @param text
     * @return
     */
    public static boolean isChineseMobile(String text)
    {
        Pattern p = Pattern.compile("^[1][0-9]{10}$");
        Matcher m = p.matcher(text);
        return m.find();
    }

}
