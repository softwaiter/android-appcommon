package com.puerlink.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.puerlink.appcommon.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangxm on 2017/7/8.
 */

public class StarLevelView extends LinearLayout {

    private int mNormalResource;
    private int mSelectedResource;

    private List<ImageView> mChilds = new ArrayList<ImageView>();

    public StarLevelView(Context context) {
        this(context, null);
    }

    public StarLevelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StarLevelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOrientation(HORIZONTAL);

        initAttrs(context, attrs);

        initChildViews();
    }

    private void initAttrs(Context context, AttributeSet attrs)
    {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StarLevelView);
            try
            {
                mNormalResource = typedArray.getResourceId(R.styleable.StarLevelView_normalImage, 0);
                mSelectedResource = typedArray.getResourceId(R.styleable.StarLevelView_selectedImage, 0);
            } catch (Exception e) {
            } finally {
                typedArray.recycle();
            }
        }
    }

    private void initChildViews()
    {
        LayoutParams lp = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        for (int i = 0; i < 5; i++) {
            ImageView child = new ImageView(getContext());
            addView(child, lp);
            if (mNormalResource != 0)
            {
                child.setImageResource(mNormalResource);
            }
            else
            {
                child.setImageResource(R.drawable.icon_star_normal);
            }

            mChilds.add(child);
        }
    }

    public void setStarNum(int num)
    {
        for (int i = 0; i < mChilds.size(); i++)
        {
            ImageView child = mChilds.get(i);
            if (child != null)
            {
                if (i <= num)
                {
                    if (mSelectedResource != 0) {
                        child.setImageResource(mSelectedResource);
                    } else {
                        child.setImageResource(R.drawable.icon_star_selected);
                    }
                }
                else
                {
                    if (mNormalResource != 0)
                    {
                        child.setImageResource(mNormalResource);
                    }
                    else
                    {
                        child.setImageResource(R.drawable.icon_star_normal);
                    }
                }
            }
        }
    }

    public void setNightMode(boolean isNight)
    {
        for (int i = 0; i < mChilds.size(); i++) {
            ImageView child = mChilds.get(i);
            if (child != null) {
                if (isNight)
                {
                    child.setColorFilter(Color.rgb(55, 55, 55), PorterDuff.Mode.MULTIPLY);
                }
                else
                {
                    child.clearColorFilter();
                }
            }
        }
    }
}
