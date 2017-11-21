package com.intelliviz.retirementhelper.data;

/**
 * Created by edm on 11/21/2017.
 */

public class PensionData {
    private AgeData mAge;
    private double mBenefit;
    private int mBenefitInfo;

    public PensionData(AgeData age, double benefit, int benefitInfo) {
        mAge = age;
        mBenefit = benefit;
        mBenefitInfo = benefitInfo;
    }

    public AgeData getAge() {
        return mAge;
    }

    public double getBenefit() {
        return mBenefit;
    }

    public int getBenefitInfo() {
        return mBenefitInfo;
    }
}
