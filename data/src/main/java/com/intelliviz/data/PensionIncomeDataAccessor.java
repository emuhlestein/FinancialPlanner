package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;

import static com.intelliviz.lowlevel.util.RetirementConstants.SC_GOOD;

/**
 * Created by edm on 6/5/2018.
 */

public class PensionIncomeDataAccessor extends AbstractIncomeDataAccessor { ;
    private AgeData mStartAge;
    private double mMonthlyAmount;
    private String mOwnerBirthdate;
    private String mSpouseBirthdate;
    private int mStatus;
    private String mMessage;
    private RetirementOptions mRO;

    public PensionIncomeDataAccessor(int owner, AgeData startAge, double monthlyAmount, String ownerBirthdate, String spouseBirthdate,
                                     int status, String message, RetirementOptions ro) {
        super(owner);
        mStartAge = startAge;
        mMonthlyAmount = monthlyAmount;
        mOwnerBirthdate = ownerBirthdate;
        mSpouseBirthdate = spouseBirthdate;
        mStatus = status;
        mMessage = message;
        mRO = ro;
    }

    @Override
    public IncomeData getIncomeData(AgeData age) {
        age = getAge(age, mRO);
        if(age.isBefore(mStartAge)) {
            return new IncomeData(age, 0, 0, SC_GOOD, null);
        } else {
            return new IncomeData(age, mMonthlyAmount, 0, mStatus, mMessage);
        }
    }
}
