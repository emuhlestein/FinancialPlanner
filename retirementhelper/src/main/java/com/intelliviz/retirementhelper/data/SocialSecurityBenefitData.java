package com.intelliviz.retirementhelper.data;

/**
 * Created by edm on 1/13/2018.
 */

public class SocialSecurityBenefitData extends BenefitData {
    private boolean mIncludeSpouse;
    private double mSpouseBenefit;
    private AgeData mSpouseAge;

    public SocialSecurityBenefitData(AgeData age, double monthlyAmount, double balance, int balanceState, boolean penalty,
                                     boolean includeSpouse, double spouseBenefit, AgeData spouseAge) {
        super(age, monthlyAmount, balance, balanceState, penalty);
        mIncludeSpouse = includeSpouse;
        mSpouseBenefit = spouseBenefit;
        mSpouseAge = spouseAge;
    }

    public boolean isIncludeSpouse() {
        return mIncludeSpouse;
    }

    public void setIncludeSpouse(boolean includeSpouse) {
        mIncludeSpouse = includeSpouse;
    }

    public double getSpouseBenefit() {
        return mSpouseBenefit;
    }

    public void setSpouseBenefit(double spouseBenefit) {
        mSpouseBenefit = spouseBenefit;
    }

    public AgeData getSpouseAge() {
        return mSpouseAge;
    }

    public void setSpouseAge(AgeData spouseAge) {
        mSpouseAge = spouseAge;
    }
}
