package com.intelliviz.income.viewmodel;

import android.app.Application;

import com.intelliviz.data.GovPension;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.data.SocialSecurityRules;
import com.intelliviz.db.entity.GovPensionEntity;
import com.intelliviz.db.entity.GovPensionEntityMapper;
import com.intelliviz.income.R;
import com.intelliviz.income.data.GovPensionViewData;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGovPensionHelper {
    private List<GovPensionEntity> mGpeList;
    private RetirementOptions mRO;
    private static String EC_NO_SPOUSE_BIRTHDATE;

    public AbstractGovPensionHelper(Application application, List<GovPensionEntity> gpeList, RetirementOptions ro) {
        mGpeList = gpeList;
        mRO = ro;
        EC_NO_SPOUSE_BIRTHDATE = application.getResources().getString(R.string.ec_no_spouse_birthdate);
    }

    public abstract int getMaxGovPensions();
    public abstract int getSupportedSpouseErrorCode();
    public abstract String getSupportedSpouseErrorMessage();
    public abstract boolean isSpouseIncluded();

    public GovPensionViewData get(long id) {

        List<GovPension> gpList = new ArrayList<>();
        for(GovPensionEntity gpe : mGpeList) {
            GovPension gp = GovPensionEntityMapper.map(gpe);
            gpList.add(gp);
        }

        // if id is 0, we're adding a new default record
        if(id == 0) {
            if (gpList.isEmpty()) {
                GovPension gp = createDefault();
                gpList.add(gp);
                SocialSecurityRules.setRulesOnGovPensionEntities(gpList, mRO);
                return new GovPensionViewData(gp, isSpouseIncluded(), RetirementConstants.EC_NO_ERROR, "");
            } else if(gpList.size() < getMaxGovPensions()) {
                // second one is a spouse
                if(mRO.getSpouseBirthdate().equals("0")) {
                    return new GovPensionViewData(null, isSpouseIncluded(), RetirementConstants.EC_NO_SPOUSE_BIRTHDATE, EC_NO_SPOUSE_BIRTHDATE);
                } else {
                    GovPension gp = createDefault();
                    gpList.add(gp);
                    SocialSecurityRules.setRulesOnGovPensionEntities(gpList, mRO);
                    return new GovPensionViewData(gp, isSpouseIncluded(), RetirementConstants.EC_NO_ERROR, "");
                }
            } else {
                return new GovPensionViewData(null, isSpouseIncluded(), getSupportedSpouseErrorCode(), getSupportedSpouseErrorMessage());
            }
        } else {
            SocialSecurityRules.setRulesOnGovPensionEntities(gpList, mRO);
            GovPension gp = getGovPension(gpList, id);
            return new GovPensionViewData(gp, isSpouseIncluded(), RetirementConstants.EC_NO_ERROR, "");
        }
    }

    private GovPension getGovPension(List<GovPension> gpList, long id) {
        if(gpList == null || gpList.isEmpty()) {
            return null;
        }

        if(gpList.size() == 1) {
            return gpList.get(0);
        } else {
            if(gpList.get(0).getId() == id) {
                return gpList.get(0);
            } else {
                return gpList.get(1);
            }
        }
    }

    private GovPension createDefault() {
        return new GovPension(0, RetirementConstants.INCOME_TYPE_GOV_PENSION, "", RetirementConstants.OWNER_SELF,
                "0", new AgeData(65, 0), false);
    }
}
