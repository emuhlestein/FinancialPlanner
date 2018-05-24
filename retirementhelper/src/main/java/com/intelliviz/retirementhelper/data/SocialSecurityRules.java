package com.intelliviz.retirementhelper.data;

import android.os.Bundle;

import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_FULL_BENEFIT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_START_AGE;

/**
 * There can be a maximum of two instances of this class: one for the principle spouse and one for
 * the second spouse. If this is a spouse benefit, need to swap birthdate,
 * start age and full benefit.
 *
 * Benefits are reduced by 5/9 of 1% per month benefits are taken early, ie before full retirement
 * age. This applies for the first 36 months. If benefits are taken more than 36 months early, the
 * monthly penalty is 5/12 of 1%.
 *
 * Credit for delaying benefits is 2/3 of 1% or 8% per year, up until age 70.
 *
 * Created by Ed Muhlestein on 8/14/2017.
 */

public class SocialSecurityRules implements IncomeTypeRules {
    private static final BigDecimal MAX_SS_PENALTY = new BigDecimal(30.0);
    private String mBirthdate;
    private AgeData mMinAge = new AgeData(62, 0);
    private AgeData mMaxAge = new AgeData(70, 0);
    private AgeData mEndAge;
    private AgeData mStartAge;
    private BigDecimal mFullMonthlyBenefit;
    private BigDecimal mMonthlyBenefit;
    private int mBenefitInfo;
    private BigDecimal mSpouseFullBenefit;
    private String mSpouseBirthdate;
    private AgeData mSpouseStartAge;
    private boolean mIsSpouseIncluded;
    private boolean mIsSpouseEntity;
    private BigDecimal mPenaltyFraction;

    public SocialSecurityRules(AgeData endAge, String birthDate) {
        this(endAge, birthDate, null, "0", null, false, false);
    }

    public SocialSecurityRules(AgeData endAge, String birthDate, String spouseBirthdate, String spouseFullBenefit,
                               AgeData spouseStartAge, boolean isSpouseIncluded, boolean isSpouseEntity) {
        mEndAge = endAge;
        mBirthdate = birthDate;
        mSpouseBirthdate = spouseBirthdate;
        if(spouseFullBenefit != null) {
            mSpouseFullBenefit = new BigDecimal(spouseFullBenefit);
        }
        mSpouseStartAge = spouseStartAge;
        mIsSpouseIncluded = isSpouseIncluded;
        mIsSpouseEntity = isSpouseEntity;

        BigDecimal five = new BigDecimal("5");
        BigDecimal nine = new BigDecimal("9");
        MathContext mc = new MathContext(4, RoundingMode.HALF_UP);
        mPenaltyFraction = five.divide(nine, mc);
    }

    @Override
    public void setValues(Bundle bundle) {
        String value = bundle.getString(EXTRA_INCOME_FULL_BENEFIT);
        mFullMonthlyBenefit = new BigDecimal(value);

        mStartAge = bundle.getParcelable(EXTRA_INCOME_START_AGE);

        int birthYear = SystemUtils.getBirthYear(mBirthdate);
        if(mIsSpouseIncluded) {
            BigDecimal two = new BigDecimal(2);
            MathContext mc = new MathContext(4, RoundingMode.HALF_UP);
            BigDecimal halfSpouseBenefit = mSpouseFullBenefit.divide(two, mc);
            if (mFullMonthlyBenefit.compareTo(halfSpouseBenefit) < 0) {
                mMonthlyBenefit = halfSpouseBenefit;

                // since this is spousal benefits, can't claim social security before principle spouse
                //if(mStartAge.isBefore(mSpouseStartAge)) {
                //    mStartAge = mSpouseStartAge;
                //}
                mMonthlyBenefit = getMonthlyBenefit(mStartAge, birthYear, mMonthlyBenefit, true);
            } else {
                mMonthlyBenefit = getMonthlyBenefit(mStartAge, birthYear, mFullMonthlyBenefit, false);
            }
        } else {
            mMonthlyBenefit = getMonthlyBenefit(mStartAge, birthYear, mFullMonthlyBenefit, false);
        }
    }

