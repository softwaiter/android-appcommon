package com.puerlink.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtils {

    public static String getUniqueFileName(String prefix, String ext)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss_SSS");
        return prefix + sdf.format(new Date()) + ext;
    }

}
