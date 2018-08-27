package com.intelliviz.income.viewmodel;

import android.app.Application;

import com.intelliviz.data.PensionData;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.lowlevel.util.RetirementConstants;

public class PensionIncomeHelper extends AbstractPensionIncomeHelper {
    public PensionIncomeHelper(Application application, PensionData pd, RetirementOptions ro, int numRecords) {
        super(pd, ro);
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
}