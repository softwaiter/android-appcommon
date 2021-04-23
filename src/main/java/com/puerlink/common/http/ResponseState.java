package com.puerlink.common.http;

/**
 * Created by wangxm on 2016/9/7.
 */
public class ResponseState {

    private boolean mState = true;
    private String mCode = "succ";
    private String mMessage = "";

    public ResponseState()
    {
    }

    public ResponseState(boolean state, String code, String message)
    {
        mState = state;
        mCode = code;
        mMessage = message;
    }

    public boolean getState()
    {
        return mState;
    }

    public void setState(boolean state)
    {
        mState = state;
    }

    public String getCode()
    {
        return mCode;
    }

    public void setCode(String code)
    {
        mCode = code;
    }

    public String getMessage()
    {
        return mMessage;
    }

    public void setMessage(String message)
    {
        mMessage = message;
    }

}
