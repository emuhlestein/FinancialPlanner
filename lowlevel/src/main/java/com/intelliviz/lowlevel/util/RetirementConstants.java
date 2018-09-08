package com.intelliviz.lowlevel.util;

/**
 * Class for constant values.
 * Created by Ed Muhlestein on 4/13/2017.
 */

public class RetirementConstants {
    // Income types also used as transaction type
    public static final String EXTRA_INCOME_TYPE = "income type";
    public static final int INCOME_TYPE_UNKNOWN = -1;
    public static final int INCOME_TYPE_SAVINGS = 0;
    public static final int INCOME_TYPE_401K = 1; // 401(k)
    public static final int INCOME_TYPE_PENSION = 2;
    public static final int INCOME_TYPE_GOV_PENSION = 3; //eg social security

    public static final String EXTRA_INCOME_SOURCE_ACTION = "income source action";
    public static final int INCOME_ACTION_ADD = 0;
    public static final int INCOME_ACTION_VIEW = 1;
    public static final int INCOME_ACTION_EDIT = 2;
    public static final int INCOME_ACTION_DELETE = 3;
    public static final int INCOME_ACTION_UPDATE = 4;
    public static final int INCOME_ACTION_GET = 5;
    public static final int ACTIVITY_RESULT = 99;

    public static final String RETIREMENT_MODE = "retirement mode";
    public static final String EXTRA_RETIREMENT_REACH_AMOUNT = "retirement reach mount";
    public static final String EXTRA_RETIREMENT_REACH_INCOME_PERCENT = "retirement reach income percent";
    public static final String EXTRA_RETIREMENT_INCOME_SUMMARY_AGE = "retirement income summary age";
    public static final String EXTRA_RETIREMENT_MONTHLY_INCOME = "retirement monthly income";

    public static final int INCOME_SUMMARY_MODE = 0;
    public static final int REACH_AMOUNT_MODE = 1;
    public static final int REACH_IMCOME_PERCENT_MODE = 2;
    public static final int UNKNOWN_MODE = 4;

    public static final int BALANCE_STATE_GOOD = 0;
    public static final int BALANCE_STATE_LOW = 1; // less than a year left
    public static final int BALANCE_STATE_EXHAUSTED = 2;

    // principle reduction mode
    public static final int WITHDRAW_MODE_PERCENT = 0; // percentage of principle
    public static final int WITHDRAW_MODE_AMOUNT = 1; // dollar amount
    public static final int WITHDRAW_MODE_UNKNOWN = 2;

    public static final String EXTRA_INCOME_SOURCE_ID = "income source id";
    public static final String EXTRA_INCOME_SOURCE_TYPE = "income source type";
    public static final String EXTRA_ACTIVITY_RESULT = "activity result";
    public static final String EXTRA_INCOME_SOURCE_NAME = "income source name";
    public static final String EXTRA_INCOME_SOURCE_START_AGE = "income source start age";
    public static final String EXTRA_INCOME_SOURCE_BALANCE = "income source balance";
    public static final String EXTRA_INCOME_SOURCE_INTEREST = "income source interest";
    public static final String EXTRA_INCOME_SOURCE_INCREASE = "income source increase";
    public static final String EXTRA_INCOME_SOURCE_BENEFIT = "income source benefit";
    public static final String EXTRA_INCOME_SOURCE_INCLUDE_SPOUSE = "income source include spouse";
    public static final String EXTRA_INCOME_SOURCE_SPOUSE_BENEFIT = "income source spouse benefit";
    public static final String EXTRA_INCOME_SOURCE_SPOUSE_BIRTHDAY = "income source spouse birthday";
    public static final String EXTRA_INCOME_STOP_MONTHLY_ADDITION_AGE = "stop monthly addtion age";
    public static final String EXTRA_INCOME_WITHDRAW_PERCENT = "withdraw percent";
    public static final String EXTRA_ANNUAL_PERCENT_INCREASE = "annual percent increase";
    public static final String EXTRA_INCOME_MONTHLY_ADDITION = "monthly addition";
    //public static final String EXTRA_INCOME_SELF = "income source self";

    public static final String EXTRA_INCOME_IS_SPOUSE_ENTITY = "include spouse";
    public static final String EXTRA_INCOME_SPOUSE_BENEFIT = "spouse benefit";
    public static final String EXTRA_INCOME_SPOUSE_BIRTHDATE = "spouse birthdate";
    public static final String EXTRA_INCOME_FULL_BENEFIT = "full benefit";
    public static final String EXTRA_INCOME_START_AGE = "start age";
    public static final String EXTRA_INCOME_STOP_AGE = "stop age";
    public static final String EXTRA_INCOME_SHOW_MONTHS = "show months";
    public static final String EXTRA_INCOME_SPOUSE_START_AGE = "spouse start age";
    public static final String EXTRA_INCOME_OWNER = "owner";

    public static final String EXTRA_LOGIN_RESPONSE = "login response";
    public static final String EXTRA_BIRTHDATE = "birthdate";
    public static final String EXTRA_INCLUDE_SPOUSE = "include_spouse";
    public static final String EXTRA_SPOUSE_BIRTHDATE = "spouse_birthdate";

    public static final String EXTRA_INCOME_DATA = "extra income data";
    public static final String EXTRA_RETIREOPTIONS_DATA = "extra retire options data";
    public static final String EXTRA_MILESTONEDATA = "milestonedata";
    public static final String EXTRA_MILESTONEAGE_DATA = "milestoneagedata";
    public static final String EXTRA_DIALOG_MESSAGE = "dialog message";
    public static final String EXTRA_DIALOG_INPUT_TEXT = "dialog input text";
    public static final String EXTRA_DIALOG_SET_CANCELLABLE = "dialog set cancellable";
    public static final String EXTRA_SELECTED_MENU_ITEM = "menu item";
    public static final String EXTRA_MENU_ITEM_LIST = "menu item list";
    public static final String EXTRA_YEAR = "year";
    public static final String EXTRA_MONTH = "month";

    public static final int REQUEST_RETIRE_OPTIONS = 0;
    public static final int REQUEST_PERSONAL_INFO = 1;
    public static final int REQUEST_INCOME_MENU = 6;
    public static final int REQUEST_YES_NO = 7;
    public static final int REQUEST_BIRTHDATE = 8;
    public static final int REQUEST_ADD_AGE = 9;
    public static final int REQUEST_ACTION_MENU = 10;
    public static final int REQUEST_TAX_DEF_INCOME = 11;
    public static final int REQUEST_SPOUSE_BIRTHDATE = 12;
    public static final int REQUEST_SIGN_IN = 13;

    public static final int EC_NO_ERROR = 0;
    public static final int EC_MAX_NUM_SOCIAL_SECURITY = 1;
    public static final int EC_MAX_NUM_SOCIAL_SECURITY_FREE = 2;
    public static final int EC_NO_SPOUSE_BIRTHDATE = 3;
    public static final int EC_PRINCIPLE_SPOUSE = 4;
    public static final int EC_SPOUSE_NOT_SUPPORTED = 5;
    public static final int EC_ONLY_ONE_SUPPORTED = 6;
    public static final int EC_ONLY_TWO_SUPPORTED = 7;
    public static final int EC_FOR_SELF_OR_SPOUSE = 8;
    public static final int EC_SPOUSE_INCLUDED = 9;

    // self
    public static final String EXTRA_OWNER_TYPE = "owner type";
    public static final int OWNER_SPOUSE = 0;    // annotation is spouse
    public static final int OWNER_SELF = 1;      // annotation is self

    public static final String EXTRA_DIALOG_RESPONSE = "dialog response";
}
