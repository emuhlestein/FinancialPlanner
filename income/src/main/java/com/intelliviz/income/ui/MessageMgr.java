package com.intelliviz.income.ui;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;

import com.intelliviz.income.R;

public class MessageMgr implements Parcelable {
    private String[] mMessages;
    public static final int EC_NO_ERROR = 0;
    public static final int EC_NO_SPOUSE_BIRTHDATE = 1;
    public static final int EC_ONLY_TWO_SOCIAL_SECURITY_ALLOWED = 2;
    public static final int EC_ONLY_ONE_SOCIAL_SECURITY_ALLOWED = 3;
    public static final int EC_ONLY_ONE_PENSION_ALLOWED = 4;
    public static final int EC_ONLY_TWO_SAVED_ALLOWED = 5;
    public static final int EC_ONLY_ONE_SOCIAL_SECURITY_FOR_SELF = 6;
    public static final int EC_TEN_PERCENT_PENALTY_FOR_WITHDRAWAL = 7;
    public static final int EC_BALANCE_EXHAUSED = 8;
    public static final int EC_BALANCE_EXHAUSED_IN_YEAR = 9;
    public static final int EC_FOR_SELF_OR_SPOUSE = 10;
    public static final int EC_SPOUSE_NOT_SUPPORTED = 11;
    public static final int EC_SPOUSE_INCLUDED = 12;
    public static final int EC_PRINCIPLE_SPOUSE = 13;


/*
   <string name="ec_no_error">No error</string>
    <string name="ec_no_spouse_birthdate">Need to add birth date for spouse before adding social security income source.</string>
    <string name="ec_only_two_social_security_allowed">Can only have two Social Security income sources.</string>
    <string name="ec_only_one_social_security_allowed">Can only have one Social Security income source with free version.</string>
    <string name="ec_only_one_pension_allowed">Can only have one Pension income source with free version.</string>
    <string name="ec_only_two_savings_allowed">Can only have two Savings or 401(k) income source with free version.</string>
    <string name="ec_only_one_social_security_allowed_for_self">You can only have one social security income source for yourself".</string>
    <string name="ec_ten_percent_penalty_for_withdrawal">There is a 10% penalty for early withdrawal.</string>
    <string name="ec_balance_exhausted">Balance has been exhausted. Need to increase savings, reduce initial monthly withdraw or delay retirement."</string>
    <string name="ec_balance_exhausted_in_year">Balance will be exhausted in less than a year"</string>
    */

    public MessageMgr(Application application) {
        mMessages = application.getResources().getStringArray(R.array.error_messages);
    }


    public String getMessage(int messageNo) {
        return mMessages[messageNo];
    }

    public MessageMgr(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(mMessages);
    }

    private void readFromParcel(Parcel in) {
        in.readStringArray(mMessages);
    }

    public static final Parcelable.Creator<MessageMgr> CREATOR = new Parcelable.Creator<MessageMgr>()
    {
        @Override
        public MessageMgr createFromParcel(Parcel in) {
            return new MessageMgr(in);
        }

        @Override
        public MessageMgr[] newArray(int size) {
            return new MessageMgr[size];
        }
    };
}
