package com.intelliviz.income.viewmodel;

import android.app.Application;

import com.intelliviz.data.GovPension;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.data.SocialSecurityRules;
import com.intelliviz.db.entity.GovPensionEntity;
import com.intelliviz.db.entity.GovPensionEntityMapper;
import com.intelliviz.income.data.GovPensionViewData;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;

import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.income.ui.MessageMgr.EC_FOR_SELF_OR_SPOUSE;
import static com.intelliviz.income.ui.MessageMgr.EC_NO_ERROR;

public abstract class AbstractGovPensionHelper {
    private List<GovPensionEntity> mGpeList;
    private RetirementOptions mRO;

    public AbstractGovPensionHelper(Application application, List<GovPensionEntity> gpeList, RetirementOptions ro) {
        mGpeList = gpeList;
        mRO = ro;
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
                GovPension gp = createDefault(RetirementConstants.OWNER_PRIMARY);
                gpList.add(gp);
                SocialSecurityRules.setRulesOnGovPensionEntities(gpList, mRO, true);
                int status = EC_NO_ERROR;
                if(isSpouseIncluded()) {
                    status = EC_FOR_SELF_OR_SPOUSE;
                }
                return new GovPensionViewData(gp, isSpouseIncluded(), status, "");
            } else if (gpList.size() < getMaxGovPensions()) {
                if (!isSpouseIncluded()) {
                    return new GovPensionViewData(null, isSpouseIncluded(), getSupportedSpouseErrorCode(), getSupportedSpouseErrorMessage());
                }
                GovPension govPension = gpList.get(0);
                GovPension gp;
                if (govPension.getOwner() == RetirementConstants.OWNER_PRIMARY) {
                    gp = createDefault(RetirementConstants.OWNER_SPOUSE);
                } else {
                    gp = createDefault(RetirementConstants.OWNER_PRIMARY);
                }
                gpList.add(gp);
                SocialSecurityRules.setRulesOnGovPensionEntities(gpList, mRO, true);
                int status = EC_NO_ERROR;
                return new GovPensionViewData(gp, isSpouseIncluded(), status, "");
            } else {
                return new GovPensionViewData(null, isSpouseIncluded(), getSupportedSpouseErrorCode(), getSupportedSpouseErrorMessage());
            }
        } else {
            SocialSecurityRules.setRulesOnGovPensionEntities(gpList, mRO, true);
            GovPension gp = getGovPension(gpList, id);
            return new GovPensionViewData(gp, isSpouseIncluded(), EC_NO_ERROR, "");
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

    private GovPension createDefault(int owner) {
        return new GovPension(0, RetirementConstants.INCOME_TYPE_GOV_PENSION, "", owner,
                "0", new AgeData(65, 0));
    }
}
