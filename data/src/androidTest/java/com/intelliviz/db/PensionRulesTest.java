package com.intelliviz.db;

import com.intelliviz.data.IncomeData;
import com.intelliviz.data.IncomeDataAccessor;
import com.intelliviz.data.PensionData;
import com.intelliviz.data.PensionRules;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.lowlevel.data.AgeData;

import org.junit.Test;

import static com.intelliviz.lowlevel.util.RetirementConstants.OWNER_SELF;
import static com.intelliviz.lowlevel.util.RetirementConstants.OWNER_SPOUSE;
import static org.junit.Assert.assertEquals;

public class PensionRulesTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        //Context appContext = InstrumentationRegistry.getTargetContext();

        //assertEquals("com.intelliviz.retirementhelper.paid", appContext.getPackageName());
    }

    @Test
    public void testOwnerMonthlyBenefitsNoSpouse() {
        String fullMonthlyBenefit = "1000";
        AgeData startAge = new AgeData(65, 0);
        String ownerBirthdate = "01-01-1960";

        PensionData spouse1 = new PensionData(OWNER_SELF, startAge, fullMonthlyBenefit);
        RetirementOptions ro = new RetirementOptions(null, ownerBirthdate, null);
        PensionRules rules = new PensionRules(ro);
        spouse1.setRules(rules);
        IncomeDataAccessor accessor = spouse1.getIncomeDataAccessor();
        IncomeData incomeData = accessor.getIncomeData(new AgeData(64, 0));
        double amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 0, 0);

        incomeData = accessor.getIncomeData(new AgeData(65, 0));
        amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 1000, 0);

        incomeData = accessor.getIncomeData(new AgeData(66, 0));
        amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 1000, 0);

        incomeData = accessor.getIncomeData(new AgeData(69, 0));
        amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 1000, 0);

        incomeData = accessor.getIncomeData(new AgeData(70, 0));
        amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 1000, 0);

        incomeData = accessor.getIncomeData(new AgeData(90, 0));
        amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 1000, 0);
    }

    @Test
    public void testOwnerMonthlyBenefitsYoungerSpouse() {
        String fullMonthlyBenefit = "1000";
        AgeData startAge = new AgeData(65, 0);
        String ownerBirthdate = "01-01-1960";
        String spouseBirthdate = "01-01-1965";

        PensionData spouse1 = new PensionData(OWNER_SELF, startAge, fullMonthlyBenefit);
        RetirementOptions ro = new RetirementOptions(null, ownerBirthdate, spouseBirthdate);
        PensionRules rules = new PensionRules(ro);
        spouse1.setRules(rules);
        IncomeDataAccessor accessor = spouse1.getIncomeDataAccessor();
        IncomeData incomeData = accessor.getIncomeData(new AgeData(64, 0));
        double amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 0, 0);

        incomeData = accessor.getIncomeData(new AgeData(65, 0));
        amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 1000, 0);

        incomeData = accessor.getIncomeData(new AgeData(66, 0));
        amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 1000, 0);

        incomeData = accessor.getIncomeData(new AgeData(69, 0));
        amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 1000, 0);

        incomeData = accessor.getIncomeData(new AgeData(70, 0));
        amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 1000, 0);

        incomeData = accessor.getIncomeData(new AgeData(90, 0));
        amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 1000, 0);
    }

    @Test
    public void testOwnerMonthlyBenefitsOlderSpouse() {
        String fullMonthlyBenefit = "1000";
        AgeData startAge = new AgeData(65, 0);
        String ownerBirthdate = "01-01-1965";
        String spouseBirthdate = "01-05-1960";

        PensionData spouse1 = new PensionData(OWNER_SELF, startAge, fullMonthlyBenefit);
        RetirementOptions ro = new RetirementOptions(null, ownerBirthdate, spouseBirthdate);
        PensionRules rules = new PensionRules(ro);
        spouse1.setRules(rules);
        IncomeDataAccessor accessor = spouse1.getIncomeDataAccessor();
        IncomeData incomeData = accessor.getIncomeData(new AgeData(64, 0));
        double amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 0, 0);

        incomeData = accessor.getIncomeData(new AgeData(65, 0));
        amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 1000, 0);

        incomeData = accessor.getIncomeData(new AgeData(66, 0));
        amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 1000, 0);

        incomeData = accessor.getIncomeData(new AgeData(69, 0));
        amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 1000, 0);

        incomeData = accessor.getIncomeData(new AgeData(70, 0));
        amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 1000, 0);

        incomeData = accessor.getIncomeData(new AgeData(90, 0));
        amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 1000, 0);
    }

    @Test
    public void testSpouseMonthlyBenefits() {
        String spouseMonthlyBenefit = "500";
        AgeData startAge = new AgeData(65, 0);
        String spouseBirthdate = "01-01-1965";
        String principleSpouseBirthdate = "01-01-1960";

        PensionData spouse1 = new PensionData(OWNER_SPOUSE, startAge, spouseMonthlyBenefit);
        RetirementOptions ro = new RetirementOptions(null, principleSpouseBirthdate, spouseBirthdate);
        PensionRules rules = new PensionRules(ro);
        spouse1.setRules(rules);
        IncomeDataAccessor accessor = spouse1.getIncomeDataAccessor();
        IncomeData incomeData = accessor.getIncomeData(new AgeData(64, 0));
        double amount = incomeData.getMonthlyAmount();
        assertEquals(amount, 0, 0);

        incomeData = accessor.getIncomeData(new AgeData(65, 0));
        amount = incomeData.getMonthlyAmount();

        // spouse is 5 years younger than principle spouse, so at age 65 the amount should be 0
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
