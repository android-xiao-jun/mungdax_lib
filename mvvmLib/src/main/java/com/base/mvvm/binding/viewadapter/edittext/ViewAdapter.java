package com.base.mvvm.binding.viewadapter.edittext;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.base.mvvm.binding.command.BindingCommand;

public class ViewAdapter {
    /**
     * EditText重新获取焦点的事件绑定
     */
    @BindingAdapter(value = {"requestFocus"}, requireAll = false)
    public static void requestFocusCommand(EditText editText, final Boolean needRequestFocus) {
        if (needRequestFocus) {
            editText.setSelection(editText.getText().length());
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
        editText.setFocusableInTouchMode(needRequestFocus);
    }

    /**
     * EditText输入文字改变的监听
     */
    @BindingAdapter(value = {"textChanged","afterTextChangedCommand"}, requireAll = false)
    public static void addTextChangedListener(EditText editText, final BindingCommand<String> textChanged,final BindingCommand<String> afterTextChangedCommand) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {
                if (textChanged != null) {
                    textChanged.execute(text.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (afterTextChangedCommand != null) {
                    afterTextChangedCommand.execute(editable.toString());
                }
            }
        });
    }

    /**
     * EditText 键盘确定建监听
     */
    @BindingAdapter(value = {"imeActionCommand"}, requireAll = false)
    public static void addActionCommand(EditText editText, final BindingCommand<Integer> command) {

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (command != null) {
                    command.execute(actionId);
                    return true;
                }
                return false;
            }
        });
    }
}
