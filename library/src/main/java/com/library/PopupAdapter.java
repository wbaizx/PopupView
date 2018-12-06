package com.library;

import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by android1 on 2017/12/18.
 */

public class PopupAdapter extends BaseAdapter {
    private List<Map<String, Object>> list;
    /**
     * 每个item布局高度
     */
    private int listItemHeight;
    /**
     * item字体大小
     */
    private int fontsize;
    /**
     * item字体对齐方式
     */
    private int itemTextGravity;
    /**
     * item字体选中颜色
     */
    private int itemTextSelectColor;
    /**
     * item字体未选中颜色
     */
    private int itemTextColor;
    /**
     * 当前选中项
     */
    private int nowPosition = -1;

    public PopupAdapter(List<Map<String, Object>> list) {
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
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.base_popupview_item, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.popupview_item_img = view.findViewById(R.id.popupview_item_img);
            viewHolder.popupview_item_text = view.findViewById(R.id.popupview_item_text);
            viewHolder.popupview_item_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontsize);
            viewHolder.popupview_item_text.setGravity(itemTextGravity);
            if (listItemHeight != 0) {
                AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) view.getLayoutParams();
                layoutParams.height = listItemHeight;
            }
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (nowPosition == i) {
            viewHolder.popupview_item_text.setTextColor(itemTextSelectColor);
        } else {
            viewHolder.popupview_item_text.setTextColor(itemTextColor);
        }

        if (list.get(i).get(Key.TITLE) != null) {
            viewHolder.popupview_item_text.setText((CharSequence) list.get(i).get(Key.TITLE));
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

    public void setItemTextGravity(int itemTextGravity) {
        switch (itemTextGravity) {
            case 2:
                this.itemTextGravity = Gravity.START | Gravity.CENTER_VERTICAL;
                break;
            case 3:
                this.itemTextGravity = Gravity.END | Gravity.CENTER_VERTICAL;
                break;
            default:
                this.itemTextGravity = Gravity.CENTER;
        }
    }

    public void setItemTextSelectColor(int itemTextSelectColor) {
        this.itemTextSelectColor = itemTextSelectColor;
    }

    public void setItemTextColor(int itemTextColor) {
        this.itemTextColor = itemTextColor;
    }

    public void setPosition(int nowPosition) {
        this.nowPosition = nowPosition;
    }

    static class ViewHolder {
        ImageView popupview_item_img;
        TextView popupview_item_text;
    }
}
