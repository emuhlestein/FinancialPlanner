package com.intelliviz.retirementhelper.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by edm on 3/27/2017.
 */

public class RetirementProvider extends ContentProvider {
    private SqliteHelper mSqliteHelper;
    private static final String DBASE_NAME = "movies";
    private static final int DBASE_VERSION = 1;
    private static final int PERSONALINFO_ID = 101;
    private static final int PERSONALINFO_LIST = 102;

    private static UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher((UriMatcher.NO_MATCH));
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mSqliteHelper = new SqliteHelper(context);
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch(sUriMatcher.match(uri)) {
            case PERSONALINFO_LIST:
                return RetirementContract.PeronsalInfoEntry.CONTENT_TYPE;
            case PERSONALINFO_ID:
                return RetirementContract.PeronsalInfoEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown uri");
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        switch(sUriMatcher.match(uri)) {
            case PERSONALINFO_ID:
                sqLiteQueryBuilder.setTables(RetirementContract.PeronsalInfoEntry.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(RetirementContract.PeronsalInfoEntry._ID +
                        "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown uri");
        }

        SQLiteDatabase db = mSqliteHelper.getWritableDatabase();
        Cursor cursor = sqLiteQueryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowId;
        SQLiteDatabase db;
        Uri returnUri;

        db = mSqliteHelper.getWritableDatabase();

        switch(sUriMatcher.match(uri)) {
            case PERSONALINFO_LIST:
                // The second parameter will allow an empty row to be inserted. If it was null, then no row
                // can be inserted if values is empty.
                rowId = db.insert(RetirementContract.PeronsalInfoEntry.TABLE_NAME, null, values);
                if (rowId > -1) {
                    returnUri = ContentUris.withAppendedId(uri, rowId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri.toString());
        }
        notifyChanges(returnUri);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        SQLiteDatabase db = mSqliteHelper.getWritableDatabase();
        int rowsDeleted = 0;
        String id;

        switch(sUriMatcher.match(uri)) {
            case PERSONALINFO_ID:
                id = uri.getLastPathSegment();
                rowsDeleted = db.delete(RetirementContract.PeronsalInfoEntry.TABLE_NAME,
                        RetirementContract.PeronsalInfoEntry._ID + "=" + id, null);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri");
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mSqliteHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated = 0;
        String id;

        switch(sUriMatcher.match(uri)) {
            case PERSONALINFO_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(RetirementContract.PeronsalInfoEntry.TABLE_NAME,
                            values,
                            RetirementContract.PeronsalInfoEntry._ID + "=?",
                            new String[]{id});
                } else {
                    rowsUpdated = db.update(RetirementContract.PeronsalInfoEntry.TABLE_NAME,
                            values,
                            RetirementContract.PeronsalInfoEntry._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri");
        }
        if (rowsUpdated != 0) {
            notifyChanges(uri);
        }
        return rowsUpdated;
    }

    private void notifyChanges(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        if(contentResolver != null) {
            contentResolver.notifyChange(uri, null);
        }
    }

    private static class SqliteHelper extends SQLiteOpenHelper {

        SqliteHelper(Context context) {
            super(context, DBASE_NAME, null, DBASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // create the movie table
            String sql = "CREATE TABLE " + RetirementContract.PeronsalInfoEntry.TABLE_NAME +
                    " ( " + RetirementContract.PeronsalInfoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RetirementContract.PeronsalInfoEntry.COLUMN_EMAIL + " TEXT NOT NULL, " +
                    RetirementContract.PeronsalInfoEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                    RetirementContract.PeronsalInfoEntry.COLUMN_PASSWORD + " TEXT NOT NULL, " +
                    RetirementContract.PeronsalInfoEntry.COLUMN_BIRTHDATE + " TEXT NOT NULL);";

            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.PeronsalInfoEntry.TABLE_NAME);
            onCreate(db);
        }
    }
}
