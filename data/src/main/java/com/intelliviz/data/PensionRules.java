package com.intelliviz.data;

import android.os.Bundle;

import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;

import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.lowlevel.util.RetirementConstants.OWNER_SPOUSE;
import static com.intelliviz.lowlevel.util.RetirementConstants.SC_GOOD;

/**
 * Created by edm on 10/19/2017.
 */

public class PensionRules implements IncomeTypeRules {
    private int mOwner;
    private String mOwnerBirthdate;
    private AgeData mStartAge;
    private AgeData mEndAge;
    private double mMonthlyAmount;
    private String mSpouseBirthdate;
    private RetirementOptions mRO;

    public PensionRules(RetirementOptions ro) {
        mEndAge = ro.getEndAge();
        mOwnerBirthdate = ro.getBirthdate();
        mSpouseBirthdate = ro.getSpouseBirthdate();
        mRO = ro;
    }

    /**
     * These parameters should come from PensionData.
     * @param bundle Values to set.
     */
    @Override
    public void setValues(Bundle bundle) {
        mOwner = bundle.getInt(RetirementConstants.EXTRA_INCOME_OWNER);
        String value = bundle.getString(RetirementConstants.EXTRA_INCOME_FULL_BENEFIT);
        mMonthlyAmount = Double.parseDouble(value);
        mStartAge = bundle.getParcelable(RetirementConstants.EXTRA_INCOME_START_AGE);
        if(mOwner == OWNER_SPOUSE) {
            String temp = mOwnerBirthdate;
            mOwnerBirthdate = mSpouseBirthdate;
            mSpouseBirthdate = temp;
        }
    }

    @Override
    public List<IncomeData> getIncomeData() {
        AgeData age = new AgeData(0);
        List<IncomeData> listAmountDate = new ArrayList<>();

        IncomeData benefitData;

        while(true) {

            age = new AgeData(age.getYear(), 0);

            if(age.isBefore(mStartAge)) {
                benefitData = new IncomeData(age, 0, 0, SC_GOOD, null);
            } else {
                benefitData = new IncomeData(age, mMonthlyAmount, 0, SC_GOOD, null);
            }
            listAmountDate.add(benefitData);

            // getList next age
            age = new AgeData(age.getYear()+1, 0);
            if(age.isAfter(mEndAge)) {
                break;
            }
        }

        return listAmountDate;
    }

    @Override
    public IncomeDataAccessor getIncomeDataAccessor() {
        return new PensionIncomeDataAccessor(mOwner, mStartAge, mMonthlyAmount,
                mOwnerBirthdate, mSpouseBirthdate, SC_GOOD, null, mRO);
    }
}
