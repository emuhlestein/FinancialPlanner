package com.intelliviz.income.viewmodel;

import com.intelliviz.data.IncomeTypeRules;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.data.Savings401kIncomeRules;
import com.intelliviz.data.SavingsData;
import com.intelliviz.data.SavingsIncomeRules;
import com.intelliviz.income.data.SavingsViewData;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;

public abstract class AbstractSavingsIncomeHelper {
    private SavingsData mSD;
    private RetirementOptions mRO;

    public AbstractSavingsIncomeHelper(SavingsData sd, RetirementOptions ro) {
        mSD = sd;
        mRO = ro;
    }

    public abstract boolean canCreateNewIncomeSource();
    public abstract int getOnlyOneSupportedErrorCode();
    public abstract String getOnlyOneSupportedErrorMessage();

    public SavingsViewData get(final long id, final int incomeType) {
        if (id == 0) {
            if (canCreateNewIncomeSource()) {
                // create default pension income source
                return createDefault(incomeType);
            } else {
                return new SavingsViewData(null, getOnlyOneSupportedErrorCode(), getOnlyOneSupportedErrorMessage());
            }
        } else {
            IncomeTypeRules sr;
            if(incomeType == RetirementConstants.INCOME_TYPE_401K) {
                sr = new Savings401kIncomeRules(mRO.getBirthdate(), mRO.getEndAge(), mRO.getSpouseBirthdate());
            } else {
                sr = new SavingsIncomeRules(mRO.getBirthdate(), mRO.getEndAge(), mRO.getSpouseBirthdate());
            }
            mSD.setRules(sr);
            return new SavingsViewData(mSD, RetirementConstants.EC_NO_ERROR, "");
        }
    }

    private SavingsViewData createDefault(final int incomeType) {
        SavingsData sd = new SavingsData(0, incomeType, "", 1,
                new AgeData(65, 0), "0", "0", "0", new AgeData(65, 0), "0", "0", 0);
        IncomeTypeRules sr;
        if(incomeType == RetirementConstants.INCOME_TYPE_401K) {
            sr = new Savings401kIncomeRules(mRO.getBirthdate(), mRO.getEndAge(), mRO.getSpouseBirthdate());
        } else {
            sr = new SavingsIncomeRules(mRO.getBirthdate(), mRO.getEndAge(), mRO.getSpouseBirthdate());
        }
        sd.setRules(sr);
        return new SavingsViewData(sd, RetirementConstants.EC_NO_ERROR, "");
    }
}
