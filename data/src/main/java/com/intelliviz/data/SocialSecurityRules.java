package com.intelliviz.data;

import android.os.Bundle;
import android.util.Pair;

import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;
import com.intelliviz.lowlevel.util.RetirementConstants;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import static com.intelliviz.lowlevel.util.RetirementConstants.OWNER_PRIMARY;


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
    private BigDecimal mOwnerFullBenefit;
    private AgeData mOwnerStartAge;

    private AgeData mMinAge = new AgeData(62, 0);
    private AgeData mMaxAge = new AgeData(70, 0);
    private AgeData mEndAge;
    private AgeData mOtherEndAge;
    private AgeData mStartAge;
    private AgeData mActualStartAge;
    private String mBirthdate;
    private BigDecimal mFullMonthlyBenefit;
    private BigDecimal mMonthlyBenefit;
    private String mOtherBirthdate;
    private BigDecimal mOtherFullBenefit;
    private AgeData mOtherStartAge;
    private boolean mIsSpouseIncluded;
    private BigDecimal mPenaltyFraction;
    private RetirementOptions mRO;
    private boolean mUseStartAge;
    private int mBirthYear;

    public SocialSecurityRules(RetirementOptions ro) {
        this(ro, "0", null, false, false);
    }

    public SocialSecurityRules(RetirementOptions ro, String otherFullBenefit,
                               AgeData otherStartAge, boolean isSpouseIncluded) {
        this(ro, otherFullBenefit, otherStartAge, isSpouseIncluded, false);
    }

    public SocialSecurityRules(RetirementOptions ro, String otherFullBenefit,
                               AgeData otherStartAge, boolean isSpouseIncluded, boolean useStartAge) {
        mRO = ro;
        mOtherStartAge = otherStartAge;
        mOtherFullBenefit = new BigDecimal(otherFullBenefit);
        mIsSpouseIncluded = isSpouseIncluded;

        mUseStartAge = useStartAge;

        BigDecimal five = new BigDecimal("5");
        BigDecimal nine = new BigDecimal("9");
        MathContext mc = new MathContext(6, RoundingMode.HALF_UP);
        mPenaltyFraction = five.divide(nine, mc);
    }

    @Override
    public void setValues(Bundle bundle) {

        mOwner = bundle.getInt(RetirementConstants.EXTRA_INCOME_OWNER);
        String value = bundle.getString(RetirementConstants.EXTRA_INCOME_FULL_BENEFIT);
        BigDecimal fullMonthlyBenefit = new BigDecimal(value);
        AgeData startAge = bundle.getParcelable(RetirementConstants.EXTRA_INCOME_START_AGE);

        mOwnerFullBenefit = fullMonthlyBenefit;
        mOwnerStartAge = startAge;

        if(mOwner == OWNER_PRIMARY) {
            mBirthYear = AgeUtils.getBirthYear(mRO.getPrimaryBirthdate());
        } else {
            mBirthYear = AgeUtils.getBirthYear(mRO.getSpouseBirthdate());
        }
    }

    /**
     *
     * @param primaryAge Age of the primary (or self)
     * @return IncomeData
     */
    public IncomeData getIncomeData(AgeData primaryAge) {
        AgeData age;
        IncomeData incomeData;

        age = convertAge(primaryAge);

        if(mUseStartAge) {
            if(age.isBefore(mOwnerStartAge)) {
                return new IncomeData(primaryAge, 0, 0, 0, null);
            } else {
                incomeData = getMonthlyBenefit(mBirthYear, age, mMinAge, mOtherStartAge);
                return incomeData;
            }
        } else {
            return getMonthlyBenefit(mBirthYear, age, mMinAge, age);
        }
    }

    private Pair<BigDecimal, AgeData> checkForSpousalBenefits(AgeData age, AgeData otherAge) {
        BigDecimal two = new BigDecimal(2);
        MathContext mc = new MathContext(6, RoundingMode.HALF_UP);
        BigDecimal halfBenefit = mOtherFullBenefit.divide(two, mc);

        if(mOwnerFullBenefit.compareTo(halfBenefit) < 0) {
            return new Pair<> (halfBenefit, new AgeData(Math.max(age.getNumberOfMonths(), otherAge.getNumberOfMonths())));
        }

        return null;
    }

    private IncomeData getMonthlyBenefit(int birthYear, AgeData age, AgeData minAge, AgeData otherStartAge) {
        BigDecimal fullMonthlyBenefit;
        BigDecimal spousalBenefits = null;
        if(mIsSpouseIncluded) {
            // see if there are spousal benefits
            Pair<BigDecimal, AgeData> pair = checkForSpousalBenefits(age, otherStartAge);
            if(pair != null) {
                fullMonthlyBenefit = pair.first;
                spousalBenefits = fullMonthlyBenefit;
                minAge = pair.second;
            } else {
                fullMonthlyBenefit = mOwnerFullBenefit;
            }
        } else {
            fullMonthlyBenefit = mOwnerFullBenefit;
        }

        BigDecimal monthlyBenefit = getActualMonthlyBenefit(birthYear, age, minAge, fullMonthlyBenefit);
        if(spousalBenefits != null) {
            // spousal benefits cannot be more than half of the primary spouse's benefits
            if(monthlyBenefit.compareTo(fullMonthlyBenefit) > 0) {
                monthlyBenefit = fullMonthlyBenefit;
            }
        }
        return new IncomeData(age, monthlyBenefit.doubleValue(), 0, 0, null);
    }

    public double getMonthlyBenefit() {
        return mMonthlyBenefit.doubleValue();
    }

    public AgeData getActualStartAge() {
        return mActualStartAge;
    }

    public AgeData getFullRetirementAge() {
        return getFullRetirementAgeFromYear(mBirthYear);
    }

    public static void setRulesOnGovPensionEntities(List<GovPension> gpList, RetirementOptions ro, boolean useStartAge) {
        if(gpList == null || gpList.isEmpty()) {
            return;
        }

        if(gpList.size() == 1) {
            GovPension spouse1 = gpList.get(0);
            SocialSecurityRules ssr = new SocialSecurityRules(ro,
                    null, null, useStartAge);
            spouse1.setRules(ssr);
        } else if(gpList.size() == 2) {
            GovPension principleSpouse;
            GovPension spouse;
            if(gpList.get(0).getOwner() == OWNER_PRIMARY) {
                principleSpouse = gpList.get(0);
                spouse = gpList.get(1);
            } else {
                principleSpouse = gpList.get(1);
                spouse = gpList.get(0);
            }

            SocialSecurityRules ssr = new SocialSecurityRules(ro,
                    spouse.getFullMonthlyBenefit(), spouse.getStartAge(), true, useStartAge);
            principleSpouse.setRules(ssr);
            ssr = new SocialSecurityRules(ro,
                    principleSpouse.getFullMonthlyBenefit(), principleSpouse.getStartAge(), true, useStartAge);
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
        } else if(birthYear < 1955) {
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

    /**
     * Calculate the monthly benefit.
     *
     * @param birthYear The birth year of the person for which the benefit is calculated.
     * @param startAge The age at which it is desired to receive monthly benefits.
     * @param minAge Age before which no benefit can be received.
     * @param fullMonthlyBenefit The full monthly benefit.
     * @return The actual monthly benefit.
     */
    private BigDecimal getActualMonthlyBenefit(int birthYear, AgeData startAge, AgeData minAge, BigDecimal fullMonthlyBenefit) {
        MathContext mc = new MathContext(6, RoundingMode.HALF_UP);
        AgeData retireAge = getFullRetirementAgeFromYear(birthYear);

        if(startAge.isBefore(minAge)) {
            return new BigDecimal("0");
        }

        if(startAge.diff(retireAge) == 0) {
            return fullMonthlyBenefit;
        } else if(startAge.isBefore(retireAge)) {
            BigDecimal adjustment = getSocialSecurityAdjustment(birthYear, startAge);
            BigDecimal one = new BigDecimal(1);
            BigDecimal temp = one.subtract(adjustment);
            return temp.multiply(fullMonthlyBenefit, mc);
        } else {
            BigDecimal adjustment = getSocialSecurityAdjustment(birthYear, startAge);
            BigDecimal one = new BigDecimal(1);
            BigDecimal temp = adjustment.add(one);
            return temp.multiply(fullMonthlyBenefit, mc);
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

    private AgeData convertAge(AgeData age) {
        if(mOwner == OWNER_PRIMARY) {
            return age;
        } else {
            return AgeUtils.getAge(mRO.getPrimaryBirthdate(), mRO.getSpouseBirthdate(), age);
        }
    }
}
