package com.puerlink.common.http;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.puerlink.common.NetUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 公共网络请求类
 * Created by wangxm on 2016/8/31.
 */
public class HttpUtils {

    public static final String S_URL_IS_EMPTY_CODE = "-1001";
    private static final String S_URL_IS_EMPTY = "请求地址为空。";

    public static final String S_REQUEST_FAILED_CODE = "-1002";   //请求调用过程失败

    public static final String S_HANDLE_RESPONSE_EXCEPT_CODE = "-1003";  //处理返回结果异常

    public static final String S_RESPONSE_STATUS_NOT_OK = "-1004";   //返回结果不成功（不是200）

    public static final String S_RESPONSE_BODY_IS_EMPTY_CODE = "-1005";
    private static final String S_RESPONSE_BODY_IS_EMPTY = "返回结果为空。";

    public interface HttpDownloadCallback
    {
        void onStart();
        void onProgress(long total, long current);
        void onSucceeded(File file);
        void onFailed(String code, String message);
        void onNetworkDisconnected(NetUtils.NetState state);
    }

    public interface HttpTextCallback
    {
        void onSucceeded(String text);
        void onFailed(String code, String message);
        void onNetworkDisconnected(NetUtils.NetState state);
    }

    public interface HttpJSONObjectCallback
    {
        void onSucceeded(JSONObject response);
        void onFailed(String code, String message);
        void onNetworkDisconnected(NetUtils.NetState state);
    }

    public interface HttpJSONArrayCallback
    {
        void onSucceeded(JSONArray response);
        void onFailed(String code, String message);
        void onNetworkDisconnected(NetUtils.NetState state);
    }

    private static OkHttpClient sHttpClient;

    private static IParamsCommonHandler sParamsCommonHandler;
    private static IResponseCommonHandler sResponseCommonHandler;

