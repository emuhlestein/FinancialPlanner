package com.intelliviz.retirementhelper.util;

/**
 * Class for constant values.
 * Created by Ed Muhlestein on 4/13/2017.
 */

public class RetirementConstants {
    // Income types also used as transaction type
    public static final int INCOME_TYPE_SAVINGS = 0;
    public static final int INCOME_TYPE_TAX_DEFERRED = 1; // 401(k)
    public static final int INCOME_TYPE_PENSION = 2;
    public static final int INCOME_TYPE_GOV_PENSION = 3; //eg social security
    public static final int TRANS_TYPE_MILESTONE_SUMMARY = 4;

    public static final String EXTRA_INCOME_SOURCE_ACTION = "income source action";
    public static final int INCOME_ACTION_ADD = 0;
    public static final int INCOME_ACTION_VIEW = 1;
    public static final int INCOME_ACTION_EDIT = 2;
    public static final int INCOME_ACTION_DELETE = 3;
    public static final int INCOME_ACTION_UPDATE = 4;
    public static final int INCOME_ACTION_GET = 5;


    public static final String DATE_FORMAT = "dd-MM-yyyy";

    // principle reduction mode
    public static final int WITHDRAW_MODE_AMOUNT = 1; // dollar amount
    public static final int WITHDRAW_MODE_PERCENT = 0; // percentage of principle

    public static final String EXTRA_INCOME_SOURCE_ID = "income source id";
    public static final String EXTRA_INCOME_SOURCE_TYPE = "income source type";

    public static final String EXTRA_LOGIN_RESPONSE = "login response";
    public static final String EXTRA_BIRTHDATE = "birthdate";

    public static final String EXTRA_INCOME_DATA = "extra income data";
    public static final String EXTRA_RETIREOPTIONS_DATA = "extra retire options data";
    public static final String EXTRA_MILESTONEDATA = "milestonedata";
    public static final String EXTRA_MILESTONEAGE_DATA = "milestoneagedata";
    public static final String EXTRA_DIALOG_MESSAGE = "dialog message";
    public static final String EXTRA_DIALOG_INPUT_TEXT = "dialog input text";
    public static final String EXTRA_DIALOG_SET_CANCELLABLE = "dialog set cancellable";
    public static final String EXTRA_SELECTED_MENU_ITEM = "menu item";
    public static final String EXTRA_MENU_ITEM_LIST = "menu item list";

    public static final int REQUEST_RETIRE_OPTIONS = 0;
    public static final int REQUEST_PERSONAL_INFO = 1;
    public static final int REQUEST_INCOME_MENU = 6;
    public static final int REQUEST_YES_NO = 7;
    public static final int REQUEST_BIRTHDATE = 8;
    public static final int REQUEST_ADD_AGE = 9;
    public static final int REQUEST_ACTION_MENU = 10;
}
