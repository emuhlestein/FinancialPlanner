package com.intelliviz.data;

import android.os.Bundle;

import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;
import com.intelliviz.lowlevel.util.RetirementConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edm on 10/19/2017.
 */

public class PensionRules implements IncomeTypeRules {
    private int mOwner;
    private String mOwnerBirthdate;
    private AgeData mStartAge;
    private AgeData mEndAge;
    private double mMonthlyAmount;
    private String mOtherBirthdate;

    /**
     * Constructor
     * NOTE: These parameters should come from retirement options.
     *
     * @param ownerBirthDate The owner's birth date.
     * @param endAge The end age.
     * @param otherBirthdate The other's birth date.
     */
    public PensionRules(String ownerBirthDate, AgeData endAge, String otherBirthdate) {
        mOwnerBirthdate = ownerBirthDate;
        mEndAge = endAge;
        mOtherBirthdate = otherBirthdate;
    }

    /**
     * These parameters should come from PensionData.
     * @param bundle
     */
    @Override
    public void setValues(Bundle bundle) {
        String value = bundle.getString(RetirementConstants.EXTRA_INCOME_FULL_BENEFIT);
        mMonthlyAmount = Double.parseDouble(value);
        mStartAge = bundle.getParcelable(RetirementConstants.EXTRA_INCOME_START_AGE);
        mOwner = bundle.getInt(RetirementConstants.EXTRA_INCOME_OWNER);
    }

    @Override
    public List<IncomeData> getIncomeData() {
        AgeData age = AgeUtils.getAge(mOwnerBirthdate);
        List<IncomeData> listAmountDate = new ArrayList<>();

        IncomeData benefitData;

        while(true) {

            age = new AgeData(age.getYear(), 0);

            if(age.isBefore(mStartAge)) {
                benefitData = new IncomeData(age, 0, 0, 0, false);
            } else {
                benefitData = new IncomeData(age, mMonthlyAmount, 0, 0, false);
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
        return new PensionIncomeDataAccessor(mOwner, mStartAge, mMonthlyAmount, mOwnerBirthdate, mOtherBirthdate);
    }
}
