package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;

/**
 * Created by edm on 6/5/2018.
 */

public class SocialSecurityIncomeDataAccessor implements IncomeDataAccessor {
    private AgeData mStartAge;
    private double mMonthlyAmount;
    private String mBirthDate;
    private boolean mIsSpouse;
    private String mSpouseBirthdate;

    public SocialSecurityIncomeDataAccessor(AgeData startAge, double monthlyAmount, String birthDate, boolean isSpouse, String spouseBirthdate) {
        mStartAge = startAge;
        mMonthlyAmount = monthlyAmount;
        mBirthDate = birthDate;
        mIsSpouse = isSpouse;
        mSpouseBirthdate = spouseBirthdate;
    }

    @Override
    public IncomeData getIncomeData(AgeData principleAge) {
        AgeData age = principleAge;
        if(mIsSpouse) {
            age = AgeUtils.getSpouseAge(mSpouseBirthdate, mBirthDate, principleAge);
        }

        if(age.isOnOrAfter(mStartAge)) {
            return new IncomeData(age, mMonthlyAmount, 0, 0, false);
        } else {
            return new IncomeData(age, 0, 0, 0, false);
        }
    }
}
