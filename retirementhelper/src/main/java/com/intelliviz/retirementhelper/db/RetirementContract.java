package com.intelliviz.retirementhelper.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Class that describes the sqlite table schemas.
 * Created by Ed Muhlestein on 3/27/2017.
 */
public class RetirementContract {
    static final String CONTENT_AUTHORITY =
            "com.intelliviz.retirementhelper.db.RetirementProvider";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    static final String PATH_RETIREMENT_PARMS = "retirement_parms";
    static final String PATH_INCOME_TYPE = "income_type";
    static final String PATH_SAVINGS_INCOME = "savings_income";
    static final String PATH_TAX_DEFERRED_INCOME = "tax_deferred_income";
    static final String PATH_PENSION_INCOME = "pension_income";
    static final String PATH_GOV_PENSION_INCOME = "gov_pension_income";
    static final String PATH_BALANCE = "balance";
    static final String PATH_MILESTONE = "milestone";
    static final String PATH_SUMMARY = "summary";
    static final String PATH_TRANSACTION_STATUS = "transaction_status";

    private RetirementContract() {
    }

    /**
     * Class for retirement params table.
     */
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
        public static final String COLUMN_BIRTHDATE = "birthdate";
    }

    /**
     * Class for income type table.
     */
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

    /**
     * Class for savings income table.
     */
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
        public static final String COLUMN_BALANCE = "balance";
    }

    /**
     * Class for tax deferred table.
     */
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
        public static final String COLUMN_BALANCE = "balance";
    }

    /**
     * Class for pension income table.
     */
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

    /**
     * Class for pension income table.
     */
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

    /**
     * Class for milestone table.
     */
    public static final class MilestoneEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MILESTONE).build();

        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MILESTONE;
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MILESTONE;

        static final String TABLE_NAME = PATH_MILESTONE;
        public static final String COLUMN_AGE = "ages";
    }

    /**
     * Class for summary table.
     */
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

    /**
     * Class for status table
     */
    public static final class TransactionStatusEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRANSACTION_STATUS).build();
        public static final int STATUS_NONE = 0;
        public static final int STATUS_UPDATED = 1;
        public static final int STATUS_UPDATING = 2;
        public static final int STATUS_ERROR = 3;

        public static final int ACTION_NONE = 0;
        public static final int ACTION_INSERT = 1;
        public static final int ACTION_UPDATE = 2;
        public static final int ACTION_DELETE = 3;

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRANSACTION_STATUS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRANSACTION_STATUS;

        public static final String TABLE_NAME = PATH_TRANSACTION_STATUS;
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_RESULT = "result";
        public static final String COLUMN_ACTION = "action";
        public static final String COLUMN_TYPE = "type";
    }
}
