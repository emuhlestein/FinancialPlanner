package com.intelliviz.income.ui;


import com.intelliviz.income.db.entity.GovPensionEntity;
import com.intelliviz.income.db.entity.IncomeSourceEntityBase;
import com.intelliviz.income.db.entity.PensionIncomeEntity;
import com.intelliviz.income.db.entity.SavingsIncomeEntity;

import static com.intelliviz.income.util.RetirementConstants.INCOME_TYPE_401K;
import static com.intelliviz.income.util.RetirementConstants.INCOME_TYPE_GOV_PENSION;
import static com.intelliviz.income.util.RetirementConstants.INCOME_TYPE_PENSION;
import static com.intelliviz.income.util.RetirementConstants.INCOME_TYPE_SAVINGS;

/**
 * Created by edm on 3/12/2018.
 */

public class IncomeSourceFactory {
    public static IncomeSource createIncomeSource(IncomeSourceEntityBase incomeSourceEntity) {
        switch(incomeSourceEntity.getType()) {
            case INCOME_TYPE_GOV_PENSION:
                return new GovPensionIncomeSource(incomeSourceEntity);
            case INCOME_TYPE_401K:
                return new SavingsIncomeSource(incomeSourceEntity);
            case INCOME_TYPE_SAVINGS:
                return new SavingsIncomeSource(incomeSourceEntity);
            case INCOME_TYPE_PENSION:
                return new PensionIncomeSource(incomeSourceEntity);
            default:
                return null;
        }
    }

    public static IncomeSource createIncomeSource(int type) {
        switch(type) {
            case INCOME_TYPE_GOV_PENSION:
                return new GovPensionIncomeSource(new GovPensionEntity(0, type));
            case INCOME_TYPE_401K:
                return new SavingsIncomeSource(new SavingsIncomeEntity(0, type));
            case INCOME_TYPE_SAVINGS:
                return new SavingsIncomeSource(new SavingsIncomeEntity(0, type));
            case INCOME_TYPE_PENSION:
                return new PensionIncomeSource(new PensionIncomeEntity(0, type));
            default:
                return null;
        }
    }
}
