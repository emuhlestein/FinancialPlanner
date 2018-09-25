package com.intelliviz.income.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.intelliviz.data.GovPension;
import com.intelliviz.data.IncomeSourceType;
import com.intelliviz.data.PensionData;
import com.intelliviz.data.PensionRules;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.data.Savings401kIncomeRules;
import com.intelliviz.data.SavingsData;
import com.intelliviz.data.SocialSecurityRules;
import com.intelliviz.db.entity.AbstractIncomeSource;
import com.intelliviz.db.entity.GovPensionEntity;
import com.intelliviz.db.entity.GovPensionEntityMapper;
import com.intelliviz.db.entity.IncomeSourceEntityBase;
import com.intelliviz.db.entity.PensionDataEntityMapper;
import com.intelliviz.db.entity.PensionIncomeEntity;
import com.intelliviz.db.entity.SavingsDataEntityMapper;
import com.intelliviz.db.entity.SavingsIncomeEntity;
import com.intelliviz.income.data.IncomeSourceViewData;
import com.intelliviz.lowlevel.util.RetirementConstants;

import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.lowlevel.util.RetirementConstants.OWNER_SELF;

public abstract class AbstractIncomeSourceHelper {
    private List<IncomeSourceEntityBase> mIncomeList;
    private RetirementOptions mRO;

    AbstractIncomeSourceHelper(List<IncomeSourceEntityBase> incomeList, RetirementOptions ro) {
        mIncomeList = incomeList;
        mRO = ro;
    }

    public abstract boolean isSpouseIncluded();

    public LiveData<IncomeSourceViewData> get() {
        int status = 0;
        if(isSpouseIncluded()) {
            status = RetirementConstants.EC_SPOUSE_INCLUDED;
        }
        List<AbstractIncomeSource> allSources = getAllIncomeSources(mIncomeList);
        MutableLiveData<IncomeSourceViewData> ldata = new MutableLiveData<>();
        ldata.setValue(new IncomeSourceViewData(allSources, status, ""));
        return ldata;
    }

    private List<AbstractIncomeSource> getAllIncomeSources(List<IncomeSourceEntityBase> list) {
        List<AbstractIncomeSource> incomeSourceList = new ArrayList<>();

        List<GovPension> gpList = new ArrayList<>();
        for(IncomeSourceEntityBase entity : list) {
            if(entity instanceof GovPensionEntity) {
                GovPension gp = GovPensionEntityMapper.map((GovPensionEntity)entity);
                if (includeIncomeSource(mRO, gp)) {
                    gpList.add(gp);
                    incomeSourceList.add(gp);
                }
            }

            if(entity instanceof PensionIncomeEntity) {
                PensionData pd = PensionDataEntityMapper.map((PensionIncomeEntity)entity);
                if (includeIncomeSource(mRO, pd)) {
                    pd.setRules(new PensionRules(mRO));
                    incomeSourceList.add(pd);
                }
            }

            if(entity instanceof SavingsIncomeEntity) {
                SavingsData sd = SavingsDataEntityMapper.map((SavingsIncomeEntity)entity);
                if (includeIncomeSource(mRO, sd)) {
                    sd.setRules(new Savings401kIncomeRules(mRO));
                    incomeSourceList.add(sd);
                }
            }
        }

        SocialSecurityRules.setRulesOnGovPensionEntities(gpList, mRO);

        return incomeSourceList;
    }

    private boolean includeIncomeSource(RetirementOptions ro, IncomeSourceType incomeSource) {
        if(isSpouseIncluded() || incomeSource.getOwner() == OWNER_SELF) {
            return true;
        } else {
            return false;
        }
    }

}
