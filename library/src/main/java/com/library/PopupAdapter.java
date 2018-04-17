package com.library;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by android1 on 2017/12/18.
 */

public class PopupAdapter extends BaseAdapter {
    private List<Map<String, Object>> list;
    private Context context;
    private int listItemHeight;
    private int fontsize;

    public PopupAdapter(Context context, List<Map<String, Object>> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.base_popupview_item, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.popupview_item_img = view.findViewById(R.id.popupview_item_img);
            viewHolder.popupview_item_text = view.findViewById(R.id.popupview_item_text);
            viewHolder.popupview_item_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontsize);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewHolder.popupview_item_text.getLayoutParams();
            layoutParams.height = listItemHeight;
            viewHolder.popupview_item_text.setLayoutParams(layoutParams);
            LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) viewHolder.popupview_item_img.getLayoutParams();
            layoutParams1.height = listItemHeight;
            viewHolder.popupview_item_img.setLayoutParams(layoutParams1);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (list.get(i).get(Key.TITLE) != null) {
            viewHolder.popupview_item_text.setVisibility(View.VISIBLE);
            viewHolder.popupview_item_text.setText((String) list.get(i).get(Key.TITLE));
        } else {
            viewHolder.popupview_item_text.setVisibility(View.GONE);
        }

        if (list.get(i).get(Key.IMG) != null) {
            viewHolder.popupview_item_img.setVisibility(View.VISIBLE);
            viewHolder.popupview_item_img.setImageDrawable((Drawable) list.get(i).get(Key.IMG));
        } else {
            viewHolder.popupview_item_img.setVisibility(View.GONE);
        }

        return view;
    }

    public void setListItemHeight(int listItemHeight) {
        this.listItemHeight = listItemHeight;
    }

    public void setFontsize(int fontsize) {
        this.fontsize = fontsize;
    }


    static class ViewHolder {
        ImageView popupview_item_img;
        TextView popupview_item_text;
    }
}
