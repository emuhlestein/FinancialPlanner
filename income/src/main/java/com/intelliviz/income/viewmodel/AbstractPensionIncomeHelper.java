package com.intelliviz.income.viewmodel;

import android.app.Application;

import com.intelliviz.data.PensionData;
import com.intelliviz.data.PensionRules;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.income.data.PensionViewData;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;

public abstract class AbstractPensionIncomeHelper {
    private PensionData mPD;
    private RetirementOptions mRO;

    public AbstractPensionIncomeHelper(Application application, PensionData pd, RetirementOptions ro) {
        mPD = pd;
        mRO = ro;
        //EC_NO_SPOUSE_BIRTHDATE = application.getResources().getString(R.string.ec_no_spouse_birthdate);
    }

    public abstract boolean canCreateNewIncomeSource();

    public PensionViewData get(long id) {
        if (id == 0) {
            if (canCreateNewIncomeSource()) {
                // create default pension income source
                return createDefault();
            } else {
                return new PensionViewData(null, RetirementConstants.EC_NO_ERROR, "");
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
