package com.base.mvvm.base;

import androidx.databinding.ViewDataBinding;

/**
 * @ProjectName: Accompany
 * @Package: com.phn.mvvm.base
 * @ClassName: BaseRequestFragment
 * @Description: java类作用描述
 * @Author: Roy
 * @CreateDate: 2020/12/1 0001 11:04
 * @Version: 1.0
 */
public abstract class BaseRequestFragment<V extends ViewDataBinding, VM extends BaseViewModel> extends BaseFragment<V, VM>{

    private long mRequestTime;

    @Override
    public void onResume() {
        super.onResume();

        /// 大于10分钟重新获取网络数据
        if (System.currentTimeMillis() - mRequestTime > 10 * 60 * 1000) {
            mRequestTime = System.currentTimeMillis();
            loadData();
        }
    }

    public abstract void loadData();
}
