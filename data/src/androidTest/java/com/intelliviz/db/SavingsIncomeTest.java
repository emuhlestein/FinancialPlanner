package com.intelliviz.db;

import com.intelliviz.data.IncomeData;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.data.SavingsData;
import com.intelliviz.data.SavingsIncomeRules;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;

import org.junit.Test;

import static com.intelliviz.lowlevel.util.RetirementConstants.OWNER_PRIMARY;
import static com.intelliviz.lowlevel.util.RetirementConstants.OWNER_SPOUSE;
import static org.junit.Assert.assertEquals;

public class SavingsIncomeTest {
    private AgeData mEndAge = new AgeData(90, 0);
    private AgeData mSpouseEndAge = new AgeData(90, 0);
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        //Context appContext = InstrumentationRegistry.getTargetContext();

        //assertEquals("com.intelliviz.retirementhelper.paid", appContext.getPackageName());
    }

    @Test
    public void testSimpleBalanceOwnerSelf() {
        String selfBirthdate;
        String spouseBirthdate;
        AgeData startAge = new AgeData(65, 0);
        AgeData stopMonthlyAdditionAge = new AgeData(60, 0);
        String startBalance = "1000";
        String interest = "0";
        String monthlyAddition = "0";
        String initWithdrawPercent = "0";
        String annualPercentIncrease = "0";

        SavingsData savingsData = new SavingsData(OWNER_PRIMARY, startAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease);

        selfBirthdate = AgeUtils.getBirthdate(new AgeData(60, 0));
        spouseBirthdate = AgeUtils.getBirthdate(new AgeData(65, 0));
        RetirementOptions ro = new RetirementOptions(mEndAge, mSpouseEndAge, selfBirthdate, spouseBirthdate);
        SavingsIncomeRules rules = new SavingsIncomeRules(ro);
        savingsData.setRules(rules);
        AgeData currentAge = AgeUtils.getAge(selfBirthdate);
        AgeData age = new AgeData(currentAge.getYear(), 0);
        IncomeData incomeData = savingsData.getIncomeData(age);
        double balance;
        balance = incomeData.getBalance();
        assertEquals(balance, 1000.0, 0);

        age = age.addYear(1);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 1000.0, 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(5);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 1000.0, 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(10);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 1000.0, 0);
    }

    @Test
    public void testSimpleBalanceOwnerSpouse() {
        String selfBirthdate;
        String spouseBirthdate;
        AgeData startAge = new AgeData(65, 0);
        AgeData stopMonthlyAdditionAge = new AgeData(60, 0);
        String startBalance = "500";
        String interest = "0";
        String monthlyAddition = "0";
        String initWithdrawPercent = "0";
        String annualPercentIncrease = "0";

        SavingsData savingsData = new SavingsData(OWNER_SPOUSE, startAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease);

        selfBirthdate = AgeUtils.getBirthdate(new AgeData(60, 0));
        spouseBirthdate = AgeUtils.getBirthdate(new AgeData(65, 0));
        RetirementOptions ro = new  RetirementOptions(mEndAge, mSpouseEndAge, selfBirthdate, spouseBirthdate);
        SavingsIncomeRules rules = new SavingsIncomeRules(ro);
        savingsData.setRules(rules);
        AgeData currentAge = AgeUtils.getAge(selfBirthdate);
        AgeData age = new AgeData(currentAge.getYear()+1, 0);
        IncomeData incomeData = savingsData.getIncomeData(age);
        double balance = incomeData.getBalance();
        assertEquals(balance, 500, 0);

        age = age.addYear(1);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 500, 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(5);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 500, 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(10);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 500, 0);
    }

    @Test
    public void testMonthlyAdditionPrimary() {
        AgeData startAge = new AgeData(65, 0);
        AgeData stopMonthlyAdditionAge = new AgeData(60, 0);
        String startBalance = "1000";
        String interest = "0";
        String monthlyAddition = "100";
        String initWithdrawPercent = "0";
        String annualPercentIncrease = "0";

        SavingsData savingsData = new SavingsData(OWNER_PRIMARY, startAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease);

        AgeData primaryAge = new AgeData(65, 0);
        AgeData spouseAge = new AgeData(60, 0);
        SavingsIncomeRules rules = createRules(primaryAge, spouseAge);
        savingsData.setRules(rules);
        AgeData currentAge = new AgeData(primaryAge);
        AgeData age = new AgeData(currentAge.getYear()+1, 0);
        IncomeData incomeData = savingsData.getIncomeData(age);
        double balance;
        balance = incomeData.getBalance();
        assertEquals(balance, 1000.0, 0);

        age = age.addYear(1);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 1000.0, 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(5);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 1000.0, 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(10);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 1000.0, 0);

        stopMonthlyAdditionAge = new AgeData(65, 0);
        savingsData = new SavingsData(OWNER_PRIMARY, startAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease);

        savingsData.setRules(rules);
        currentAge = new AgeData(primaryAge);
        age = new AgeData(currentAge.getYear()+1, 0);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 1000.0, 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(5);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 1000.0, 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(6);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 1000.0, 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(10);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 1000.0, 0);

        primaryAge = new AgeData(60, 0);
        spouseAge = new AgeData(55, 0);
        rules = createRules(primaryAge, spouseAge);
        savingsData.setRules(rules);
        currentAge = new AgeData(primaryAge);

        age = new AgeData(currentAge.getYear()+1, 0);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 2200, 0);

        age = new AgeData(currentAge.getYear()+5, 0);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 7000, 0);

        age = new AgeData(currentAge.getYear()+6, 0);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 7000, 0);

        age = new AgeData(currentAge.getYear()+10, 0);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 7000, 0);
    }

    @Test
    public void testMonthlyAdditionOlderSpouse() {
        AgeData startAge = new AgeData(65, 0);
        AgeData stopMonthlyAdditionAge = new AgeData(60, 0);
        String startBalance = "500";
        String interest = "0";
        String monthlyAddition = "100";
        String initWithdrawPercent = "0";
        String annualPercentIncrease = "0";

        SavingsData savingsData = new SavingsData(OWNER_SPOUSE, startAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease);

        AgeData primaryAge = new AgeData(60, 0);
        AgeData spouseAge = new AgeData(65, 0);
        SavingsIncomeRules rules = createRules(primaryAge, spouseAge);
        savingsData.setRules(rules);
        AgeData currentAge = new AgeData(primaryAge);
        AgeData age = new AgeData(currentAge.getYear()+1, 0);

        IncomeData incomeData = savingsData.getIncomeData(age);
        double balance;
        balance = incomeData.getBalance();
        assertEquals(balance, 500.0, 0);

        age = age.addYear(1);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 500.0, 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(5);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 500.0, 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(10);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 500.0, 0);

        stopMonthlyAdditionAge = new AgeData(65, 0);
        savingsData = new SavingsData(OWNER_SPOUSE, startAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease);

        primaryAge = new AgeData(55, 0);
        spouseAge = new AgeData(60, 0);
        rules = createRules(primaryAge, spouseAge);
        savingsData.setRules(rules);
        currentAge = new AgeData(primaryAge);
        age = new AgeData(currentAge.getYear()+1, 0);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 1700, 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(5);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 6500, 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(6);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 6500, 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(10);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 6500, 0);
    }

    @Test
    public void testAnnualInterestPrimary() {
        AgeData startAge = new AgeData(65, 0);
        AgeData stopMonthlyAdditionAge = new AgeData(60, 0);
        String startBalance = "1000";
        String interest = "10";
        String monthlyAddition = "0";
        String initWithdrawPercent = "0";
        String annualPercentIncrease = "0";

        SavingsData savingsData = new SavingsData(OWNER_PRIMARY, startAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease);

        AgeData primaryAge = new AgeData(65, 0);
        SavingsIncomeRules rules = createRules(primaryAge, null);
        savingsData.setRules(rules);
        AgeData currentAge = new AgeData(primaryAge);
        AgeData age = new AgeData(currentAge.getYear()+1, 0);
        IncomeData incomeData = savingsData.getIncomeData(age);
        double balance;
        balance = incomeData.getBalance();
        assertEquals(balance, 1104.71, 0.01);

        age = age.addYear(1);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 1220.39, 0.01);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(5);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 1645.31, 0.01);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(10);
        incomeData = savingsData.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 2707.04, 0.01);
    }


