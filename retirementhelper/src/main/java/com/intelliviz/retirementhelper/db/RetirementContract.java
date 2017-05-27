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
    public static final String PATH_RETIREMENT_PARMS = "retirement_parms";
    public static final String PATH_EXPENSE = "expense";
    public static final String PATH_CATEGORY = "category";
    public static final String PATH_INCOME_TYPE = "income_type";
    public static final String PATH_SAVINGS_INCOME = "savings_income";
    public static final String PATH_TAX_DEFERRED_INCOME = "tax_deferred_income";
    public static final String PATH_PENSION_INCOME = "pension_income";
    public static final String PATH_GOV_PENSION_INCOME = "gov_pension_income";
    public static final String PATH_BALANCE = "balance";
    public static final String PATH_MILESTONE = "milestone";

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
    }

    public static final class RetirementParmsEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RETIREMENT_PARMS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RETIREMENT_PARMS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RETIREMENT_PARMS;

        public static final String TABLE_NAME = PATH_RETIREMENT_PARMS;
        public static final String COLUMN_START_AGE = "start_age";
        public static final String COLUMN_END_AGE = "end_age";
        public static final String COLUMN_INC_INFLATION = "include_inflation";
        public static final String COLUMN_INFL_AMOUNT = "infl_amount";
        public static final String COLUMN_WITHDRAW_MODE = "withdraw_mode";
        public static final String COLUMN_WITHDRAW_PERCENT = "withdraw_percent";
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

    public static final class IncomeTypeEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_INCOME_TYPE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INCOME_TYPE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INCOME_TYPE;

        public static final String TABLE_NAME = PATH_INCOME_TYPE;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TYPE = "type";
    }

    public static final class SavingsIncomeEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SAVINGS_INCOME).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SAVINGS_INCOME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SAVINGS_INCOME;

        public static final String TABLE_NAME = PATH_SAVINGS_INCOME;
        public static final String COLUMN_INCOME_TYPE_ID = "income_type_id";
        public static final String COLUMN_INTEREST = "interest";
        public static final String COLUMN_MONTH_ADD = "month_add";
    }

    public static final class TaxDeferredIncomeEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TAX_DEFERRED_INCOME).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TAX_DEFERRED_INCOME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TAX_DEFERRED_INCOME;

        public static final String TABLE_NAME = PATH_TAX_DEFERRED_INCOME;
        public static final String COLUMN_INCOME_TYPE_ID = "income_type_id";
        public static final String COLUMN_INTEREST = "interest";
        public static final String COLUMN_MONTH_ADD = "month_add";
        public static final String COLUMN_PENALTY = "penalty";
        public static final String COLUMN_MIN_AGE = "min_age";
        public static final String COLUMN_IS_401K = "is_401k";
    }

    public static final class PensionIncomeEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PENSION_INCOME).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PENSION_INCOME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PENSION_INCOME;

        public static final String TABLE_NAME = PATH_PENSION_INCOME;
        public static final String COLUMN_INCOME_TYPE_ID = "income_type_id";
        public static final String COLUMN_START_AGE = "start_age";
        public static final String COLUMN_MONTH_BENEFIT = "month_benefit";
    }

    public static final class GovPensionIncomeEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_GOV_PENSION_INCOME).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GOV_PENSION_INCOME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GOV_PENSION_INCOME;

        public static final String TABLE_NAME = PATH_GOV_PENSION_INCOME;
        public static final String COLUMN_INCOME_TYPE_ID = "income_type_id";
        public static final String COLUMN_MIN_AGE = "min_age";
        public static final String COLUMN_MONTH_BENEFIT = "month_benefit";
    }

    public static final class BalanceEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_BALANCE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BALANCE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BALANCE;

        public static final String TABLE_NAME = PATH_BALANCE;
        public static final String COLUMN_INCOME_TYPE_ID = "income_type_id";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_DATE = "date";
    }

    public static final class MileStoneEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MILESTONE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MILESTONE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MILESTONE;

        public static final String TABLE_NAME = PATH_MILESTONE;
        public static final String COLUMN_AGE = "age";
    }
}
