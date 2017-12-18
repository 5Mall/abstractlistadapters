package com.walkermanx.abstractlistadapters;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by 张伟 on 2017/6/26 : 16:37
 * Email:zhangwei-baba@163.com
 * Description: 所有list的item布局都继承此类
 */

public abstract class BaseItemView<T> extends LinearLayout {
    public BaseItemView(Context context) {
        super(context);
    }

    public BaseItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public abstract void setModel(T model);

    public abstract void setPosition(int position,int sum);

    @Override
    public void setSelected(boolean b) {
        super.setSelected(b);
    }
}
