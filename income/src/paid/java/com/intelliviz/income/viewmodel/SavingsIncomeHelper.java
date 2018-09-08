package com.intelliviz.income.viewmodel;

import android.app.Application;

import com.intelliviz.data.RetirementOptions;
import com.intelliviz.data.SavingsData;
import com.intelliviz.lowlevel.util.RetirementConstants;

public class SavingsIncomeHelper extends AbstractSavingsIncomeHelper {
    private boolean mSpouseIncluded;

    public SavingsIncomeHelper(Application application, SavingsData sd, RetirementOptions ro, int numRecords) {
        super(sd, ro);
        mSpouseIncluded = ro.getIncludeSpouse()==1;
    }

    @Override
    public boolean canCreateNewIncomeSource() {
        return true;
    }

    @Override
    public int getOnlyOneSupportedErrorCode() {
        return RetirementConstants.EC_NO_ERROR;
    }

    @Override
    public String getOnlyOneSupportedErrorMessage() {
        return "";
    }

    @Override
    public boolean isSpouseIncluded() {
        return mSpouseIncluded;
    }
}
