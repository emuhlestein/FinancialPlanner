package com.intelliviz.retirementhelper.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by edm on 3/27/2017.
 */

public class RetirementContract {
    static final String CONTENT_AUTHORITY =
            "com.intelliviz.retirementhelper.db.RetirementProvider";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    static final String PATH_PERSONALINFO = "personalinfo";
    static final String PATH_RETIREMENT_PARMS = "retirement_parms";
    static final String PATH_INCOME_TYPE = "income_type";
    static final String PATH_SAVINGS_INCOME = "savings_income";
    static final String PATH_TAX_DEFERRED_INCOME = "tax_deferred_income";
    static final String PATH_PENSION_INCOME = "pension_income";
    static final String PATH_GOV_PENSION_INCOME = "gov_pension_income";
    static final String PATH_BALANCE = "balance";
    static final String PATH_MILESTONE = "milestone";
    static final String PATH_SUMMARY = "summary";

    private RetirementContract() {
    }

    public static final class PersonalInfoEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PERSONALINFO).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PERSONALINFO;
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PERSONALINFO;

        static final String TABLE_NAME = PATH_PERSONALINFO;
        // yyyy-MM-dd
        public static final String COLUMN_BIRTHDATE = "birthdate";
    }

    public static final class RetirementParmsEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RETIREMENT_PARMS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RETIREMENT_PARMS;
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RETIREMENT_PARMS;

        static final String TABLE_NAME = PATH_RETIREMENT_PARMS;
        public static final String COLUMN_END_AGE = "end_age";
        public static final String COLUMN_WITHDRAW_MODE = "withdraw_mode";
        public static final String COLUMN_WITHDRAW_AMOUNT = "withdraw_amount";
    }

    public static final class IncomeTypeEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_INCOME_TYPE).build();

        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INCOME_TYPE;
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INCOME_TYPE;

        static final String TABLE_NAME = PATH_INCOME_TYPE;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TYPE = "type";
    }

    public static final class SavingsIncomeEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SAVINGS_INCOME).build();

        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SAVINGS_INCOME;
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SAVINGS_INCOME;

        static final String TABLE_NAME = PATH_SAVINGS_INCOME;
        public static final String COLUMN_INCOME_TYPE_ID = "income_type_id";
        public static final String COLUMN_INTEREST = "interest";
        public static final String COLUMN_MONTH_ADD = "month_add";
    }

    public static final class TaxDeferredIncomeEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TAX_DEFERRED_INCOME).build();

        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TAX_DEFERRED_INCOME;
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TAX_DEFERRED_INCOME;

        static final String TABLE_NAME = PATH_TAX_DEFERRED_INCOME;
        public static final String COLUMN_INCOME_TYPE_ID = "income_type_id";
        public static final String COLUMN_INTEREST = "interest";
        public static final String COLUMN_MONTH_ADD = "month_add";
        public static final String COLUMN_PENALTY = "penalty";
        public static final String COLUMN_MIN_AGE = "min_age";
        public static final String COLUMN_IS_401K = "is_401k";
    }

    public static final class PensionIncomeEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PENSION_INCOME).build();

        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PENSION_INCOME;
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PENSION_INCOME;

        static final String TABLE_NAME = PATH_PENSION_INCOME;
        public static final String COLUMN_INCOME_TYPE_ID = "income_type_id";
        public static final String COLUMN_START_AGE = "start_age";
        public static final String COLUMN_MONTH_BENEFIT = "month_benefit";
    }

    public static final class GovPensionIncomeEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_GOV_PENSION_INCOME).build();

        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GOV_PENSION_INCOME;
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GOV_PENSION_INCOME;

        static final String TABLE_NAME = PATH_GOV_PENSION_INCOME;
        public static final String COLUMN_INCOME_TYPE_ID = "income_type_id";
        public static final String COLUMN_MIN_AGE = "min_age";
        public static final String COLUMN_MONTH_BENEFIT = "month_benefit";
    }

    public static final class BalanceEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_BALANCE).build();

        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BALANCE;
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BALANCE;

        static final String TABLE_NAME = PATH_BALANCE;
        public static final String COLUMN_INCOME_TYPE_ID = "income_type_id";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_DATE = "date";
    }

    public static final class MilestoneEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MILESTONE).build();

        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MILESTONE;
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MILESTONE;

        static final String TABLE_NAME = PATH_MILESTONE;
        public static final String COLUMN_AGE = "ages";
    }

    public static final class SummaryEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUMMARY).build();

        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUMMARY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUMMARY;

        static final String TABLE_NAME = PATH_SUMMARY;
        public static final String COLUMN_AGE = "age";
        public static final String COLUMN_AMOUNT = "amount";
    }
}