    @Override
    public List<BenefitData> getBenefitData() {
        AgeData age;
        if(mIsSpouseEntity) {
            // need to convert age to be in terms of principle spouse age.
            age = SystemUtils.getSpouseAge(mSpouseBirthdate, mBirthdate, mSpouseStartAge);
        } else {
            age = mStartAge;
        }
        age = mStartAge;
        age = new AgeData(age.getYear(), 0);
        List<BenefitData> listAmountDate = new ArrayList<>();

        BenefitData benefitData;
        while(true) {
            if(age.equals(mStartAge) || age.isAfter(mStartAge)) {
                benefitData = new BenefitData(age, mMonthlyBenefit.doubleValue(), 0, 0, false);
            } else {
                benefitData = new BenefitData(age, 0, 0, 0, false);
            }
            listAmountDate.add(benefitData);

            // get next age
            AgeData nextAge = new AgeData(age.getYear()+1, 0);
            if(nextAge.isAfter(mEndAge)) {
                break;
            }

            age = new AgeData(nextAge.getYear(), 0);
        }

        return listAmountDate;
    }

    @Override
    public BenefitData getBenefitData(BenefitData benefitData) {
        return null;
    }

    public double getMonthlyBenefit() {
        return mMonthlyBenefit.doubleValue();
    }

    public AgeData getFullRetirementAge() {
        int birthyear = SystemUtils.getBirthYear(mBirthdate);
        return getFullRetirementAgeFromYear(birthyear);
    }

    public AgeData getFullRetirementAge(String birthdate) {
        int birthyear = SystemUtils.getBirthYear(birthdate);
        return getFullRetirementAgeFromYear(birthyear);
    }

    public double getSpousalMonthlyBenefit() {
        return 0;
    }

    /**
     * Get the monthly benefit for the specified age.
     * @param principleSpouseAge The age of the principle spouse.
     * @return The benefit data.
     */
    public BenefitData getMonthlyBenefitForAge(AgeData principleSpouseAge) {

        AgeData age;
        if(mIsSpouseEntity) {
            // need to convert age to be in terms of principle spouse age.
            age = SystemUtils.getSpouseAge(mSpouseBirthdate, mBirthdate, principleSpouseAge);
        } else {
            age = principleSpouseAge;
        }

        if(age.equals(mStartAge) || age.isAfter(mStartAge)) {
            return new BenefitData(age, mMonthlyBenefit.doubleValue(), 0, 0, false);
        } else {
            return new BenefitData(age, 0, 0, 0, false);
        }
    }

    public static void setRulesOnGovPensionEntities(List<GovPensionEntity> gpeList, RetirementOptionsEntity roe) {
        if(gpeList == null || gpeList.isEmpty()) {
            return;
        }
        String birthdate = roe.getBirthdate();
        AgeData endAge = roe.getEndAge();
        if(gpeList.size() == 1) {
            GovPensionEntity spouse1 = gpeList.get(0);
            SocialSecurityRules ssr = new SocialSecurityRules(endAge, birthdate, "", "0", null, false, false);
            spouse1.setRules(ssr);
        } else if(gpeList.size() == 2) {
            GovPensionEntity principleSpouse;
            GovPensionEntity spouse;
            if(gpeList.get(0).getSpouse() == 0) {
                principleSpouse = gpeList.get(0);
                spouse = gpeList.get(1);
            } else {
                principleSpouse = gpeList.get(1);
                spouse = gpeList.get(0);
            }

            SocialSecurityRules ssr = new SocialSecurityRules(endAge, birthdate, roe.getSpouseBirthdate(),
                    spouse.getFullMonthlyBenefit(), spouse.getStartAge(), true, false);
            principleSpouse.setRules(ssr);
            principleSpouse.setPrincipleSpouse(true);
            ssr = new SocialSecurityRules(endAge, roe.getSpouseBirthdate(), birthdate,
                    principleSpouse.getFullMonthlyBenefit(), principleSpouse.getStartAge(), true, true);
            spouse.setRules(ssr);
        }
    }

