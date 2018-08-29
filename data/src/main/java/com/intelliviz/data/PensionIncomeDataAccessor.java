package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;

import static com.intelliviz.lowlevel.util.RetirementConstants.OWNER_SPOUSE;

/**
 * Created by edm on 6/5/2018.
 */

public class PensionIncomeDataAccessor implements IncomeDataAccessor {
    private int mOwner;
    private AgeData mStartAge;
    private double mMonthlyAmount;
    private String mBirthDate;
    private String mOtherBirthdate;

    public PensionIncomeDataAccessor(int owner, AgeData startAge, double monthlyAmount, String birthDate, String otherBirthdate) {
        mOwner = owner;
        mStartAge = startAge;
        mMonthlyAmount = monthlyAmount;
        mBirthDate = birthDate;
        mOtherBirthdate = otherBirthdate;
    }

    @Override
    public IncomeData getIncomeData(AgeData principleAge) {
        AgeData age = principleAge;
        if(mOwner == OWNER_SPOUSE) {
            age = AgeUtils.getOtherAge(mOtherBirthdate, mBirthDate, principleAge);
        }

        if(age.isOnOrAfter(mStartAge)) {
            return new IncomeData(age, mMonthlyAmount, 0, 0, false);
        } else {
            return new IncomeData(age, 0, 0, 0, false);
        }
    }
}
