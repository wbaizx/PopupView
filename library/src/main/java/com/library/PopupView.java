package com.library;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.MenuRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by android1 on 2017/12/16.
 */

public class PopupView extends RelativeLayout implements View.OnClickListener, PopupWindow.OnDismissListener {
    private TextView popupview_text;
    private ImageView popupview_img;
    private PopupWindow popupWindow;
    private List<Map<String, Object>> list;
    private List<Map<String, Object>> temporaryList;
    private PopupAdapter popupAdapter;


    private OnPopupItemClickListener onPopupItemClickListener;
    private OnDismissListener onDismissListener;
    private Drawable popupDrawable;
    private int heightLineWidth;
    private int maxNum;
    private int nowPosition = 0;
    private int direction;
    private boolean needDivider;
    private boolean hideSelected;

    public PopupView(Context context) {
        this(context, null);
    }

    public PopupView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PopupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mWidthMeasureSpec;
        int mHeightMeasureSpec;
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            mWidthMeasureSpec = widthMeasureSpec;
        } else {
            mWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                    AndroidUtil.dp2px(getContext(), 210),
                    MeasureSpec.AT_MOST);
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            mHeightMeasureSpec = heightMeasureSpec;
        } else {
            mHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    AndroidUtil.dp2px(getContext(), 35),
                    MeasureSpec.AT_MOST);
        }
        super.onMeasure(mWidthMeasureSpec, mHeightMeasureSpec);
    }

    private void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.base_popupview, this);
        popupview_text = findViewById(R.id.popupview_text);
        popupview_img = findViewById(R.id.popupview_img);
        list = new ArrayList<>();
        temporaryList = new ArrayList<>();
        popupview_text.setOnClickListener(this);
        popupview_img.setOnClickListener(this);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.popup_attr);

        int menuResId = typedArray.getResourceId(R.styleable.popup_attr_menuRes, 0);
        if (menuResId != 0) {
            setItemsFromMenu((Activity) getContext(), menuResId);
        }
        int arrayResId = typedArray.getResourceId(R.styleable.popup_attr_arrayRes, 0);
        if (arrayResId != 0) {
            String[] stringArray = getResources().getStringArray(arrayResId);
            setItemsFromList(Arrays.asList(stringArray));
        }
        heightLineWidth = (int) typedArray.getDimension(R.styleable.popup_attr_heightLineWidth, 1);
        maxNum = typedArray.getInt(R.styleable.popup_attr_maxNum, 0);

        if (typedArray.getDrawable(R.styleable.popup_attr_popupDrawable) != null) {
            popupDrawable = typedArray.getDrawable(R.styleable.popup_attr_popupDrawable);
        } else {
            popupDrawable = ContextCompat.getDrawable(getContext(), R.drawable.popupdrawable);
        }
        if (typedArray.getDrawable(R.styleable.popup_attr_rightDrawable) != null) {
            popupview_img.setImageDrawable(typedArray.getDrawable(R.styleable.popup_attr_rightDrawable));
        } else {
            popupview_img.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.popup_down));
        }
        needDivider = typedArray.getBoolean(R.styleable.popup_attr_needDivider, true);
        hideSelected = typedArray.getBoolean(R.styleable.popup_attr_hideSelected, false);
        direction = typedArray.getInt(R.styleable.popup_attr_direction, 0);
        typedArray.recycle();
    }

    @Override
    public void onClick(View view) {
        OpenOrDismissPopupWindow();
    }

    private void OpenOrDismissPopupWindow() {
        if (popupWindow == null) {
            initPopupWindow();
        }
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            setTemporaryList();
            ObjectAnimator.ofFloat(popupview_img, "rotation", 0, 180).setDuration(100).start();
            showPopup();
        }
    }

    private void showPopup() {
        int[] location = new int[2];
        popupview_text.getLocationOnScreen(location);
        switch (direction) {
            case 0:
                popupWindow.setAnimationStyle(R.style.dialog_style_down);
                popupWindow.showAsDropDown(popupview_text);
                break;
            case 1:
                popupWindow.setAnimationStyle(R.style.dialog_style_up);
                popupWindow.showAtLocation(popupview_text, Gravity.NO_GRAVITY, location[0], location[1] - popupWindow.getHeight());
                break;
            case 2:
                popupWindow.setAnimationStyle(R.style.dialog_style_left);
                popupWindow.showAtLocation(popupview_text, Gravity.NO_GRAVITY, location[0] - popupWindow.getWidth(), location[1]);
                break;
            case 3:
                popupWindow.setAnimationStyle(R.style.dialog_style_right);
                popupWindow.showAtLocation(popupview_text, Gravity.NO_GRAVITY, location[0] + popupview_text.getWidth(), location[1]);
                break;
            default:
                popupWindow.setAnimationStyle(R.style.dialog_style_down);
                popupWindow.showAsDropDown(popupview_text);
                break;
        }
    }

    private void setTemporaryList() {
        temporaryList.clear();
        if (hideSelected) {
            for (int i = 0; i < list.size(); i++) {
                if (i != nowPosition) {
                    temporaryList.add(list.get(i));
                }
            }
        } else {
            temporaryList.addAll(list);
        }
        popupAdapter.notifyDataSetChanged();
    }

    private void initPopupWindow() {
        popupAdapter = new PopupAdapter(getContext(), temporaryList);
        ListView listView = new ListView(getContext());
        listView.setDividerHeight(AndroidUtil.dp2px(getContext(), heightLineWidth));
        if (!needDivider) {
            listView.setDivider(null);
        }
        listView.setAdapter(popupAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listViewOnClick(i);
            }
        });
        popupWindow = new PopupWindow(listView, popupview_text.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(this);
        popupWindow.setElevation(15);
        popupWindow.setBackgroundDrawable(popupDrawable);

        if ((maxNum > 0) && (maxNum < list.size())) {
            //35控件高度 + 2paddingTop
            popupWindow.setHeight(maxNum * (AndroidUtil.dp2px(getContext(), (float) 37) + listView.getDividerHeight()) - listView.getDividerHeight());
        } else {
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void listViewOnClick(int i) {
        popupWindow.dismiss();
        if (onPopupItemClickListener != null) {
            if ((i >= nowPosition) && hideSelected) {
                i++;
            }
            onPopupItemClickListener.onItemClickListener((int) list.get(i).get(Key.ID), i, (String) list.get(i).get(Key.TITLE));
        }
        setPostion(i);
    }

    @Override
    public void onDismiss() {
        ObjectAnimator.ofFloat(popupview_img, "rotation", 180, 360).setDuration(100).start();
        if (onDismissListener != null) {
            onDismissListener.onDismissListener();
        }
    }

    public void setItemsFromMenu(Activity activity, @MenuRes int menuRes) {
        PopupMenu popupMenu = new PopupMenu(activity, null);
        Menu menu = popupMenu.getMenu();
        activity.getMenuInflater().inflate(menuRes, menu);
        for (int i = 0; i < menu.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            MenuItem item = menu.getItem(i);
            map.put(Key.TITLE, item.getTitle());
            map.put(Key.IMG, item.getIcon());
            map.put(Key.ID, item.getItemId());
            list.add(map);
        }
        popupview_text.setText((String) list.get(0).get(Key.TITLE));
    }

    public void setItemsFromList(List<String> stringList) {
        for (int i = 0; i < stringList.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put(Key.TITLE, stringList.get(i));
            map.put(Key.IMG, null);
            map.put(Key.ID, -1);
            list.add(map);
        }
        popupview_text.setText((String) list.get(0).get(Key.TITLE));
    }

    public void addItems(PoputItem poputItem) {
        Map<String, Object> map = new HashMap<>();
        map.put(Key.TITLE, poputItem.getTitle());
        map.put(Key.IMG, poputItem.getImage());
        map.put(Key.ID, poputItem.getId());
        list.add(map);
        popupview_text.setText((String) list.get(0).get(Key.TITLE));
    }

    public void setOnItemClickListener(OnPopupItemClickListener onPopupItemClickListener) {
        this.onPopupItemClickListener = onPopupItemClickListener;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public void setPostion(int position) {
        nowPosition = position;
        if (position < list.size()) {
            popupview_text.setText((String) list.get(position).get(Key.TITLE));
        }
    }

    public void setPopupDrawable(Drawable popupDrawable) {
        this.popupDrawable = popupDrawable;
    }

    public void setHideSelected(boolean hideSelected) {
        this.hideSelected = hideSelected;
    }

    public void setRightDrawable(Drawable rightDrawable) {
        popupview_img.setImageDrawable(rightDrawable);
    }

    public void setNeedDivider(boolean needDivider) {
        this.needDivider = needDivider;
    }

    public void setHeightLineWidth(int heightLineWidthDP) {
        this.heightLineWidth = AndroidUtil.dp2px(getContext(), heightLineWidthDP);
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
