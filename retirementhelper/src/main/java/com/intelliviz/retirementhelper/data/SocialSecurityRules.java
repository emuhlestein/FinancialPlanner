package com.intelliviz.retirementhelper.data;

import android.os.Bundle;

import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_FULL_BENEFIT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_IS_SPOUSE_ENTITY;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SPOUSE_BIRTHDATE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_START_AGE;

/**
 * There can be a maximum of two instances of this class: one for the first spouse and one for
 * the second spouse. If this is a spouse benefit, need to swap birthdate,
 * start age and full benefit.
 *
 * Created by Ed Muhlestein on 8/14/2017.
 */

public class SocialSecurityRules implements IncomeTypeRules {
    private static final double MAX_SS_PENALTY = 30.0;
    private String mBirthdate;
    private AgeData mMinAge;
    private AgeData mMaxAge;
    private AgeData mEndAge;
    private AgeData mStartAge;
    private double mFullMonthlyBenefit;
    private boolean mIsSpouseEntity;
    private double mSpouseFullBenefit;
    private String mSpouseBirthdate;
    private AgeData mSpouseStartAge;
    private boolean mAreThereSpouseBenefits;

    public SocialSecurityRules(String birthDate, AgeData endAge, double spouseFullBenefit,
                               AgeData spouseStartAge, boolean isSpouseIncluded) {
        mBirthdate = birthDate;
        mMinAge = new AgeData(62, 0);
        mMaxAge = new AgeData(70, 0);
        mEndAge = endAge;
        mSpouseFullBenefit = spouseFullBenefit;
        mSpouseStartAge = spouseStartAge;
        mAreThereSpouseBenefits = isSpouseIncluded;
    }

    @Override
    public void setValues(Bundle bundle) {
        mFullMonthlyBenefit = bundle.getDouble(EXTRA_INCOME_FULL_BENEFIT);
        mStartAge = bundle.getParcelable(EXTRA_INCOME_START_AGE);
        mIsSpouseEntity = bundle.getBoolean(EXTRA_INCOME_IS_SPOUSE_ENTITY, false);
        mSpouseBirthdate = bundle.getString(EXTRA_INCOME_SPOUSE_BIRTHDATE);

        if(mIsSpouseEntity) {
            double benefit = mSpouseFullBenefit;
            mSpouseFullBenefit = mFullMonthlyBenefit;
            mFullMonthlyBenefit = benefit;

            AgeData age = mSpouseStartAge;
            mSpouseStartAge = mStartAge;
            mStartAge = age;
        }
    }

