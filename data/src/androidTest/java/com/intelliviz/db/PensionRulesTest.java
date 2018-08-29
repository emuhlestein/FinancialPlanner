package com.intelliviz.db;

import com.intelliviz.data.IncomeData;
import com.intelliviz.data.IncomeDataAccessor;
import com.intelliviz.data.PensionData;
import com.intelliviz.data.PensionRules;
import com.intelliviz.lowlevel.data.AgeData;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PensionRulesTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        //Context appContext = InstrumentationRegistry.getTargetContext();

        //assertEquals("com.intelliviz.retirementhelper.paid", appContext.getPackageName());
    }

    @Test
    public void testMontlyBenefits() {
        String fullMonthlyBenefit = "1000";
        String otherMonthlyBenefit = "500";
        AgeData startAge = new AgeData(65, 0);
        AgeData endAge = new AgeData(90, 0);
        String ownerBirthdate = "01-01-1960";
        String otherBirthdate = "01-01-1965";
        PensionData spouse1 = new PensionData(0, 0, "", 1, startAge, fullMonthlyBenefit, 0);
        PensionRules rules = new PensionRules(ownerBirthdate, endAge, otherBirthdate);
        spouse1.setRules(rules);

        IncomeDataAccessor accessor = spouse1.getIncomeDataAccessor();
        IncomeData incomeData = accessor.getIncomeData(new AgeData(64, 0));
        double amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 0, 0);

        incomeData = accessor.getIncomeData(new AgeData(65, 0));
        amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 1000, 0);

        PensionData spouse2 = new PensionData(0, 0, "", 0, startAge, otherMonthlyBenefit, 0);
        rules = new PensionRules(otherBirthdate, endAge, ownerBirthdate);
        spouse2.setRules(rules);

        accessor = spouse2.getIncomeDataAccessor();
        incomeData = accessor.getIncomeData(new AgeData(65, 0));
        amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 0, 0);

        incomeData = accessor.getIncomeData(new AgeData(66, 0));
        amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 0, 0);

        incomeData = accessor.getIncomeData(new AgeData(69, 0));
        amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 0, 0);

        incomeData = accessor.getIncomeData(new AgeData(70, 0));
        amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 500, 0);
    }
}
