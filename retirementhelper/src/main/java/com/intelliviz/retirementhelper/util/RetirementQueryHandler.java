package com.intelliviz.retirementhelper.util;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by edm on 4/19/2017.
 */

public class RetirementQueryHandler extends AsyncQueryHandler {
    private RetirementQueryListener mRetirementQueryListener = null;

    public RetirementQueryHandler(Context context) {
        super(context.getContentResolver());
    }

    public void setRetirementQueryListener(RetirementQueryListener retirementQueryListener) {
        mRetirementQueryListener = retirementQueryListener;
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        if(mRetirementQueryListener != null) {
            mRetirementQueryListener.onQueryComplete(token, cookie, cursor);
        }
    }
}
