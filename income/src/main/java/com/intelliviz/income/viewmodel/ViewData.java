package com.intelliviz.income.viewmodel;

/**
 * Created by edm on 2/10/2018.
 *
 */

public class ViewData<T> {
    private T mData;
    private int mState;
    private String mMemo;

    public ViewData(T obj) {
        this(obj, 0);
    }

    public ViewData(T obj, int state) {
        this(obj, state, "");
    }

    public ViewData(T obj, int state, String memo) {
        this.mData = obj;
        this.mState = state;
        this.mMemo = memo;
    }

    public T getData() {
        return mData;
    }

    public void setData(T data) {
        this.mData = data;
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        this.mState = state;
    }

    public String getMemo() {
        return mMemo;
    }

    public void setMemo(String memo) {
        this.mMemo = memo;
    }
}
