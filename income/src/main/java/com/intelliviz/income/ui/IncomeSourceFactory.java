package com.intelliviz.income.ui;

import com.intelliviz.data.GovPension;
import com.intelliviz.data.PensionData;
import com.intelliviz.data.SavingsData;
import com.intelliviz.db.entity.AbstractIncomeSource;

import static com.intelliviz.lowlevel.util.RetirementConstants.INCOME_TYPE_401K;
import static com.intelliviz.lowlevel.util.RetirementConstants.INCOME_TYPE_GOV_PENSION;
import static com.intelliviz.lowlevel.util.RetirementConstants.INCOME_TYPE_PENSION;
import static com.intelliviz.lowlevel.util.RetirementConstants.INCOME_TYPE_SAVINGS;

/**
 * Created by edm on 3/12/2018.
 *
 */

public class IncomeSourceFactory {
    public static IncomeSource createIncomeSource(AbstractIncomeSource incomeSourceEntity, MessageMgr messageMgr) {
        switch(incomeSourceEntity.getType()) {
            case INCOME_TYPE_GOV_PENSION:
                return new GovPensionIncomeSource(incomeSourceEntity, messageMgr);
            case INCOME_TYPE_401K:
                return new SavingsIncomeSource(incomeSourceEntity, messageMgr);
            case INCOME_TYPE_SAVINGS:
                return new SavingsIncomeSource(incomeSourceEntity, messageMgr);
            case INCOME_TYPE_PENSION:
                return new PensionIncomeSource(incomeSourceEntity, messageMgr);
            default:
                return null;
        }
    }

    public static IncomeSource createIncomeSource(int type, MessageMgr messageMgr) {
        switch(type) {
            case INCOME_TYPE_GOV_PENSION:
                return new GovPensionIncomeSource(new GovPension(0, type), messageMgr);
            case INCOME_TYPE_401K:
                return new SavingsIncomeSource(new SavingsData(0, type), messageMgr);
            case INCOME_TYPE_SAVINGS:
                return new SavingsIncomeSource(new SavingsData(0, type), messageMgr);
            case INCOME_TYPE_PENSION:
                return new PensionIncomeSource(new PensionData(0, type), messageMgr);
            default:
                return null;
        }
    }
}
