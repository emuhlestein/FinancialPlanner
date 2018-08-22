package com.intelliviz.income.viewmodel;

import android.app.Application;

import com.intelliviz.data.RetirementOptions;
import com.intelliviz.data.SavingsData;
import com.intelliviz.income.R;
import com.intelliviz.lowlevel.util.RetirementConstants;

public class SavingsIncomeHelper extends AbstractSavingsIncomeHelper {
    private int mNumRecords;
    private static String EC_ONLY_TWO_SUPPORTED;

    public SavingsIncomeHelper(Application application, SavingsData sd, RetirementOptions ro, int numRecords) {
        super(sd, ro);
        mNumRecords = numRecords;
        EC_ONLY_TWO_SUPPORTED = application.getResources().getString(R.string.ec_only_two_savings_allowed);
    }

    @Override
    public boolean canCreateNewIncomeSource() {
        if(mNumRecords < 2) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getOnlyOneSupportedErrorCode() {
        return RetirementConstants.EC_ONLY_TWO_SUPPORTED;
    }

    @Override
    public String getOnlyOneSupportedErrorMessage() {
        return EC_ONLY_TWO_SUPPORTED;
    }
}
