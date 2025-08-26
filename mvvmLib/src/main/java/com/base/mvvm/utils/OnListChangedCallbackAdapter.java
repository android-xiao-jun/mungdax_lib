package com.base.mvvm.utils;

import androidx.databinding.ObservableList;

public class OnListChangedCallbackAdapter<T extends ObservableList> extends ObservableList.OnListChangedCallback<T> {

    @Override
    public void onChanged(T sender) {
        onSomethingChanged(sender);
    }

    @Override
    public void onItemRangeChanged(T sender, int positionStart, int itemCount) {
        onSomethingChanged(sender);
    }

    @Override
    public void onItemRangeInserted(T sender, int positionStart, int itemCount) {
        onSomethingChanged(sender);
    }

    @Override
    public void onItemRangeMoved(T sender, int fromPosition, int toPosition, int itemCount) {
        onSomethingChanged(sender);
    }

    @Override
    public void onItemRangeRemoved(T sender, int positionStart, int itemCount) {
        onSomethingChanged(sender);
    }

    public void onSomethingChanged(T sender){

    }
}
