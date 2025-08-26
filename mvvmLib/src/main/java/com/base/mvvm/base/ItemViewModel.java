package com.base.mvvm.base;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

/**
 * ItemViewModel 这里其实使用直接的泛型不用规定上界，
 * @param <VM>
 */
public class ItemViewModel<VM extends BaseViewModel> {
    //protected VM viewModel;

    protected WeakReference<VM> viewModelWeakReference;

    public ItemViewModel(@NonNull VM viewModel) {

        viewModelWeakReference = new WeakReference<>(viewModel);
        //this.viewModel = viewModel;
    }

    public VM getViewModel() {
        if (viewModelWeakReference != null) {
            return viewModelWeakReference.get();
        }
        return null;
    }
}
