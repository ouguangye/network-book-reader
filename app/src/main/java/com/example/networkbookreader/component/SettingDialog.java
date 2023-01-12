package com.example.networkbookreader.component;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.networkbookreader.R;

public class SettingDialog extends Dialog {
    private int textSize;
    private View.OnClickListener confirmClickListener;

    public SettingDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_dialog);

        EditTextWithBt editTextWithBt = findViewById(R.id.ed_bt);
        editTextWithBt.setDefaultValue(textSize);
        editTextWithBt.setOnChangeListener(value -> {
            Log.d("textSize", String.valueOf(value));
            textSize = value;
        });

        // 确定、取消按钮
        TextView dialog_confirm = (TextView) findViewById(R.id.dialog_confirm);
        TextView dialog_cancel = (TextView) findViewById(R.id.dialog_cancel);
        dialog_confirm.setOnClickListener(confirmClickListener);
        dialog_cancel.setOnClickListener(view-> SettingDialog.this.dismiss());
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setConfirmClickListener(View.OnClickListener confirmClickListener) {
        this.confirmClickListener = confirmClickListener;
    }
}
