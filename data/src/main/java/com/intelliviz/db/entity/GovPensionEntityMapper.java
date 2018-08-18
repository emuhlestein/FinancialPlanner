package com.intelliviz.db.entity;

import com.intelliviz.data.GovPension;

/**
 * Created by edm on 6/19/2018.
 */

public class GovPensionEntityMapper {
    public static GovPension map(GovPensionEntity gpe) {
        GovPension govPension = new GovPension(gpe.getId(), gpe.getType(), gpe.getName());
        govPension.setPrincipleSpouse(false); // TODO maybe need to remove principle spouse
        govPension.setFullMonthlyBenefit(gpe.getFullMonthlyBenefit());
        govPension.setSpouse(gpe.getSpouse() == 1 ? true : false);
        govPension.setStartAge(gpe.getStartAge());
        return govPension;
    }

    public static GovPensionEntity map(GovPension gp) {
        GovPensionEntity gpe = new GovPensionEntity(gp.getId(), gp.getType(), gp.getName(),
                gp.getFullMonthlyBenefit(), gp.getStartAge(), gp.isSpouse() ? 1 : 0);
        return gpe;
    }
}
