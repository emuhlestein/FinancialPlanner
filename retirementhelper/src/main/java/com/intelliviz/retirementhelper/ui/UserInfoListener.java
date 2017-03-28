package com.intelliviz.retirementhelper.ui;

import android.database.Cursor;
import android.net.Uri;

/**
 * Created by edm on 3/27/2017.
 */

public interface UserInfoListener {
    void onQueryUserInfo(Cursor cursor, Object cookie);
    void onInsertEmail(Uri uri, Object cookie);
}
