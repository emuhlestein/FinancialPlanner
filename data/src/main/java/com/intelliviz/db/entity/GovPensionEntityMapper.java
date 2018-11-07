package com.intelliviz.db.entity;

import com.intelliviz.data.GovPension;

/**
 * Created by edm on 6/19/2018.
 */

public class GovPensionEntityMapper {
    public static GovPension map(GovPensionEntity gpe) {
        GovPension govPension = new GovPension(gpe.getId(), gpe.getType(), gpe.getName(), gpe.getOwner(), gpe.getIncluded());
        govPension.setFullMonthlyBenefit(gpe.getFullMonthlyBenefit());
        govPension.setStartAge(gpe.getStartAge());
        return govPension;
    }

    public static GovPensionEntity map(GovPension gp) {
        GovPensionEntity gpe = new GovPensionEntity(gp.getId(), gp.getType(), gp.getName(), gp.getOwner(), gp.getIncluded(),
                gp.getFullMonthlyBenefit(), gp.getStartAge());
        return gpe;
    }
}
