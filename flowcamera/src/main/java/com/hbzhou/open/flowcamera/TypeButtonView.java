package com.hbzhou.open.flowcamera;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

/**
 * Desc
 *
 * @author zhangxiaolin
 * Date 2023/3/7
 * Copyright © ALLO
 */
public class TypeButtonView extends RelativeLayout {

    public static final int TYPE_SEND = 1;
    public static final int TYPE_SEND_ONLY_ONE = 0;

    public TypeButtonView(Context context) {
        super(context);
    }

    public TypeButtonView(Context context, int type) {
        super(context);
        removeAllViews();
        if (type == TYPE_SEND) {
            LayoutInflater.from(context).inflate(R.layout.layout_bottom_send_view, this);
        } else {
            LayoutInflater.from(context).inflate(R.layout.layout_bottom_send_only_one_view, this);
        }
    }

}
