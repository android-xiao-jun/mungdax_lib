package com.allo.view;

import android.content.Context;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;

public class PhoneEditText extends androidx.appcompat.widget.AppCompatEditText implements TextWatcher {

    public PhoneEditText(Context context) {
        super(context);
        setInputType(InputType.TYPE_CLASS_PHONE);
        addTextChangedListener(this);
    }

    public PhoneEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setInputType(InputType.TYPE_CLASS_PHONE);
        addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public String getPhoneNumber() {
        CharSequence text = super.getText();
        return text.toString().replaceAll(" ", "");
    }

    public String getOriginalNumber() {
        return getText().toString();
    }
}
