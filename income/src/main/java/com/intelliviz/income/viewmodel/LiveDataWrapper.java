package com.intelliviz.income.viewmodel;

/**
 * Created by edm on 2/10/2018.
 *
 */

public class LiveDataWrapper<T> {
    private T obj;
    private int state;
    private String message;

    public LiveDataWrapper(T obj) {
        this(obj, 0);
    }

    public LiveDataWrapper(T obj, int state) {
        this(obj, state, "");
    }

    public LiveDataWrapper(T obj, int state, String message) {
        this.obj = obj;
        this.state = state;
        this.message = message;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
