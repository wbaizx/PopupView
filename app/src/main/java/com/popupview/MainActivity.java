package com.popupview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.library.OnDismissListener;
import com.library.OnPopupItemClickListener;
import com.library.PopupView;

public class MainActivity extends AppCompatActivity {
    private PopupView popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        popup = findViewById(R.id.popup);

        popup.setMaxNum(5);

        popup.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismissListener() {

            }
        });

        popup.setOnItemClickListener(new OnPopupItemClickListener() {
            @Override
            public void onItemClickListener(int id, int position, String title) {
                Log.d("onItemClickListener", id + "--" + position + "--" + title);
            }
        });
    }
}
