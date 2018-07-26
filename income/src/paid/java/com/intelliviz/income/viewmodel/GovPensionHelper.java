package com.intelliviz.income.viewmodel;


import android.app.Application;

import com.intelliviz.data.RetirementOptions;
import com.intelliviz.db.entity.GovPensionEntity;

import java.util.List;

public class GovPensionHelper extends AbstractGovPensionHelper {
    private static final int MAX_GOV_PENSION = 2;
    private List<GovPensionEntity> mGpeList;
    private RetirementOptions mRO;
    private static String EC_NO_SPOUSE_BIRTHDATE;
    private static String EC_ONLY_TWO_SUPPORTED;

    public GovPensionHelper(Application application, List<GovPensionEntity> gpeList, RetirementOptions ro) {
        super(application, gpeList, ro);
    }

    @Override
    public int getMaxGovPensions() {
        return MAX_GOV_PENSION;
    }

}
