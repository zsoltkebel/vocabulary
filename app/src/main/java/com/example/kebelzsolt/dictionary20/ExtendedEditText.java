package com.example.kebelzsolt.dictionary20;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by Kébel Zsolt on 1/22/2017.
 */

public class ExtendedEditText extends EditText {
    Context context;

    public ExtendedEditText(Context context) {
        super(context);
        this.context = context;

    }

    public ExtendedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ExtendedEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // User has pressed Back key. So hide the keyboard
            this.clearFocus();
            this.setHint(R.string.txt_search);
            // TODO: Hide your view as you do it in your activity
        }
        return super.dispatchKeyEvent(event);
    }
}
