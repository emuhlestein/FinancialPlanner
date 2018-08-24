package com.intelliviz.income.util;


import com.intelliviz.data.GovPension;
import com.intelliviz.data.SocialSecurityRules;
import com.intelliviz.db.entity.GovPensionEntity;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.income.viewmodel.ViewData;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;
import com.intelliviz.lowlevel.util.RetirementConstants;

import java.util.List;

import static com.intelliviz.lowlevel.util.RetirementConstants.EC_NO_SPOUSE_BIRTHDATE;
import static com.intelliviz.lowlevel.util.RetirementConstants.EC_PRINCIPLE_SPOUSE;

/**
 * Created by edm on 2/24/2018.
 *
 */
// TODO remove class, it is now obsolete
public abstract class AbstractGovEntityAccessor implements EntityAccessor {
    private List<GovPension> mGpList;
    private RetirementOptionsEntity mROE;

    public AbstractGovEntityAccessor(List<GovPension> gpList, RetirementOptionsEntity roe) {
        mGpList = gpList;
        mROE = roe;
    }

    public ViewData getEntity(long id) {
        if(id == 0) {
            // a new entity is requested. see if one can be created
            return createDefault();
        } else {
            GovPension gp = getEntityById(id);
            if(gp == null) {
                return null;
            }
            if(gp.isSpouse()) {
                gp.setRules(new SocialSecurityRules(mROE.getEndAge(), mROE.getSpouseBirthdate()));
                return new ViewData(gp);
            } else {
                gp.setRules(new SocialSecurityRules(mROE.getEndAge(), mROE.getBirthdate()));
                if(mGpList.size() == 2) {
                    return new ViewData(gp, EC_PRINCIPLE_SPOUSE);
                } else {
                    return new ViewData(gp);
                }
            }
        }
    }

    public abstract int getMaxEntities();

    public abstract int getMaxErrorCode();

    private ViewData createDefault() {
        int max_num = getMaxEntities();
        if(mGpList.size() == max_num) {
            int message = getMaxErrorCode();
            return new ViewData(null, getMaxErrorCode());
        } else {
            if(mGpList.size() == 0) {
                return createNew(false, false);
            } else {
                if(!mGpList.get(0).isSpouse()) {
                    return createNew(true, false);
                } else {
                    return createNew(false, true);
                }
            }
        }
    }

    private ViewData createNew(boolean spouse, boolean isPrincipleSpouse) {
        int year;
        AgeData age;
        String birthdate;
        if(spouse) {
            if(!AgeUtils.validateBirthday(mROE.getSpouseBirthdate())) {
                return new ViewData(null, EC_NO_SPOUSE_BIRTHDATE);
            } else {
                birthdate = mROE.getSpouseBirthdate();
                year = AgeUtils.getBirthYear(birthdate);
                age = SocialSecurityRules.getFullRetirementAgeFromYear(year);
            }
        } else {
            birthdate = mROE.getBirthdate();
            year = AgeUtils.getBirthYear(birthdate);
            age = SocialSecurityRules.getFullRetirementAgeFromYear(year);
        }

        GovPensionEntity gpe = new GovPensionEntity(0, RetirementConstants.INCOME_TYPE_GOV_PENSION,
                "", 1, "0", age, spouse ? 1 : 0);
        //gpe.setRules(new SocialSecurityRules(mROE.getEndAge(), birthdate));
        if(isPrincipleSpouse) {
            return new ViewData(gpe, EC_PRINCIPLE_SPOUSE);
        } else {
            return new ViewData(gpe);
        }
    }

    private GovPension getEntityById(long id) {
        if(mGpList.size() == 2) {
            if (mGpList.get(0).getId() == id) {
                return mGpList.get(0);
            } else if (mGpList.get(1).getId() == id) {
                return mGpList.get(1);
            } else {
                return null;
            }
        } else if(mGpList.size() == 1) {
            if (mGpList.get(0).getId() == id) {
                return mGpList.get(0);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