    public static AgeData getFullRetirementAgeFromYear(int birthYear) {
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
    private static BigDecimal getDelayedCredit(int birthyear) {
        if(birthyear < 1925) {
            return new BigDecimal("3");
        } else if(birthyear < 1927) {
            return new BigDecimal("3.5");
        } else if(birthyear < 1929) {
            return new BigDecimal("4.0");
        } else if(birthyear < 1931) {
            return new BigDecimal("4.5");
        } else if(birthyear < 1933 ) {
            return new BigDecimal("5.0");
        } else if(birthyear < 1935) {
            return new BigDecimal("5.5");
        } else if(birthyear < 1937) {
            return new BigDecimal("6.0");
        } else if(birthyear < 1939) {
            return new BigDecimal("6.5");
        } else if(birthyear < 1941) {
            return new BigDecimal("7.0");
        } else if(birthyear < 1943) {
            return new BigDecimal("7.5");
        } else {
            return new BigDecimal("8.0"); // the max
        }
    }

    private BigDecimal getMonthlyBenefit(AgeData startAge, int birthYear, BigDecimal monthlyBenefit, boolean spousalBenefit) {
        AgeData retireAge = getFullRetirementAgeFromYear(birthYear);
        if(startAge.isBefore(mMinAge)) {
            return new BigDecimal("0");
        } else if(startAge.diff(retireAge) == 0) {
            return mFullMonthlyBenefit;
        } else if(startAge.isBefore(retireAge)) {
            BigDecimal adjustment = getSocialSecurityAdjustment(birthYear, startAge);
            BigDecimal one = new BigDecimal(1);
            BigDecimal temp = one.subtract(adjustment);
            return temp.multiply(monthlyBenefit);
        } else {
            if(spousalBenefit) {
                return monthlyBenefit;
            } else {
                BigDecimal adjustment = getSocialSecurityAdjustment(birthYear, startAge);
                BigDecimal one = new BigDecimal(1);
                BigDecimal temp = adjustment.add(one);
                return temp.multiply(monthlyBenefit);
            }
        }
    }

    private BigDecimal getSocialSecurityAdjustment(int birthYear, AgeData startAge) {
        AgeData retireAge = getFullRetirementAgeFromYear(birthYear);
        int numMonths = retireAge.diff(startAge);
        if(startAge.isBefore(retireAge)) {
            // this is early retirement; the adjustment will be a penalty.
            BigDecimal penalty;
            if(numMonths < 37) {
                return calculatePenalty(numMonths);
            } else {
                penalty = calculatePenalty(36);
                BigDecimal alternatePenalty = calculateAlternatePenalty(numMonths-36);
                BigDecimal penaltySum = penalty.add(alternatePenalty);
                //penalty += (((numMonths-36) * 5.0) / 12.0)/100;
                int comp = penaltySum.compareTo(MAX_SS_PENALTY);
                if(comp > 0) {
                    penaltySum = MAX_SS_PENALTY;
                }
                return penaltySum;
            }
        } else {
            // this is delayed retirement; the adjustment is a credit.
            if(startAge.isAfter(mMaxAge)) {
                startAge = mMaxAge;
                numMonths = retireAge.diff(startAge);
            }

            BigDecimal annualCredit = getDelayedCredit(birthYear);
            BigDecimal twelve = new BigDecimal(12);
            MathContext mc = new MathContext(4, RoundingMode.HALF_UP);
            BigDecimal monthlyCredit = annualCredit.divide(twelve, mc);
            BigDecimal nMonths = new BigDecimal(numMonths);
            BigDecimal hundred = new BigDecimal("100");
            return nMonths.multiply(monthlyCredit).divide(hundred, mc);
            //return (numMonths * (annualCredit / 12.0))/100;
        }
    }

    private BigDecimal calculatePenalty(int numMonths) {
        BigDecimal months = new BigDecimal(numMonths);
        BigDecimal temp = months.multiply(mPenaltyFraction);
        BigDecimal hundred = new BigDecimal("100");
        MathContext mc = new MathContext(4, RoundingMode.HALF_UP);
        return temp.divide(hundred, mc);
    }

    private BigDecimal calculateAlternatePenalty(int numMonths) {
        BigDecimal months = new BigDecimal(numMonths);
        BigDecimal five = new BigDecimal("5");
        BigDecimal twelve = new BigDecimal("12");
        MathContext mc = new MathContext(4, RoundingMode.HALF_UP);
        BigDecimal penaltyFraction = five.divide(twelve, mc);
        BigDecimal temp = months.multiply(penaltyFraction);
        BigDecimal hundred = new BigDecimal("100");
        return temp.divide(hundred, mc);
    }
}
