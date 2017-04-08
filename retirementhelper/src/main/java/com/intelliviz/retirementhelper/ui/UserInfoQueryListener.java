package com.intelliviz.retirementhelper.ui;

import android.database.Cursor;
import android.net.Uri;

/**
 * Created by edm on 3/27/2017.
 */

public interface UserInfoQueryListener {
    void onQueryUserInfo(int token, Object cookie, Cursor cursor);
    void onInsertUserInfo(int token, Object cookie, Uri uri);
    void onUpdateUserInfo(int token, Object cookie, int result);
}
