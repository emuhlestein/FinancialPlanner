package com.intelliviz.retirementhelper.data;

import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by edm on 8/14/2017.
 */

public class SocialSecurityRules implements IncomeTypeRules {
    private static final double MAX_SS_PENALTY = 30.0;
    private String mBirthdate;
    private AgeData mMinAge;
    private AgeData mMaxAge;
    private AgeData mEndAge;
    private double mFullMonthlyBenefit;
    private boolean mIncludeSpouse;
    private double mSpouseFullBenefit;
    private String mSpouseBirthdate;

    public SocialSecurityRules(String birthDate, AgeData endAge, double fullRetirementBenefit,
                               int includeSpouse, double spouseBenefit, String spouseBirthdate) {
        mBirthdate = birthDate;
        //mBirthYear = SystemUtils.getBirthYear(birthDate);
        mMinAge = new AgeData(62, 0);
        mMaxAge = new AgeData(70, 0);
        mEndAge = endAge;
        mFullMonthlyBenefit = fullRetirementBenefit;
        mIncludeSpouse = includeSpouse == 1;
        mSpouseFullBenefit = spouseBenefit;
        mSpouseBirthdate = spouseBirthdate;
    }

    private double getMonthlyBenefitForAge(AgeData startAge) {
        if(startAge.isBefore(mMinAge)) {
            return 0;
        }

        if(mMaxAge.isBefore(startAge)) {
            startAge = mMaxAge;
        }

        int birthYear = SystemUtils.getBirthYear(mBirthdate);
        AgeData retireAge = getFullRetirementAgeFromYear(birthYear);
        double monthlyBenefit = getMonthlyBenefit(startAge, birthYear, retireAge, mFullMonthlyBenefit);
        double monthlySpouseBenefit;

        if(mIncludeSpouse) {
            int spouseBirthyear = SystemUtils.getBirthYear(mSpouseBirthdate);
            AgeData spouseRetireAge = getFullRetirementAgeFromYear(spouseBirthyear);
            if(mSpouseFullBenefit < mFullMonthlyBenefit / 2) {
                if(startAge.isBefore(spouseRetireAge)) {
                    monthlySpouseBenefit = getMonthlyBenefit(startAge, birthYear, spouseRetireAge, mSpouseFullBenefit);
                } else {
                    monthlySpouseBenefit = mFullMonthlyBenefit / 2;
                }
                return monthlySpouseBenefit + monthlyBenefit;
            } else if (mFullMonthlyBenefit < mSpouseFullBenefit / 2){
                monthlySpouseBenefit = getMonthlyBenefit(startAge, birthYear, retireAge, mSpouseFullBenefit);
                if(startAge.isBefore(retireAge)) {
                    monthlyBenefit = getMonthlyBenefit(startAge, birthYear, spouseRetireAge, mFullMonthlyBenefit);
                } else {
                    monthlyBenefit = mSpouseFullBenefit / 2;
                }
                return monthlySpouseBenefit + monthlyBenefit;
            } else {
                monthlyBenefit = getMonthlyBenefit(startAge, birthYear, retireAge, mFullMonthlyBenefit);
                monthlySpouseBenefit = getMonthlyBenefit(startAge, birthYear, spouseRetireAge, mSpouseFullBenefit);
                return monthlyBenefit + monthlySpouseBenefit;
            }
        } else {
            return monthlyBenefit;
        }
    }

    @Override
    public List<AgeData> getAges() {
        int birthyear = SystemUtils.getBirthYear(mBirthdate);
        AgeData retireAge = getFullRetirementAgeFromYear(birthyear);
        if(mIncludeSpouse) {
            birthyear = SystemUtils.getBirthYear(mSpouseBirthdate);
            AgeData spouseRetireAge = getFullRetirementAgeFromYear(birthyear);
            return new ArrayList<>(Arrays.asList(mMinAge, retireAge, spouseRetireAge, mMaxAge));
        } else {
            return new ArrayList<>(Arrays.asList(mMinAge, retireAge, mMaxAge));
        }
    }

    @Override
    public MilestoneData getMilestone(AgeData age) {
        double monthlyBenefit = getMonthlyBenefitForAge(age);
        return new MilestoneData(age, mEndAge, mMinAge, monthlyBenefit, 0, 0, 0, 0, 0, 0, 0);
    }

    public AgeData getFullRetirementAge() {
        int birthyear = SystemUtils.getBirthYear(mBirthdate);
        return getFullRetirementAgeFromYear(birthyear);
    }

    public AgeData getFullRetirementAge(String birthdate) {
        int birthyear = SystemUtils.getBirthYear(birthdate);
        return getFullRetirementAgeFromYear(birthyear);
    }

    private AgeData getFullRetirementAgeFromYear(int birthYear) {
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
        AgeData retireAge = getFullRetirementAgeFromYear(birthYear);
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

    private double getMonthlyBenefit(AgeData startAge, int birthYear, AgeData retireAge, double monthlyBenefit) {
        AgeData diffAge = retireAge.subtract(startAge);
        int numMonths = diffAge.getNumberOfMonths();
        if(numMonths > 0) {
            double adjustment = getSocialSecurityAdjustment(birthYear, startAge);
            return (1.0 - adjustment) * monthlyBenefit;
        } else if(numMonths < 0) {
            double adjustment = -getSocialSecurityAdjustment(birthYear, startAge);
            adjustment += 1.0;
            return monthlyBenefit * adjustment;
        } else {
            return monthlyBenefit;
        }
    }
}
