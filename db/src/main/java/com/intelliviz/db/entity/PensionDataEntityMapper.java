package com.intelliviz.db.entity;

import com.intelliviz.data.PensionData;

public class PensionDataEntityMapper {
    public static PensionData map(PensionIncomeEntity pie) {
        PensionData pensionData = new PensionData(pie.getId(), pie.getType(), pie.getName());
        pensionData.setAge(pie.getMinAge());
        pensionData.setBenefit(pie.getMonthlyBenefit());
        return pensionData;
    }

    public static PensionIncomeEntity map(PensionData pd) {
        PensionIncomeEntity pie = new PensionIncomeEntity(pd.getId(), pd.getType(), pd.getName(),
                pd.getAge(), pd.getBenefit());
        return pie;
    }
}
