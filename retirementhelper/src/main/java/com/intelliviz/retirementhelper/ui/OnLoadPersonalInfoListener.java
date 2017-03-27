package com.intelliviz.retirementhelper.ui;

import android.database.Cursor;

/**
 * Created by edm on 3/27/2017.
 */

public interface OnLoadPersonalInfoListener {
    void onLoadEmail(Cursor cursor, String email);
}
