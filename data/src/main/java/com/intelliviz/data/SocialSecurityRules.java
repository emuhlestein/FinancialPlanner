package com.intelliviz.data;

import android.os.Bundle;

import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;
import com.intelliviz.lowlevel.util.RetirementConstants;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.lowlevel.util.RetirementConstants.SC_GOOD;
import static com.intelliviz.lowlevel.util.RetirementConstants.OWNER_SELF;


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
    private int mOwner;
    private AgeData mMinAge = new AgeData(62, 0);
    private AgeData mMaxAge = new AgeData(70, 0);
    private AgeData mEndAge;
    private AgeData mStartAge;
    private AgeData mActualStartAge;
    private String mBirthdate;
    private BigDecimal mFullMonthlyBenefit;
    private BigDecimal mMonthlyBenefit;
    private String mSpouseBirthdate;
    private BigDecimal mSpouseFullBenefit;
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
        MathContext mc = new MathContext(6, RoundingMode.HALF_UP);
        mPenaltyFraction = five.divide(nine, mc);
    }

    @Override
    public void setValues(Bundle bundle) {
        String value = bundle.getString(RetirementConstants.EXTRA_INCOME_FULL_BENEFIT);
        mFullMonthlyBenefit = new BigDecimal(value);

        mStartAge = bundle.getParcelable(RetirementConstants.EXTRA_INCOME_START_AGE);
        mOwner = bundle.getInt(RetirementConstants.EXTRA_INCOME_OWNER);

        int birthYear = AgeUtils.getBirthYear(mBirthdate);
        if(mIsSpouseIncluded) {
            BigDecimal two = new BigDecimal(2);
            MathContext mc = new MathContext(6, RoundingMode.HALF_UP);
            BigDecimal halfSpouseBenefit = mSpouseFullBenefit.divide(two, mc);
            if (mFullMonthlyBenefit.compareTo(halfSpouseBenefit) < 0) {
                mMonthlyBenefit = getMonthlySpousalBenefit(halfSpouseBenefit);
            } else {
                mMonthlyBenefit = getMonthlyBenefit(birthYear, mFullMonthlyBenefit);
            }
        } else {
            mMonthlyBenefit = getMonthlyBenefit(birthYear, mFullMonthlyBenefit);
        }
    }

    @Override
    public List<IncomeData> getIncomeData() {
        AgeData startAge = mStartAge;

        if(mActualStartAge != null) {
            startAge = mActualStartAge;
        }

        List<IncomeData> listAmountDate = new ArrayList<>();

        AgeData currentAge = AgeUtils.getAge(mBirthdate);
        IncomeData incomeData;
        for (int month = currentAge.getNumberOfMonths(); month <= mEndAge.getNumberOfMonths(); month++) {
            AgeData age = new AgeData(month);
            if(age.equals(startAge) || age.isAfter(startAge)) {
                incomeData = new IncomeData(age, mMonthlyBenefit.doubleValue(), 0, SC_GOOD, null);
            } else {
                incomeData = new IncomeData(age, 0, 0, SC_GOOD, null);
            }
            listAmountDate.add(incomeData);

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
    public IncomeDataAccessor getIncomeDataAccessor() {
        return new SocialSecurityIncomeDataAccessor(getIncomeData(), mOwner);
    }

    public double getMonthlyBenefit() {
        return mMonthlyBenefit.doubleValue();
    }

    public AgeData getActualStartAge() {
        return mActualStartAge;
    }

    public AgeData getFullRetirementAge() {
        int birthyear = AgeUtils.getBirthYear(mBirthdate);
        return getFullRetirementAgeFromYear(birthyear);
    }

    public static void setRulesOnGovPensionEntities(List<GovPension> gpList, RetirementOptions roe) {
        if(gpList == null || gpList.isEmpty()) {
            return;
        }

        if(gpList.size() == 1) {
            GovPension spouse1 = gpList.get(0);
            SocialSecurityRules ssr = new SocialSecurityRules(roe.getEndAge(), roe.getBirthdate());
            spouse1.setRules(ssr);
        } else if(gpList.size() == 2) {
            GovPension principleSpouse;
            GovPension spouse;
            if(gpList.get(0).getOwner() == OWNER_SELF) {
                principleSpouse = gpList.get(0);
                spouse = gpList.get(1);
            } else {
                principleSpouse = gpList.get(1);
                spouse = gpList.get(0);
            }

            SocialSecurityRules ssr = new SocialSecurityRules(roe.getEndAge(), roe.getBirthdate(), roe.getSpouseBirthdate(),
                    spouse.getFullMonthlyBenefit(), spouse.getStartAge(), true, false);
            principleSpouse.setRules(ssr);
            ssr = new SocialSecurityRules(roe.getEndAge(), roe.getSpouseBirthdate(), roe.getBirthdate(),
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
        } else if(birthYear == 1942) {
            fullAge = new AgeData(65, 10);
        } else if(birthYear >= 1943 && birthYear < 1955) {
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

    private BigDecimal getMonthlyBenefit(int birthYear, BigDecimal monthlyBenefit) {
        MathContext mc = new MathContext(6, RoundingMode.HALF_UP);
        AgeData retireAge = getFullRetirementAgeFromYear(birthYear);
        AgeData startAge = mStartAge;
        if(mStartAge.isBefore(mMinAge)) {
            startAge = mMinAge;
        }

        if(startAge.diff(retireAge) == 0) {
            return mFullMonthlyBenefit;
        } else if(startAge.isBefore(retireAge)) {
            BigDecimal adjustment = getSocialSecurityAdjustment(birthYear, startAge);
            BigDecimal one = new BigDecimal(1);
            BigDecimal temp = one.subtract(adjustment);
            return temp.multiply(monthlyBenefit, mc);
        } else {
            BigDecimal adjustment = getSocialSecurityAdjustment(birthYear, startAge);
            BigDecimal one = new BigDecimal(1);
            BigDecimal temp = adjustment.add(one);
            return temp.multiply(monthlyBenefit, mc);
        }
    }

    private BigDecimal getMonthlySpousalBenefit(BigDecimal fullMonthlyBenefit) {
        // calculate spouse start min age.
        AgeData minStartAge = AgeUtils.getAge(mSpouseBirthdate, mBirthdate, mSpouseStartAge);
        AgeData startAge;
        if(mStartAge.isBefore(minStartAge)) {
            startAge = minStartAge;
            mActualStartAge = minStartAge;
        } else {
            startAge = mStartAge;
        }

        if(startAge.isBefore(mMinAge)) {
            startAge = mMinAge; // TODO need to set some flag
        }

        int birthYear = AgeUtils.getBirthYear(mBirthdate);
        BigDecimal adjustment = getSocialSecurityAdjustment(birthYear, startAge);
        BigDecimal one = new BigDecimal(1);
        BigDecimal temp = one.subtract(adjustment);
        MathContext mc = new MathContext(6, RoundingMode.HALF_UP);
        return temp.multiply(fullMonthlyBenefit, mc);
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
            MathContext mc = new MathContext(6, RoundingMode.HALF_UP);
            BigDecimal monthlyCredit = annualCredit.divide(twelve, mc);
            BigDecimal nMonths = new BigDecimal(numMonths);
            BigDecimal hundred = new BigDecimal("100");
            return nMonths.multiply(monthlyCredit, mc).divide(hundred, mc);
        }
    }

    private BigDecimal calculatePenalty(int numMonths) {
        BigDecimal months = new BigDecimal(numMonths);
        MathContext mc = new MathContext(6, RoundingMode.HALF_UP);
        BigDecimal temp = months.multiply(mPenaltyFraction, mc);
        BigDecimal hundred = new BigDecimal("100");
        return temp.divide(hundred, mc);
    }

    private BigDecimal calculateAlternatePenalty(int numMonths) {
        BigDecimal months = new BigDecimal(numMonths);
        BigDecimal five = new BigDecimal("5");
        BigDecimal twelve = new BigDecimal("12");
        MathContext mc = new MathContext(6, RoundingMode.HALF_UP);
        BigDecimal penaltyFraction = five.divide(twelve, mc);
        BigDecimal temp = months.multiply(penaltyFraction, mc);
        BigDecimal hundred = new BigDecimal("100");
        return temp.divide(hundred, mc);
    }
}
