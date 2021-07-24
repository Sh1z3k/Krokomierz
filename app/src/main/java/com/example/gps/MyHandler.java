package com.example.gps;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class MyHandler extends Handler {

    public static final int SET_TEXT_VIEW = 1;
    private TextView textView;

    public MyHandler() {}

    public void handleMessage(Message message) {
        switch (message.what) {
            case SET_TEXT_VIEW:
                if(textView == null) break;
                else textView.setText((String) message.obj);
                break;
        }
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }
}
