package com.intelliviz.retirementhelper.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by edm on 3/27/2017.
 */

public class RetirementProvider extends ContentProvider {
    private SqliteHelper mSqliteHelper;
    private static final String DBASE_NAME = "movies";
    private static final int DBASE_VERSION = 14;
    private static UriMatcher sUriMatcher;

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    private static class SqliteHelper extends SQLiteOpenHelper {

        public SqliteHelper(Context context) {
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
