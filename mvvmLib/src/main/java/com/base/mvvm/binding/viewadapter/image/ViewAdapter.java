package com.base.mvvm.binding.viewadapter.image;


import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.adapters.Converters;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

//import jp.wasabeef.glide.transformations.BlurTransformation;

public final class ViewAdapter {
    @BindingAdapter(value = {"url", "placeholderRes", "blur", "circle_crop", "none_cache", "rounded_corner", "request_height", "request_width", "errorRes"}, requireAll = false)
    public static void setImageUri(ImageView imageView, CharSequence url, int placeholderRes, int blurNum, boolean isCircle, boolean noCache, int roundedCorner,
                                   int requestHeight, int requestWidth, int errorRes) {
        //使用Glide框架加载图片
        /*if (blurNum > 0) {
            // 高斯模糊
            BlurTransformation blur = new BlurTransformation(blurNum, 3);
            Glide.with(imageView.getContext())
                    .load(url)
                    .apply(new RequestOptions().placeholder(placeholderRes))
                    .apply(RequestOptions.bitmapTransform(blur))
                    .into(imageView);
        } else {
            // 普通加载
            Glide.with(imageView.getContext())
                    .load(url)
                    .apply(new RequestOptions().placeholder(placeholderRes))
                    .into(imageView);
        }*/

        /*if (isCircle) {
            Glide.with(imageView.getContext())
                    .load(url)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    //.diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                    .placeholder(placeholderRes).into(imageView);
        } else {
            Glide.with(imageView.getContext())
                    .load(url)
                    .apply(new RequestOptions().placeholder(placeholderRes))
                    .into(imageView);
        }*/

        RequestOptions options = new RequestOptions();
        /// 圆形
        if (isCircle) {
            options.transform(new CircleCrop());
        }

        /// 圆角
        if (roundedCorner > 0) {
            options.transform(new CenterCrop(), new RoundedCorners(roundedCorner));
        }

        /// 是否使用缓存
        if (noCache) {
            options.diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true);
        }
        options.placeholder(placeholderRes);
        options.error(errorRes);
        options.disallowHardwareConfig();
        if (requestWidth > 0 && requestHeight > 0) {
            options.override(requestWidth, requestHeight);
            /*ViewGroup.LayoutParams params = imageView.getLayoutParams();
            params.height = requestHeight;
            params.width = requestWidth;
            imageView.setLayoutParams(params);*/
        }

        try {
            if (TextUtils.isEmpty(url)) {
                Glide.with(imageView.getContext()).load(placeholderRes).apply(options).into(imageView);
                return;
            }
            Glide.with(imageView.getContext()).load(url).apply(options).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @BindingAdapter(value = {"resId", "isGif"}, requireAll = false)
    public static void setImageGif(ImageView imageView, int resId, boolean isGif) {
        Context context = imageView.getContext();
        if(context==null){
            return;
        }
        if( context instanceof Activity){
            Activity activity = (Activity) context;
            if (activity.isFinishing() || activity.isDestroyed()) {
                // 如果 Activity 已经销毁，直接返回
                return;
            }
        }
        if (isGif) {
            Glide.with(context)
                    .asGif()
                    .skipMemoryCache(true)
                    .load(resId)
                    .into(imageView);
        } else {
            Glide.with(context)
                    .load(resId)
                    .into(imageView);
        }
    }

    @BindingAdapter(value = {"tintColor"}, requireAll = false)
    public static void setTint(ImageView view, int colorId) {
        //int color = view.getContext().getColor(colorId);
        view.setImageTintList(Converters.convertColorToColorStateList(colorId));
    }

}

