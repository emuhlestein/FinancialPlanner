package com.intelliviz.retirementhelper.data;

import android.os.Bundle;

import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_FULL_BENEFIT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_INCLUDE_SPOUSE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SPOUSE_BENEFIT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SPOUSE_BIRTHDATE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_START_AGE;

/**
 * Created by edm on 8/14/2017.
 */

public class SocialSecurityRules implements IncomeTypeRules {
    private static final double MAX_SS_PENALTY = 30.0;
    private String mBirthdate;
    private AgeData mMinAge;
    private AgeData mMaxAge;
    private AgeData mEndAge;
    private AgeData mStartAge;
    private double mFullMonthlyBenefit;
    private boolean mIncludeSpouse;
    private double mSpouseFullBenefit;
    private String mSpouseBirthdate;

    public SocialSecurityRules(String birthDate, AgeData endAge) {
        mBirthdate = birthDate;
        mMinAge = new AgeData(62, 0);
        mMaxAge = new AgeData(70, 0);
        mEndAge = endAge;
    }

    @Override
    public void setValues(Bundle bundle) {
        mFullMonthlyBenefit = bundle.getDouble(EXTRA_INCOME_FULL_BENEFIT);
        mStartAge = bundle.getParcelable(EXTRA_INCOME_START_AGE);
        mIncludeSpouse = bundle.getBoolean(EXTRA_INCOME_INCLUDE_SPOUSE, false);
        mSpouseFullBenefit = bundle.getDouble(EXTRA_INCOME_SPOUSE_BENEFIT);
        mSpouseBirthdate = bundle.getString(EXTRA_INCOME_SPOUSE_BIRTHDATE);
    }

    @Override
    public List<AgeData> getAges() {
        int birthyear = SystemUtils.getBirthYear(mBirthdate);
        AgeData retireAge = getFullRetirementAgeFromYear(birthyear);
        if(mIncludeSpouse) {
            birthyear = SystemUtils.getBirthYear(mSpouseBirthdate);
            AgeData spouseRetireAge = getFullRetirementAgeFromYear(birthyear);
            AgeData age1 = SystemUtils.getAge(mBirthdate, mSpouseBirthdate, spouseRetireAge);
            return new ArrayList<>(Arrays.asList(mMinAge, retireAge, age1, mMaxAge));
        } else {
            return new ArrayList<>(Arrays.asList(mMinAge, retireAge, mMaxAge));
        }
    }

    @Override
    public MilestoneData getMilestone(AgeData age) {
        double monthlyBenefit = getMonthlyBenefitForAge(age).getBenefit();
        return new MilestoneData(age, mEndAge, mMinAge, monthlyBenefit, 0, 0, 0, 0, 0, 0, 0);
    }

    @Override
    public List<BenefitData> getBenefitData() {
        AgeData age = mMinAge;
        List<BenefitData> listAmountDate = new ArrayList<>();

        int birthYear = SystemUtils.getBirthYear(mBirthdate);
        double amount = getMonthlyBenefit(age, birthYear, mFullMonthlyBenefit);

        if(mStartAge.isBefore(mEndAge)) {
            age = mMinAge;
        } else {
            age = mStartAge;
        }

        BenefitData benefitData = new BenefitData(age, amount, 0, 0, false);
        listAmountDate.add(benefitData);

        while(true) {
            // get next age
            AgeData nextAge = new AgeData(age.getYear()+1, 0);
            if(nextAge.isAfter(mEndAge)) {
                break;
            }

            age = new AgeData(nextAge.getYear(), 0);

            benefitData = new BenefitData(nextAge, amount, 0, 0, false);
            listAmountDate.add(benefitData);
        }

        return listAmountDate;
    }

    @Override
    public BenefitData getBenefitForAge(AgeData age) {
        return null;
    }

    public AgeData getFullRetirementAge() {
        int birthyear = SystemUtils.getBirthYear(mBirthdate);
        return getFullRetirementAgeFromYear(birthyear);
    }

    public AgeData getFullRetirementAge(String birthdate) {
        int birthyear = SystemUtils.getBirthYear(birthdate);
        return getFullRetirementAgeFromYear(birthyear);
    }

    public GovPensionData getMonthlyBenefitForAge(AgeData startAge) {

        if(mIncludeSpouse) {
            if(includeSpousalBenefits()) {
                return calculateSpousalBenefits(startAge);
            }

            int birthYear = SystemUtils.getBirthYear(mBirthdate);
            AgeData retireAge = getFullRetirementAgeFromYear(birthYear);

            AgeData spouseStartAge = SystemUtils.getSpouseAge(mBirthdate, mSpouseBirthdate, startAge);
            int spouseBirthyear = SystemUtils.getBirthYear(mSpouseBirthdate);

            double monthlySpouseBenefit;
            double monthlyBenefit;
            int benefitInfo;
            if(startAge.isBefore(retireAge)) {
                benefitInfo = 1;
            } else {
                benefitInfo = 2;
            }

            monthlySpouseBenefit = getMonthlyBenefit(spouseStartAge, spouseBirthyear, mSpouseFullBenefit);
            monthlyBenefit = getMonthlyBenefit(startAge, birthYear, mFullMonthlyBenefit);

            return new GovPensionData(startAge, monthlyBenefit, benefitInfo, true, spouseStartAge, monthlySpouseBenefit);
        } else {
            if(startAge.isBefore(mMinAge)) {
                return new GovPensionData(startAge, 0d, 0);
            } else {
                if(mMaxAge.isBefore(startAge)) {
                    startAge = mMaxAge;
                }
                int birthYear = SystemUtils.getBirthYear(mBirthdate);
                AgeData retireAge = getFullRetirementAgeFromYear(birthYear);
                double monthlyBenefit = getMonthlyBenefit(startAge, birthYear, mFullMonthlyBenefit);
                int benefitInfo;
                if(startAge.isBefore(retireAge)) {
                    benefitInfo = 1;
                } else {
                    benefitInfo = 2;
                }
                return new GovPensionData(startAge, monthlyBenefit, benefitInfo);
            }
        }
    }

