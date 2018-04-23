package com.tdr.yunwei.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tdr.yunwei.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 定制的 radioGroup组件 描述 横向排列N个条目组件 关联Resource: layout: custom_radio_button.xml
 */
@SuppressLint("NewApi")
public class CustomRadioGroup extends LinearLayout {

    // 相关的资源ID
    private final int ID_LAYOUT = R.layout.custom_radio_button,//自定义radiogroup的布局id
            ID_IMAGE_TOP = R.id.custom_radio_button_image_top,//没选中状态显示的图片
            ID_IMAGE_BOTTOM = R.id.custom_radio_button_image_botom;//选中状态显示的图片
    // 条目变更监听
    private OnItemChangedListener onItemChangedListener;
    // 条目的LinearLayout.LayoutParams
    private LayoutParams itemLayoutParams = new LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
    private LayoutInflater inflater;
    // 当前选择的条目
    private int checkedIndex = 0;
    // 条目列表
    private List<RadioButton> lists = new ArrayList<RadioButton>();

    public CustomRadioGroup(Context c) {
        super(c);
        init();
    }

    public CustomRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 构造函数里适用的初始化部分
     */
    private void init() {
        inflater = LayoutInflater.from(getContext());
        itemLayoutParams.weight = 1;
        setOrientation(HORIZONTAL);
    }

    /**
     * 添加条目
     *
     * @param unSelected 没有选中时的图片
     * @param selected   选中时的图片
     */
    public void addItem(int unSelected, int selected) {
        RadioButton rb = new RadioButton(unSelected, selected);
        final int i = lists.size();
        rb.v.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                setCheckedIndex(i);
            }
        });
        addView(rb.v);
        lists.add(rb);
    }

    /**
     * 获取选中的条目索引
     */
    public int getCheckedIndex() {
        return checkedIndex;
    }

    /**
     * 两个item 改变透明度
     *
     * @param leftIndex  左边的条目索引
     * @param rightIndex 右边的条目索索引
     * @param alpha      [0,1)透明度
     */
    public void itemChangeChecked(int leftIndex, int rightIndex, float alpha) {
        if (leftIndex < 0 || leftIndex >= lists.size() || rightIndex < 0
                || rightIndex >= lists.size()) {
            return;
        }
        RadioButton a = lists.get(leftIndex);
        RadioButton b = lists.get(rightIndex);
        a.top.setAlpha(alpha);
        a.bottom.setAlpha(1 - alpha);
        b.top.setAlpha(1 - alpha);
        b.bottom.setAlpha(alpha);
    }

    /**
     * 选择制定索引的条目
     */
    public void setCheckedIndex(int index) {
        for (int i = 0; i < lists.size(); i++) {
            if (i == index) {
                lists.get(i).setCheceked(true);
            } else {
                lists.get(i).setCheceked(false);
            }
        }
        this.checkedIndex = index;
        if (this.onItemChangedListener != null) {
            onItemChangedListener.onItemChanged();
        }
    }

    /**
     * 设置条目变更监听
     *
     * @param onItemChangedListener
     */
    public void setOnItemChangedListener(
            OnItemChangedListener onItemChangedListener) {
        this.onItemChangedListener = onItemChangedListener;
    }

    /**
     * 自定义的RadioButton
     */
    private class RadioButton {
        View v; // 条目样式
        ImageView top, bottom; // 条目的图片

        public RadioButton(int unSelected, int selected) {
            v = inflater.inflate(ID_LAYOUT, null);
            top = (ImageView) v.findViewById(ID_IMAGE_TOP);
            bottom = (ImageView) v.findViewById(ID_IMAGE_BOTTOM);
            top.setImageResource(unSelected);
            top.setAlpha(1.0f);
            bottom.setImageResource(selected);
            bottom.setAlpha(0.0f);
            v.setLayoutParams(itemLayoutParams);
        }

        void setCheceked(boolean b) {
            if (b) {
                top.setAlpha(0.0f);
                bottom.setAlpha(1.0f);
            } else {
                top.setAlpha(1.0f);
                bottom.setAlpha(0.0f);
            }
        }

    }

    /**
     * 条目变更监听接口
     */
    public interface OnItemChangedListener {
        public void onItemChanged();
    }

}



