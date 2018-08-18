package com.intelliviz.income.viewmodel;

import android.app.Application;

import com.intelliviz.data.PensionData;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.income.R;
import com.intelliviz.lowlevel.util.RetirementConstants;

public class PensionIncomeHelper extends AbstractPensionIncomeHelper {
    private int mNumRecords;
    private static String EC_ONLY_ONE_SUPPORTED;

    public PensionIncomeHelper(Application application, PensionData pd, RetirementOptions ro, int numRecords) {
        super(pd, ro);
        mNumRecords = numRecords;
        EC_ONLY_ONE_SUPPORTED = application.getResources().getString(R.string.ec_only_one_pension_allowed);
    }

    @Override
    public boolean canCreateNewIncomeSource() {
        if(mNumRecords == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getOnlyOneSupportedErrorCode() {
        return RetirementConstants.EC_ONLY_ONE_SUPPORTED;
    }

    @Override
    public String getOnlyOneSupportedErrorMessage() {
        return EC_ONLY_ONE_SUPPORTED;
    }
}
