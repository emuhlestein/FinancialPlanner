package com.intelliviz.retirementhelper.data;

import com.intelliviz.retirementhelper.util.SystemUtils;

/**
 * Created by edm on 8/14/2017.
 */

public class SocialSecurityRules implements IncomeTypeRules {
    private static final double MAX_SS_PENALTY = 30.0;
    private int mBirthYear;
    private AgeData mMinAge;
    private AgeData mMaxAge;
    private double mFullMonthlyBenefit;

    public SocialSecurityRules(String birthDate, double fullRetirementBenefit) {
        mBirthYear = SystemUtils.getBirthYear(birthDate);
        mMinAge = new AgeData(62, 0);
        mMaxAge = new AgeData(70, 0);
        mFullMonthlyBenefit = fullRetirementBenefit;
    }

    @Override
    public double getMonthlyBenefitForAge(AgeData startAge) {
        if(startAge.isBefore(mMinAge)) {
            return 0;
        }

        if(mMaxAge.isBefore(startAge)) {
            startAge = mMaxAge;
        }

        AgeData retireAge = getFullRetirementAge(mBirthYear);
        AgeData diffAge = retireAge.subtract(startAge);
        int numMonths = diffAge.getNumberOfMonths();
        if(numMonths > 0) {
            double adjustment = getSocialSecurityAdjustment(mBirthYear, startAge);
            return (1.0 - adjustment) * mFullMonthlyBenefit;
        } else if(numMonths < 0) {
            double adjustment = -getSocialSecurityAdjustment(mBirthYear, startAge);
            adjustment += 1.0;
            return mFullMonthlyBenefit * adjustment;
        } else {
            return mFullMonthlyBenefit;
        }
    }

    @Override
    public AgeData getFullRetirementAge() {
        return getFullRetirementAge(mBirthYear);
    }

    @Override
    public double getFullMonthlyBenefit() {
        return mFullMonthlyBenefit;
    }

    @Override
    public AgeData getMinimumAge() {
        return mMinAge;
    }

    private AgeData getFullRetirementAge(int birthYear) {
        AgeData fullAge;
        if(birthYear <= 1937) {
            fullAge = new AgeData(65, 0);
        } else if(birthYear == 1938) {
            fullAge = new AgeData(65, 2);
        } else if(birthYear == 1939) {
            fullAge = new AgeData(65, 4);
        }else if(birthYear == 1940) {
            fullAge = new AgeData(65, 6);
        } else if(birthYear == 1941) {
            fullAge = new AgeData(65, 8);
        } else if(birthYear == 19342) {
            fullAge = new AgeData(65, 10);
        } else if(birthYear >= 1939 && birthYear < 1955) {
            fullAge = new AgeData(66, 0);
        } else if(birthYear == 1955) {
            fullAge = new AgeData(66, 2);
        } else if(birthYear == 1956) {
            fullAge = new AgeData(66, 4);
        } else if(birthYear == 1957) {
            fullAge = new AgeData(66, 6);
        } else if(birthYear == 1958) {
            fullAge = new AgeData(66, 8);
        } else if(birthYear == 1959) {
            fullAge = new AgeData(66, 10);
        } else {
            fullAge = new AgeData(67, 0);
        }

        return fullAge;
    }

    /**
     * Get the percent credit per year.
     * @param birthyear The birth year.
     * @return THe delayed credit.
     */
    private static double getDelayedCredit(int birthyear) {
        if(birthyear < 1925) {
            return 3;
        } else if(birthyear < 1927) {
            return 3.5;
        } else if(birthyear < 1929) {
            return 4.0;
        } else if(birthyear < 1931) {
            return 4.5;
        } else if(birthyear < 1933 ) {
            return 5.0;
        } else if(birthyear < 1935) {
            return 5.5;
        } else if(birthyear < 1937) {
            return 6.0;
        } else if(birthyear < 1939) {
            return 6.5;
        } else if(birthyear < 1941) {
            return 7.0;
        } else if(birthyear < 1943) {
            return 7.5;
        } else {
            return 8.0; // the max
        }
    }

    private double getSocialSecurityAdjustment(int birthYear, AgeData startAge) {
        AgeData retireAge = getFullRetirementAge(birthYear);
        AgeData diffAge = retireAge.subtract(startAge);
        int numMonths = diffAge.getNumberOfMonths();
        if(numMonths > 0) {
            // this is early retirement; the adjustment will be a penalty.
            double penalty = 0;
            if(numMonths < 37) {
                penalty = ((numMonths * 5.0) / 9.0)/100;
                return penalty;
            } else {
                penalty = ((36 * 5.0) / 9.0)/100;
                penalty += (((numMonths-36) * 5.0) / 12.0)/100;
                if(penalty > MAX_SS_PENALTY) {
                    penalty = MAX_SS_PENALTY;
                }
                return penalty;
            }
        } else if(numMonths < 0) {
            // this is delayed retirement; the adjustment is a credit.
            double annualCredit = getDelayedCredit(birthYear);
            return (numMonths * (annualCredit / 12.0))/100;
        } else {
            return 0; // exact retirement age
        }
    }
}
