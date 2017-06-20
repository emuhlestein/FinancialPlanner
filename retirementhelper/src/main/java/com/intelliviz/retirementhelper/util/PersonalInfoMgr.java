package com.intelliviz.retirementhelper.util;

/**
 * Created by edm on 6/19/2017.
 */

public class PersonalInfoMgr {
    private String mBirthdate = "0";
    private static PersonalInfoMgr mInstance;

    private PersonalInfoMgr() {
    }

    public static PersonalInfoMgr getmInstance() {
        if(mInstance == null) {
            mInstance = new PersonalInfoMgr();
        }
        return mInstance;
    }

    public String getBirthdate() {
        return mBirthdate;
    }

    public void setBirthdate(String birthdate) {
        mBirthdate = birthdate;
    }

}
