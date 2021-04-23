package com.puerlink.common;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.view.View;

import java.io.FileOutputStream;

/**
 * Created by wangxm on 2017/2/27.
 */
public class BitmapUtils {

    private static Bitmap sScreenshot = null;

    public static void screenshot(Activity activity)
    {
        try {
            // View是你需要截图的View
            View view = activity.getWindow().getDecorView();
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            Bitmap screenBmp = view.getDrawingCache();

            // 获取状态栏高度
            Rect frame = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;

            // 获取屏幕长和高
            int width = screenBmp.getWidth();
            int height = screenBmp.getHeight();
            // 去掉标题栏
            sScreenshot = Bitmap.createBitmap(screenBmp, 0, statusBarHeight,
                    width, height - statusBarHeight);
            view.destroyDrawingCache();
        }
        catch (Exception exp)
        {
        }
    }

    public static Bitmap getScreenshotImage()
    {
        return sScreenshot;
    }

    public static boolean save(Bitmap bitmap, String filename)
    {
        try
        {
            if (bitmap == null)
            {
                bitmap = sScreenshot;
            }

            FileOutputStream fos = new FileOutputStream(filename);
            if (fos != null)
            {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
                return true;
            }
        }
        catch (Exception exp)
        {
        }
        return false;
    }

    public static Bitmap zoomout(Bitmap bitmap, int maxWidth, int maxHeight)
    {
        int wSource = bitmap.getWidth();
        int hSource = bitmap.getHeight();
        if (wSource > maxWidth && hSource > maxHeight)
        {
            if (wSource > hSource)
            {
                int wDest = maxWidth;
                int hDest = (int)((float)maxWidth * ((float)hSource / (float)wSource));
                return ThumbnailUtils.extractThumbnail(bitmap, wDest, hDest);
            }
            else
            {
                int hDest = maxHeight;
                int wDest = (int)((float)maxHeight * ((float)wSource / (float)hSource));
                return ThumbnailUtils.extractThumbnail(bitmap, wDest, hDest);
            }
        }
        else if (wSource > maxWidth)
        {
            int wDest = maxWidth;
            int hDest = (int)((float)maxWidth * ((float)hSource / (float)wSource));
            return ThumbnailUtils.extractThumbnail(bitmap, wDest, hDest);
        }
        else if (hSource > maxHeight)
        {
            int hDest = maxHeight;
            int wDest = (int)((float)maxHeight * ((float)wSource / (float)hSource));
            return ThumbnailUtils.extractThumbnail(bitmap, wDest, hDest);
        }
        else
        {
            Bitmap bmpResult = bitmap.copy(Bitmap.Config.ARGB_8888, false);
            return bmpResult;
        }
    }

}
