package com.intelliviz.retirementhelper.util;

/**
 * Created by edm on 4/13/2017.
 */

public class RetirementConstants {
    public static final int INCOME_TYPE_SAVINGS = 0;
    public static final int INCOME_TYPE_TAX_DEFERRED = 1; // 401(k)
    public static final int INCOME_TYPE_PENSION = 2;
    public static final int INCOME_TYPE_GOV_PENSION = 3; //eg social security


    public static final int INCOME_ACTION_ADD = 0;
    public static final int INCOME_ACTION_VIEW = 1;
    public static final int INCOME_ACTION_EDIT = 2;
    public static final int INCOME_ACTION_DELETE = 3;

    public static final String EXTRA_SERVICE_ACTION = "service action";



    public static final int SERVICE_DATA_SAVINGS = 0;
    public static final int SERVICE_DATA_TAX_DEFERRED = 1;
    public static final int SERVICE_DATA_PENSION = 2;
    public static final int SERVICE_DATA_GOV_PENSION = 3;
    public static final int SERVICE_DATA_PERSONAL = 4;
    public static final int SERVICE_DATA_RETIRE_OPTIONS = 5;

    public static final int MILESTONE_AGE_EDIT = 0;
    public static final int MILESTONE_AGE_DELETE = 1;


    public static final String DATE_FORMAT = "yyyy-MM-dd";

    // principle reduction mode
    public static final int WITHDRAW_MODE_AMOUNT = 0; // dollar amount
    public static final int WITHDRAW_MODE_PERCENT = 1; // percentage of principle

    public static final String EXTRA_INCOME_SOURCE_ID = "income source id";
    public static final String EXTRA_INCOME_SOURCE_NAME = "income source name";
    public static final String EXTRA_INCOME_SOURCE_TYPE = "income source type";
    public static final String EXTRA_INCOME_SOURCE_MONTHLY_BENEFIT = "income source monthly benefit";
    public static final String EXTRA_INCOME_SOURCE_MINIMUM_AGE = "income source minimum age";
    public static final String EXTRA_INCOME_SOURCE_ACTION = "income source action";
    public static final String EXTRA_LOGIN_RESPONSE = "login response";
    public static final String EXTRA_BIRTHDATE = "birthdate";
    public static final String EXTRA_MILESONTE_AGE_ACTION = "milestone age action";

    public static final String EXTRA_INCOME_DATA = "extra income data";
    public static final String EXTRA_RETIREOPTIONS_DATA = "extra retire options data";
    public static final String EXTRA_MILESTONEDATA = "milestonedata";
    public static final String EXTRA_MILESTONEAGE_DATA = "milestoneagedata";
    public static final String EXTRA_DATEDATA = "datedata";
    public static final String EXTRA_BUNDLE = "bundle";
    public static final String EXTRA_AGE_DATA = "age data";
    public static final String EXTRA_ID_DATA = "id data";

    public static final int REQUEST_RETIRE_OPTIONS = 0;
    public static final int REQUEST_PERSONAL_INFO = 1;
    public static final int REQUEST_SAVINGS = 2;
    public static final int REQUEST_TAX_DEFERRED = 3;
    public static final int REQUEST_PENSION = 4;
    public static final int REQUEST_GOV_PENSION = 5;
    public static final int REQUEST_INCOME_MENU = 6;
    public static final int REQUEST_YES_NO = 7;
    public static final int REQUEST_BIRTHDATE = 8;
    public static final int REQUEST_ADD_AGE = 9;
    public static final int REQUEST_ACTION_MENU = 10;

    public static final String LOCAL_SAVINGS = "savingsDataBroadcast";
    public static final String LOCAL_TAX_DEFERRED = "viewTaxDeferredBroadcast";
    public static final String LOCAL_PENSION = "pensionBroadcast";
    public static final String LOCAL_GOV_PENSION = "govPensionBroadcast";
    public static final String LOCAL_RETIRE_OPTIONS = "retireOptionsBroadcast";
    public static final String LOCAL_PERSONAL_DATA = "personalDataBroadcast";
    public static final String LOCAL_MILESTONE_AGE = "milestoneAgeBroadcast";

    public static final String EXTRA_DB_ACTION = "db action";
    public static final String EXTRA_DB_DATA = "db data";
    public static final String EXTRA_DB_PERID = "db perid";
    public static final String EXTRA_DB_ROD = "db rod";
    public static final String EXTRA_DB_EXTRA_DATA = "db extra data";
    public static final String EXTRA_DB_ROWS_UPDATED = "rows updated";
    public static final String EXTRA_DB_ID = "db id";

    public static final int SERVICE_DB_INSERT = 0;
    public static final int SERVICE_DB_QUERY = 1;
    public static final int SERVICE_DB_UPDATE = 2;
    public static final int SERVICE_DB_DELETE = 3;



    public static final String DIALOG_MENU = "DialogIncomeMenu";
    public static final String DIALOG_YES_NO = "DialogYesNo";
    public static final String DIALOG_BIRTHDATE = "DialogBirthdate";
}
