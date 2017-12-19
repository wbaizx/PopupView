# PopupView

![image](https://github.com/wbaizx/PopupView/raw/master/in.png)


Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}Copy
Step 2. Add the dependency

	dependencies {
	        compile 'com.github.wbaizx:PopupView:1.0.3'
	}


用法：

    <com.library.PopupView xmlns:att="http://schemas.android.com/apk/res-auto"
        android:id="@+id/popup"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"
        att:arrayRes="@array/ar"
        att:rightDrawable="@drawable/ad"
        att:direction="down"
        att:heightLineWidth="0.5dp"
        att:hideSelected="false"
        att:maxNum="5"
        att:menuRes="@menu/a1"
        att:needDivider="false"
        att:popupDrawable="@drawable/pd"></com.library.PopupView>
    
    
  解释一个各个参数作用

        arrayRes：注入数组资源
        rightDrawable：箭头图片资源
        direction：弹出方向
        heightLineWidth：分割线高度
        hideSelected：是否隐藏当前选中项，默认false
        maxNum：最大显示行数，默认包含屏幕能容纳所有
        menuRes：menu资源注入
        needDivider：是否需要分割线，默认true
        popupDrawable：下拉菜单背景，注意设置此项后弹出框阴影消失
  
  
  xml中的各种参数设置同样都可以在代码中完成，介绍一下代码中的不同地方

  注入数据有三种方式：

  通过一项一项注入数据，其中PoputItem构造方法有三种，参见源码
  
        popup.addItems(new PoputItem("11"));
        popup.addItems(new PoputItem(ContextCompat.getDrawable(this, R.mipmap.ic_launcher_round)));
        popup.addItems(new PoputItem("22", ContextCompat.getDrawable(this, R.mipmap.ic_launcher_round)));
        
  通过menu菜单注入
  
          popup.setItemsFromMenu(this, R.menu.aaaa);
          
  通过List<String>列表注入：
  
        List<String> stringList = new ArrayList<>();
        stringList.add("55");
        popup.setItemsFromList(stringList);
        
  设置默认选中项，小于0或大于数据长度则默认选中第一项：
  
        popup.setPostion();
        
  获取当前选中位置

        popup.getNowPosition();

  获取当前选中位置文本

        popup.getNowText();

  设置点击监听，返回值如果对应项有id则返回对应id，没有返回-1，position表示在数据中的索引，title表示对应文本信息

        popup.setOnItemClickListener(new OnPopupItemClickListener() {
            @Override
            public void onItemClickListener(int id, int position, String title) {
            }
        });
        
  设置关闭监听
  
        popup.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismissListener() {
            }
        });
        

