package com.intelliviz.retirementhelper.data;

import android.os.Bundle;

import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_FULL_BENEFIT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_IS_SPOUSE_ENTITY;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SPOUSE_BIRTHDATE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_START_AGE;

/**
 * There can be a maximum of two instances of this class: one for the principle spouse and one for
 * the second spouse. If this is a spouse benefit, need to swap birthdate,
 * start age and full benefit.
 *
 * Created by Ed Muhlestein on 8/14/2017.
 */

public class SocialSecurityRules implements IncomeTypeRules {
    private static final double MAX_SS_PENALTY = 30.0;
    private String mBirthdate;
    private AgeData mMinAge = new AgeData(62, 0);
    private AgeData mMaxAge = new AgeData(70, 0);
    private AgeData mEndAge;
    private AgeData mStartAge;
    private double mFullMonthlyBenefit;
    private double mMonthlyBenefit;
    private int mBenefitInfo;
    private double mSpouseFullBenefit;
    private String mSpouseBirthdate;
    private AgeData mSpouseStartAge;
    private boolean mIsSpouseIncluded;
    private boolean mIsSpouseEntity;

    public SocialSecurityRules(AgeData endAge, String birthDate) {
        this(endAge, birthDate, null, 0, null, false, false);
    }

    public SocialSecurityRules(AgeData endAge, String birthDate, String spouseBirthdate, double spouseFullBenefit,
                               AgeData spouseStartAge, boolean isSpouseIncluded, boolean isSpouseEntity) {
        mEndAge = endAge;
        mBirthdate = birthDate;
        mSpouseBirthdate = spouseBirthdate;
        mSpouseFullBenefit = spouseFullBenefit;
        mSpouseStartAge = spouseStartAge;
        mIsSpouseIncluded = isSpouseIncluded;
        mIsSpouseEntity = isSpouseEntity;
    }

    @Override
    public void setValues(Bundle bundle) {
        mFullMonthlyBenefit = bundle.getDouble(EXTRA_INCOME_FULL_BENEFIT);
        mStartAge = bundle.getParcelable(EXTRA_INCOME_START_AGE);

        int birthYear = SystemUtils.getBirthYear(mBirthdate);
        if(mIsSpouseIncluded) {
            if (mFullMonthlyBenefit < mSpouseFullBenefit / 2) {
                mMonthlyBenefit = mSpouseFullBenefit / 2;

                if(mStartAge.isBefore(mSpouseStartAge)) {
                    mStartAge = mSpouseStartAge;
                }
                mMonthlyBenefit = getMonthlyBenefit(mStartAge, birthYear, mMonthlyBenefit);
            } else {
                mMonthlyBenefit = getMonthlyBenefit(mStartAge, birthYear, mFullMonthlyBenefit);
            }
        } else {
            mMonthlyBenefit = getMonthlyBenefit(mStartAge, birthYear, mFullMonthlyBenefit);
        }
    }

    @Override
    public List<BenefitData> getBenefitData() {
        AgeData age = SystemUtils.getAge(mBirthdate);
        age = new AgeData(age.getYear(), 0);
        List<BenefitData> listAmountDate = new ArrayList<>();

        BenefitData benefitData;
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
    public BenefitData getBenefitData(BenefitData benefitData) {
        return null;
    }

    public double getMonthlyBenefit() {
        return mMonthlyBenefit;
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
            return new BenefitData(age, mMonthlyBenefit, 0, 0, false);
        } else {
            return new BenefitData(age, 0, 0, 0, false);
        }
        /*
        int birthYear;
        AgeData startAge;
        AgeData fullRetireAge;
        AgeData spouseAge = age;
        double fullMonthlyBenefit;

        if(mIsSpouseEntity) {
            startAge = mSpouseStartAge;
            birthYear = SystemUtils.getBirthYear(mSpouseBirthdate);
            fullMonthlyBenefit = mSpouseFullBenefit;
            spouseAge = SystemUtils.getSpouseAge(mBirthdate, mSpouseBirthdate, age);

            if(spouseAge.isBefore(startAge)) {
                return new BenefitData(age, 0, 0, 0, false);
            }
        } else {
            startAge = mStartAge;
            birthYear = SystemUtils.getBirthYear(mBirthdate);
            fullMonthlyBenefit = mFullMonthlyBenefit;

            if(age.isBefore(startAge)) {
                return new BenefitData(age, 0, 0, 0, false);
            }
        }

        fullRetireAge = getFullRetirementAgeFromYear(birthYear);

        if (startAge.isAfter(mMaxAge)) {
            startAge = mMaxAge;
        }

        if(!mIsSpouseIncluded) {
            int benefitInfo;
            if (startAge.isBefore(fullRetireAge)) {
                benefitInfo = RetirementConstants.BALANCE_STATE_LOW;
            } else {
                benefitInfo = RetirementConstants.BALANCE_STATE_GOOD;
            }
            double monthlyBenefit = getMonthlyBenefit(startAge, birthYear, fullMonthlyBenefit);
            return new BenefitData(age, monthlyBenefit, benefitInfo, benefitInfo, false);
        } else {
            // yes there are spouse social security benefits; need to determine if there are
            // spousal benefits for either spouse.
            if (mFullMonthlyBenefit < mSpouseFullBenefit / 2) {
                if(mIsSpouseEntity) {
                    if(spouseAge.isBefore(startAge)) {
                        return new BenefitData(spouseAge, 0, 0, 0, false);
                    } else {
                        double monthlyBenefit = getMonthlyBenefit(startAge, birthYear, fullMonthlyBenefit);
                        return new BenefitData(startAge, monthlyBenefit, 0, 0, false);
                    }
                } else {
                    fullMonthlyBenefit = mSpouseFullBenefit / 2;
                    if(startAge.isBefore(mSpouseStartAge)) {
                        startAge = mSpouseStartAge;
                    }
                    if(age.isBefore(startAge)) {
                        return new BenefitData(age, 0, 0, 0, false);
                    } else {
                        double monthlyBenefit = getMonthlyBenefit(startAge, birthYear, fullMonthlyBenefit);
                        return new BenefitData(age, monthlyBenefit, 0, 0, false);
                    }
                }
            } else if (mSpouseFullBenefit < mFullMonthlyBenefit / 2) {
                if(mIsSpouseEntity) {
                    fullMonthlyBenefit = mFullMonthlyBenefit / 2;
                    if(mSpouseStartAge.isBefore(mStartAge)) {
                        startAge = mStartAge;
                    }

                    if(spouseAge.isBefore(startAge)) {
                        return new BenefitData(spouseAge, 0, 0, 0, false);
                    } else {
                        double monthlyBenefit = getMonthlyBenefit(startAge, birthYear, fullMonthlyBenefit);
                        if (monthlyBenefit > fullMonthlyBenefit) {
                            monthlyBenefit = fullMonthlyBenefit;
                        }
                        return new BenefitData(age, monthlyBenefit, 0, 0, false);
                    }
                } else {
                    if(age.isBefore(startAge)) {
                        return new BenefitData(age, 0, 0, 0, false);
                    } else {
                        double monthlyBenefit = getMonthlyBenefit(startAge, birthYear, fullMonthlyBenefit);
                        return new BenefitData(age, monthlyBenefit, 0, 0, false);
                    }
                }
            } else {
                double monthlyBenefit = getMonthlyBenefit(startAge, birthYear, fullMonthlyBenefit);
                return new BenefitData(startAge, monthlyBenefit, 0, 0, false);
            }
        }
        */
    }

