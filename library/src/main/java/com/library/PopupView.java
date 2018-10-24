package com.library;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.MenuRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
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
    /**
     * popup text
     */
    private TextView popupViewText;
    /**
     * popup img
     */
    private ImageView popupViewImg;
    /**
     * popup listView
     */
    private ListView listView;
    /**
     * 真正的弹出框PopupWindow
     */
    private PopupWindow popupWindow;
    /**
     * 所有item选项
     */
    private List<Map<String, Object>> list;
    /**
     * 需要显示的item选项，主要用于隐藏当前选中项
     */
    private List<Map<String, Object>> temporaryList;
    /**
     * 弹出框列表适配器
     */
    private PopupAdapter popupAdapter;
    /**
     * 弹出框背景
     */
    private Drawable popupDrawable;
    /**
     * 分割线高度
     */
    private int dividerHeight;
    /**
     * PopupView字体大小
     */
    private int textViewSize;
    /**
     * 水平方向弹出时弹出框宽度
     */
    private int horizontalWidth;
    /**
     * 控件宽度
     */
    private int thisHeight;
    /**
     * 每个item布局高度
     */
    private int listItemHeight;
    /**
     * item字体大小
     */
    private int itemFontSize;
    /**
     * 最大显示行数
     */
    private int maxNum;
    /**
     * 当前选中位置
     */
    private int nowPosition = 0;
    /**
     * 弹出方向
     */
    private int direction;
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
     * 是否需要分割线
     */
    private boolean needDivider;
    /**
     * 是否隐藏当前选中项
     */
    private boolean hideSelected;

    /**
     * 弹出框高度
     */
    private int popupHeight;
    /**
     * 弹出框宽度
     */
    private int popupWidth;
    /**
     * 弹出框输入框相对屏幕坐标
     */
    private int[] location;


    private OnPopupItemClickListener onPopupItemClickListener;
    private OnDismissListener onDismissListener;

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

    private void init(AttributeSet attrs) {
        //加载布局
        LayoutInflater.from(getContext()).inflate(R.layout.base_popupview, this, true);
        popupViewText = findViewById(R.id.popupview_text);
        popupViewImg = findViewById(R.id.popupview_img);

        list = new ArrayList<>();
        temporaryList = new ArrayList<>();
        //初始化下拉框列表适配器
        popupAdapter = new PopupAdapter(temporaryList);

        setOnClickListener(this);

        //获取xml配置
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PopupView);
        //menu id
        int menuResId = typedArray.getResourceId(R.styleable.PopupView_menuRes, 0);
        if (menuResId != 0) {
            setItemsFromMenu((Activity) getContext(), menuResId);
        }
        //array id
        int arrayResId = typedArray.getResourceId(R.styleable.PopupView_arrayRes, 0);
        if (arrayResId != 0) {
            String[] stringArray = getResources().getStringArray(arrayResId);
            setItemsFromList(Arrays.asList(stringArray));
        }
        //分割线高度，默认2px
        dividerHeight = (int) typedArray.getDimension(R.styleable.PopupView_dividerHeight, 2);
        //item行高，默认0dp代表wrap_content,单位px
        listItemHeight = (int) typedArray.getDimension(R.styleable.PopupView_listItemHeight, 0);
        //item字体大小，默认15sp,单位px
        itemFontSize = (int) typedArray.getDimension(R.styleable.PopupView_itemFontSize,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
        //水平方向弹出时弹出框宽度，默认200dp
        horizontalWidth = (int) typedArray.getDimension(R.styleable.PopupView_horizontalWidth,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics()));
        //PopupView字体大小，默认20sp,单位px
        textViewSize = (int) typedArray.getDimension(R.styleable.PopupView_textViewSize,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics()));
        popupViewText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textViewSize);
        //根据PopupView字体大小设置控件高度
        thisHeight = (int) (textViewSize * 1.5);
        //最大显示项数
        maxNum = typedArray.getInt(R.styleable.PopupView_maxNum, 0);
        //下拉框背景
        if (getBackground() == null) {
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.popupbackground));
        }
        //弹出框背景
        if (typedArray.getDrawable(R.styleable.PopupView_popupDrawable) != null) {
            popupDrawable = typedArray.getDrawable(R.styleable.PopupView_popupDrawable);
        } else {
            popupDrawable = ContextCompat.getDrawable(getContext(), R.drawable.popupdowndrawable);
        }
        //下拉图标
        if (typedArray.getDrawable(R.styleable.PopupView_rightDrawable) != null) {
            popupViewImg.setImageDrawable(typedArray.getDrawable(R.styleable.PopupView_rightDrawable));
        } else {
            popupViewImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.popup_down));
        }
        //是否需要分割线
        needDivider = typedArray.getBoolean(R.styleable.PopupView_needDivider, true);
        //是否隐藏当前选中项
        hideSelected = typedArray.getBoolean(R.styleable.PopupView_hideSelected, false);
        //弹出方向
        direction = typedArray.getInt(R.styleable.PopupView_direction, 0);
        //item字体对齐方式
        itemTextGravity = typedArray.getInt(R.styleable.PopupView_itemTextGravity, 0);
        //item字体选中颜色
        itemTextSelectColor = typedArray.getColor(R.styleable.PopupView_itemTextSelectColor, 0x8a000000);
        //item字体未选中颜色
        itemTextColor = typedArray.getColor(R.styleable.PopupView_itemTextColor, 0x8a000000);
        //设置popup字体颜色
        popupViewText.setTextColor(typedArray.getColor(R.styleable.PopupView_popupTextColor, 0x8a000000));

        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mWidthMeasureSpec;
        int mHeightMeasureSpec;
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            mWidthMeasureSpec = widthMeasureSpec;
        } else {
            mWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, getResources().getDisplayMetrics()),
                    MeasureSpec.AT_MOST);
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            mHeightMeasureSpec = heightMeasureSpec;
        } else {
            mHeightMeasureSpec = MeasureSpec.makeMeasureSpec(thisHeight, MeasureSpec.AT_MOST);
        }
        super.onMeasure(mWidthMeasureSpec, mHeightMeasureSpec);
    }

    @Override
    public void onClick(View view) {
        openOrDismissPopupWindow();
    }

    /**
     * 整个控件点击事件
     */
    private void openOrDismissPopupWindow() {
        if (popupWindow == null) {
            //初始化PopupWindow
            initPopupWindow();
        }
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            //每次展开前设置临时数据
            setTemporaryList();
            //下拉图片翻转动画
            ObjectAnimator.ofFloat(popupViewImg, "rotation", 0, 180).setDuration(100).start();
            //展开popup
            showPopup();
        }
    }

    /**
     * 初始化PopupWindow
     */
    private void initPopupWindow() {
        listView = new ListView(getContext());
        //是否使用分割线
        if (needDivider) {
            listView.setDividerHeight(dividerHeight);
        }
        //初始化下拉框列表适配器参数
        popupAdapter.setFontsize(itemFontSize);
        popupAdapter.setListItemHeight(listItemHeight);
        popupAdapter.setItemTextColor(itemTextColor);
        popupAdapter.setItemTextGravity(itemTextGravity);
        popupAdapter.setItemTextSelectColor(itemTextSelectColor);
        if (!hideSelected) {
            popupAdapter.setPosition(nowPosition);
        }
        listView.setAdapter(popupAdapter);
        //下拉列表点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listViewOnClick(i);
            }
        });
        //实例popupWindow
        popupWindow = new PopupWindow(listView, getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //点击外部关闭
        popupWindow.setOutsideTouchable(true);
        //关闭监听
        popupWindow.setOnDismissListener(this);
        //设置下拉框阴影
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(15);
        }
        //设置下拉框背景
        popupWindow.setBackgroundDrawable(popupDrawable);

        location = new int[2];
        this.getLocationInWindow(location);

        //根据弹出方向设置宽度
        switch (direction) {
            case 0:
                popupWidth = getWidth();
                popupWindow.setAnimationStyle(R.style.dialog_style_down);
                break;
            case 1:
                popupWidth = getWidth();
                popupWindow.setAnimationStyle(R.style.dialog_style_up);
                break;
            case 2:
                popupWidth = location[0] > horizontalWidth ? horizontalWidth : location[0];
                popupWindow.setAnimationStyle(R.style.dialog_style_left);
                break;
            case 3:
                popupWidth = location[0] + getWidth() + horizontalWidth < getScreenWidth() ? horizontalWidth :
                        getScreenWidth() - (location[0] + getWidth());
                popupWindow.setAnimationStyle(R.style.dialog_style_right);
                break;
            default:
                popupWidth = getWidth();
        }
        popupWindow.setWidth(popupWidth);
    }

    /**
     * 每次展开前设置临时数据
     */
    private void setTemporaryList() {
        if (hideSelected) {
            //如果需要隐藏当前项，先添加所有，然后去掉当前选中项
            temporaryList.clear();
            temporaryList.addAll(list);
            temporaryList.remove(nowPosition);
        } else {
            if (temporaryList.isEmpty()) {
                temporaryList.addAll(list);
            }
        }
        popupAdapter.notifyDataSetChanged();
    }

    /**
     * 判断方向，展开popup
     */
    private void showPopup() {
        switch (direction) {
            case 0:
                setPopupHeightDown();
                popupWindow.showAsDropDown(this);
                break;
            case 1:
                setPopupHeightUp();
                popupWindow.showAtLocation(this, Gravity.NO_GRAVITY, location[0], location[1] - popupWindow.getHeight());
                break;
            case 2:
                setPopupHeightLeftAndRight();
                popupWindow.showAtLocation(this, Gravity.NO_GRAVITY, location[0] - popupWidth,
                        location[1] - (popupWindow.getHeight() - getHeight()) / 2);
                break;
            case 3:
                setPopupHeightLeftAndRight();
                popupWindow.showAtLocation(this, Gravity.NO_GRAVITY, location[0] + getWidth(),
                        location[1] - (popupWindow.getHeight() - getHeight()) / 2);
                break;
            default:
        }
    }

    private void setPopupHeightLeftAndRight() {
        if (listItemHeight != 0) {
            popupHeight = setHaveItemHeight();
        } else {
            popupHeight = getListHeight();
        }
        popupWindow.setHeight((int) Math.min(popupHeight,
                location[1] - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics())));
    }

    private void setPopupHeightUp() {
        if (listItemHeight != 0) {
            popupHeight = setHaveItemHeight();
        } else {
            popupHeight = getListHeight();
        }
        popupWindow.setHeight((int) Math.min(popupHeight,
                location[1] - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics())));
    }

    private void setPopupHeightDown() {
        if (listItemHeight != 0) {
            popupHeight = setHaveItemHeight();
            popupWindow.setHeight(Math.min(popupHeight, getScreenHeight() - (location[1] + getHeight())));
        } else {
            popupHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
    }

    /**
     * 当设置了listItemHeight时候计算高度
     */
    private int setHaveItemHeight() {
        if ((maxNum > 0) && (maxNum < list.size())) {
            return maxNum * (listItemHeight + listView.getDividerHeight()) - listView.getDividerHeight();
        } else {
            if (hideSelected) {
                return (list.size() - 1) * (listItemHeight + listView.getDividerHeight()) - listView.getDividerHeight();
            } else {
                return list.size() * (listItemHeight + listView.getDividerHeight()) - listView.getDividerHeight();
            }
        }
    }

    /**
     * 动态计算高度
     */
    private int getListHeight() {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return 0;
        }
        int totalHeight = 0;
        int widthSpec = View.MeasureSpec.makeMeasureSpec(getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(widthSpec, 0);

            int itemHeight = listItem.getMeasuredHeight();
            totalHeight += itemHeight;
        }
        // 减掉底部分割线的高度
        return totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
    }

    /**
     * 下拉列表点击事件
     */
    private void listViewOnClick(int i) {
        popupWindow.dismiss();
        //i为此次展示列表选中ID，如果隐藏了当前选中项并且i大于上次选中项
        //则实际选中的项在所有列表中的索引需要+1
        if ((i >= nowPosition) && hideSelected) {
            i++;
        }
        if (onPopupItemClickListener != null) {
            onPopupItemClickListener.onItemClickListener((int) list.get(i).get(Key.ID), i, (String) list.get(i).get(Key.TITLE));
        }
        setPostion(i);
    }

    @Override
    public void onDismiss() {
        ObjectAnimator.ofFloat(popupViewImg, "rotation", 180, 360).setDuration(100).start();
        if (onDismissListener != null) {
            onDismissListener.onDismissListener();
        }
    }

    /**
     * 设置当前选中项
     */
    public void setPostion(int position) {
        nowPosition = position;
        if (position < list.size()) {
            popupViewText.setText((String) list.get(position).get(Key.TITLE));
            //需要通知adapter
            if (!hideSelected) {
                popupAdapter.setPosition(nowPosition);
            }
        }
    }

    /**
     * 通过menu设置数据
     */
    public void setItemsFromMenu(Activity activity, @MenuRes int menuRes) {
        PopupMenu popupMenu = new PopupMenu(activity, null);
        Menu menu = popupMenu.getMenu();
        activity.getMenuInflater().inflate(menuRes, menu);
        for (int i = 0; i < menu.size(); i++) {
            Map<String, Object> map = new HashMap<>(3);
            MenuItem item = menu.getItem(i);
            map.put(Key.TITLE, item.getTitle());
            map.put(Key.IMG, item.getIcon());
            map.put(Key.ID, item.getItemId());
            list.add(map);
        }
        popupViewText.setText((String) list.get(0).get(Key.TITLE));
    }

    /**
     * 通过List<String>设置数据
     */
    public void setItemsFromList(List<String> stringList) {
        for (int i = 0; i < stringList.size(); i++) {
            Map<String, Object> map = new HashMap<>(3);
            map.put(Key.TITLE, stringList.get(i));
            map.put(Key.IMG, null);
            map.put(Key.ID, -1);
            list.add(map);
        }
        popupViewText.setText((String) list.get(0).get(Key.TITLE));
    }

    /**
     * 手动设置数据
     */
    public void addItems(PoputItem poputItem) {
        Map<String, Object> map = new HashMap<>(3);
        map.put(Key.TITLE, poputItem.getTitle());
        map.put(Key.IMG, poputItem.getImage());
        map.put(Key.ID, poputItem.getId());
        list.add(map);
        popupViewText.setText((String) list.get(0).get(Key.TITLE));
    }

    /**
     * 清空数据
     */
    public void clearData() {
        if (list != null) {
            list.clear();
        }
    }

    public void setOnItemClickListener(OnPopupItemClickListener onPopupItemClickListener) {
        this.onPopupItemClickListener = onPopupItemClickListener;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public void setItemTextSelectColor(int itemTextSelectColor) {
        this.itemTextSelectColor = itemTextSelectColor;
    }

    public void setItemTextColor(int itemTextColor) {
        this.itemTextColor = itemTextColor;
    }

    public void setPopupDrawable(Drawable popupDrawable) {
        this.popupDrawable = popupDrawable;
    }

    public int getNowPosition() {
        return nowPosition;
    }

    public String getNowText() {
        return String.valueOf(list.get(nowPosition).get(Key.TITLE));
    }

    public void setHideSelected(boolean hideSelected) {
        this.hideSelected = hideSelected;
    }

    public void setRightDrawable(Drawable rightDrawable) {
        popupViewImg.setImageDrawable(rightDrawable);
    }

    public void setNeedDivider(boolean needDivider) {
        this.needDivider = needDivider;
    }

    public void setDividerHeight(int dividerHeight) {
        this.dividerHeight = dividerHeight;
    }

    public void setHorizontalWidth(int horizontalWidth) {
        this.horizontalWidth = horizontalWidth;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }


    public void setListItemHeight(int listItemHeight) {
        this.listItemHeight = listItemHeight;
    }

    public void setItemFontSize(int itemFontSize) {
        this.itemFontSize = itemFontSize;
    }

    public void setPopupTextColor(int color) {
        popupViewText.setTextColor(color);
    }

    public void setTextViewSize(int dptextViewSize) {
        popupViewText.setTextSize(TypedValue.COMPLEX_UNIT_PX, dptextViewSize);
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    /**
     * 获取屏幕宽度
     */
    public int getScreenWidth() {
        return getContext().getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public int getScreenHeight() {
        return getContext().getResources().getDisplayMetrics().heightPixels;
    }
}
