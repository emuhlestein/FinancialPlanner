package com.intelliviz.income.viewmodel;

import com.intelliviz.data.IncomeData;
import com.intelliviz.data.IncomeDataAccessor;
import com.intelliviz.data.IncomeDetails;
import com.intelliviz.data.IncomeTypeRules;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.data.Savings401kIncomeRules;
import com.intelliviz.data.SavingsData;
import com.intelliviz.data.SavingsIncomeRules;
import com.intelliviz.income.data.SavingsViewData;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;
import com.intelliviz.lowlevel.util.RetirementConstants;
import com.intelliviz.lowlevel.util.SystemUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.intelliviz.lowlevel.util.RetirementConstants.BALANCE_STATE_EXHAUSTED;
import static com.intelliviz.lowlevel.util.RetirementConstants.BALANCE_STATE_LOW;
import static com.intelliviz.lowlevel.util.RetirementConstants.BI_EXHAUSTED_BALANCE;
import static com.intelliviz.lowlevel.util.RetirementConstants.BI_GOOD;
import static com.intelliviz.lowlevel.util.RetirementConstants.BI_LOW_BALANCE;
import static com.intelliviz.lowlevel.util.RetirementConstants.BI_PENALTY;

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
    public abstract boolean isSpouseIncluded();

    public SavingsViewData get(final long id, final int incomeType) {
        if (id == 0) {
            if (canCreateNewIncomeSource()) {
                // create default pension income source
                return createDefault(incomeType);
            } else {
                return new SavingsViewData(null, Collections.<IncomeDetails>emptyList(), isSpouseIncluded(), getOnlyOneSupportedErrorCode(), getOnlyOneSupportedErrorMessage());
            }
        } else {
            IncomeTypeRules sr;
            if(mSD.getType() == RetirementConstants.INCOME_TYPE_401K) {
                sr = new Savings401kIncomeRules(mRO.getBirthdate(), mRO.getEndAge(), mRO.getSpouseBirthdate());
            } else {
                sr = new SavingsIncomeRules(mRO.getBirthdate(), mRO.getEndAge(), mRO.getSpouseBirthdate());
            }
            mSD.setRules(sr);
            IncomeDataAccessor accessor = mSD.getIncomeDataAccessor();
            AgeData age = AgeUtils.getAge(mRO.getBirthdate());
            AgeData endAge = mRO.getEndAge();

            List<IncomeData> incomeDataList = new ArrayList();
            for(int year = age.getYear(); year <= endAge.getYear(); year++) {
                IncomeData benefitData = accessor.getIncomeData(new AgeData(year, 0));
                if(benefitData != null) {
                    incomeDataList.add(benefitData);
                }
            }
            List<IncomeDetails> incomeDetails = getIncomeDetailsList(incomeDataList);

            return new SavingsViewData(mSD, incomeDetails, isSpouseIncluded(), RetirementConstants.EC_NO_ERROR, "");
        }
    }

    private SavingsViewData createDefault(final int incomeType) {
        SavingsData sd = new SavingsData(0, incomeType, "", RetirementConstants.OWNER_SELF,
                new AgeData(65, 0), "0", "0", "0", new AgeData(65, 0), "0", "0", 0);
        IncomeTypeRules sr;

        int status = RetirementConstants.EC_NO_ERROR;
        if(isSpouseIncluded()) {
            status = RetirementConstants.EC_SPOUSE_INCLUDED;
        }

        if(incomeType == RetirementConstants.INCOME_TYPE_401K) {
            sr = new Savings401kIncomeRules(mRO.getBirthdate(), mRO.getEndAge(), mRO.getSpouseBirthdate());
        } else {
            sr = new SavingsIncomeRules(mRO.getBirthdate(), mRO.getEndAge(), mRO.getSpouseBirthdate());
        }
        sd.setRules(sr);
        return new SavingsViewData(sd, Collections.<IncomeDetails>emptyList(), isSpouseIncluded(), status, "");
    }

    // TODO make utils method
    private List<IncomeDetails> getIncomeDetailsList(List<IncomeData> incomeDataList) {
        List<IncomeDetails> incomeDetails = new ArrayList<>();

        for (IncomeData benefitData : incomeDataList) {
            AgeData age = benefitData.getAge();
            String amount = SystemUtils.getFormattedCurrency(benefitData.getMonthlyAmount());
            String balance = SystemUtils.getFormattedCurrency(benefitData.getBalance());
            String line1 = age.toString() + "   " + amount + "  " + balance;
            int benefitInfo = BI_GOOD;
            String message = "";
            boolean clickAccepted = false;
            if(benefitData.isPenalty()) {
                benefitInfo |= BI_PENALTY;
            }
            if(benefitData.getBalanceState() == BALANCE_STATE_LOW) {
                benefitInfo |= BI_LOW_BALANCE;
                message = "balance will be exhausted in less than a year";
                clickAccepted = true;
            } else if(benefitData.getBalanceState() == BALANCE_STATE_EXHAUSTED) {
                benefitInfo |= BI_EXHAUSTED_BALANCE;
            }

            IncomeDetails incomeDetail = new IncomeDetails(line1, benefitInfo, message);
            incomeDetail.setClickAccepted(clickAccepted);
            incomeDetails.add(incomeDetail);
        }

        return incomeDetails;
    }
}
