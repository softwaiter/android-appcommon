package com.puerlink.common;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by wangxm on 2016/11/11.
 */
public class PermissionUtils {

    /**
     * 是否允许录音
     * @param context
     * @return
     */
    public static boolean allowRecordAudio(Context context)
    {
        int result = context.checkCallingPermission(Manifest.permission.RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 是否允许访问扩展存储设备
     * @param context
     * @return
     */
    public static boolean allowReadExternalStorage(Context context)
    {
        int result = context.checkCallingPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;

/*
        CALENDAR

                READ_CALENDAR

                WRITE_CALENDAR

        CAMERA

                CAMERA

        CONTACTS

                READ_CONTACTS
                WRITE_CONTACTS
                GET_ACCOUNTS

        LOCATION

                ACCESS_FINE_LOCATION
                ACCESS_COARSE_LOCATION

        MICROPHONE

                RECORD_AUDIO

        PHONE

                READ_PHONE_STATE
                CALL_PHONE
                READ_CALL_LOG
                WRITE_CALL_LOG
                ADD_VOICEMAIL
                USE_SIP
                PROCESS_OUTGOING_CALLS

        SENSORS

                BODY_SENSORS

        SMS

                SEND_SMS
                RECEIVE_SMS
                READ_SMS
                RECEIVE_WAP_PUSH
                RECEIVE_MMS

        STORAGE

                READ_EXTERNAL_STORAGE
                WRITE_EXTERNAL_STORAGE
*/

//        context.checkCallingPermission()
//        context.checkPermission()
//        context.checkCallingOrSelfPermission()
    }

}
