package com.zhihu.matisse;


import android.app.Activity;

import android.net.Uri;

import com.yalantis.ucrop.UCropActivity;
import com.yalantis.ucrop.util.FileUtils;
import com.zhihu.matisse.internal.entity.SelectionSpec;

import java.io.File;

/**
 * @ProjectName: matisse
 * @Package: com.zhihu.matisse
 * @ClassName: Crop
 * @Description: java类作用描述
 * @Author: Roy
 * @CreateDate: 2020/12/14 18:03
 * @Version: 1.0
 */
public class UCrop {

    /**
     * 去裁剪
     */
    public static void startCrop(Activity activity, Uri originalUri) {

        SelectionSpec spec = SelectionSpec.getInstance();
        String originFile = FileUtils.getPath(activity, originalUri);
        String mime = MimeType.getLastImgType(originFile);
        File file = new File(spec.cropCacheFolder + mime);
        file.deleteOnExit();

        Uri destinationUri = Uri.fromFile(file);

        com.yalantis.ucrop.UCrop uCrop = com.yalantis.ucrop.UCrop.of(originalUri, destinationUri);

        com.yalantis.ucrop.UCrop.Options options = new com.yalantis.ucrop.UCrop.Options();
        //设置裁剪图片可操作的手势
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        //是否隐藏底部容器，默认显示
        options.setHideBottomControls(true);
        //设置标题栏文字
        /*SpannableStringBuilder ssb = null;
        try {
            int fontResId = activity.getResources().getIdentifier("ukijtor", "font", activity.getPackageName());
            Typeface tf = ResourcesCompat.getFont(activity, fontResId);
            ssb = SpanUtils.Companion.with(null)
                    .appendLine("يۆتكەش ۋە تارايتىش")
                    .setFontSize(9, true)
                    .setTypeface(tf)
                    .append("移动和缩放")
                    .setFontSize(12, true).create();
        }catch (Exception e) {}
        if (ssb == null)
            options.setToolbarTitle("移动和缩放");
        else
            options.setToolbarTitle(ssb.toString());*/
        //设置是否显示裁剪网格
        options.setShowCropGrid(false);
        //设置是否为圆形裁剪框
//        options.setCircleDimmedLayer(true);
        options.setCircleDimmedLayer(spec.isCircleCrop);
        //设置是否显示裁剪边框(true为方形边框)
        options.setShowCropFrame(false);
        /*//设置toolbar颜色
        options.setToolbarColor(ActivityCompat.getColor(activity, R.color.colorPrimary));
        //设置状态栏颜色
        options.setStatusBarColor(ActivityCompat.getColor(activity, R.color.colorPrimary));*/
        //是否能调整裁剪框
        options.setFreeStyleCropEnabled(false);
        //UCrop配置
        uCrop.withOptions(options);
        //设置裁剪图片的宽高比，比如16：9
        uCrop.withAspectRatio(1, 1);

        //跳转裁剪页面
        uCrop.start(activity, com.yalantis.ucrop.UCrop.REQUEST_CROP);
    }
}
