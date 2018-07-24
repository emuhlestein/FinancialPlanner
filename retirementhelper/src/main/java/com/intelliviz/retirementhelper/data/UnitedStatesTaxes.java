package com.intelliviz.retirementhelper.data;

/**
 * Created by edm on 3/20/2018.
 */

public class UnitedStatesTaxes {
    private static final int MARRIED_FILING_JOINTLY = 1;
    private static final int MARRIED_FILING_SEPARATELY = 2;
    private static final int SINGLE_HEAD_OF_HOUSE = 3;

    public void foo() {
        double totalAnnualSocialSecurity = 4200;
        double halfAnnualSocialSecurity = totalAnnualSocialSecurity / 2;
        double totalOtherAnnualIncome = 40000; // This amount does not include social security
        double totalAnnualIncome = totalOtherAnnualIncome + halfAnnualSocialSecurity;
        double exemptions = 0;
        double agi = totalAnnualIncome - exemptions;
        double baseAmount = 0;
        int marritalStatus = MARRIED_FILING_JOINTLY;
        switch(marritalStatus) {
            case MARRIED_FILING_JOINTLY:
                baseAmount = 32000;
                break;
            case MARRIED_FILING_SEPARATELY:
                break;
            case SINGLE_HEAD_OF_HOUSE:
                baseAmount = 25000;
                break;
            default:
                return;
        }

        if(baseAmount < agi) {

        } else {
            // no taxes on social security
        }



    }

}
