package com.intelliviz.income.viewmodel;


import android.app.Application;

import com.intelliviz.data.RetirementOptions;
import com.intelliviz.db.entity.GovPensionEntity;
import com.intelliviz.income.R;

import java.util.List;

import static com.intelliviz.income.ui.MessageMgr.EC_SPOUSE_NOT_SUPPORTED;

public class GovPensionHelper extends AbstractGovPensionHelper {
    private static final int MAX_GOV_PENSION = 2;
    private String EC_ONLY_TWO_SUPPORTED;
    private boolean mSpouseIncluded;

    public GovPensionHelper(Application application, List<GovPensionEntity> gpeList, RetirementOptions ro) {
        super(application, gpeList, ro);
        EC_ONLY_TWO_SUPPORTED = application.getResources().getString(R.string.ec_only_two_social_security_allowed);
        mSpouseIncluded = ro.getIncludeSpouse()==1;
    }

    @Override
    public int getMaxGovPensions() {
        return MAX_GOV_PENSION;
    }

    @Override
    public int getSupportedSpouseErrorCode() {
        return EC_SPOUSE_NOT_SUPPORTED;
    }

    @Override
    public String getSupportedSpouseErrorMessage() {
        return EC_ONLY_TWO_SUPPORTED;
    }

    @Override
    public boolean isSpouseIncluded() {
        return mSpouseIncluded;
    }
}
