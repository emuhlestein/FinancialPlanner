package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;

public class RetirementOptions {
    private long id;
    private AgeData mEndAge;
    private String mBirthdate;
    private String mSpouseBirthdate;
    private int mIncludeSpouse;

    public RetirementOptions(long id, AgeData endAge, String birthdate, String spouseBirthdate, int includeSpouse) {
        this.id = id;
        mEndAge = endAge;
        mBirthdate = birthdate;
        mSpouseBirthdate = spouseBirthdate;
        mIncludeSpouse = includeSpouse;
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
}
