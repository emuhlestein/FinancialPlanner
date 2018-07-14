package com.intelliviz.data;

import com.intelliviz.db.entity.GovPensionEntity;
import com.intelliviz.db.entity.RetirementOptionsEntity;

import java.util.List;

public class GovPensionEx {
    private List<GovPensionEntity> mGpeList;
    private RetirementOptionsEntity mROE;

    public GovPensionEx(List<GovPensionEntity> gpeList, RetirementOptionsEntity ROE) {
        mGpeList = gpeList;
        mROE = ROE;
    }

    public List<GovPensionEntity> getGpeList() {
        return mGpeList;
    }

    public RetirementOptionsEntity getROE() {
        return mROE;
    }
}
