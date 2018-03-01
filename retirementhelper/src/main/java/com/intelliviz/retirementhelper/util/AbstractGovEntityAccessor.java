package com.intelliviz.retirementhelper.util;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.SocialSecurityRules;
import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.viewmodel.LiveDataWrapper;

import java.util.List;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EC_NO_SPOUSE_BIRTHDATE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EC_PRINCIPLE_SPOUSE;

/**
 * Created by edm on 2/24/2018.
 */

public abstract class AbstractGovEntityAccessor implements EntityAccessor {
    private List<GovPensionEntity> mGpeList;
    private RetirementOptionsEntity mROE;

    public AbstractGovEntityAccessor(List<GovPensionEntity> gpeList, RetirementOptionsEntity roe) {
        mGpeList = gpeList;
        mROE = roe;
    }

    public LiveDataWrapper getEntity(long id) {
        if(id == 0) {
            // a new entity is requested. see if one can be created
            return createDefault();
        } else {
            GovPensionEntity gpe = getEntityById(id);
            if(gpe == null) {
                return null;
            }
            if(gpe.getSpouse() == 1) {
                gpe.setRules(new SocialSecurityRules(mROE.getEndAge(), mROE.getSpouseBirthdate()));
                return new LiveDataWrapper(gpe);
            } else {
                gpe.setRules(new SocialSecurityRules(mROE.getEndAge(), mROE.getBirthdate()));
                if(mGpeList.size() == 2) {
                    return new LiveDataWrapper(gpe, EC_PRINCIPLE_SPOUSE);
                } else {
                    return new LiveDataWrapper(gpe);
                }
            }
        }
    }

    public abstract int getMaxEntities();

    public abstract int getMaxErrorCode();

    private LiveDataWrapper createDefault() {
        int max_num = getMaxEntities();
        if(mGpeList.size() == max_num) {
            return new LiveDataWrapper(null, getMaxErrorCode());
        } else {
            if(mGpeList.size() == 0) {
                return createNew(false, false);
            } else {
                if(mGpeList.get(0).getSpouse() == 0) {
                    return createNew(true, false);
                } else {
                    return createNew(false, true);
                }
            }
        }
    }

    private LiveDataWrapper createNew(boolean spouse, boolean isPrincipleSpouse) {
        int year;
        AgeData age;
        String birthdate;
        if(spouse) {
            if(!SystemUtils.validateBirthday(mROE.getSpouseBirthdate())) {
                return new LiveDataWrapper(null, EC_NO_SPOUSE_BIRTHDATE);
            } else {
                birthdate = mROE.getSpouseBirthdate();
                year = SystemUtils.getBirthYear(birthdate);
                age = SocialSecurityRules.getFullRetirementAgeFromYear(year);
            }
        } else {
            birthdate = mROE.getBirthdate();
            year = SystemUtils.getBirthYear(birthdate);
            age = SocialSecurityRules.getFullRetirementAgeFromYear(year);
        }

        GovPensionEntity gpe = new GovPensionEntity(0, RetirementConstants.INCOME_TYPE_GOV_PENSION,
                "", "0", age, spouse ? 1 : 0);
        gpe.setRules(new SocialSecurityRules(mROE.getEndAge(), birthdate));
        if(isPrincipleSpouse) {
            return new LiveDataWrapper(gpe, EC_PRINCIPLE_SPOUSE);
        } else {
            return new LiveDataWrapper(gpe);
        }
    }

    private GovPensionEntity getEntityById(long id) {
        if(mGpeList.size() == 2) {
            if (mGpeList.get(0).getId() == id) {
                return mGpeList.get(0);
            } else if (mGpeList.get(1).getId() == id) {
                return mGpeList.get(1);
            } else {
                return null;
            }
        } else if(mGpeList.size() == 1) {
            if (mGpeList.get(0).getId() == id) {
                return mGpeList.get(0);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
