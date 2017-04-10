package com.intelliviz.retirementhelper.util;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.intelliviz.retirementhelper.ui.UserInfoQueryListener;

import java.lang.ref.WeakReference;

/**
 * Created by edm on 4/10/2017.
 */

public class UserInfoQueryHandler extends AsyncQueryHandler {

    private WeakReference<UserInfoQueryListener> mListener;

    public UserInfoQueryHandler(ContentResolver cr, UserInfoQueryListener listener) {
        super(cr);
        mListener = new WeakReference<>(listener);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        final UserInfoQueryListener listener = mListener.get();
        if(listener != null) {
            listener.onQueryUserInfo(token, cookie, cursor);
        }
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        final UserInfoQueryListener listener = mListener.get();
        if(listener != null) {
            listener.onInsertUserInfo(token, cookie, uri);
        }
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        final UserInfoQueryListener listener = mListener.get();
        if(listener != null) {
            listener.onUpdateUserInfo(token, cookie, result);
        }
    }
}
