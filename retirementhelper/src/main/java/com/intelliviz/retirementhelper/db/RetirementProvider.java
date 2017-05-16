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
    private static final String DBASE_NAME = "retirement";
    private static final int DBASE_VERSION = 2;
    private static final int PERSONALINFO_ID = 101;
    private static final int RETIREMENT_PARMS_ID = 102;
    private static final int CATEGORY_LIST = 201;
    private static final int CATEGORY_ID = 202;
    private static final int EXPENSE_LIST = 301;
    private static final int EXPENSE_ID = 302;
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

    private static UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_PERSONALINFO, PERSONALINFO_ID);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_RETIREMENT_PARMS, RETIREMENT_PARMS_ID);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_CATEGORY, CATEGORY_LIST);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_CATEGORY + "/#", CATEGORY_ID);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_EXPENSE, EXPENSE_LIST);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_EXPENSE + "/#", EXPENSE_ID);

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
            case PERSONALINFO_ID:
                return RetirementContract.PeronsalInfoEntry.CONTENT_ITEM_TYPE;
            case RETIREMENT_PARMS_ID:
                return RetirementContract.RetirementParmsEntry.CONTENT_ITEM_TYPE;
            case EXPENSE_LIST:
                return RetirementContract.ExpenseEntery.CONTENT_TYPE;
            case EXPENSE_ID:
                return RetirementContract.ExpenseEntery.CONTENT_ITEM_TYPE;
            case CATEGORY_LIST:
                return RetirementContract.CategoryEntry.CONTENT_TYPE;
            case CATEGORY_ID:
                return RetirementContract.CategoryEntry.CONTENT_ITEM_TYPE;
            case INCOME_TYPE_LIST:
                return RetirementContract.IncomeTypeEntry.CONTENT_TYPE;
            case INCOME_TYPE_ID:
                return RetirementContract.IncomeTypeEntry.CONTENT_ITEM_TYPE;
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
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        switch(sUriMatcher.match(uri)) {
            case PERSONALINFO_ID:
                // get the personal info table, there should be only one.
                sqLiteQueryBuilder.setTables(RetirementContract.PeronsalInfoEntry.TABLE_NAME);
                break;
            case RETIREMENT_PARMS_ID:
                sqLiteQueryBuilder.setTables(RetirementContract.RetirementParmsEntry.TABLE_NAME);
                break;
            case CATEGORY_ID:
                sqLiteQueryBuilder.setTables(RetirementContract.CategoryEntry.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(RetirementContract.CategoryEntry._ID +
                        "=" + uri.getLastPathSegment());
                break;
            case CATEGORY_LIST:
                sqLiteQueryBuilder.setTables(RetirementContract.CategoryEntry.TABLE_NAME);
                break;
            case EXPENSE_ID:
                sqLiteQueryBuilder.setTables(RetirementContract.ExpenseEntery.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(RetirementContract.ExpenseEntery._ID +
                        "=" + uri.getLastPathSegment());
                break;
            case EXPENSE_LIST:
                sqLiteQueryBuilder.setTables(RetirementContract.ExpenseEntery.TABLE_NAME);
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
                sqLiteQueryBuilder.appendWhere(RetirementContract.SavingsIncomeEntry._ID +
                        "=" + uri.getLastPathSegment());
                break;
            case SAVINGS_INCOME_LIST:
                sqLiteQueryBuilder.setTables(RetirementContract.SavingsIncomeEntry.TABLE_NAME);
                break;
            case TAX_DEFERRED_INCOME_ID:
                sqLiteQueryBuilder.setTables(RetirementContract.TaxDeferredIncomeEntry.TABLE_NAME);
                if(TextUtils.isEmpty(selection)) {
                    sqLiteQueryBuilder.appendWhere(RetirementContract.TaxDeferredIncomeEntry._ID +
                            "=" + uri.getLastPathSegment());
                }
                break;
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
            case CATEGORY_LIST:
                // The second parameter will allow an empty row to be inserted. If it was null, then no row
                // can be inserted if values is empty.
                rowId = db.insert(RetirementContract.CategoryEntry.TABLE_NAME, null, values);
                if (rowId > -1) {
                    returnUri = ContentUris.withAppendedId(uri, rowId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case EXPENSE_LIST:
                // The second parameter will allow an empty row to be inserted. If it was null, then no row
                // can be inserted if values is empty.
                rowId = db.insert(RetirementContract.ExpenseEntery.TABLE_NAME, null, values);
                if (rowId > -1) {
                    returnUri = ContentUris.withAppendedId(uri, rowId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
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
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri.toString());
        }
        notifyChanges(returnUri);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mSqliteHelper.getWritableDatabase();
        int rowsDeleted = 0;
        String id;

        switch(sUriMatcher.match(uri)) {
            case CATEGORY_ID:
                id = uri.getLastPathSegment();
                rowsDeleted = db.delete(RetirementContract.CategoryEntry.TABLE_NAME,
                        RetirementContract.CategoryEntry._ID + "=" + id, null);
                break;
            case EXPENSE_ID:
                id = uri.getLastPathSegment();
                rowsDeleted = db.delete(RetirementContract.ExpenseEntery.TABLE_NAME,
                        RetirementContract.ExpenseEntery._ID + "=" + id, null);
                break;
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
                rowsUpdated = db.update(RetirementContract.PeronsalInfoEntry.TABLE_NAME,
                        values, null, null);
                break;
            case RETIREMENT_PARMS_ID:
                rowsUpdated = db.update(RetirementContract.RetirementParmsEntry.TABLE_NAME,
                        values, null, null);
                break;
            case CATEGORY_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(RetirementContract.CategoryEntry.TABLE_NAME,
                            values,
                            RetirementContract.ExpenseEntery._ID + "=?",
                            new String[]{id});
                } else {
                    rowsUpdated = db.update(RetirementContract.CategoryEntry.TABLE_NAME,
                            values,
                            RetirementContract.CategoryEntry._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case EXPENSE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(RetirementContract.ExpenseEntery.TABLE_NAME,
                            values,
                            RetirementContract.ExpenseEntery._ID + "=?",
                            new String[]{id});
                } else {
                    rowsUpdated = db.update(RetirementContract.ExpenseEntery.TABLE_NAME,
                            values,
                            RetirementContract.ExpenseEntery._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
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
            // create the personal info table
            String sql = "CREATE TABLE " + RetirementContract.PeronsalInfoEntry.TABLE_NAME +
                    " ( " + RetirementContract.PeronsalInfoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RetirementContract.PeronsalInfoEntry.COLUMN_EMAIL + " TEXT NOT NULL, " +
                    RetirementContract.PeronsalInfoEntry.COLUMN_PASSWORD + " TEXT, " +
                    RetirementContract.PeronsalInfoEntry.COLUMN_NAME + " TEXT, " +
                    RetirementContract.PeronsalInfoEntry.COLUMN_PIN + " TEXT, " +
                    RetirementContract.PeronsalInfoEntry.COLUMN_DEATH_AGE + " TEXT, " +
                    RetirementContract.PeronsalInfoEntry.COLUMN_START_AGE + " TEXT, " +
                    RetirementContract.PeronsalInfoEntry.COLUMN_BIRTHDATE + " TEXT);";

            db.execSQL(sql);

            // create the retirement parms table
            sql = "CREATE TABLE " + RetirementContract.RetirementParmsEntry.TABLE_NAME +
                    " ( " + RetirementContract.RetirementParmsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RetirementContract.RetirementParmsEntry.COLUMN_START_AGE + " TEXT NOT NULL, " +
                    RetirementContract.RetirementParmsEntry.COLUMN_END_AGE + " TEXT NOT NULL, " +
                    RetirementContract.RetirementParmsEntry.COLUMN_WITHDRAW_MODE + " INTEGER NOT NULL, " +
                    RetirementContract.RetirementParmsEntry.COLUMN_WITHDRAW_PERCENT + " TEXT NOT NULL, " +
                    RetirementContract.RetirementParmsEntry.COLUMN_INC_INFLATION + " TEXT NOT NULL, " +
                    RetirementContract.RetirementParmsEntry.COLUMN_INFL_AMOUNT + " TEXT NOT NULL);";

            db.execSQL(sql);

            // create the category table
            sql = "CREATE TABLE " + RetirementContract.CategoryEntry.TABLE_NAME +
                    " ( " + RetirementContract.CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RetirementContract.CategoryEntry.COLUMN_NAME + " INTEGER NOT NULL, " +
                    RetirementContract.CategoryEntry.COLUMN_PARENT_NAME + " TEXT NOT NULL);";

            db.execSQL(sql);

            // create the expense table
            sql = "CREATE TABLE " + RetirementContract.ExpenseEntery.TABLE_NAME +
                    " ( " + RetirementContract.ExpenseEntery._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RetirementContract.ExpenseEntery.COLUMN_CAT_ID + " INTEGER NOT NULL, " +
                    RetirementContract.ExpenseEntery.COLUMN_YEAR + " TEXT NOT NULL, " +
                    RetirementContract.ExpenseEntery.COLUMN_MONTH + " TEXT NOT NULL, " +
                    RetirementContract.ExpenseEntery.COLUMN_ACTUAL_AMOUNT + " TEXT NOT NULL, " +
                    RetirementContract.ExpenseEntery.COLUMN_RETIRE_AMOUNT + " TEXT NOT NULL);";

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

            String ROW = "INSERT INTO " + RetirementContract.PeronsalInfoEntry.TABLE_NAME + " Values ('0', '-1', '-1', '-1', '-1', '90', 'NOW', '-1');";
            db.execSQL(ROW);
            ROW = "INSERT INTO " + RetirementContract.RetirementParmsEntry.TABLE_NAME + " Values ('62', '90', '0', '0', '0', '0');";
            db.execSQL(ROW);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.PeronsalInfoEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.RetirementParmsEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.CategoryEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.ExpenseEntery.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.IncomeTypeEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.SavingsIncomeEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.TaxDeferredIncomeEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.PensionIncomeEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.GovPensionIncomeEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.BalanceEntry.TABLE_NAME);

            onCreate(db);
        }
    }
}