    //SocialSecurityRules(AgeData endAge, String birthDate, String spouseBirthdate, double spouseFullBenefit,
    //                    AgeData spouseStartAge, boolean isSpouseIncluded, boolean isSpouseEntity) {

    public static void setRulesOnGovPensionEntities(List<GovPensionEntity> gpeList, RetirementOptionsEntity roe) {
        if(gpeList == null || gpeList.isEmpty()) {
            return;
        }
        String birthdate = roe.getBirthdate();
        AgeData endAge = roe.getEndAge();
        if(gpeList.size() == 1) {
            GovPensionEntity spouse1 = gpeList.get(0);
            SocialSecurityRules ssr = new SocialSecurityRules(endAge, birthdate, "", 0, null, false, false);
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
                    Double.parseDouble(spouse.getFullMonthlyBenefit()), spouse.getStartAge(), true, false);
            principleSpouse.setRules(ssr);
            ssr = new SocialSecurityRules(endAge, roe.getSpouseBirthdate(), birthdate,
                    Double.parseDouble(principleSpouse.getFullMonthlyBenefit()), principleSpouse.getStartAge(), true, true);
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

    private int getBenefitInfo(int birthYear) {
        AgeData fullRetireAge = getFullRetirementAgeFromYear(birthYear);
        if (mStartAge.isBefore(fullRetireAge)) {
            return RetirementConstants.BALANCE_STATE_LOW;
        } else {
            return RetirementConstants.BALANCE_STATE_GOOD;
        }
    }

    public void setValuesOld(Bundle bundle) {
        mFullMonthlyBenefit = bundle.getDouble(EXTRA_INCOME_FULL_BENEFIT);
        mStartAge = bundle.getParcelable(EXTRA_INCOME_START_AGE);
        mIsSpouseEntity = bundle.getBoolean(EXTRA_INCOME_IS_SPOUSE_ENTITY, false);
        mSpouseBirthdate = bundle.getString(EXTRA_INCOME_SPOUSE_BIRTHDATE);

        int birthYear = SystemUtils.getBirthYear(mBirthdate);
        mBenefitInfo = getBenefitInfo(birthYear);
        if(mIsSpouseIncluded) {
            if (mFullMonthlyBenefit < mSpouseFullBenefit / 2) {
                mMonthlyBenefit = mSpouseFullBenefit / 2;
                if(mStartAge.isBefore(mSpouseStartAge)) {
                    mStartAge = new AgeData(mSpouseStartAge.getNumberOfMonths());
                }
                mMonthlyBenefit = getMonthlyBenefit(mStartAge, birthYear, mMonthlyBenefit);
            } else if (mSpouseFullBenefit < mFullMonthlyBenefit / 2) {
                mMonthlyBenefit = mFullMonthlyBenefit / 2;
                if(mSpouseStartAge.isBefore(mStartAge)) {
                    mSpouseStartAge = new AgeData(mStartAge.getNumberOfMonths());
                }
                mMonthlyBenefit = getMonthlyBenefit(mSpouseStartAge, birthYear, mMonthlyBenefit);
            } else {
                mMonthlyBenefit = getMonthlyBenefit(mStartAge, birthYear, mFullMonthlyBenefit);
            }
        } else {
            mMonthlyBenefit = getMonthlyBenefit(mStartAge, birthYear, mFullMonthlyBenefit);
        }
    }

}
