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
    public static final int INCOME_ACTION_VIEW = 0;
    public static final int INCOME_ACTION_EDIT = 1;
    public static final int INCOME_ACTION_DELETE = 2;

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

    public static final String EXTRA_RETIRE_PARMS_START_AGE = "retire parms start age";
    public static final String EXTRA_RETIRE_PARMS_END_AGE = "retire parms end age";
    public static final String EXTRA_RETIRE_PARMS_WITHDRAW_MODE = "retire parms withdraw mode";
    public static final String EXTRA_RETIRE_PARMS_WITHDRAW_PERCENT = "retire parms percent";
    public static final String EXTRA_RETIRE_PARMS_INCLUDE_INFLAT = "retire parms include inflation";
    public static final String EXTRA_RETIRE_PARMS_INFLAT_AMOUNT = "retire parms inflate amount";
    public static final String DEFAULT_START_AGE = "62";
    public static final String DEFAULT_END_AGE = "90";
    public static final String EXTRA_RETIRMENTOPTIONSDATA = "retirementoptionsdata";
}