    /**
     * Calculate spousal benefits. If either spouse makes less than one half the benefit of the other
     * spouse, there are spousal benefits. There are no spousal benefits if both spouses make more than
     * one half the benefit of the other spouse.
     *
     * Spousal benefits cannot be taken until the other spouse starts taking social security.
     *
     * 1) If divorced can still get spousal benefits.
     * 2) Claiming spousal benefits early (before spouse full retirement age) reduces the benefit amount.
     * 3) Waiting past full retirement age will not increase benefit amount.
     * 4) You can get spousal benefits if you've never worked; if your benefit amount is 0;
     *
     * @return The government pension data. null if there are no spousal benefits.
     */
    private GovPensionData calculateSpousalBenefits(AgeData startAge) {
        AgeData spouseStartAge = SystemUtils.getSpouseAge(mBirthdate, mSpouseBirthdate, startAge);

        if(startAge.isBefore(mMinAge)) {
            return new GovPensionData(startAge, 0, 0, true, spouseStartAge, 0);
        }
        int birthYear = SystemUtils.getBirthYear(mBirthdate);
        AgeData retireAge = getFullRetirementAgeFromYear(birthYear);


        int spouseBirthyear = SystemUtils.getBirthYear(mSpouseBirthdate);
        AgeData spouseRetireAge = getFullRetirementAgeFromYear(spouseBirthyear);

        double monthlySpouseBenefit;
        double monthlyBenefit;
        int benefitInfo;

        if(mSpouseFullBenefit < mFullMonthlyBenefit / 2) {
            double spouseMaxBenefit = mFullMonthlyBenefit / 2;

            if(spouseStartAge.isBefore(spouseRetireAge)) {
                monthlySpouseBenefit = getMonthlyBenefit(startAge, spouseBirthyear, spouseMaxBenefit);
            } else {
                monthlySpouseBenefit = spouseMaxBenefit;
            }
            if(startAge.isBefore(retireAge)) {
                benefitInfo = 1;
            } else {
                benefitInfo = 2;
            }
            monthlyBenefit = getMonthlyBenefit(startAge, birthYear, mFullMonthlyBenefit);
            return new GovPensionData(startAge, monthlyBenefit, benefitInfo, true, spouseStartAge, monthlySpouseBenefit);
        } else if (mFullMonthlyBenefit < mSpouseFullBenefit / 2){
            double maxBenefit = mSpouseFullBenefit / 2;
            if(spouseStartAge.isBefore(spouseRetireAge)) {
                monthlySpouseBenefit = getMonthlyBenefit(startAge, spouseBirthyear, mSpouseFullBenefit);
                benefitInfo = 1;
            } else {
                monthlySpouseBenefit = mSpouseFullBenefit;
                benefitInfo = 2;
            }
            if(startAge.isBefore(retireAge)) {
                monthlyBenefit = getMonthlyBenefit(startAge, birthYear, maxBenefit);
            } else {
                monthlyBenefit = mSpouseFullBenefit / 2;
            }
            return new GovPensionData(startAge, monthlyBenefit, benefitInfo, true, spouseStartAge, monthlySpouseBenefit);
        } else {
            return null; // no spousal benefits
        }
    }

    private boolean includeSpousalBenefits() {
        if((mSpouseFullBenefit < mFullMonthlyBenefit / 2) ||
           (mFullMonthlyBenefit < mSpouseFullBenefit / 2)) {
            return true;
        } else {
            return false;
        }
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
        int numMonths = retireAge.diff(startAge);
        if(startAge.isBefore(retireAge)) {
            // this is early retirement; the adjustment will be a penalty.
            double penalty;
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
        } else {
            // this is delayed retirement; the adjustment is a credit.
            double annualCredit = getDelayedCredit(birthYear);
            return (numMonths * (annualCredit / 12.0))/100;
        }
    }

    private double getMonthlyBenefit(AgeData startAge, int birthYear, double monthlyBenefit) {
        AgeData retireAge = getFullRetirementAgeFromYear(birthYear);
        if(startAge.isBefore(retireAge)) {
            double adjustment = getSocialSecurityAdjustment(birthYear, startAge);
            return (1.0 - adjustment) * monthlyBenefit;
        } else {
            double adjustment = getSocialSecurityAdjustment(birthYear, startAge);
            adjustment += 1.0;
            return monthlyBenefit * adjustment;
        }
    }
}
