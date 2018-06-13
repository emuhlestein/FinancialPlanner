package com.intelliviz.income.util;


import com.intelliviz.income.db.entity.GovPensionEntity;
import com.intelliviz.income.db.entity.RetirementOptionsEntity;

import java.util.List;

import static com.intelliviz.income.util.RetirementConstants.EC_MAX_NUM_SOCIAL_SECURITY_FREE;


/**
 * Created by edm on 2/24/2018.
 */

public class GovEntityAccessor extends AbstractGovEntityAccessor {
    public GovEntityAccessor(List<GovPensionEntity> gpeList, RetirementOptionsEntity roe) {
        super(gpeList, roe);
    }

    @Override
    public int getMaxEntities() {
        return 1;
    }

    @Override
    public int getMaxErrorCode() {
        return EC_MAX_NUM_SOCIAL_SECURITY_FREE;
    }
}
