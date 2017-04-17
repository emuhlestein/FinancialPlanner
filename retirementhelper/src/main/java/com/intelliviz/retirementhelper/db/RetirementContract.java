package com.intelliviz.retirementhelper.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by edm on 3/27/2017.
 */

public class RetirementContract {
    public static final String CONTENT_AUTHORITY =
            "com.intelliviz.retirementhelper.db.RetirementProvider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PERSONALINFO = "personalinfo";
    public static final String PATH_EXPENSE = "expense";
    public static final String PATH_CATEGORY = "category";
    public static final String PATH_INCOME_SOURCE = "income_source";

    private RetirementContract() {
    }

    public static final class PeronsalInfoEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PERSONALINFO).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PERSONALINFO;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PERSONALINFO;

        public static final String TABLE_NAME = PATH_PERSONALINFO;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PASSWORD = "password";
        // yyyy-MM-dd
        public static final String COLUMN_BIRTHDATE = "birthdate";
        public static final String COLUMN_PIN = "pin";
        public static final String COLUMN_DEATH_AGE = "death_age";
        public static final String COLUMN_START_AGE = "start_age";

    }

    public static final class ExpenseEntery implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EXPENSE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXPENSE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXPENSE;

        public static final String TABLE_NAME = PATH_EXPENSE;
        public static final String COLUMN_CAT_ID = "cat_id";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_MONTH = "month";
        public static final String COLUMN_ACTUAL_AMOUNT = "actual_amount";
        public static final String COLUMN_RETIRE_AMOUNT = "retire_amount";
    }

    public static final class CategoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;

        public static final String TABLE_NAME = PATH_CATEGORY;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PARENT_NAME = "parent_name";
    }

    public static final class IncomeSourceEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_INCOME_SOURCE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INCOME_SOURCE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INCOME_SOURCE;

        public static final String TABLE_NAME = PATH_INCOME_SOURCE;
        public static final String COLUMN_TYPE = "income_type";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_BALANCE = "balance";
        public static final String COLUMN_INTEREST = "interest";
        public static final String COLUMN_MONTHLY_INCREASE = "monthly_increase";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_MONTHLY_BENEFIT = "monthly_benefit";
    }
}
