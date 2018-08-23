package com.intelliviz.income.viewmodel;

import android.app.Application;

import com.intelliviz.data.RetirementOptions;
import com.intelliviz.data.SavingsData;
import com.intelliviz.lowlevel.util.RetirementConstants;

public class SavingsIncomeHelper extends AbstractSavingsIncomeHelper {

    public SavingsIncomeHelper(Application application, SavingsData sd, RetirementOptions ro, int numRecords) {
        super(sd, ro);
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