    static
    {
        sHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public static void configParamsCommonHandler(IParamsCommonHandler handler)
    {
        sParamsCommonHandler = handler;
    }

    public static void configResponseCommonHandler(IResponseCommonHandler handler)
    {
        sResponseCommonHandler = handler;
    }

    private static Headers buildHeaders(Map<String, String> headers)
    {
        if (headers != null && headers.size() > 0)
        {
            return Headers.of(headers);
        }
        return new Headers.Builder().build();
    }

    private static String buildUrl(String url, Map<String, String> params)
    {
        String result = url;

        if (sParamsCommonHandler != null)
        {
            params = sParamsCommonHandler.handleParams(url, params);
        }

        if (params != null && params.size() >0) {
            if (!result.contains("?")) {
                result += "?";
            }

            Iterator<String> iterator = params.keySet().iterator();
            while (iterator.hasNext())
            {
                if (!result.endsWith("?"))
                {
                    result += "&";
                }

                String key = iterator.next();
                result += (key + "=" + params.get(key));
            }
        }
        return result;
    }

    private static Context getApplicationContext(Object context)
    {
        if (context != null)
        {
            if (context instanceof Activity)
            {
                Activity activity = (Activity)context;
                if (activity != null && !activity.isFinishing())
                {
                    return activity.getApplicationContext();
                }
            }
            else if (context instanceof Fragment)
            {
                Fragment frag = (Fragment)context;
                if (frag != null) {
                    Activity activity = frag.getActivity();
                    if (activity != null && !activity.isFinishing())
                    {
                        return activity.getApplicationContext();
                    }
                }
            }
            else if (context instanceof Application)
            {
                return ((Application)context).getApplicationContext();
            }
        }
        return null;
    }

    private static boolean isValidContext(Object context)
    {
        if (context != null)
        {
            if (context instanceof Activity)
            {
                Activity activity = (Activity)context;
                return activity != null && !activity.isFinishing();
            }
            else if (context instanceof Fragment)
            {
                Fragment frag = (Fragment)context;
                return frag != null && !frag.isRemoving() && !frag.isDetached();
            }
            else if (context instanceof Application)
            {
                return true;
            }
        }
        return false;
    }

    private static void innerGetText(final Object context, String url,
                                     Map<String, String> headers,
                                     Map<String, String> params,
                                     final HttpTextCallback callback)
    {
        if (!TextUtils.isEmpty(url)) {
            if (isValidContext(context)) {
                final String realUrl = buildUrl(url, params);
                final Headers realHeaders = buildHeaders(headers);

                Context appCtx = getApplicationContext(context);
                NetUtils.test(appCtx, new NetUtils.NetworkTestCallback() {

                    @Override
                    public void onDone(NetUtils.NetState state) {
                        if (state != NetUtils.NetState.Disconnected) {
                            Request request = new Request.Builder()
                                    .url(realUrl)
                                    .headers(realHeaders)
                                    .get()
                                    .build();
                            Call call = sHttpClient.newCall(request);
                            addHttpCall(context, call);
                            call.enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    removeHttpCall(context, call);

                                    if (callback != null) {
                                        if (isValidContext(context)) {
                                            callback.onFailed(S_REQUEST_FAILED_CODE, e.getMessage());
                                        }
                                    }
                                }

                                @Override
                                public void onResponse(Call call, Response response) {
                                    removeHttpCall(context, call);

                                    if (callback != null) {
                                        if (isValidContext(context)) {
                                            try {
                                                if (response.code() == 200) {
                                                    if (response.body() != null) {
                                                        if (sResponseCommonHandler != null)
                                                        {
                                                            String responseText = response.body().string();
                                                            ResponseState rs = sResponseCommonHandler.handleText(responseText);
                                                            if (rs.getState())
                                                            {
                                                                if (isValidContext(context))
                                                                {
                                                                    callback.onSucceeded(responseText);
                                                                }
                                                            }
                                                            else
                                                            {
                                                                if (isValidContext(context))
                                                                {
                                                                    callback.onFailed(rs.getCode(), rs.getMessage());
                                                                }
                                                            }
                                                        }
                                                        else {
                                                            if (isValidContext(context)) {
                                                                callback.onSucceeded(response.body().string());
                                                            }
                                                        }
                                                    } else {
                                                        callback.onFailed(S_RESPONSE_BODY_IS_EMPTY_CODE, S_RESPONSE_BODY_IS_EMPTY);
                                                    }
                                                } else {
                                                    callback.onFailed(S_RESPONSE_STATUS_NOT_OK, "(" + response.code() + ")" + response.message());
                                                }
                                            } catch (Exception exp) {
                                                callback.onFailed(S_HANDLE_RESPONSE_EXCEPT_CODE, exp.getMessage());
                                            }
                                        }
                                    }
                                }
                            });
                        }
                        else
                        {
                            if (callback != null)
                            {
                                if (isValidContext(context))
                                {
                                    callback.onNetworkDisconnected(state);
                                }
                            }
                        }
                    }
                });
            }
        }
        else
        {
            if (callback != null)
            {
                if (isValidContext(context)) {
                    callback.onFailed(S_URL_IS_EMPTY_CODE, S_URL_IS_EMPTY);
                }
            }
        }
    }

    public static void getText(Context context, String url,
                               Map<String, String> headers,
                               Map<String, String> params,
                               HttpTextCallback callback)
    {
        innerGetText(context, url, headers, params, callback);
    }

    public static void getText(Context context, String url,
                               Map<String, String> params,
                               HttpTextCallback callback)
    {
        getText(context, url, null, params, callback);
    }

    public static void getText(Context context, String url,
                               HttpTextCallback callback)
    {
        getText(context, url, null, callback);
    }

    public static void getText(Activity activity, String url,
                               Map<String, String> headers,
                               Map<String, String> params,
                               HttpTextCallback callback)
    {
        innerGetText(activity, url, headers, params, callback);
    }

    public static void getText(Activity activity, String url,
                               Map<String, String> params,
                               HttpTextCallback callback)
    {
        getText(activity, url, null, params, callback);
    }

    public static void getText(Activity activity, String url,
                               HttpTextCallback callback)
    {
        getText(activity, url, null, callback);
    }

    public static void getText(Fragment fragment, String url,
                               Map<String, String> headers,
                               Map<String, String> params,
                               HttpTextCallback callback)
    {
        innerGetText(fragment, url, headers, params, callback);
    }

    public static void getText(Fragment fragment, String url,
                               Map<String, String> params,
                               HttpTextCallback callback)
    {
        getText(fragment, url, null, params, callback);
    }

    public static void getText(Fragment fragment, String url,
                               HttpTextCallback callback)
    {
        getText(fragment, url, null, callback);
    }

    private static void innerGetJSONObject(final Object context, String url,
                                     Map<String, String> headers,
                                     Map<String, String> params,
                                     final HttpJSONObjectCallback callback)
    {
        if (!TextUtils.isEmpty(url)) {
            if (isValidContext(context)) {
                final String realUrl = buildUrl(url, params);
                final Headers realHeaders = buildHeaders(headers);

                Context appCtx = getApplicationContext(context);
                NetUtils.test(appCtx, new NetUtils.NetworkTestCallback() {

                    @Override
                    public void onDone(NetUtils.NetState state) {
                        if (state != NetUtils.NetState.Disconnected) {
                            Request request = new Request.Builder()
                                    .url(realUrl)
                                    .headers(realHeaders)
                                    .get()
                                    .build();
                            Call call = sHttpClient.newCall(request);
                            addHttpCall(context, call);
                            call.enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    removeHttpCall(context, call);

                                    if (callback != null) {
                                        if (isValidContext(context)) {
                                            callback.onFailed(S_REQUEST_FAILED_CODE, e.getMessage());
                                        }
                                    }
                                }

                                @Override
                                public void onResponse(Call call, Response response) {
                                    removeHttpCall(context, call);

                                    if (callback != null) {
                                        if (isValidContext(context)) {
                                            try {
                                                if (response.code() == 200) {
                                                    if (response.body() != null) {
                                                        JSONObject responseObj = JSON.parseObject(response.body().string());
                                                        if (sResponseCommonHandler != null)
                                                        {
                                                            ResponseState rs = sResponseCommonHandler.handleJSONObject(responseObj);
                                                            if (rs.getState())
                                                            {
                                                                if (isValidContext(context))
                                                                {
                                                                    callback.onSucceeded(responseObj);
                                                                }
                                                            }
                                                            else
                                                            {
                                                                if (isValidContext(context))
                                                                {
                                                                    callback.onFailed(rs.getCode(), rs.getMessage());
                                                                }
                                                            }
                                                        }
                                                        else
                                                        {
                                                            if (isValidContext(context)) {
                                                                callback.onSucceeded(responseObj);
                                                            }
                                                        }
                                                    } else {
                                                        callback.onFailed(S_RESPONSE_BODY_IS_EMPTY_CODE, S_RESPONSE_BODY_IS_EMPTY);
                                                    }
                                                } else {
                                                    callback.onFailed(S_RESPONSE_STATUS_NOT_OK, "(" + response.code() + ")" + response.message());
                                                }
                                            } catch (Exception exp) {
                                                callback.onFailed(S_HANDLE_RESPONSE_EXCEPT_CODE, exp.getMessage());
                                            }
                                        }
                                    }
                                }
                            });
                        }
                        else
                        {
                            if (callback != null)
                            {
                                if (isValidContext(context))
                                {
                                    callback.onNetworkDisconnected(state);
                                }
                            }
                        }
                    }
                });
            }
        }
        else
        {
            if (callback != null)
            {
                if (isValidContext(context)) {
                    callback.onFailed(S_URL_IS_EMPTY_CODE, S_URL_IS_EMPTY);
                }
            }
        }
    }

    public static void getJSONObject(Context context, String url,
                                     Map<String, String> headers,
                                     Map<String, String> params,
                                     HttpJSONObjectCallback callback)
    {
        innerGetJSONObject(context, url, headers, params, callback);
    }

    public static void getJSONObject(Context context, String url,
                                     Map<String, String> params,
                                     HttpJSONObjectCallback callback)
    {
        getJSONObject(context, url, null, params, callback);
    }

    public static void getJSONObject(Context context, String url,
                                     HttpJSONObjectCallback callback)
    {
        getJSONObject(context, url, null, callback);
    }

    public static void getJSONObject(Activity activity, String url,
                                     Map<String, String> headers,
                                     Map<String, String> params,
                                     HttpJSONObjectCallback callback)
    {
        innerGetJSONObject(activity, url, headers, params, callback);
    }

    public static void getJSONObject(Activity activity, String url,
                                     Map<String, String> params,
                                     HttpJSONObjectCallback callback)
    {
        getJSONObject(activity, url, null, params, callback);
    }

    public static void getJSONObject(Activity activity, String url,
                                     HttpJSONObjectCallback callback)
    {
        getJSONObject(activity, url, null, callback);
    }

    public static void getJSONObject(Fragment fragment, String url,
                                     Map<String, String> headers,
                                     Map<String, String> params,
                                     HttpJSONObjectCallback callback)
    {
        innerGetJSONObject(fragment, url, headers, params, callback);
    }

    public static void getJSONObject(Fragment fragment, String url,
                                     Map<String, String> params,
                                     HttpJSONObjectCallback callback)
    {
        getJSONObject(fragment, url, null, params, callback);
    }

    public static void getJSONObject(Fragment fragment, String url,
                                     HttpJSONObjectCallback callback)
    {
        getJSONObject(fragment, url, null, callback);
    }

    private static void innerGetJSONArray(final Object context, String url,
                                          Map<String, String> headers,
                                          Map<String, String> params,
                                          final HttpJSONArrayCallback callback)
    {
        if (!TextUtils.isEmpty(url))
        {
            if (isValidContext(context)) {
                final String realUrl = buildUrl(url, params);
                final Headers realHeaders = buildHeaders(headers);

                Context appCtx = getApplicationContext(context);
                NetUtils.test(appCtx, new NetUtils.NetworkTestCallback() {

                    @Override
                    public void onDone(NetUtils.NetState state) {
                        if (state != NetUtils.NetState.Disconnected) {
                            Request request = new Request.Builder()
                                    .url(realUrl)
                                    .headers(realHeaders)
                                    .get()
                                    .build();
                            Call call = sHttpClient.newCall(request);
                            addHttpCall(context, call);
                            call.enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    removeHttpCall(context, call);

                                    if (callback != null) {
                                        if (isValidContext(context)) {
                                            callback.onFailed(S_REQUEST_FAILED_CODE, e.getMessage());
                                        }
                                    }
                                }

                                @Override
                                public void onResponse(Call call, Response response) {
                                    removeHttpCall(context, call);

                                    if (callback != null) {
                                        if (isValidContext(context)) {
                                            try {
                                                if (response.code() == 200) {
                                                    if (response.body() != null) {
                                                        JSONArray responseArray = JSON.parseArray(response.body().string());
                                                        if (sResponseCommonHandler != null)
                                                        {
                                                            ResponseState rs = sResponseCommonHandler.handleJSONArray(responseArray);
                                                            if (rs.getState())
                                                            {
                                                                if (isValidContext(context))
                                                                {
                                                                    callback.onSucceeded(responseArray);
                                                                }
                                                            }
                                                            else
                                                            {
                                                                if (isValidContext(context))
                                                                {
                                                                    callback.onFailed(rs.getCode(), rs.getMessage());
                                                                }
                                                            }
                                                        }
                                                        else
                                                        {
                                                            if (isValidContext(context))
                                                            {
                                                                callback.onSucceeded(responseArray);
                                                            }
                                                        }
                                                    } else {
                                                        callback.onFailed(S_RESPONSE_BODY_IS_EMPTY_CODE, S_RESPONSE_BODY_IS_EMPTY);
                                                    }
                                                } else {
                                                    callback.onFailed(S_RESPONSE_STATUS_NOT_OK, "(" + response.code() + ")" + response.message());
                                                }
                                            } catch (Exception exp) {
                                                callback.onFailed(S_HANDLE_RESPONSE_EXCEPT_CODE, exp.getMessage());
                                            }
                                        }
                                    }
                                }
                            });
                        }
                        else
                        {
                            if (callback != null)
                            {
                                if (isValidContext(context))
                                {
                                    callback.onNetworkDisconnected(state);
                                }
                            }
                        }
                    }
                });
            }
        }
        else
        {
            if (callback != null)
            {
                if (isValidContext(context)) {
                    callback.onFailed(S_URL_IS_EMPTY_CODE, S_URL_IS_EMPTY);
                }
            }
        }
    }

    public static void getJSONArray(Context context, String url,
                                    Map<String, String> headers,
                                    Map<String, String> params,
                                    HttpJSONArrayCallback callback)
    {
        innerGetJSONArray(context, url, headers, params, callback);
    }

    public static void getJSONArray(Context context, String url,
                                    Map<String, String> params,
                                    HttpJSONArrayCallback callback)
    {
        getJSONArray(context, url, null, params, callback);
    }

    public static void getJSONArray(Context context, String url,
                                    HttpJSONArrayCallback callback)
    {
        getJSONArray(context, url, null, callback);
    }

    public static void getJSONArray(Activity activity, String url,
                                    Map<String, String> headers,
                                    Map<String, String> params,
                                    HttpJSONArrayCallback callback)
    {
        innerGetJSONArray(activity, url, headers, params, callback);
    }

    public static void getJSONArray(Activity activity, String url,
                                    Map<String, String> params,
                                    HttpJSONArrayCallback callback)
    {
        getJSONArray(activity, url, null, params, callback);
    }

    public static void getJSONArray(Activity activity, String url,
                                    HttpJSONArrayCallback callback)
    {
        getJSONArray(activity, url, null, callback);
    }

    public static void getJSONArray(Fragment fragment, String url,
                                    Map<String, String> headers,
                                    Map<String, String> params,
                                    HttpJSONArrayCallback callback)
    {
        innerGetJSONArray(fragment, url, headers, params, callback);
    }

    public static void getJSONArray(Fragment fragment, String url,
                                    Map<String, String> params,
                                    HttpJSONArrayCallback callback)
    {
        getJSONArray(fragment, url, null, params, callback);
    }

    public static void getJSONArray(Fragment fragment, String url,
                                    HttpJSONArrayCallback callback)
    {
        getJSONArray(fragment, url, null, callback);
    }

    private static void innerPostForm(final Object context, String url,
                                     Map<String, String> headers,
                                     Map<String, String> fields,
                                     final HttpTextCallback callback)
    {
        if (!TextUtils.isEmpty(url))
        {
            if (isValidContext(context)) {
                final String realUrl = buildUrl(url, null);
                final Headers realHeaders = buildHeaders(headers);

                FormBody.Builder bodyBuilder = new FormBody.Builder();
                if (fields != null && fields.size() > 0) {
                    Iterator<String> iterator = fields.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        bodyBuilder.add(key, fields.get(key));
                    }
                }
                final FormBody body = bodyBuilder.build();

                Context appCtx = getApplicationContext(context);
                NetUtils.test(appCtx, new NetUtils.NetworkTestCallback() {

                    @Override
                    public void onDone(NetUtils.NetState state) {
                        if (state != NetUtils.NetState.Disconnected) {

                            Request request = new Request.Builder()
                                    .url(realUrl)
                                    .headers(realHeaders)
                                    .post(body)
                                    .build();
                            Call call = sHttpClient.newCall(request);
                            addHttpCall(context, call);
                            call.enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    removeHttpCall(context, call);

                                    if (callback != null) {
                                        if (isValidContext(context)) {
                                            callback.onFailed(S_REQUEST_FAILED_CODE, e.getMessage());
                                        }
                                    }
                                }

                                @Override
                                public void onResponse(Call call, Response response) {
                                    removeHttpCall(context, call);

                                    if (callback != null) {
                                        if (isValidContext(context)) {
                                            try {
                                                if (response.code() == 200) {
                                                    if (response.body() != null) {
                                                        if (sResponseCommonHandler != null)
                                                        {
                                                            String responseText = response.body().string();
                                                            ResponseState rs = sResponseCommonHandler.handleText(responseText);
                                                            if (rs.getState())
                                                            {
                                                                if (isValidContext(context))
                                                                {
                                                                    callback.onSucceeded(responseText);
                                                                }
                                                            }
                                                            else
                                                            {
                                                                if (isValidContext(context))
                                                                {
                                                                    callback.onFailed(rs.getCode(), rs.getMessage());
                                                                }
                                                            }
                                                        }
                                                        else
                                                        {
                                                            if (isValidContext(context)) {
                                                                callback.onSucceeded(response.body().string());
                                                            }
                                                        }
                                                    } else {
                                                        callback.onFailed(S_RESPONSE_BODY_IS_EMPTY_CODE, S_RESPONSE_BODY_IS_EMPTY);
                                                    }
                                                } else {
                                                    callback.onFailed(S_RESPONSE_STATUS_NOT_OK, "(" + response.code() + ")" + response.message());
                                                }
                                            } catch (Exception exp) {
                                                callback.onFailed(S_HANDLE_RESPONSE_EXCEPT_CODE, exp.getMessage());
                                            }
                                        }
                                    }
                                }
                            });
                        }
                        else
                        {
                            if (callback != null)
                            {
                                if (isValidContext(context))
                                {
                                    callback.onNetworkDisconnected(state);
                                }
                            }
                        }
                    }
                });
            }
        }
        else
        {
            if (callback != null)
            {
                if (isValidContext(context))
                {
                    callback.onFailed(S_URL_IS_EMPTY_CODE, S_URL_IS_EMPTY);
                }
            }
        }
    }

    public static void postForm(Context context, String url,
                                Map<String, String> headers,
                                Map<String, String> fields,
                                HttpTextCallback callback)
    {
        innerPostForm(context, url, headers, fields, callback);
    }

    public static void postForm(Context context, String url,
                                Map<String, String> fields,
                                HttpTextCallback callback)
    {
        postForm(context, url, null, fields, callback);
    }

    public static void postForm(Activity activity, String url,
                                Map<String, String> headers,
                                Map<String, String> fields,
                                HttpTextCallback callback)
    {
        innerPostForm(activity, url, headers, fields, callback);
    }

    public static void postForm(Activity activity, String url,
                                Map<String, String> fields,
                                HttpTextCallback callback)
    {
        postForm(activity, url, null, fields, callback);
    }

    public static void postForm(Fragment fragment, String url,
                                Map<String, String> headers,
                                Map<String, String> fields,
                                HttpTextCallback callback)
    {
        innerPostForm(fragment, url, headers, fields, callback);
    }

    public static void postForm(Fragment fragment, String url,
                                Map<String, String> fields,
                                HttpTextCallback callback)
    {
        postForm(fragment, url, null, fields, callback);
    }

    private static void innerPostForm(final Object context, String url,
                                Map<String, String> headers,
                                Map<String, Object> fields,
                                final HttpJSONObjectCallback callback)
    {
        if (!TextUtils.isEmpty(url))
        {
            if (isValidContext(context)) {
                final String realUrl = buildUrl(url, null);
                final Headers realHeaders = buildHeaders(headers);

                MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
                bodyBuilder.setType(MultipartBody.FORM);

                if (fields != null && fields.size() > 0)
                {
                    Iterator<String> iterator = fields.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        Object value = fields.get(key);
                        if (value != null)
                        {
                            if (!(value instanceof File))
                            {
                                bodyBuilder.addFormDataPart(key, value.toString());
                            }
                            else
                            {
                                File file = (File)value;
                                bodyBuilder.addFormDataPart(key, file.getName(), RequestBody.create(null, file));
                            }
                        }
                    }
                }

                final RequestBody requestBody = bodyBuilder.build();

                Context appCtx = getApplicationContext(context);
                NetUtils.test(appCtx, new NetUtils.NetworkTestCallback() {

                    @Override
                    public void onDone(NetUtils.NetState state) {
                        if (state != NetUtils.NetState.Disconnected) {
                            Request request = new Request.Builder()
                                    .url(realUrl)
                                    .headers(realHeaders)
                                    .post(requestBody)
                                    .build();
                            Call call = sHttpClient.newCall(request);
                            addHttpCall(context, call);
                            call.enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    removeHttpCall(context, call);

                                    if (callback != null) {
                                        if (isValidContext(context)) {
                                            callback.onFailed(S_REQUEST_FAILED_CODE, e.getMessage());
                                        }
                                    }
                                }

                                @Override
                                public void onResponse(Call call, Response response) {
                                    removeHttpCall(context, call);

                                    if (callback != null) {
                                        if (isValidContext(context)) {
                                            try {
                                                if (response.code() == 200) {
                                                    if (response.body() != null) {
                                                        JSONObject responseObject = JSON.parseObject(response.body().string());
                                                        if (sResponseCommonHandler != null) {
                                                            ResponseState rs = sResponseCommonHandler.handleJSONObject(responseObject);
                                                            if (rs.getState()) {
                                                                if (isValidContext(context)) {
                                                                    callback.onSucceeded(responseObject);
                                                                }
                                                            } else {
                                                                if (isValidContext(context)) {
                                                                    callback.onFailed(rs.getCode(), rs.getMessage());
                                                                }
                                                            }
                                                        } else {
                                                            if (isValidContext(context)) {
                                                                callback.onSucceeded(responseObject);
                                                            }
                                                        }
                                                    } else {
                                                        callback.onFailed(S_RESPONSE_BODY_IS_EMPTY_CODE, S_RESPONSE_BODY_IS_EMPTY);
                                                    }
                                                } else {
                                                    callback.onFailed(S_RESPONSE_STATUS_NOT_OK, "(" + response.code() + ")" + response.message());
                                                }
                                            } catch (Exception exp) {
                                                callback.onFailed(S_HANDLE_RESPONSE_EXCEPT_CODE, exp.getMessage());
                                            }
                                        }
                                    }
                                }
                            });
                        }
                        else
                        {
                            if (callback != null)
                            {
                                if (isValidContext(context))
                                {
                                    callback.onNetworkDisconnected(state);
                                }
                            }
                        }
                    }
                });
            }
        }
        else
        {
            if (callback != null)
            {
                if (isValidContext(context))
                {
                    callback.onFailed(S_URL_IS_EMPTY_CODE, S_URL_IS_EMPTY);
                }
            }
        }
    }

    public static void postForm(Context context, String url,
                                Map<String, String> headers,
                                Map<String, Object> fields,
                                HttpJSONObjectCallback callback)
    {
        innerPostForm(context, url, headers, fields, callback);
    }

    public static void postForm(Context context, String url,
                                Map<String, Object> fields,
                                HttpJSONObjectCallback callback)
    {
        postForm(context, url, null, fields, callback);
    }

    public static void postForm(Activity activity, String url,
                                Map<String, String> headers,
                                Map<String, Object> fields,
                                HttpJSONObjectCallback callback)
    {
        innerPostForm(activity, url, headers, fields, callback);
    }

    public static void postForm(Activity activity, String url,
                                Map<String, Object> fields,
                                HttpJSONObjectCallback callback)
    {
        postForm(activity, url, null, fields, callback);
    }

    public static void postForm(Fragment fragment, String url,
                                Map<String, String> headers,
                                Map<String, Object> fields,
                                HttpJSONObjectCallback callback)
    {
        innerPostForm(fragment, url, headers, fields, callback);
    }

    public static void postForm(Fragment fragment, String url,
                                Map<String, Object> fields,
                                HttpJSONObjectCallback callback)
    {
        postForm(fragment, url, null, fields, callback);
    }

    public static void postForm(Context context, String url,
                                Map<String, String> fields)
    {
        postForm(context, url, fields, new HttpTextCallback() {
            @Override
            public void onSucceeded(String text) {
            }

            @Override
            public void onFailed(String code, String message) {
            }

            @Override
            public void onNetworkDisconnected(NetUtils.NetState state) {
            }
        });
    }

    public static void postForm(Activity activity, String url,
                                Map<String, String> fields)
    {
        postForm(activity, url, fields, new HttpTextCallback() {
            @Override
            public void onSucceeded(String text) {
            }

            @Override
            public void onFailed(String code, String message) {
            }

            @Override
            public void onNetworkDisconnected(NetUtils.NetState state) {
            }
        });
    }

    public static void postForm(Fragment fragment, String url,
                                Map<String, String> fields)
    {
        postForm(fragment, url, fields, new HttpTextCallback() {
            @Override
            public void onSucceeded(String text) {
            }

            @Override
            public void onFailed(String code, String message) {
            }

            @Override
            public void onNetworkDisconnected(NetUtils.NetState state) {
            }
        });
    }

    private static Exception saveResponseStreamToFile(Response response, String filePath,
                                                      HttpDownloadCallback callback)
    {
        InputStream inStream = null;
        FileOutputStream outStream = null;

        try
        {
            int len = 0;
            byte[] buf = new byte[2048];

            long total = response.body().contentLength();
            long current = 0;

            if (total == -1)
            {
                total = 1024 * 3000;
            }

            inStream = response.body().byteStream();
            File file = new File(filePath);
            outStream = new FileOutputStream(file);
            while ((len = inStream.read(buf)) != -1)
            {
                outStream.write(buf, 0, len);
                current += len;

                if (callback != null)
                {
                    callback.onProgress(total, current);
                }
            }
            outStream.flush();
        }
        catch (Exception exp)
        {
            return exp;
        }
        finally
        {
            try
            {
                if (outStream != null)
                {
                    outStream.close();
                    outStream = null;
                }
            }
            catch (Exception exp)
            {
            }

            try
            {
                if (inStream != null)
                {
                    inStream.close();
                    inStream = null;
                }
            }
            catch (Exception exp)
            {
            }
        }

        return null;
    }

    public static void download(final Object context, String url, final String savePath,
                           final HttpDownloadCallback callback)
    {
        if (callback != null)
        {
            callback.onStart();
        }

        if (!TextUtils.isEmpty(url)) {
            if (isValidContext(context)) {
                final String realUrl = buildUrl(url, null);
                final Headers realHeaders = buildHeaders(null);

                Context appCtx = getApplicationContext(context);
                NetUtils.test(appCtx, new NetUtils.NetworkTestCallback() {

                    @Override
                    public void onDone(NetUtils.NetState state) {
                        if (state != NetUtils.NetState.Disconnected) {
                            Request request = new Request.Builder()
                                    .url(realUrl)
                                    .headers(realHeaders)
                                    .get()
                                    .build();
                            Call call = sHttpClient.newCall(request);
                            addHttpCall(context, call);
                            call.enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    removeHttpCall(context, call);

                                    if (callback != null) {
                                        if (isValidContext(context)) {
                                            callback.onFailed(S_REQUEST_FAILED_CODE, e.getMessage());
                                        }
                                    }
                                }

                                @Override
                                public void onResponse(Call call, Response response) {
                                    try {
                                        removeHttpCall(context, call);

                                        if (response.code() == 200)
                                        {
                                            Exception exp = saveResponseStreamToFile(response, savePath, callback);
                                            if (exp == null)
                                            {
                                                if (callback != null && isValidContext(context)) {
                                                    callback.onSucceeded(new File(savePath));
                                                }
                                            }
                                            else
                                            {
                                                if (callback != null && isValidContext(context)) {
                                                    callback.onFailed(S_HANDLE_RESPONSE_EXCEPT_CODE, exp.getMessage());
                                                }
                                            }
                                        }
                                        else
                                        {
                                            if (callback != null && isValidContext(context)) {
                                                callback.onFailed(S_RESPONSE_STATUS_NOT_OK, "(" + response.code() + ")" + response.message());
                                            }
                                        }
                                    } catch (Exception exp) {
                                        if (callback != null && isValidContext(context)) {
                                            callback.onFailed(S_HANDLE_RESPONSE_EXCEPT_CODE, exp.getMessage());
                                        }
                                    }
                                }
                            });
                        }
                        else
                        {
                            if (callback != null)
                            {
                                if (isValidContext(context))
                                {
                                    callback.onNetworkDisconnected(state);
                                }
                            }
                        }
                    }
                });
            }
        }
        else
        {
            if (callback != null)
            {
                if (isValidContext(context)) {
                    callback.onFailed(S_URL_IS_EMPTY_CODE, S_URL_IS_EMPTY);
                }
            }
        }
    }

    private static HashMap<Object, List<WeakReference<Call>>> sCalls = new HashMap<Object, List<WeakReference<Call>>>();
    private static Object sCallsLock = new Object();

    private static void addHttpCall(Object context, Call call)
    {
        synchronized (sCallsLock)
        {
            if (!sCalls.containsKey(context))
            {
                sCalls.put(context, new ArrayList<WeakReference<Call>>());
            }
            sCalls.get(context).add(new WeakReference<Call>(call));
        }
    }

    private static void removeHttpCall(Object context, Call call)
    {
        synchronized (sCallsLock)
        {
            if (sCalls.containsKey(context))
            {
                List<WeakReference<Call>> callList = sCalls.get(context);
                if (callList != null && callList.size() > 0)
                {
                    for (int i = 0; i < callList.size(); i++)
                    {
                        try {
                            WeakReference<Call> weakRef = callList.get(i);
                            if (weakRef != null) {
                                Call c = weakRef.get();
                                if (c != null && c == call) {
                                    weakRef.clear();
                                    callList.remove(i);
                                    break;
                                }
                            }
                        }
                        catch (Exception exp)
                        {
                        }
                    }

                    if (callList.size() == 0)
                    {
                        sCalls.remove(context);
                    }
                }
            }
        }
    }

    public static void cancelAll(Context context)
    {
        List<WeakReference<Call>> callList = null;

        synchronized (sCallsLock)
        {
            if (sCalls.containsKey(context))
            {
                callList = sCalls.get(context);
                sCalls.remove(context);
            }
        }

        if (callList != null && callList.size() > 0)
        {
            for (int i = 0; i < callList.size(); i++)
            {
                try {
                    WeakReference<Call> weakRef = callList.get(i);
                    if (weakRef != null) {
                        Call c = weakRef.get();
                        if (c != null && !c.isCanceled()) {
                            c.cancel();
                        }
                    }
                }
                catch (Exception exp)
                {
                }
            }
        }
    }

    public static void cancelAll()
    {
        try {
            sHttpClient.dispatcher().cancelAll();
        }
        catch (Exception exp)
        {
        }
    }

}
