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
    private final static int INCLUDED = 1;
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
        String initWithdrawPercent = "4";
        String annualPercentIncrease = "0";

        SavingsData savingsData = new SavingsData(OWNER_PRIMARY, startAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease);

        selfBirthdate = AgeUtils.getBirthdate(new AgeData(60, 0));
        spouseBirthdate = AgeUtils.getBirthdate(new AgeData(65, 0));
        RetirementOptions ro = new RetirementOptions(mEndAge, mSpouseEndAge, selfBirthdate, spouseBirthdate);
        SavingsIncomeRules rules = new SavingsIncomeRules(ro, false);
        savingsData.setRules(rules);
        AgeData currentAge = AgeUtils.getAge(selfBirthdate);

        AgeData age = new AgeData(currentAge.getYear(), 0);
        IncomeData incomeData = savingsData.getIncomeData(age);
        assertEquals(1000.0, incomeData.getBalance(), 0);

        age = age.addYear(1);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(1000.0, incomeData.getBalance(), 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(5);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(1000.0, incomeData.getBalance(), 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(10);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(1000.0, incomeData.getBalance(), 0);
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
        SavingsIncomeRules rules = new SavingsIncomeRules(ro, false);
        savingsData.setRules(rules);
        AgeData currentAge = AgeUtils.getAge(selfBirthdate);
        AgeData age = new AgeData(currentAge.getYear()+1, 0);
        IncomeData incomeData = savingsData.getIncomeData(age);
        assertEquals(500, incomeData.getBalance(), 0);

        age = age.addYear(1);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(500, incomeData.getBalance(), 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(5);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(500, incomeData.getBalance(), 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(10);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(500, incomeData.getBalance(), 0);
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
        SavingsIncomeRules rules = createRules(primaryAge, spouseAge, true);
        savingsData.setRules(rules);
        AgeData currentAge = new AgeData(primaryAge);
        AgeData age = new AgeData(currentAge.getYear()+1, 0);
        IncomeData incomeData = savingsData.getIncomeData(age);
        assertEquals(1000.0, incomeData.getBalance(), 0);

        age = age.addYear(1);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(1000.0, incomeData.getBalance(), 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(5);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(1000.0, incomeData.getBalance(), 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(10);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(1000.0, incomeData.getBalance(), 0);

        stopMonthlyAdditionAge = new AgeData(65, 0);
        savingsData = new SavingsData(OWNER_PRIMARY, startAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease);

        savingsData.setRules(rules);
        currentAge = new AgeData(primaryAge);
        age = new AgeData(currentAge.getYear()+1, 0);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(1000.0, incomeData.getBalance(), 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(5);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(1000.0, incomeData.getBalance(), 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(6);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(1000.0, incomeData.getBalance(), 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(10);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(1000.0, incomeData.getBalance(), 0);

        primaryAge = new AgeData(60, 0);
        spouseAge = new AgeData(55, 0);
        rules = createRules(primaryAge, spouseAge, true);
        savingsData.setRules(rules);
        currentAge = new AgeData(primaryAge);

        age = new AgeData(currentAge.getYear()+1, 0);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(2200, incomeData.getBalance(), 0);

        age = new AgeData(currentAge.getYear()+5, 0);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(7000, incomeData.getBalance(), 0);

        age = new AgeData(currentAge.getYear()+6, 0);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(7000, incomeData.getBalance(), 0);

        age = new AgeData(currentAge.getYear()+10, 0);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(7000, incomeData.getBalance(), 0);
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
        SavingsIncomeRules rules = createRules(primaryAge, spouseAge, true);
        savingsData.setRules(rules);
        AgeData currentAge = new AgeData(primaryAge);
        AgeData age = new AgeData(currentAge.getYear()+1, 0);

        IncomeData incomeData = savingsData.getIncomeData(age);
        assertEquals(500.0, incomeData.getBalance(), 0);

        age = age.addYear(1);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(500.0, incomeData.getBalance(), 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(5);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(500.0, incomeData.getBalance(), 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(10);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(500.0, incomeData.getBalance(), 0);

        stopMonthlyAdditionAge = new AgeData(65, 0);
        savingsData = new SavingsData(OWNER_SPOUSE, startAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease);

        primaryAge = new AgeData(55, 0);
        spouseAge = new AgeData(60, 0);
        rules = createRules(primaryAge, spouseAge, true);
        savingsData.setRules(rules);
        currentAge = new AgeData(primaryAge);
        age = new AgeData(currentAge.getYear()+1, 0);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(1700, incomeData.getBalance(), 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(5);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(6500, incomeData.getBalance(), 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(6);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(6500, incomeData.getBalance(), 0);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(10);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(6500, incomeData.getBalance(), 0);
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
        SavingsIncomeRules rules = createRules(primaryAge, null, true);
        savingsData.setRules(rules);
        AgeData currentAge = new AgeData(primaryAge);
        AgeData age = new AgeData(currentAge.getYear()+1, 0);
        IncomeData incomeData = savingsData.getIncomeData(age);
        assertEquals(1104.71, incomeData.getBalance(), 0.01);

        age = age.addYear(1);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(1220.39, incomeData.getBalance(), 0.01);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(5);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(1645.31, incomeData.getBalance(), 0.01);

        age = new AgeData(currentAge.getYear(), 0);
        age = age.addYear(10);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(2707.04, incomeData.getBalance(), 0.01);
    }

    @Test
    public void testSimpleBalanceWithMonthlyAddition() {
        AgeData primaryEndAge = new AgeData(90, 0);
        AgeData spouseEndAge = new AgeData(90, 0);
        String ownerBirthdate = "01-01-1960";
        String spouseBirthdate = "01-06-1965";
        AgeData startAge = new AgeData(65, 0);
        AgeData stopMonthlyAdditionAge = new AgeData(90, 0);
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
        SavingsData savingsData = new SavingsData(0, 0, "SAVINGS", OWNER_PRIMARY, INCLUDED, startAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease);
        RetirementOptions ro = new  RetirementOptions(primaryEndAge, spouseEndAge, ownerBirthdate, spouseBirthdate);
        SavingsIncomeRules rules = new SavingsIncomeRules(ro, false);
        savingsData.setRules(rules);

        age = new AgeData(currentAge.getNumberOfMonths());
        IncomeData incomeData = savingsData.getIncomeData(age);
        assertEquals(1000.0, incomeData.getBalance(), 0);

        age = age.add(1);
        incomeData = savingsData.getIncomeData(age);
        double balance = incomeData.getBalance();
        assertEquals(balance, 1100.0, 0);
        assertEquals(0.0, incomeData.getMonthlyAmount(), 0);

        age = new AgeData(currentAge.getNumberOfMonths());
        age = age.add(12);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(2200.0, incomeData.getBalance(), 0);
        assertEquals(0.0, incomeData.getMonthlyAmount(), 0);

        age = new AgeData(currentAge.getNumberOfMonths());
        age = age.add(12 * 20);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(25000.0, incomeData.getBalance(), 0);
        assertEquals(0.0, incomeData.getMonthlyAmount(), 0);
    }

    @Test
    public void testMonthlyWithdraw() {
        AgeData primaryEndAge = new AgeData(90, 0);
        AgeData spouseEndAge = new AgeData(90, 0);
        String ownerBirthdate = "01-01-1960";
        String spouseBirthdate = "01-06-1965";
        AgeData endAge = new AgeData(90, 0);
        AgeData startWithdrawalAge = new AgeData(90, 0);
        AgeData stopMonthlyAdditionAge = new AgeData(90, 0);
        String startBalance = "1000";
        String interest = "0";
        String monthlyAddition = "100";
        String initWithdrawPercent = "10";
        String annualPercentIncrease = "0";

        RetirementOptions ro = new  RetirementOptions(primaryEndAge, spouseEndAge, ownerBirthdate, spouseBirthdate);
        SavingsIncomeRules rules = new SavingsIncomeRules(ro, true);
        SavingsData savingsData = new SavingsData(OWNER_PRIMARY, startWithdrawalAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease);
        savingsData.setRules(rules);

        AgeData currentAge = AgeUtils.getAge(ownerBirthdate);
        IncomeData incomeData = savingsData.getIncomeData(currentAge);
        assertEquals(1000.0, incomeData.getBalance(), 0);
        assertEquals(8.33, incomeData.getMonthlyAmount(), 0.01);

        AgeData age = new AgeData(currentAge.getNumberOfMonths() + 1);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(1100.0, incomeData.getBalance(), 0);
        assertEquals(9.1666, incomeData.getMonthlyAmount(), 0.01);

        age = new AgeData(currentAge.getNumberOfMonths() + 2);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(1200.0, incomeData.getBalance(), 0);
        assertEquals(10, incomeData.getMonthlyAmount(), 0.01);

        age = new AgeData(currentAge.getNumberOfMonths() + 12);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(2200, incomeData.getBalance(), 0);
        assertEquals(18.333, incomeData.getMonthlyAmount(), 0.001);

        age = new AgeData(currentAge.getNumberOfMonths() + 20);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(3000.0, incomeData.getBalance(), 0);
        assertEquals(25.0, incomeData.getMonthlyAmount(), 0.01);

        age = new AgeData(currentAge.getNumberOfMonths() + 25);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(3500.0, incomeData.getBalance(), 0);
        assertEquals(29.166, incomeData.getMonthlyAmount(), 0.01);

        age = new AgeData(currentAge.getNumberOfMonths() + 26);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(3600.0, incomeData.getBalance(),0);
        assertEquals(30.0, incomeData.getMonthlyAmount(), 0.01);

        age = new AgeData(currentAge.getNumberOfMonths() + 36);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(4600.0, incomeData.getBalance(), 0);
        assertEquals(38.333, incomeData.getMonthlyAmount(), 0.01);
    }

    @Test
    public void testAnnualPercentage() {
        AgeData primaryEndAge = new AgeData(90, 0);
        AgeData spouseEndAge = new AgeData(90, 0);
        String ownerBirthdate = "01-01-1960";
        String spouseBirthdate = "01-06-1965";
        AgeData endAge = new AgeData(90, 0);
        AgeData startWithdrawalAge = new AgeData(65, 0);
        AgeData stopMonthlyAdditionAge = new AgeData(60, 0);
        String startBalance = "1000";
        String interest = "10";
        String monthlyAddition = "0";
        String initWithdrawPercent = "10";
        String annualPercentIncrease = "0";

        RetirementOptions ro = new  RetirementOptions(primaryEndAge, spouseEndAge, ownerBirthdate, spouseBirthdate);
        SavingsIncomeRules rules = new SavingsIncomeRules(ro, true);
        SavingsData savingsData = new SavingsData(0, 0, "SAVINGS", OWNER_PRIMARY, INCLUDED, startWithdrawalAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease);
        savingsData.setRules(rules);

        AgeData currentAge = AgeUtils.getAge(ownerBirthdate);
        AgeData age = new AgeData(currentAge.getNumberOfMonths());

        IncomeData incomeData = savingsData.getIncomeData(age);
        assertEquals(1000.0, incomeData.getBalance(), 0);
        assertEquals(8.333, incomeData.getMonthlyAmount(), 0.001);

        age = new AgeData(currentAge.getNumberOfMonths() + 1);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(1008.333, incomeData.getBalance(), 0.001);

        age = new AgeData(currentAge.getNumberOfMonths() + 12);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(1104.713, incomeData.getBalance(), 0.01);

        interest = "3";
        savingsData = new SavingsData(0, 0, "SAVINGS", OWNER_PRIMARY, INCLUDED, startWithdrawalAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease);
        savingsData.setRules(rules);

        currentAge = AgeUtils.getAge(ownerBirthdate);
        age = new AgeData(currentAge.getNumberOfMonths());

        incomeData = savingsData.getIncomeData(age);
        assertEquals(1000.0, incomeData.getBalance(), 0);

        age = new AgeData(currentAge.getNumberOfMonths() + 12);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(1030.4159, incomeData.getBalance(), 0.0001);

        interest = "3";
        startBalance = "100000";
        initWithdrawPercent = "0";
        monthlyAddition = "0";
        annualPercentIncrease = "0";
        savingsData = new SavingsData(0, 0, "SAVINGS", OWNER_PRIMARY, INCLUDED, startWithdrawalAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease);
        savingsData.setRules(rules);

        currentAge = AgeUtils.getAge(ownerBirthdate);
        age = new AgeData(currentAge.getNumberOfMonths());

        incomeData = savingsData.getIncomeData(age);
        assertEquals(100000.0, incomeData.getBalance(), 0);

        age = new AgeData(currentAge.getNumberOfMonths() + 12);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(103041.5956, incomeData.getBalance(), 0.0001);

        age = new AgeData(currentAge.getNumberOfMonths() + 36);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(109405.1400, incomeData.getBalance(), 0.0001);

        age = new AgeData(currentAge.getNumberOfMonths() + 72);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(119694.8467, incomeData.getBalance(), 0.0001);
    }

    @Test
    public void testAnnualPercentageAndMonthlyAddition() {
        AgeData primaryEndAge = new AgeData(90, 0);
        AgeData spouseEndAge = new AgeData(90, 0);
        String ownerBirthdate = "01-01-1960";
        String spouseBirthdate = "01-06-1965";
        AgeData startAge = new AgeData(65, 0);
        AgeData stopMonthlyAdditionAge = new AgeData(60, 0);
        String startBalance = "1000000";
        String interest = "4";
        String monthlyAddition = "0";
        String initWithdrawPercent = "10";
        String annualPercentIncrease = "0";

        RetirementOptions ro = new  RetirementOptions(primaryEndAge, spouseEndAge, ownerBirthdate, spouseBirthdate);
        SavingsIncomeRules rules = new SavingsIncomeRules(ro, true);
        SavingsData savingsData = new SavingsData(0, 0, "SAVINGS", OWNER_PRIMARY, INCLUDED, startAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease);
        savingsData.setRules(rules);

        AgeData currentAge = AgeUtils.getAge(ownerBirthdate);
        AgeData age = new AgeData(currentAge.getNumberOfMonths());

        IncomeData incomeData = savingsData.getIncomeData(age);
        assertEquals(1000000.0, incomeData.getBalance(), 0);
        assertEquals(8333.33, incomeData.getMonthlyAmount(), 0.01);

        age = new AgeData(currentAge.getNumberOfMonths() + 12);
        incomeData = savingsData.getIncomeData(age);
        assertEquals(1040741.5429, incomeData.getBalance(), 0.0001);
        assertEquals(1040741.5429, incomeData.getBalance(), 0.0001);
    }

    private SavingsIncomeRules createRules(AgeData primaryAge, AgeData spouseAge, boolean override) {
        String primaryBirthdate = AgeUtils.getBirthdate(primaryAge);
        String spouseBirthdate = null;
        if(spouseAge != null) {
            spouseBirthdate = AgeUtils.getBirthdate(spouseAge);
        }
        RetirementOptions ro = new RetirementOptions(mEndAge, mSpouseEndAge, primaryBirthdate, spouseBirthdate);
        return new SavingsIncomeRules(ro, override);
    }
}
