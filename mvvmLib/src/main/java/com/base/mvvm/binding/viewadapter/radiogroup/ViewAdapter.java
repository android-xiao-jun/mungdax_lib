package com.base.mvvm.binding.viewadapter.radiogroup;

import androidx.annotation.IdRes;
import androidx.databinding.BindingAdapter;

import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.base.mvvm.binding.command.BindingCommand;

public class ViewAdapter {
    @BindingAdapter(value = {"onCheckedChangedCommand"}, requireAll = false)
    public static void onCheckedChangedCommand(final RadioGroup radioGroup, final BindingCommand<String> bindingCommand) {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                bindingCommand.execute(radioButton.getText().toString());
            }
        });
    }
}
