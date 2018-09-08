package com.intelliviz.income.viewmodel;

import com.intelliviz.data.RetirementOptions;
import com.intelliviz.db.entity.IncomeSourceEntityBase;

import java.util.List;

public class IncomeSourceHelper extends AbstractIncomeSourceHelper {
    private boolean mSpouseIncluded;
    public IncomeSourceHelper(List<IncomeSourceEntityBase> incomeList, RetirementOptions ro) {
        super(incomeList, ro);
        mSpouseIncluded = ro.getIncludeSpouse()==1;
    }

    @Override
    public boolean isSpouseIncluded() {
        return mSpouseIncluded;
    }
}
