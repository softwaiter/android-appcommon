package com.puerlink.common.http;

import java.util.Map;

/**
 * Created by wangxm on 2016/8/28.
 */
public interface IParamsCommonHandler {

    Map<String, String> handleParams(String url, Map<String, String> params);

}
