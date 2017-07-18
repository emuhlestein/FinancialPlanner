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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Content provider.
 * Created by Ed Muhlestein on 3/27/2017.
 */
@SuppressWarnings("ConstantConditions")
public class RetirementProvider extends ContentProvider {
    private SqliteHelper mSqliteHelper;
    private static final String DBASE_NAME = "retirement";
    private static final int DBASE_VERSION = 1;
    private static final int RETIREMENT_OPTIONS_ID = 102;
    private static final int INCOME_TYPE_LIST = 401;
    private static final int INCOME_TYPE_ID = 402;
    private static final int SAVINGS_INCOME_LIST = 501;
    private static final int SAVINGS_INCOME_ID = 502;
    private static final int TAX_DEFERRED_INCOME_LIST = 601;
    private static final int TAX_DEFERRED_INCOME_ID = 602;
    private static final int PENSION_INCOME_LIST = 701;
    private static final int PENSION_INCOME_ID = 702;
    private static final int GOV_PENSION_INCOME_LIST = 801;
    private static final int GOV_PENSION_INCOME_ID = 802;
    private static final int BALANCE_LIST = 901;
    private static final int BALANCE_ID = 902;
    private static final int MILESTONE_LIST = 1001;
    private static final int MILESTONE_ID = 1002;
    private static final int SUMMARY_LIST = 1101;

    private static final String QUERY_TABLES_FOR_TAX_DEFERRED_ITEM =
            RetirementContract.IncomeTypeEntry.TABLE_NAME +
                    " INNER JOIN " +
                    RetirementContract.TaxDeferredIncomeEntry.TABLE_NAME + " ON " +
                    RetirementContract.IncomeTypeEntry.TABLE_NAME + "." +
                    RetirementContract.IncomeTypeEntry._ID + " = " +
                    RetirementContract.TaxDeferredIncomeEntry.TABLE_NAME + "." +
                    RetirementContract.TaxDeferredIncomeEntry.COLUMN_INCOME_TYPE_ID + " " +
                    " INNER JOIN " +
                    RetirementContract.BalanceEntry.TABLE_NAME + " ON " +
                    RetirementContract.IncomeTypeEntry.TABLE_NAME + "." +
                    RetirementContract.IncomeTypeEntry._ID + " = " +
                    RetirementContract.BalanceEntry.TABLE_NAME + "." +
                    RetirementContract.BalanceEntry.COLUMN_INCOME_TYPE_ID;


