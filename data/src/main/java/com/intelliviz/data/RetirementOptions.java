package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;

public class RetirementOptions {
    private long id;
    private AgeData mEndAge;
    private String mPrimaryBirthdate;
    private String mSpouseBirthdate;
    private int mIncludeSpouse;
    private String mCountryCode;
    private boolean mCountryAvailable;

    /**
     * Constructor for unit tests.
     * @param primaryBirthdate Birthdate of primary (or self).
     * @param spouseBirthdate Birhtdate of spouse if included.
     * @param endAge End age of primary.
     */
    public RetirementOptions(AgeData endAge, String primaryBirthdate, String spouseBirthdate) {
        mEndAge = endAge;
        mPrimaryBirthdate = primaryBirthdate;
        mSpouseBirthdate = spouseBirthdate;
        id = -1;
        mIncludeSpouse = 0;
        mCountryCode = "US";
        mCountryAvailable = false;
    }

    /**
     * Constructor
     * @param id Database id. -1 if it's a new record.
     * @param endAge End age of primary.
     * @param primaryBirthdate Birthdate of primary (or self).
     * @param spouseBirthdate Birthdate of spouse if included.
     * @param includeSpouse Flag for if spouse is included. Is 1 if included, otherwise, 0.
     * @param countryCode Country code.
     */
    public RetirementOptions(long id, AgeData endAge, String primaryBirthdate, String spouseBirthdate, int includeSpouse, String countryCode) {
        this.id = id;
        mEndAge = endAge;
        mPrimaryBirthdate = primaryBirthdate;
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

    public String getPrimaryBirthdate() {
        return mPrimaryBirthdate;
    }

    public void setPrimaryBirthdate(String birthdate) {
        mPrimaryBirthdate = birthdate;
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
