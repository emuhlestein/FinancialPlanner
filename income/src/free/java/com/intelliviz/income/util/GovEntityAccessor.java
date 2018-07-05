package com.intelliviz.income.util;


import com.intelliviz.data.GovPension;
import com.intelliviz.db.entity.RetirementOptionsEntity;

import java.util.List;

import static com.intelliviz.lowlevel.util.RetirementConstants.EC_MAX_NUM_SOCIAL_SECURITY_FREE;


/**
 * Created by edm on 2/24/2018.
 */

public class GovEntityAccessor extends AbstractGovEntityAccessor {
    public GovEntityAccessor(List<GovPension> gpList, RetirementOptionsEntity roe) {
        super(gpList, roe);
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