    private static UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_RETIREMENT_PARMS, RETIREMENT_OPTIONS_ID);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_INCOME_TYPE, INCOME_TYPE_LIST);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_INCOME_TYPE + "/#", INCOME_TYPE_ID);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_SAVINGS_INCOME, SAVINGS_INCOME_LIST);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_SAVINGS_INCOME + "/#", SAVINGS_INCOME_ID);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_TAX_DEFERRED_INCOME, TAX_DEFERRED_INCOME_LIST);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_TAX_DEFERRED_INCOME + "/#", TAX_DEFERRED_INCOME_ID);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_PENSION_INCOME, PENSION_INCOME_LIST);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_PENSION_INCOME + "/#", PENSION_INCOME_ID);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_GOV_PENSION_INCOME, GOV_PENSION_INCOME_LIST);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_GOV_PENSION_INCOME + "/#", GOV_PENSION_INCOME_ID);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_BALANCE, BALANCE_LIST);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_BALANCE + "/#", BALANCE_ID);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_MILESTONE, MILESTONE_LIST);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_MILESTONE + "/#", MILESTONE_ID);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_SUMMARY, SUMMARY_LIST);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mSqliteHelper = new SqliteHelper(context);
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch(sUriMatcher.match(uri)) {
            case RETIREMENT_OPTIONS_ID:
                return RetirementContract.RetirementParmsEntry.CONTENT_ITEM_TYPE;
            case INCOME_TYPE_LIST:
                return RetirementContract.IncomeTypeEntry.CONTENT_TYPE;
            case INCOME_TYPE_ID:
                return RetirementContract.IncomeTypeEntry.CONTENT_ITEM_TYPE;
            case MILESTONE_LIST:
                return RetirementContract.MilestoneEntry.CONTENT_TYPE;
            case MILESTONE_ID:
                return RetirementContract.MilestoneEntry.CONTENT_ITEM_TYPE;
            case SAVINGS_INCOME_LIST:
                return RetirementContract.SavingsIncomeEntry.CONTENT_TYPE;
            case SAVINGS_INCOME_ID:
                return RetirementContract.SavingsIncomeEntry.CONTENT_ITEM_TYPE;
            case PENSION_INCOME_LIST:
                return RetirementContract.PensionIncomeEntry.CONTENT_TYPE;
            case PENSION_INCOME_ID:
                return RetirementContract.PensionIncomeEntry.CONTENT_ITEM_TYPE;
            case GOV_PENSION_INCOME_LIST:
                return RetirementContract.GovPensionIncomeEntry.CONTENT_TYPE;
            case GOV_PENSION_INCOME_ID:
                return RetirementContract.GovPensionIncomeEntry.CONTENT_ITEM_TYPE;
            case BALANCE_LIST:
                return RetirementContract.BalanceEntry.CONTENT_TYPE;
            case BALANCE_ID:
                return RetirementContract.BalanceEntry.CONTENT_ITEM_TYPE;
            case SUMMARY_LIST:
                return RetirementContract.SummaryEntry.CONTENT_TYPE;
            case TAX_DEFERRED_INCOME_LIST:
                return RetirementContract.TaxDeferredIncomeEntry.CONTENT_TYPE;
            case TAX_DEFERRED_INCOME_ID:
                return RetirementContract.TaxDeferredIncomeEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown uri");
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        switch(sUriMatcher.match(uri)) {
            case RETIREMENT_OPTIONS_ID:
                sqLiteQueryBuilder.setTables(RetirementContract.RetirementParmsEntry.TABLE_NAME);
                break;
            case INCOME_TYPE_ID:
                sqLiteQueryBuilder.setTables(RetirementContract.IncomeTypeEntry.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(RetirementContract.IncomeTypeEntry._ID +
                        "=" + uri.getLastPathSegment());
                break;
            case INCOME_TYPE_LIST:
                sqLiteQueryBuilder.setTables(RetirementContract.IncomeTypeEntry.TABLE_NAME);
                break;
            case SAVINGS_INCOME_ID:
                sqLiteQueryBuilder.setTables(RetirementContract.SavingsIncomeEntry.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(RetirementContract.SavingsIncomeEntry.COLUMN_INCOME_TYPE_ID +
                        "=" + uri.getLastPathSegment());
                break;
            case SAVINGS_INCOME_LIST:
                sqLiteQueryBuilder.setTables(RetirementContract.SavingsIncomeEntry.TABLE_NAME);
                break;
            case SUMMARY_LIST:
                sqLiteQueryBuilder.setTables(RetirementContract.SummaryEntry.TABLE_NAME);
                break;
            case TAX_DEFERRED_INCOME_ID:
                /*
                sqLiteQueryBuilder.setTables(RetirementContract.TaxDeferredIncomeEntry.TABLE_NAME);
                if(TextUtils.isEmpty(selection)) {
                    sqLiteQueryBuilder.appendWhere(RetirementContract.TaxDeferredIncomeEntry._ID +
                            "=" + uri.getLastPathSegment());
                }
                break;
                */
                Cursor cursor = getTaxDeferredIncomeSource(uri, projection, selection, selectionArgs, sortOrder);
                return cursor;
            case TAX_DEFERRED_INCOME_LIST:
                sqLiteQueryBuilder.setTables(RetirementContract.TaxDeferredIncomeEntry.TABLE_NAME);
                break;
            case PENSION_INCOME_ID:
                sqLiteQueryBuilder.setTables(RetirementContract.PensionIncomeEntry.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(RetirementContract.PensionIncomeEntry._ID +
                        "=" + uri.getLastPathSegment());
                break;
            case PENSION_INCOME_LIST:
                sqLiteQueryBuilder.setTables(RetirementContract.PensionIncomeEntry.TABLE_NAME);
                break;
            case GOV_PENSION_INCOME_ID:
                sqLiteQueryBuilder.setTables(RetirementContract.GovPensionIncomeEntry.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(RetirementContract.GovPensionIncomeEntry._ID +
                        "=" + uri.getLastPathSegment());
                break;
            case GOV_PENSION_INCOME_LIST:
                sqLiteQueryBuilder.setTables(RetirementContract.GovPensionIncomeEntry.TABLE_NAME);
                break;
            case BALANCE_ID:
                sqLiteQueryBuilder.setTables(RetirementContract.BalanceEntry.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(RetirementContract.BalanceEntry._ID +
                        "=" + uri.getLastPathSegment());
                break;
            case BALANCE_LIST:
                sqLiteQueryBuilder.setTables(RetirementContract.BalanceEntry.TABLE_NAME);
                break;
            case MILESTONE_ID:
                sqLiteQueryBuilder.setTables(RetirementContract.MilestoneEntry.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(RetirementContract.MilestoneEntry._ID +
                        "=" + uri.getLastPathSegment());
                break;
            case MILESTONE_LIST:
                sqLiteQueryBuilder.setTables(RetirementContract.MilestoneEntry.TABLE_NAME);
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
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long rowId;
        SQLiteDatabase db;
        Uri returnUri;

        db = mSqliteHelper.getWritableDatabase();

        switch(sUriMatcher.match(uri)) {
            case INCOME_TYPE_LIST:
                // The second parameter will allow an empty row to be inserted. If it was null, then no row
                // can be inserted if values is empty.
                rowId = db.insert(RetirementContract.IncomeTypeEntry.TABLE_NAME, null, values);
                if (rowId > -1) {
                    returnUri = ContentUris.withAppendedId(uri, rowId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case SAVINGS_INCOME_LIST:
                // The second parameter will allow an empty row to be inserted. If it was null, then no row
                // can be inserted if values is empty.
                rowId = db.insert(RetirementContract.SavingsIncomeEntry.TABLE_NAME, null, values);
                if (rowId > -1) {
                    returnUri = ContentUris.withAppendedId(uri, rowId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case SUMMARY_LIST:
                // The second parameter will allow an empty row to be inserted. If it was null, then no row
                // can be inserted if values is empty.
                rowId = db.insert(RetirementContract.SummaryEntry.TABLE_NAME, null, values);
                if (rowId > -1) {
                    returnUri = ContentUris.withAppendedId(uri, rowId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case TAX_DEFERRED_INCOME_LIST:
                // The second parameter will allow an empty row to be inserted. If it was null, then no row
                // can be inserted if values is empty.
                rowId = db.insert(RetirementContract.TaxDeferredIncomeEntry.TABLE_NAME, null, values);
                if (rowId > -1) {
                    returnUri = ContentUris.withAppendedId(uri, rowId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case PENSION_INCOME_LIST:
                // The second parameter will allow an empty row to be inserted. If it was null, then no row
                // can be inserted if values is empty.
                rowId = db.insert(RetirementContract.PensionIncomeEntry.TABLE_NAME, null, values);
                if (rowId > -1) {
                    returnUri = ContentUris.withAppendedId(uri, rowId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case GOV_PENSION_INCOME_LIST:
                // The second parameter will allow an empty row to be inserted. If it was null, then no row
                // can be inserted if values is empty.
                rowId = db.insert(RetirementContract.GovPensionIncomeEntry.TABLE_NAME, null, values);
                if (rowId > -1) {
                    returnUri = ContentUris.withAppendedId(uri, rowId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case BALANCE_LIST:
                // The second parameter will allow an empty row to be inserted. If it was null, then no row
                // can be inserted if values is empty.
                rowId = db.insert(RetirementContract.BalanceEntry.TABLE_NAME, null, values);
                if (rowId > -1) {
                    returnUri = ContentUris.withAppendedId(uri, rowId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case MILESTONE_LIST:
                // The second parameter will allow an empty row to be inserted. If it was null, then no row
                // can be inserted if values is empty.
                rowId = db.insert(RetirementContract.MilestoneEntry.TABLE_NAME, null, values);
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
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mSqliteHelper.getWritableDatabase();
        int rowsDeleted;
        String id;

        switch(sUriMatcher.match(uri)) {
            case INCOME_TYPE_ID:
                id = uri.getLastPathSegment();
                rowsDeleted = db.delete(RetirementContract.IncomeTypeEntry.TABLE_NAME,
                        RetirementContract.IncomeTypeEntry._ID + "=" + id, null);
                break;
            case SAVINGS_INCOME_ID:
                id = uri.getLastPathSegment();
                rowsDeleted = db.delete(RetirementContract.SavingsIncomeEntry.TABLE_NAME,
                        RetirementContract.SavingsIncomeEntry.COLUMN_INCOME_TYPE_ID + "=" + id, null);
                break;
            case TAX_DEFERRED_INCOME_ID:
                if(TextUtils.isEmpty(selection)) {
                    id = uri.getLastPathSegment();
                    rowsDeleted = db.delete(RetirementContract.TaxDeferredIncomeEntry.TABLE_NAME,
                            RetirementContract.TaxDeferredIncomeEntry.COLUMN_INCOME_TYPE_ID + "=" + id, null);
                } else {
                    rowsDeleted = db.delete(RetirementContract.TaxDeferredIncomeEntry.TABLE_NAME,
                            selection, selectionArgs);
                }
                break;
            case PENSION_INCOME_ID:
                id = uri.getLastPathSegment();
                rowsDeleted = db.delete(RetirementContract.PensionIncomeEntry.TABLE_NAME,
                        RetirementContract.PensionIncomeEntry.COLUMN_INCOME_TYPE_ID + "=" + id, null);
                break;
            case GOV_PENSION_INCOME_ID:
                id = uri.getLastPathSegment();
                rowsDeleted = db.delete(RetirementContract.GovPensionIncomeEntry.TABLE_NAME,
                        RetirementContract.GovPensionIncomeEntry.COLUMN_INCOME_TYPE_ID + "=" + id, null);
                break;

            case BALANCE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(RetirementContract.BalanceEntry.TABLE_NAME,
                            RetirementContract.BalanceEntry.COLUMN_INCOME_TYPE_ID + " = ?", new String[]{id});
                } else {
                    rowsDeleted = db.delete(RetirementContract.BalanceEntry.TABLE_NAME, selection, selectionArgs);
                }
                break;
            case BALANCE_LIST:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(RetirementContract.BalanceEntry.TABLE_NAME, selection, selectionArgs);
                } else {
                    rowsDeleted = db.delete(RetirementContract.BalanceEntry.TABLE_NAME,
                            RetirementContract.BalanceEntry.COLUMN_INCOME_TYPE_ID + " = ?", new String[]{id});
                }
                break;
            case MILESTONE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(RetirementContract.MilestoneEntry.TABLE_NAME,
                            RetirementContract.MilestoneEntry._ID + " = ?", new String[]{id});
                } else {
                    rowsDeleted = db.delete(RetirementContract.MilestoneEntry.TABLE_NAME, selection, selectionArgs);
                }
                break;
            case MILESTONE_LIST:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(RetirementContract.MilestoneEntry.TABLE_NAME, selection, selectionArgs);
                } else {
                    rowsDeleted = db.delete(RetirementContract.MilestoneEntry.TABLE_NAME,
                            RetirementContract.MilestoneEntry._ID + " = ?", new String[]{id});
                }
                break;
            case SUMMARY_LIST:
                rowsDeleted = db.delete(RetirementContract.SummaryEntry.TABLE_NAME, null, null);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri");
        }

        if(rowsDeleted > 0) {
            notifyChanges(uri);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mSqliteHelper.getWritableDatabase();
        int rowsUpdated;
        String id;

        switch(sUriMatcher.match(uri)) {
            case RETIREMENT_OPTIONS_ID:
                rowsUpdated = db.update(RetirementContract.RetirementParmsEntry.TABLE_NAME,
                        values, null, null);
                break;
            case INCOME_TYPE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(RetirementContract.IncomeTypeEntry.TABLE_NAME,
                            values,
                            RetirementContract.IncomeTypeEntry._ID + "=?",
                            new String[]{id});
                } else {
                    rowsUpdated = db.update(RetirementContract.IncomeTypeEntry.TABLE_NAME,
                            values,
                            RetirementContract.IncomeTypeEntry._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case SAVINGS_INCOME_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(RetirementContract.SavingsIncomeEntry.TABLE_NAME,
                            values,
                            RetirementContract.SavingsIncomeEntry.COLUMN_INCOME_TYPE_ID + " = ?",
                            new String[]{id});
                } else {
                    rowsUpdated = db.update(RetirementContract.SavingsIncomeEntry.TABLE_NAME,
                            values, selection, selectionArgs);
                }
                break;
            case TAX_DEFERRED_INCOME_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(RetirementContract.TaxDeferredIncomeEntry.TABLE_NAME,
                            values,
                            RetirementContract.TaxDeferredIncomeEntry.COLUMN_INCOME_TYPE_ID + "=?",
                            new String[]{id});
                } else {
                    rowsUpdated = db.update(RetirementContract.TaxDeferredIncomeEntry.TABLE_NAME,
                            values,
                            RetirementContract.TaxDeferredIncomeEntry.COLUMN_INCOME_TYPE_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case PENSION_INCOME_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(RetirementContract.PensionIncomeEntry.TABLE_NAME,
                            values,
                            RetirementContract.PensionIncomeEntry.COLUMN_INCOME_TYPE_ID + "=?",
                            new String[]{id});
                } else {
                    rowsUpdated = db.update(RetirementContract.PensionIncomeEntry.TABLE_NAME,
                            values,
                            RetirementContract.PensionIncomeEntry.COLUMN_INCOME_TYPE_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case GOV_PENSION_INCOME_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(RetirementContract.GovPensionIncomeEntry.TABLE_NAME,
                            values,
                            RetirementContract.GovPensionIncomeEntry.COLUMN_INCOME_TYPE_ID + "=?",
                            new String[]{id});
                } else {
                    rowsUpdated = db.update(RetirementContract.GovPensionIncomeEntry.TABLE_NAME,
                            values,
                            RetirementContract.GovPensionIncomeEntry.COLUMN_INCOME_TYPE_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;

            case BALANCE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(RetirementContract.BalanceEntry.TABLE_NAME,
                            values,
                            RetirementContract.BalanceEntry.COLUMN_INCOME_TYPE_ID + "=?",
                            new String[]{id});
                } else {
                    rowsUpdated = db.update(RetirementContract.BalanceEntry.TABLE_NAME,
                            values,
                            RetirementContract.BalanceEntry.COLUMN_INCOME_TYPE_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case MILESTONE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(RetirementContract.MilestoneEntry.TABLE_NAME,
                            values,
                            RetirementContract.MilestoneEntry._ID + "=?",
                            new String[]{id});
                } else {
                    rowsUpdated = db.update(RetirementContract.MilestoneEntry.TABLE_NAME,
                            values,
                            RetirementContract.MilestoneEntry._ID + "=" + id
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

    private void notifyChanges(@NonNull Uri uri) {
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
            // create the retirement parms table
            String sql = "CREATE TABLE " + RetirementContract.RetirementParmsEntry.TABLE_NAME +
                    " ( " + RetirementContract.RetirementParmsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RetirementContract.RetirementParmsEntry.COLUMN_END_AGE + " TEXT NOT NULL, " +
                    RetirementContract.RetirementParmsEntry.COLUMN_WITHDRAW_MODE + " INTEGER NOT NULL, " +
                    RetirementContract.RetirementParmsEntry.COLUMN_WITHDRAW_AMOUNT + " TEXT NOT NULL, " +
                    RetirementContract.RetirementParmsEntry.COLUMN_BIRTHDATE + " TEXT NOT NULL);";

            db.execSQL(sql);

            // create the income type table
            sql = "CREATE TABLE " + RetirementContract.IncomeTypeEntry.TABLE_NAME +
                    " ( " + RetirementContract.IncomeTypeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RetirementContract.IncomeTypeEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                    RetirementContract.IncomeTypeEntry.COLUMN_TYPE + " INTEGER NOT NULL);";

            db.execSQL(sql);

            // create the savings income table
            sql = "CREATE TABLE " + RetirementContract.SavingsIncomeEntry.TABLE_NAME +
                    " ( " + RetirementContract.SavingsIncomeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RetirementContract.SavingsIncomeEntry.COLUMN_INCOME_TYPE_ID + " INTEGER NOT NULL, " +
                    RetirementContract.SavingsIncomeEntry.COLUMN_MONTH_ADD + " TEXT NOT NULL, " +
                    RetirementContract.SavingsIncomeEntry.COLUMN_INTEREST + " TEXT NOT NULL);";

            db.execSQL(sql);

            // create the tax deferred income table
            sql = "CREATE TABLE " + RetirementContract.TaxDeferredIncomeEntry.TABLE_NAME +
                    " ( " + RetirementContract.TaxDeferredIncomeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RetirementContract.TaxDeferredIncomeEntry.COLUMN_INCOME_TYPE_ID + " INTEGER NOT NULL, " +
                    RetirementContract.TaxDeferredIncomeEntry.COLUMN_INTEREST + " TEXT NOT NULL, " +
                    RetirementContract.TaxDeferredIncomeEntry.COLUMN_MONTH_ADD + " TEXT NOT NULL, " +
                    RetirementContract.TaxDeferredIncomeEntry.COLUMN_MIN_AGE + " TEXT NOT NULL, " +
                    RetirementContract.TaxDeferredIncomeEntry.COLUMN_PENALTY + " TEXT NOT NULL, " +
                    RetirementContract.TaxDeferredIncomeEntry.COLUMN_IS_401K + " INTEGER NOT NULL);";

            db.execSQL(sql);

            // create the pension income table
            sql = "CREATE TABLE " + RetirementContract.PensionIncomeEntry.TABLE_NAME +
                    " ( " + RetirementContract.PensionIncomeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RetirementContract.PensionIncomeEntry.COLUMN_INCOME_TYPE_ID + " INTEGER NOT NULL, " +
                    RetirementContract.PensionIncomeEntry.COLUMN_START_AGE + " TEXT NOT NULL, " +
                    RetirementContract.PensionIncomeEntry.COLUMN_MONTH_BENEFIT + " TEXT NOT NULL);";

            db.execSQL(sql);

            // create the gov pension income table
            sql = "CREATE TABLE " + RetirementContract.GovPensionIncomeEntry.TABLE_NAME +
                    " ( " + RetirementContract.GovPensionIncomeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RetirementContract.GovPensionIncomeEntry.COLUMN_INCOME_TYPE_ID + " INTEGER NOT NULL, " +
                    RetirementContract.GovPensionIncomeEntry.COLUMN_MIN_AGE + " TEXT NOT NULL, " +
                    RetirementContract.GovPensionIncomeEntry.COLUMN_MONTH_BENEFIT + " TEXT NOT NULL);";

            db.execSQL(sql);

            // create the balance table
            sql = "CREATE TABLE " + RetirementContract.BalanceEntry.TABLE_NAME +
                    " ( " + RetirementContract.BalanceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RetirementContract.BalanceEntry.COLUMN_INCOME_TYPE_ID + " INTEGER NOT NULL, " +
                    RetirementContract.BalanceEntry.COLUMN_AMOUNT + " TEXT NOT NULL, " +
                    RetirementContract.BalanceEntry.COLUMN_DATE + " TEXT NOT NULL);";

            db.execSQL(sql);

            // create the milestone table
            sql = "CREATE TABLE " + RetirementContract.MilestoneEntry.TABLE_NAME +
                    " ( " + RetirementContract.MilestoneEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RetirementContract.MilestoneEntry.COLUMN_AGE + " TEXT NOT NULL);";

            db.execSQL(sql);

            // create the summary table
            sql = "CREATE TABLE " + RetirementContract.SummaryEntry.TABLE_NAME +
                    " ( " + RetirementContract.SummaryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RetirementContract.SummaryEntry.COLUMN_AMOUNT + " TEXT NOT NULL, " +
                    RetirementContract.SummaryEntry.COLUMN_AGE + " TEXT NOT NULL);";

            db.execSQL(sql);

            // add default ages
            String ROW = "INSERT INTO " + RetirementContract.MilestoneEntry.TABLE_NAME +
                    " Values ('1', '60 0');";
            db.execSQL(ROW);

            ROW = "INSERT INTO " + RetirementContract.MilestoneEntry.TABLE_NAME +
                    " Values ('2', '62 0');";
            db.execSQL(ROW);

            ROW = "INSERT INTO " + RetirementContract.MilestoneEntry.TABLE_NAME +
                    " Values ('3', '65 0');";
            db.execSQL(ROW);

            ROW = "INSERT INTO " + RetirementContract.MilestoneEntry.TABLE_NAME +
                    " Values ('4', '70 0');";
            db.execSQL(ROW);

            ROW = "INSERT INTO " + RetirementContract.RetirementParmsEntry.TABLE_NAME + " Values ('0', '90 0', '0', '4', 0);";
            db.execSQL(ROW);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.RetirementParmsEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.IncomeTypeEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.SavingsIncomeEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.TaxDeferredIncomeEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.PensionIncomeEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.GovPensionIncomeEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.BalanceEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.MilestoneEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.SummaryEntry.TABLE_NAME);

            onCreate(db);
        }
    }

    private Cursor getTaxDeferredIncomeSource(
            Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(QUERY_TABLES_FOR_TAX_DEFERRED_ITEM);

        return builder.query(mSqliteHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }
}
