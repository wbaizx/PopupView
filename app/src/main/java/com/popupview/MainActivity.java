package com.popupview;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.library.OnPopupItemClickListener;
import com.library.PopupView;
import com.library.PoputItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private PopupView popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        popup = findViewById(R.id.popup);
        //-------1
        popup.addItems(new PoputItem("11"));
        popup.addItems(new PoputItem(ContextCompat.getDrawable(this, R.mipmap.ic_launcher_round)));
        popup.addItems(new PoputItem("22", ContextCompat.getDrawable(this, R.mipmap.ic_launcher_round)));
        //-------2
        popup.setItemsFromMenu(this, R.menu.aaaa);
        //-------3
        List<String> stringList = new ArrayList<>();
        stringList.add("55");
        popup.setItemsFromList(stringList);

        popup.setOnItemClickListener(new OnPopupItemClickListener() {
            @Override
            public void onItemClickListener(int id, int position, String title) {
                Log.d("wefawef", id + "--" + position + "--" + title);
            }
        });
    }
}
