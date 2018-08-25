package com.intelliviz.data;

import com.intelliviz.db.entity.IncomeSourceEntityBase;
import com.intelliviz.db.entity.RetirementOptionsEntity;

import java.util.List;

public class IncomeSourceDataEx {
    private List<IncomeSourceEntityBase> mIncomeSourceList;
    private RetirementOptionsEntity mROE;

    public IncomeSourceDataEx(List<IncomeSourceEntityBase> isList, RetirementOptionsEntity ROE) {
        mIncomeSourceList = isList;
        mROE = ROE;
    }

    public List<IncomeSourceEntityBase> getList() {
        return mIncomeSourceList;
    }

    public RetirementOptionsEntity getROE() {
        return mROE;
    }
}
