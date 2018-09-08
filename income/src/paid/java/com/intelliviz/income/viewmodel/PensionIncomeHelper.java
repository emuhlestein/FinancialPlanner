package com.intelliviz.income.viewmodel;

import android.app.Application;

import com.intelliviz.data.PensionData;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.lowlevel.util.RetirementConstants;

public class PensionIncomeHelper extends AbstractPensionIncomeHelper {
    private boolean mSpouseIncluded;
    public PensionIncomeHelper(Application application, PensionData pd, RetirementOptions ro, int numRecords) {
        super(pd, ro);
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
