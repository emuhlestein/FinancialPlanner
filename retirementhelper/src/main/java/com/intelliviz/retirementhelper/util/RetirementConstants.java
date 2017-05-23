package com.intelliviz.retirementhelper.util;

/**
 * Created by edm on 4/13/2017.
 */

public class RetirementConstants {
    public static final int INCOME_TYPE_SAVINGS = 0;
    public static final int INCOME_TYPE_TAX_DEFERRED = 1; // 401(k)
    public static final int INCOME_TYPE_PENSION = 2;
    public static final int INCOME_TYPE_GOV_PENSION = 3; //eg social security
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final int INCOME_ACTION_ADD = 0;
    public static final int INCOME_ACTION_VIEW = 1;
    public static final int INCOME_ACTION_EDIT = 2;
    public static final int INCOME_ACTION_DELETE = 3;

    // principle reduction mode
    public static final int WITHDRAW_MODE_ZERO_PRI = 0; // zero principle  by end age
    public static final int WITHDRAW_MODE_NO_REDUC = 1; // no principle reduction
    public static final int WITHDRAW_MODE_PERCENT = 2; // percentage of principle

    public static final String EXTRA_INCOME_SOURCE_ID = "income source id";
    public static final String EXTRA_INCOME_SOURCE_NAME = "income source name";
    public static final String EXTRA_INCOME_SOURCE_TYPE = "income source type";
    public static final String EXTRA_INCOME_SOURCE_BALANCE = "income source balance";
    public static final String EXTRA_INCOME_SOURCE_BALANCE_DATE = "income source balance date";
    public static final String EXTRA_INCOME_SOURCE_MONTHLY_INCREASE = "income source monthly increase";
    public static final String EXTRA_INCOME_SOURCE_MONTHLY_BENEFIT = "income source monthly benefit";
    public static final String EXTRA_INCOME_SOURCE_MINIMUM_AGE = "income source minimum age";
    public static final String EXTRA_INCOME_SOURCE_PENALTY_AMOUNT = "income source penalty amount";
    public static final String EXTRA_INCOME_SOURCE_INTEREST = "income source interest";
    public static final String EXTRA_INCOME_SOURCE_ACTION = "income source action";

    public static final String EXTRA_INCOME_SAVINGS = "extra income savings";

    public static final String EXTRA_RETIRMENTOPTIONSDATA = "retirementoptionsdata";
    public static final String EXTRA_PERSONALINFODATA = "personalinfodata";
    public static final String EXTRA_DATEDATA = "datedata";

    public static final int REQUEST_RETIRE_OPTIONS = 0;
    public static final int REQUEST_PERSONAL_INFO = 1;
    public static final int REQUEST_SAVINGS = 2;
    public static final int REQUEST_TAX_DEFERRED = 3;
    public static final int REQUEST_PENSION = 4;
    public static final int REQUEST_GOV_PENSION = 5;
}
