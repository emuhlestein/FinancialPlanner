package com.intelliviz.retirementhelper.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.intelliviz.retirementhelper.data.IncomeType;
import com.intelliviz.retirementhelper.data.IncomeTypeData;

/**
 * Created by edm on 9/30/2017.
 */

public abstract class BaseDatabase {
    private ContentResolver mContentResolver;

    public BaseDatabase(ContentResolver contentResolver) {
        mContentResolver = contentResolver;
    }

    public ContentResolver getContentResolver() {
        return mContentResolver;
    }

    public abstract long insert(IncomeTypeData data);
    public abstract IncomeTypeData get(long incomeId);
    public abstract int update(IncomeTypeData data);
    public abstract int delete(long incomeId);

    protected String addIncomeType(IncomeType incomeType) {
        ContentValues values = new ContentValues();
        values.put(RetirementContract.IncomeTypeEntry.COLUMN_NAME, incomeType.getName());
        values.put(RetirementContract.IncomeTypeEntry.COLUMN_TYPE, incomeType.getType());
        Uri uri = getContentResolver().insert(RetirementContract.IncomeTypeEntry.CONTENT_URI, values);
        if(uri == null) {
            return null;
        } else {
            return uri.getLastPathSegment();
        }
    }

    protected int updateIncomeTypeName(IncomeType incomeType) {
        ContentValues values  = new ContentValues();
        values.put(RetirementContract.IncomeTypeEntry.COLUMN_NAME, incomeType.getName());

        String sid = String.valueOf(incomeType.getId());
        String selectionClause = RetirementContract.IncomeTypeEntry._ID + " = ?";
        String[] selectionArgs = new String[]{sid};
        Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return getContentResolver().update(uri, values, selectionClause, selectionArgs);
    }

    protected IncomeData getData(long incomeId) {
        Cursor cursor = getIncomeType(incomeId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int nameIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry.COLUMN_NAME);
        int typeIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry.COLUMN_TYPE);
        String name = cursor.getString(nameIndex);
        int type = cursor.getInt(typeIndex);
        cursor.close();
        return new IncomeData(name, type);
    }

    private Cursor getIncomeType(long incomeId) {
        Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
        String selection = RetirementContract.IncomeTypeEntry._ID + " = ?";
        String id = String.valueOf(incomeId);
        String[] selectionArgs = {id};
        return getContentResolver().query(uri, null, selection, selectionArgs, null);
    }

    protected static class IncomeData {
        public String name;
        public int type;
        public IncomeData(String name, int type) {
            this.name = name;
            this.type = type;
        }
    }
}
