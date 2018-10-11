package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;

import static com.intelliviz.lowlevel.util.RetirementConstants.SC_GOOD;

/**
 * Created by edm on 6/5/2018.
 */

public class SocialSecurityIncomeDataAccessor extends AbstractIncomeDataAccessor {
    private AgeData mStartAge;
    private double mMonthlyAmount;
    private int mStatus;
    private String mMessage;
    private RetirementOptions mRO;

    public SocialSecurityIncomeDataAccessor(int owner, AgeData startAge, double monthlyAmount,
                                            int status, String message, RetirementOptions ro) {
        super(owner);
        mStartAge = startAge;
        mMonthlyAmount = monthlyAmount;
        mStatus = status;
        mMessage = message;
        mRO = ro;
    }

    @Override
    public IncomeData getIncomeData(AgeData age) {
        age = getOwnerAge(age, mRO);
        if(age.isBefore(mStartAge)) {
            return new IncomeData(age, 0, 0, SC_GOOD, null);
        } else {
            return new IncomeData(age, mMonthlyAmount, 0, mStatus, mMessage);
        }
    }
}
