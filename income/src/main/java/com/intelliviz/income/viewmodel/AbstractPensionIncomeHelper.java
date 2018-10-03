package com.intelliviz.income.viewmodel;

import com.intelliviz.data.PensionData;
import com.intelliviz.data.PensionRules;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.income.data.PensionViewData;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;

import static com.intelliviz.income.ui.MessageMgr.EC_NO_ERROR;
import static com.intelliviz.income.ui.MessageMgr.EC_SPOUSE_INCLUDED;

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
    public abstract boolean isSpouseIncluded();

    public PensionViewData get(long id) {
        if (id == 0) {
            if (canCreateNewIncomeSource()) {
                // create default pension income source
                return createDefault();
            } else {
                return new PensionViewData(null, isSpouseIncluded(), getOnlyOneSupportedErrorCode(), getOnlyOneSupportedErrorMessage());
            }
        } else {
            PensionRules pr = new PensionRules(mRO);
            mPD.setRules(pr);
            return new PensionViewData(mPD, isSpouseIncluded(), EC_NO_ERROR, "");
        }
    }

    private PensionViewData createDefault() {
        PensionData pd = new PensionData(0, RetirementConstants.INCOME_TYPE_PENSION, "", RetirementConstants.OWNER_PRIMARY,
                new AgeData(65, 0), "0");
        PensionRules pr = new PensionRules(mRO);
        pd.setRules(pr);

        int status = EC_NO_ERROR;
        if(isSpouseIncluded()) {
            status = EC_SPOUSE_INCLUDED;
        }
        return new PensionViewData(pd, isSpouseIncluded(), status, "");
    }
}
