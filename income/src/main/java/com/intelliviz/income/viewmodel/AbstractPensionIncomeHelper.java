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

    public PensionViewData get(long id, int owner) {
        if (id == 0) {
            if (canCreateNewIncomeSource()) {
                // create default pension income source
                return createDefault(owner);
            } else {
                return new PensionViewData(null, getOnlyOneSupportedErrorCode(), getOnlyOneSupportedErrorMessage());
            }
        } else {
            PensionRules pr = new PensionRules(mRO.getBirthdate(), mRO.getEndAge(), mRO.getSpouseBirthdate());
            mPD.setRules(pr);
            return new PensionViewData(mPD, RetirementConstants.EC_NO_ERROR, "");
        }
    }

    private PensionViewData createDefault(int owner) {
        PensionData pd = new PensionData(0, RetirementConstants.INCOME_TYPE_PENSION, "", owner,
                new AgeData(65, 0), "0", 0);
        PensionRules pr = new PensionRules(mRO.getBirthdate(),  mRO.getEndAge(), mRO.getSpouseBirthdate());
        pd.setRules(pr);
        return new PensionViewData(pd, RetirementConstants.EC_NO_ERROR, "");
    }
}
