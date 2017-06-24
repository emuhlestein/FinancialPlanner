package com.intelliviz.retirementhelper.util;

import com.intelliviz.retirementhelper.data.PersonalInfoData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;

/**
 * Created by edm on 6/19/2017.
 */

public class RetirementInfoMgr {
    private String mBirthdate = "0";
    private String mEndAge;
    private int mWithdrawalMode;
    private String mWithdrawlAmount;
    private static RetirementInfoMgr mInstance;

    private RetirementInfoMgr() {
    }

    public static RetirementInfoMgr getInstance() {
        if(mInstance == null) {
            mInstance = new RetirementInfoMgr();
        }
        return mInstance;
    }

    public void setPersonalInfoData(PersonalInfoData pid) {
        if(pid != null) {
            mBirthdate = pid.getBirthdate();
        }
    }

    public void setRetirementInfoData(RetirementOptionsData rod) {
        if(rod != null) {
            mEndAge = rod.getEndAge();
            mWithdrawalMode = rod.getWithdrawMode();
            mWithdrawlAmount = rod.getWithdrawAmount();
        }
    }

    public String getBirthdate() {
        return mBirthdate;
    }

    public void setBirthdate(String birthdate) {
        mBirthdate = birthdate;
    }
}
