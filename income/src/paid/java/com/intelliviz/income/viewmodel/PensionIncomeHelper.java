package com.intelliviz.income.viewmodel;

import android.app.Application;

import com.intelliviz.data.PensionData;
import com.intelliviz.data.RetirementOptions;

public class PensionIncomeHelper extends AbstractPensionIncomeHelper {
    public PensionIncomeHelper(Application application, PensionData pd, RetirementOptions ro, int numRecords) {
        super(application, pd, ro);
    }

    @Override
    public boolean canCreateNewIncomeSource() {
        return true;
    }
}
