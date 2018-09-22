package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;

public class RetirementOptions {
    private long id;
    private AgeData mEndAge;
    private String mBirthdate;
    private String mSpouseBirthdate;
    private int mIncludeSpouse;
    private String mCountryCode;
    private boolean mCountryAvailable;

    public RetirementOptions(String birthdate, String spouseBirthdate) {
        mBirthdate = birthdate;
        mSpouseBirthdate = spouseBirthdate;
        id = -1;
        mEndAge = new AgeData(0);
        mIncludeSpouse = 0;
        mCountryCode = "US";
        mCountryAvailable = false;
    }

    public RetirementOptions(long id, AgeData endAge, String birthdate, String spouseBirthdate, int includeSpouse, String countryCode) {
        this.id = id;
        mEndAge = endAge;
        mBirthdate = birthdate;
        mSpouseBirthdate = spouseBirthdate;
        mIncludeSpouse = includeSpouse;
        mCountryCode = countryCode;
        mCountryAvailable = true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AgeData getEndAge() {
        return mEndAge;
    }

    public void setEndAge(AgeData endAge) {
        mEndAge = endAge;
    }

    public String getBirthdate() {
        return mBirthdate;
    }

    public void setBirthdate(String birthdate) {
        mBirthdate = birthdate;
    }

    public String getSpouseBirthdate() {
        return mSpouseBirthdate;
    }

    public void setSpouseBirthdate(String spouseBirthdate) {
        mSpouseBirthdate = spouseBirthdate;
    }

    public int getIncludeSpouse() {
        return mIncludeSpouse;
    }

    public void setIncludeSpouse(int includeSpouse) {
        mIncludeSpouse = includeSpouse;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(String countryCode) {
        mCountryCode = countryCode;
    }

    public void setCountryAvailable(boolean countryAvailable) {
        mCountryAvailable = countryAvailable;
    }

    public boolean isCountryAvailable() {
        return mCountryAvailable;
    }
}