    @Override
    public List<AgeData> getAges() {
        int birthyear = SystemUtils.getBirthYear(mBirthdate);
        AgeData retireAge = getFullRetirementAgeFromYear(birthyear);
        if(mIsSpouseEntity) {
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
        //double monthlyBenefit = getMonthlyBenefitForAge(age).getBenefit();
        return new MilestoneData(age, mEndAge, mMinAge, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    @Override
    public List<BenefitData> getBenefitData() {
        AgeData age = SystemUtils.getAge(mBirthdate);
        age = new AgeData(age.getYear(), 0);
        List<BenefitData> listAmountDate = new ArrayList<>();

        BenefitData benefitData = null;
        BenefitData monthlyBenefitData = null;
        while(true) {
            benefitData = getMonthlyBenefitForAge(age);
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
    public BenefitData getBenefitForAge(AgeData age) {
        return getMonthlyBenefitForAge(age);
        /*
        if(age.isBefore(mStartAge)) {
            AgeData spouseAge = SystemUtils.getSpouseAge(mBirthdate, mSpouseBirthdate, age);
            return new SocialSecurityBenefitData(age, 0, 0, RetirementConstants.BALANCE_STATE_EXHAUSTED, false, mIsSpouseEntity, 0, spouseAge);
        } else {
            return getMonthlyBenefitForAge(age);
        }
        */
    }

    @Override
    public double getBalanceForAge(AgeData age) {
        return 0;
    }

    @Override
    public BenefitData getBenefitData(BenefitData benefitData) {
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

    public BenefitData getMonthlyBenefitForAge(AgeData age) {
        int birthYear;
        AgeData startAge;
        AgeData fullRetireAge;
        double fullMonthlyBenefit;

        if(mIsSpouseEntity) {
            startAge = mSpouseStartAge;
            birthYear = SystemUtils.getBirthYear(mSpouseBirthdate);
            fullMonthlyBenefit = mSpouseFullBenefit;
        } else {
            startAge = mStartAge;
            birthYear = SystemUtils.getBirthYear(mBirthdate);
            fullMonthlyBenefit = mFullMonthlyBenefit;
        }

        fullRetireAge = getFullRetirementAgeFromYear(birthYear);

        if (startAge.isAfter(mMaxAge)) {
            startAge = mMaxAge;
        }

        if(age.isBefore(startAge)) {
            return new BenefitData(age, 0, 0, 0, false);
        }

        if(!mAreThereSpouseBenefits) {
            int benefitInfo;
            if (startAge.isBefore(fullRetireAge)) {
                benefitInfo = RetirementConstants.BALANCE_STATE_LOW;
            } else {
                benefitInfo = RetirementConstants.BALANCE_STATE_GOOD;
            }
            double monthlyBenefit = getMonthlyBenefit(startAge, birthYear, fullMonthlyBenefit);
            return new BenefitData(startAge, monthlyBenefit, benefitInfo, benefitInfo, false);
        } else {
            // yes there are spouse social security benefits; need to determine if there are
            // spousal benefits for either spouse.
            if (mFullMonthlyBenefit < mSpouseFullBenefit / 2) {
                if(mIsSpouseEntity) {
                    double monthlyBenefit = getMonthlyBenefit(startAge, birthYear, fullMonthlyBenefit);
                    return new BenefitData(startAge, monthlyBenefit, 0, 0, false);
                } else {
                    fullMonthlyBenefit = mSpouseFullBenefit / 2;
                    if(startAge.isBefore(mSpouseStartAge)) {
                        startAge = mSpouseStartAge;
                    }
                    double monthlyBenefit = getMonthlyBenefit(startAge, birthYear, fullMonthlyBenefit);
                    return new BenefitData(startAge, monthlyBenefit, 0, 0, false);
                }
            } else if (mSpouseFullBenefit < mFullMonthlyBenefit / 2) {
                if(mIsSpouseEntity) {
                    fullMonthlyBenefit = mFullMonthlyBenefit / 2;
                    if(mSpouseStartAge.isBefore(mStartAge)) {
                        startAge = mStartAge;
                    }
                    double monthlyBenefit = getMonthlyBenefit(startAge, birthYear, fullMonthlyBenefit);
                    return new BenefitData(startAge, monthlyBenefit, 0, 0, false);
                } else {
                    double monthlyBenefit = getMonthlyBenefit(startAge, birthYear, fullMonthlyBenefit);
                    return new BenefitData(startAge, monthlyBenefit, 0, 0, false);
                }
            } else {
                double monthlyBenefit = getMonthlyBenefit(startAge, birthYear, fullMonthlyBenefit);
                return new BenefitData(startAge, monthlyBenefit, 0, 0, false);
            }
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
            SocialSecurityRules ssr = new SocialSecurityRules(birthdate, endAge, 0, null, false);
            spouse1.setRules(ssr);
        } else if(gpeList.size() == 2) {
            GovPensionEntity spouse1 = gpeList.get(0);
            GovPensionEntity spouse2 = gpeList.get(1);
            SocialSecurityRules ssr = new SocialSecurityRules(birthdate, endAge,
                    Double.parseDouble(spouse2.getFullMonthlyBenefit()), spouse2.getStartAge(), true);
            spouse1.setRules(ssr);
            ssr = new SocialSecurityRules(birthdate, endAge,
                    Double.parseDouble(spouse1.getFullMonthlyBenefit()), spouse1.getStartAge(), true);
            spouse2.setRules(ssr);
        }
    }

    /**
     * Calculate spousal benefits. If either spouse gets less than one half the benefit of the other
     * spouse, there are spousal benefits. There are no spousal benefits if both spouses get more than
     * one half the benefit of the other spouse.
     *
     * Spousal benefits cannot be taken until the other spouse starts taking social security.
     *
     * 1) If divorced can still get spousal benefits.
     * 2) Claiming spousal benefits early (before spouse full retirement age) reduces the benefit amount.
     * 3) Waiting past full retirement age will not increase benefit amount.
     * 4) You can get spousal benefits if spouse has never worked; if spouse benefit amount is 0;
     *
     * @param age Age of the primary spouse.
     *
     * @return The social security data. null if there are no spousal benefits.
     */
    private BenefitData calculateSpousalBenefits(AgeData age) {

        // if primary spouse cannot receive benefits, neither can the spouse.
        if(age.isBefore(mSpouseStartAge)) {
            return null;
        }

        int birthYear;
        if(mIsSpouseEntity) {
            birthYear = SystemUtils.getBirthYear(mSpouseBirthdate);
        } else {
            birthYear = SystemUtils.getBirthYear(mBirthdate);
        }

        AgeData retireAge = getFullRetirementAgeFromYear(birthYear);

        double monthlyBenefit;
        int benefitInfo;

        if(mFullMonthlyBenefit < (mSpouseFullBenefit / 2)) {
            double maxBenefit = mSpouseFullBenefit / 2;
            if(age.isBefore(retireAge)) {
                monthlyBenefit = getMonthlyBenefit(age, birthYear, maxBenefit);
                benefitInfo = RetirementConstants.BALANCE_STATE_LOW;
            } else {
                monthlyBenefit = maxBenefit;
                benefitInfo = RetirementConstants.BALANCE_STATE_GOOD;
            }
            return new BenefitData(age, monthlyBenefit, 0, benefitInfo, true);
        } else {
            return null;
        }
    }

    private boolean includeSpousalBenefits() {
        if (mFullMonthlyBenefit < mSpouseFullBenefit / 2) {
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
