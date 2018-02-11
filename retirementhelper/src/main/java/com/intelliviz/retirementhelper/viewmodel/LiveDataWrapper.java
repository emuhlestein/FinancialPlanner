package com.intelliviz.retirementhelper.viewmodel;

/**
 * Created by edm on 2/10/2018.
 */

public class LiveDataWrapper {
    private Object obj;
    private int state;
    private String message;

    public LiveDataWrapper(Object obj, int state, String message) {
        this.obj = obj;
        this.state = state;
        this.message = message;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
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
