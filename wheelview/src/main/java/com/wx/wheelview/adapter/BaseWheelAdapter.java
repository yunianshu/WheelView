/*
 * Copyright (C) 2016 venshine.cn@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wx.wheelview.adapter;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.wx.wheelview.util.WheelUtils;
import com.wx.wheelview.widget.IWheelView;

import java.util.List;

/**
 * 滚轮抽象数据适配器
 *
 * @author venshine
 */
public abstract class BaseWheelAdapter<T> extends BaseAdapter {

    protected List<T> mList = null;

    private boolean mLoop = IWheelView.LOOP;

    private int mWheelSize = IWheelView.WHEEL_SIZE;

    private boolean mClickable = IWheelView.CLICKABLE;

    private int gravity = Gravity.CENTER;

    private int mCurrentPositon = -1;

    private OnClickListener mOnClickListener;

    protected abstract View bindView(int position, View convertView, ViewGroup parent);

    /**
     * 设置当前刻度
     *
     * @param position
     */
    public final void setCurrentPosition(int position) {
        mCurrentPositon = position;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @Override
    public final int getCount() {
        if (mLoop) {
            return Integer.MAX_VALUE;
        }
        return !WheelUtils.isEmpty(mList) ? (mList.size() + mWheelSize - 1) : 0;
    }

    @Override
    public final long getItemId(int position) {
        return !WheelUtils.isEmpty(mList) ? position % mList.size() : position;
    }

    @Override
    public final T getItem(int position) {
        return !WheelUtils.isEmpty(mList) ? mList.get(position % mList.size()) : null;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return !mClickable;
    }

    @Override
    public boolean isEnabled(int position) {
        if (mClickable) {
            if (mLoop) {
                if (position % mList.size() == mCurrentPositon) {
                    return true;
                }
            } else {
                if (position == (mCurrentPositon + mWheelSize / 2)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        if (mLoop) {
            position = position % mList.size();
        } else {
            if (position < mWheelSize / 2) {
                position = -1;
            } else if (position >= mWheelSize / 2 + mList.size()) {
                position = -1;
            } else {
                position = position - mWheelSize / 2;
            }
        }
        View view;
        if (position == -1) {
            view = bindView(0, convertView, parent);
        } else {
            view = bindView(position, convertView, parent);
        }
        if (!mLoop) {
            if (position == -1) {
                view.setVisibility(View.INVISIBLE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
        }
        if (mOnClickListener != null) {
            final int finalPosition = position;
            view.setOnClickListener(v -> mOnClickListener.onPositionClick(finalPosition));
        }
        return view;
    }

    public final BaseWheelAdapter setClickable(boolean clickable) {
        if (clickable != mClickable) {
            mClickable = clickable;
            super.notifyDataSetChanged();
        }
        return this;
    }

    public final BaseWheelAdapter setLoop(boolean loop) {
        if (loop != mLoop) {
            mLoop = loop;
            super.notifyDataSetChanged();
        }
        return this;
    }

    public final BaseWheelAdapter setWheelSize(int wheelSize) {
        mWheelSize = wheelSize;
        super.notifyDataSetChanged();
        return this;
    }

    public final BaseWheelAdapter setData(List<T> list) {
        mList = list;
        super.notifyDataSetChanged();
        return this;
    }

    public final BaseWheelAdapter setGravity(int gravity) {
        this.gravity = gravity;
        super.notifyDataSetChanged();
        return this;
    }

    public int getGravity() {
            return gravity;
    }

    /**
     * 数据已改变，重绘可见区域
     */
    @Override
    @Deprecated
    public final void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    /**
     * 数据失效，重绘控件
     */
    @Override
    @Deprecated
    public final void notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated();
    }

    public interface OnClickListener {
        void onPositionClick(int position);
    }
}
