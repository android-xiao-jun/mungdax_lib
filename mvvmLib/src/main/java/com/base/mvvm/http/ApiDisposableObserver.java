package com.base.mvvm.http;


import com.base.mvvm.utils.KLog;
import com.base.mvvm.utils.ToastUtils;
import com.base.mvvm.utils.Utils;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;

/**
 * 统一的Code封装处理
 */

public abstract class ApiDisposableObserver<T extends BaseResponse> extends DisposableObserver<T> {
    public abstract void onResult(T t);

    public abstract void onResultError(String e);

    @Override
    public void onComplete() {  }

    @Override
    public void onError(Throwable e) {

        //KLog.e("ApiObserver", e);
        e.printStackTrace();
        if (e instanceof ResponseThrowable) {
            ResponseThrowable rError = (ResponseThrowable) e;
            //ToastUtils.showShort(rError.message);
            onResultError(rError.message);
            return;
        }
        //其他全部甩锅网络异常
        ToastUtils.showShort("网络异常");

        onResultError("网络异常");
    }

    @Override
    public void onStart() {
        super.onStart();
        //ToastUtils.showShort("http is start");
        // if  NetworkAvailable no !   must to call onCompleted
        if (!NetworkUtil.isNetworkAvailable(Utils.getContext())) {
            KLog.d("无网络，读取缓存数据");

            onResultError("无网络");
        }
    }

    @Override
    public void onNext(@NonNull T t) {
        switch (t.getStatus()) {
            case 0:
                KLog.e("@@@--------------" + t.getMsg());
                onResultError(t.getMsg());
                break;
            case 1:
                onResult(t);
                break;
        }
    }

}