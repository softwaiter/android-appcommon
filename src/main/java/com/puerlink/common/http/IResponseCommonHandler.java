package com.puerlink.common.http;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by wangxm on 2016/9/7.
 */
public interface IResponseCommonHandler {

    ResponseState handleText(String text);
    ResponseState handleJSONObject(JSONObject obj);
    ResponseState handleJSONArray(JSONArray array);

}
