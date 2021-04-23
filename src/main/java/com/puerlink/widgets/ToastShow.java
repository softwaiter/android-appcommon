package com.puerlink.widgets;

import com.puerlink.appcommon.R;
import com.puerlink.common.DisplayUtils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class ToastShow {

    private static WeakReference<Toast> sToast;

    private static void closeNow()
    {
        if (sToast != null)
        {
            Toast t = sToast.get();
            if (t != null)
            {
                try
                {
                    t.cancel();
                }
                catch (Exception exp)
                {
                }
                finally {
                    t = null;

                    sToast.clear();
                    sToast = null;
                }
            }
        }
    }

    private static int sCurrTheme = -1;
    private static GradientDrawable sBgDrawable = null;
    private static int sTextColor;
    private static int sTextSize;

    private static TextView getToastContentView(Context context)
    {
        TextView result = new TextView(context);
        result.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        result.setPadding(DisplayUtils.dp2px(context, 20), DisplayUtils.dp2px(context, 8),
                DisplayUtils.dp2px(context, 20), DisplayUtils.dp2px(context, 8));

        Resources.Theme theme = context.getTheme();
        if (sBgDrawable == null || (theme != null && theme.hashCode() != sCurrTheme)) {
            int backgroundColor = Color.parseColor("#777777");
            int borderSize = 1;
            int borderColor = Color.parseColor("#7B7B7B");
            int cornerRadius = DisplayUtils.dp2px(context, 5);
            sTextColor = Color.WHITE;
            sTextSize = 17;

            if (theme != null)
            {
                TypedArray ta = theme.obtainStyledAttributes(R.styleable.Puerlink_Toast);
                if (ta != null)
                {
                    try
                    {
                        backgroundColor = ta.getColor(R.styleable.Puerlink_Toast_pl_toast_background_color, backgroundColor);
                        cornerRadius = ta.getDimensionPixelSize(R.styleable.Puerlink_Toast_pl_toast_corner_size, cornerRadius);
                        borderSize = ta.getDimensionPixelSize(R.styleable.Puerlink_Toast_pl_toast_border_size, borderSize);
                        borderColor = ta.getColor(R.styleable.Puerlink_Toast_pl_toast_border_color, borderColor);
                        sTextColor = ta.getColor(R.styleable.Puerlink_Toast_pl_toast_text_color, sTextColor);

                        int textSize = ta.getDimensionPixelSize(R.styleable.Puerlink_Toast_pl_toast_text_size, -1);
                        if (textSize != -1) {
                            sTextSize = DisplayUtils.px2dp(context, textSize);
                        }
                    }
                    catch (Exception exp)
                    {
                    }
                    finally {
                        ta.recycle();
                    }
                }
            }

            sBgDrawable = new GradientDrawable();
            sBgDrawable.setColor(backgroundColor);
            sBgDrawable.setStroke(borderSize, borderColor);
            sBgDrawable.setCornerRadius(cornerRadius);
        }

        result.setBackgroundDrawable(sBgDrawable);
        result.setTextColor(sTextColor);
        result.setTextSize(sTextSize);

        return result;
    }

    public synchronized static void show(Context context, String text, int textColor, float textSize, int gravity, int xOffset, int yOffset, int showtime)
    {
        if (!TextUtils.isEmpty(text)) {
            closeNow();

            Toast t = Toast.makeText(context, "", showtime);
            t.setGravity(gravity, xOffset, yOffset);
            TextView tv = getToastContentView(context);
            tv.setText(text);
            if (textColor != -1)
            {
                tv.setTextColor(textColor);
            }
            if (textSize != -1)
            {
                tv.setTextSize(textSize);
            }
            t.setView(tv);

            sToast = new WeakReference<Toast>(t);

            t.show();
        }
    }

    public static void centerShort(Context context, String text, int textColor, int textSize)
    {
        show(context, text, textColor, textSize,
                Gravity.CENTER_HORIZONTAL |
                        Gravity.CENTER_VERTICAL,
                0, 0, Toast.LENGTH_SHORT);
    }

    public static void centerShort(Context context, int strId, int textColor, int textSize)
    {
        centerShort(context, context.getString(strId), textColor, textSize);
    }

    public static void centerShort(Context context, String text, int textSize)
    {
        centerShort(context, text, -1, textSize);
    }

    public static void centerShort(Context context, int strId, int textSize)
    {
        centerShort(context, context.getString(strId), textSize);
    }

    public static void centerShort(Context context, String text)
    {
        centerShort(context, text, -1);
    }

    public static void centerShort(Context context, int strId)
    {
        centerShort(context, context.getString(strId));
    }

    public static void centerLong(Context context, String text, int textColor, int textSize)
    {
        show(context, text, textColor, textSize,
                Gravity.CENTER_HORIZONTAL |
                        Gravity.CENTER_VERTICAL,
                0, 0, Toast.LENGTH_LONG);
    }

    public static void centerLong(Context context, int strId, int textColor, int textSize)
    {
        centerLong(context, context.getString(strId), textColor, textSize);
    }

    public static void centerLong(Context context, String text, int textSize)
    {
        centerLong(context, text, -1, textSize);
    }

    public static void centerLong(Context context, int strId, int textSize)
    {
        centerLong(context, context.getString(strId), textSize);
    }

    public static void centerLong(Context context, String text)
    {
        centerLong(context, text, -1);
    }

    public static void centerLong(Context context, int strId)
    {
        centerLong(context, context.getString(strId));
    }

    public static void topShort(Context context, String text, int textColor, int textSize)
    {
        show(context, text, textColor, textSize,
                Gravity.CENTER_HORIZONTAL | Gravity.TOP,
                0, DisplayUtils.dp2px(context, 85), Toast.LENGTH_SHORT);
    }

    public static void topShort(Context context, int strId, int textColor, int textSize)
    {
        topShort(context, context.getString(strId), textColor, textSize);
    }

    public static void topShort(Context context, String text, int textSize)
    {
        topShort(context, text, -1, textSize);
    }

    public static void topShort(Context context, int strId, int textSize)
    {
        topShort(context, context.getString(strId), textSize);
    }

    public static void topShort(Context context, String text)
    {
        topShort(context, text, -1);
    }

    public static void topShort(Context context, int strId)
    {
        topShort(context, context.getString(strId));
    }

    public static void topLong(Context context, String text, int textColor, int textSize)
    {
        show(context, text, textColor, textSize,
                Gravity.CENTER_HORIZONTAL | Gravity.TOP,
                0, DisplayUtils.dp2px(context, 85), Toast.LENGTH_LONG);
    }

    public static void topLong(Context context, int strId, int textColor, int textSize)
    {
        topLong(context, context.getString(strId), textColor, textSize);
    }

    public static void topLong(Context context, String text, int textSize)
    {
        topLong(context, text, -1, textSize);
    }

    public static void topLong(Context context, int strId, int textSize)
    {
        topLong(context, context.getString(strId), textSize);
    }

    public static void topLong(Context context, String text)
    {
        topLong(context, text, -1);
    }

    public static void topLong(Context context, int strId)
    {
        topLong(context, context.getString(strId));
    }

    public static void bottomShort(Context context, String text, int textColor, int textSize)
    {
        show(context, text, textColor, textSize,
                Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM,
                0, DisplayUtils.dp2px(context, 66), Toast.LENGTH_SHORT);
    }

    public static void bottomShort(Context context, int strId, int textColor, int textSize)
    {
        bottomShort(context, context.getString(strId), textColor, textSize);
    }

    public static void bottomShort(Context context, String text, int textSize)
    {
        bottomShort(context, text, -1, textSize);
    }

    public static void bottomShort(Context context, int strId, int textSize)
    {
        bottomShort(context, context.getString(strId), textSize);
    }

    public static void bottomShort(Context context, String text)
    {
        bottomShort(context, text, -1);
    }

    public static void bottomShort(Context context, int strId)
    {
        bottomShort(context, context.getString(strId));
    }

    public static void bottomLong(Context context, String text, int textColor, int textSize)
    {
        show(context, text, textColor, textSize,
                Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM,
                0, DisplayUtils.dp2px(context, 66), Toast.LENGTH_LONG);
    }

    public static void bottomLong(Context context, int strId, int textColor, int textSize)
    {
        bottomLong(context, context.getString(strId), textColor, textSize);
    }

    public static void bottomLong(Context context, String text, int textSize)
    {
        bottomLong(context, text, -1, textSize);
    }

    public static void bottomLong(Context context, int strId, int textSize)
    {
        bottomLong(context, context.getString(strId), textSize);
    }

    public static void bottomLong(Context context, String text)
    {
        bottomLong(context, text, -1);
    }

    public static void bottomLong(Context context, int strId)
    {
        bottomLong(context, context.getString(strId));
    }

}
