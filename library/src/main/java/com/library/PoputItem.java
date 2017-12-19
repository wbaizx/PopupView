package com.library;

import android.graphics.drawable.Drawable;

/**
 * Created by android1 on 2017/12/16.
 */

public class PoputItem {
    private int id;
    private String title;
    private Drawable image;

    public PoputItem(String title, Drawable image) {
        this.title = title;
        this.image = image;
        this.id = -1;
    }

    public PoputItem(Drawable image) {
        this.title = null;
        this.image = image;
        this.id = -1;
    }

    public PoputItem(String title) {
        this.title = title;
        this.image = null;
        this.id = -1;
    }

    public String getTitle() {
        return title;
    }

    public Drawable getImage() {
        return image;
    }

    public int getId() {
        return id;
    }
}
