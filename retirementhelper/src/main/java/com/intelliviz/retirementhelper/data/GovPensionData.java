package com.intelliviz.retirementhelper.data;

/**
 * Created by edm on 11/17/2017.
 */

public class GovPensionData {
    private AgeData mAge;
    private double mBenefit;
    private int mBenefitInfo;
    private boolean mIncludeSpouse;
    private AgeData mSpouseAge;
    private double mSpouseBenefit;

    public GovPensionData(AgeData age, AgeData spouseAge, boolean includeSpouse) {
        mAge = age;
        mSpouseAge = spouseAge;
        mBenefit = 0;
        mBenefitInfo = 0;
        mSpouseBenefit = 0;
        mIncludeSpouse = includeSpouse;
    }

    public GovPensionData(AgeData age, double benefit, int benefitInfo) {
        mAge = age;
        mBenefit = benefit;
        mBenefitInfo = benefitInfo;
        mIncludeSpouse = false;
        mSpouseAge = null;
        mSpouseBenefit = 0;
    }

    public GovPensionData(AgeData age, double benefit, int benefitInfo,
                           boolean includeSpouse, AgeData spouseAge, double spouseBenefit) {
        mAge = age;
        mBenefit = benefit;
        mBenefitInfo = benefitInfo;
        mIncludeSpouse = includeSpouse;
        mSpouseAge = spouseAge;
        mSpouseBenefit = spouseBenefit;
    }

    public AgeData getAge() {
        return mAge;
    }

    public double getBenefit() {
        return mBenefit;
    }

    public double getSpouseBenefit() {
        return mSpouseBenefit;
    }

    public boolean isIncludeSpouse() {
        return mIncludeSpouse;
    }

    public AgeData getSpouseAge() {
        return mSpouseAge;
    }

    public int getBenefitInfo() {
        return mBenefitInfo;
    }
}
