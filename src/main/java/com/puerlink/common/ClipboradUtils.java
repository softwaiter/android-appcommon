package com.puerlink.common;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by wangxm on 2017/2/27.
 */
public class ClipboradUtils {

    private static ClipboardManager sClipboardManager = null;

    private static ClipboardManager getClipboardManager(Context context)
    {
        if (sClipboardManager == null) {
            sClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        }
        return sClipboardManager;
    }

    public static void copy(Context context, String text)
    {
        ClipboardManager cm = getClipboardManager(context);
        ClipData cd = ClipData.newPlainText("文本", text);
        cm.setPrimaryClip(cd);
    }

    public static void copy(EditText view)
    {
        copy(view.getContext(), view.getText().toString());
    }

    public static void copy(TextView view)
    {
        copy(view.getContext(), view.getText().toString());
    }

    public static String getText(Context context)
    {
        String result = "";
        ClipboardManager cm = getClipboardManager(context);
        ClipData cd = cm.getPrimaryClip();
        if (cd != null && cd.getItemCount() > 0)
        {
            result = cd.getItemAt(0).getText().toString();
        }
        return result;
    }

    public static void paste(EditText view)
    {
        String text = getText(view.getContext());
        view.setText(text);
    }

    public static void paste(TextView view)
    {
        String text = getText(view.getContext());
        view.setText(text);
    }

}
