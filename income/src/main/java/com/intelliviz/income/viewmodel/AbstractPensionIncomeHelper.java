package com.intelliviz.income.viewmodel;

import com.intelliviz.data.PensionData;
import com.intelliviz.data.PensionRules;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.income.data.PensionViewData;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;

public abstract class AbstractPensionIncomeHelper {
    private PensionData mPD;
    private RetirementOptions mRO;


    public AbstractPensionIncomeHelper(PensionData pd, RetirementOptions ro) {
        mPD = pd;
        mRO = ro;
    }

    public abstract boolean canCreateNewIncomeSource();
    public abstract int getOnlyOneSupportedErrorCode();
    public abstract String getOnlyOneSupportedErrorMessage();

    public PensionViewData get(long id) {
        if (id == 0) {
            if (canCreateNewIncomeSource()) {
                // create default pension income source
                return createDefault();
            } else {
                return new PensionViewData(null, getOnlyOneSupportedErrorCode(), getOnlyOneSupportedErrorMessage());
            }
        } else {
            PensionRules pr = new PensionRules(mRO.getBirthdate(), mPD.getAge(), mRO.getEndAge(), mPD.getBenefit());
            mPD.setRules(pr);
            return new PensionViewData(mPD, RetirementConstants.EC_NO_ERROR, "");
        }
    }

    private PensionViewData createDefault() {
        PensionData pd = new PensionData(0, RetirementConstants.INCOME_TYPE_PENSION, "",
                new AgeData(65, 0), "0", 0);
        PensionRules pr = new PensionRules(mRO.getBirthdate(), pd.getAge(), mRO.getEndAge(), pd.getBenefit());
        pd.setRules(pr);
        return new PensionViewData(pd, RetirementConstants.EC_NO_ERROR, "");
    }
}