/*
    @Test
    public void testSimpleBalanceWithMonthlyAddition() {
        String ownerBirthdate = "01-01-1960";
        String spouseBirthdate = "01-06-1965";
        AgeData startAge = new AgeData(65, 0);
        AgeData stopMonthlyAdditionAge = new AgeData(60, 0);
        String startBalance = "1000";
        String interest = "0";
        String monthlyAddition = "0";
        String initWithdrawPercent = "0";
        String annualPercentIncrease = "0";
        AgeData currentAge = AgeUtils.getAge(ownerBirthdate);
        AgeData age = new AgeData(currentAge);
        age = new AgeData(currentAge.getNumberOfMonths());
        monthlyAddition = "100";
        startAge = age.add(1);
        SavingsData savingsData = new SavingsData(0, 0, "SAVINGS", OWNER_SELF, startAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease, 0);
        RetirementOptions ro = new  RetirementOptions(ownerBirthdate, spouseBirthdate, mEndAge);
        SavingsIncomeRules rules = new SavingsIncomeRules(ro);
        savingsData.setRules(rules);
        IncomeDataAccessor accessor = savingsData.getIncomeDataAccessor();

        age = new AgeData(currentAge.getNumberOfMonths());
        IncomeData incomeData = accessor.getIncomeData(age);
        assertEquals(incomeData.getBalance(), 1000.0, 0);

        age = age.add(1);
        incomeData = accessor.getIncomeData(age);
        double balance = incomeData.getBalance();
        assertEquals(balance, 1100.0, 0);
        assertEquals(incomeData.getMonthlyAmount(), 0.0, 0);

        age = new AgeData(currentAge.getNumberOfMonths());
        age = age.add(12);
        incomeData = accessor.getIncomeData(age);
        assertEquals(incomeData.getBalance(), 2200.0, 0);
        assertEquals(incomeData.getMonthlyAmount(), 0.0, 0);

        age = new AgeData(currentAge.getNumberOfMonths());
        age = age.add(12 * 20);
        incomeData = accessor.getIncomeData(age);
        assertEquals(incomeData.getBalance(), 2800.0, 0);
        assertEquals(incomeData.getMonthlyAmount(), 0.0, 0);
    }

    @Test
    public void testMonthlyWithdraw() {
        String ownerBirthdate = "01-01-1960";
        String spouseBirthdate = "01-06-1965";
        AgeData endAge = new AgeData(90, 0);
        AgeData startAge = new AgeData(65, 0);
        AgeData stopMonthlyAdditionAge = new AgeData(60, 0);
        String startBalance = "1000";
        String interest = "0";
        String monthlyAddition = "100";
        String initWithdrawPercent = "10";
        String annualPercentIncrease = "0";

        RetirementOptions ro = new  RetirementOptions(ownerBirthdate, spouseBirthdate, mEndAge);
        SavingsIncomeRules rules = new SavingsIncomeRules(ro);
        SavingsData savingsData = new SavingsData(OWNER_SELF, startAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease);
        savingsData.setRules(rules);
        IncomeDataAccessor accessor = savingsData.getIncomeDataAccessor();

        AgeData currentAge = AgeUtils.getAge(ownerBirthdate);
        AgeData age = new AgeData(currentAge.getNumberOfMonths());

        IncomeData incomeData = accessor.getIncomeData(currentAge);
        assertEquals(incomeData.getBalance(), 1000.0, 0);
        assertEquals(incomeData.getMonthlyAmount(), 0.0, 0);

        startAge = age.add(1);
        savingsData = new SavingsData(OWNER_SELF, startAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease);
        savingsData.setRules(rules);
        accessor = savingsData.getIncomeDataAccessor();

        age = new AgeData(currentAge.getNumberOfMonths());
        incomeData = accessor.getIncomeData(age);
        assertEquals(incomeData.getBalance(), 1000.0, 0);
        assertEquals(incomeData.getMonthlyAmount(), 0.0, 0);

        age = new AgeData(currentAge.getNumberOfMonths() + 1);
        incomeData = accessor.getIncomeData(age);
        assertEquals(incomeData.getBalance(), 990.0, 0);
        assertEquals(incomeData.getMonthlyAmount(), 110.0, 0);

        age = new AgeData(currentAge.getNumberOfMonths() + 12);
        incomeData = accessor.getIncomeData(age);
        assertEquals(incomeData.getBalance(), 880.0, 0);
        assertEquals(incomeData.getMonthlyAmount(), 110.0, 0);

        age = new AgeData(currentAge.getNumberOfMonths() + 20);
        incomeData = accessor.getIncomeData(age);
        assertEquals(incomeData.getBalance(), 600.0, 0);
        assertEquals(incomeData.getMonthlyAmount(), 110.0, 0);

        age = new AgeData(currentAge.getNumberOfMonths() + 25);
        incomeData = accessor.getIncomeData(age);
        assertEquals(incomeData.getBalance(), 50.0, 0);
        assertEquals(incomeData.getMonthlyAmount(), 110.0, 0);

        age = new AgeData(currentAge.getNumberOfMonths() + 26);
        incomeData = accessor.getIncomeData(age);
        assertEquals(incomeData.getBalance(), 0.0, 0);
        assertEquals(incomeData.getMonthlyAmount(), 50.0, 0);

        age = new AgeData(currentAge.getNumberOfMonths() + 36);
        incomeData = accessor.getIncomeData(age);
        assertEquals(incomeData.getBalance(), 0.0, 0);
        assertEquals(incomeData.getMonthlyAmount(), 0.0, 0);
    }

    @Test
    public void testAnnualPercentage() {
        String ownerBirthdate = "01-01-1960";
        String spouseBirthdate = "01-06-1965";
        AgeData endAge = new AgeData(90, 0);
        AgeData startAge = new AgeData(65, 0);
        AgeData stopMonthlyAdditionAge = new AgeData(60, 0);
        String startBalance = "1000";
        String interest = "2";
        String monthlyAddition = "0";
        String initWithdrawPercent = "10";
        String annualPercentIncrease = "0";

        RetirementOptions ro = new  RetirementOptions(ownerBirthdate, spouseBirthdate, mEndAge);
        SavingsIncomeRules rules = new SavingsIncomeRules(ro);
        SavingsData savingsData = new SavingsData(0, 0, "SAVINGS", OWNER_SELF, startAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease, 0);
        savingsData.setRules(rules);
        IncomeDataAccessor accessor = savingsData.getIncomeDataAccessor();

        AgeData currentAge = AgeUtils.getAge(ownerBirthdate);
        AgeData age = new AgeData(currentAge.getNumberOfMonths());

        IncomeData incomeData = accessor.getIncomeData(age);
        assertEquals(incomeData.getBalance(), 1000.0, 0);
        assertEquals(incomeData.getMonthlyAmount(), 0.0, 0);

        age = new AgeData(currentAge.getNumberOfMonths() + 12);
        incomeData = accessor.getIncomeData(age);
        assertEquals(incomeData.getBalance(), 1020.1843, 0.0001);
        assertEquals(incomeData.getMonthlyAmount(), 0.0, 0);

        interest = "3";
        savingsData = new SavingsData(0, 0, "SAVINGS", 1, startAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease, 0);
        savingsData.setRules(rules);
        accessor = savingsData.getIncomeDataAccessor();

        currentAge = AgeUtils.getAge(ownerBirthdate);
        age = new AgeData(currentAge.getNumberOfMonths());

        incomeData = accessor.getIncomeData(age);
        assertEquals(incomeData.getBalance(), 1000.0, 0);
        assertEquals(incomeData.getMonthlyAmount(), 0.0, 0);

        age = new AgeData(currentAge.getNumberOfMonths() + 12);
        incomeData = accessor.getIncomeData(age);
        assertEquals(incomeData.getBalance(), 1030.4159, 0.0001);
        assertEquals(incomeData.getMonthlyAmount(), 0.0, 0);

        interest = "3";
        startBalance = "100000";
        initWithdrawPercent = "0";
        monthlyAddition = "0";
        annualPercentIncrease = "0";
        savingsData = new SavingsData(0, 0, "SAVINGS", 1, startAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease, 0);
        savingsData.setRules(rules);
        accessor = savingsData.getIncomeDataAccessor();

        currentAge = AgeUtils.getAge(ownerBirthdate);
        age = new AgeData(currentAge.getNumberOfMonths());

        incomeData = accessor.getIncomeData(age);
        assertEquals(incomeData.getBalance(), 100000.0, 0);
        assertEquals(incomeData.getMonthlyAmount(), 0.0, 0);

        age = new AgeData(currentAge.getNumberOfMonths() + 12);
        incomeData = accessor.getIncomeData(age);
        assertEquals(incomeData.getBalance(), 103041.5956, 0.0001);
        assertEquals(incomeData.getMonthlyAmount(), 0.0, 0);

        age = new AgeData(currentAge.getNumberOfMonths() + 36);
        incomeData = accessor.getIncomeData(age);
        assertEquals(incomeData.getBalance(), 109405.1400, 0.0001);
        assertEquals(incomeData.getMonthlyAmount(), 0.0, 0);

        age = new AgeData(currentAge.getNumberOfMonths() + 72);
        incomeData = accessor.getIncomeData(age);
        assertEquals(incomeData.getBalance(), 119694.8467, 0.0001);
        assertEquals(incomeData.getMonthlyAmount(), 0.0, 0);
    }

    @Test
    public void testAnnualPercentageANdMonthlyAddition() {
        String ownerBirthdate = "01-01-1960";
        String spouseBirthdate = "01-06-1965";
        AgeData startAge = new AgeData(65, 0);
        AgeData stopMonthlyAdditionAge = new AgeData(60, 0);
        String startBalance = "1000000";
        String interest = "4";
        String monthlyAddition = "0";
        String initWithdrawPercent = "10";
        String annualPercentIncrease = "0";

        RetirementOptions ro = new  RetirementOptions(ownerBirthdate, spouseBirthdate, mEndAge);
        SavingsIncomeRules rules = new SavingsIncomeRules(ro);
        SavingsData savingsData = new SavingsData(0, 0, "SAVINGS", OWNER_SELF, startAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease, 0);
        savingsData.setRules(rules);
        IncomeDataAccessor accessor = savingsData.getIncomeDataAccessor();

        AgeData currentAge = AgeUtils.getAge(ownerBirthdate);
        AgeData age = new AgeData(currentAge.getNumberOfMonths());

        IncomeData incomeData = accessor.getIncomeData(age);
        assertEquals(incomeData.getBalance(), 100000.0, 0);
        assertEquals(incomeData.getMonthlyAmount(), 0.0, 0);

        age = new AgeData(currentAge.getNumberOfMonths() + 12);
        incomeData = accessor.getIncomeData(age);
        assertEquals(incomeData.getBalance(), 1020.1843, 0.0001);
        assertEquals(incomeData.getMonthlyAmount(), 0.0, 0);
    }
    */

    private SavingsIncomeRules createRules(AgeData primaryAge, AgeData spouseAge) {
        String primaryBirthdate = AgeUtils.getBirthdate(primaryAge);
        String spouseBirthdate = null;
        if(spouseAge != null) {
            spouseBirthdate = AgeUtils.getBirthdate(spouseAge);
        }
        RetirementOptions ro = new RetirementOptions(mEndAge, mSpouseEndAge, primaryBirthdate, spouseBirthdate);
        return new SavingsIncomeRules(ro);
    }
}
