package com.intelliviz.retirementhelper.util;

import android.database.Cursor;

/**
 * Created by edm on 4/19/2017.
 */

public interface RetirementQueryListener {
    void onQueryComplete(int token, Object cookie, Cursor cursor);
}
