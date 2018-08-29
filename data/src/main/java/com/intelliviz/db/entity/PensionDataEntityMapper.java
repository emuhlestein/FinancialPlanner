package com.intelliviz.db.entity;

import com.intelliviz.data.PensionData;

public class PensionDataEntityMapper {
    public static PensionData map(PensionIncomeEntity pie) {
        PensionData pensionData = new PensionData(pie.getId(), pie.getType(), pie.getName(), pie.getSelf());
        pensionData.setStartAge(pie.getMinAge());
        pensionData.setMonthlyBenefit(pie.getMonthlyBenefit());
        return pensionData;
    }

    public static PensionIncomeEntity map(PensionData pd) {
        PensionIncomeEntity pie = new PensionIncomeEntity(pd.getId(), pd.getType(),
                pd.getName(), pd.getSelf(), pd.getStartAge(), pd.getMonthlyBenefit());
        return pie;
    }
}
