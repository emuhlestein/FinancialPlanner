package com.intelliviz.income.viewmodel;

import com.intelliviz.data.PensionData;
import com.intelliviz.data.RetirementOptions;

import static com.intelliviz.income.ui.MessageMgr.EC_NO_ERROR;

public class PensionIncomeHelper extends AbstractPensionIncomeHelper {
    private boolean mSpouseIncluded;
    public PensionIncomeHelper(PensionData pd, RetirementOptions ro, int numRecords) {
        super(pd, ro);
        mSpouseIncluded = ro.getIncludeSpouse()==1;
    }

    @Override
    public boolean canCreateNewIncomeSource() {
        return true;
    }


    @Override
    public int getOnlyOneSupportedErrorCode() {
        return EC_NO_ERROR;
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
