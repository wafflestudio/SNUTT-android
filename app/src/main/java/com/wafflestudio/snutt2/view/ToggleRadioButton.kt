package com.wafflestudio.snutt2.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioGroup;

/**
 * Created by makesource on 2017. 8. 27..
 */

public class ToggleRadioButton extends androidx.appcompat.widget.AppCompatRadioButton {

    public ToggleRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void toggle() {
        if(isChecked()) {
            if(getParent() instanceof RadioGroup) {
                ((RadioGroup)getParent()).clearCheck();
            }
        } else {
            setChecked(true);
        }
    }
}