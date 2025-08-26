package com.base.mvvm.binding.viewadapter.checkbox;

import android.graphics.drawable.Drawable;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.databinding.BindingAdapter;

import com.base.mvvm.binding.command.BindingCommand;

public class ViewAdapter {
    /**
     * @param bindingCommand //绑定监听
     */
    @SuppressWarnings("unchecked")
    @BindingAdapter(value = {"onCheckedChangedCommand", "buttonDrawable"}, requireAll = false)
    public static void setCheckedChanged(final CheckBox checkBox, Drawable buttonDrawable, final BindingCommand<Boolean> bindingCommand) {

        checkBox.setButtonDrawable(buttonDrawable);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                bindingCommand.execute(b);
            }
        });
    }
}
