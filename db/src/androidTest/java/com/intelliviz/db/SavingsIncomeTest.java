package com.intelliviz.db;

import com.intelliviz.data.IncomeData;
import com.intelliviz.data.IncomeDataAccessor;
import com.intelliviz.data.SavingsData;
import com.intelliviz.data.SavingsIncomeRules;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SavingsIncomeTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        //Context appContext = InstrumentationRegistry.getTargetContext();

        //assertEquals("com.intelliviz.retirementhelper.paid", appContext.getPackageName());
    }

    @Test
    public void testSimpleBalance() {
        String birthDate = "01-01-1960";
        AgeData endAge = new AgeData(90, 0);
        AgeData startAge = new AgeData(65, 0);
        AgeData stopMonthlyAdditionAge = new AgeData(60, 0);
        String startBalance = "1000";
        String interest = "0";
        String monthlyAddition = "0";
        String initWithdrawPercent = "0";
        String annualPercentIncrease = "0";

        SavingsData savingsData = new SavingsData(0, 0, "SAVINGS", startAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease, 0);

        SavingsIncomeRules rules = new SavingsIncomeRules(birthDate, endAge);

        savingsData.setRules(rules);

        IncomeDataAccessor accessor = savingsData.getIncomeDataAccessor();
        AgeData currentAge = AgeUtils.getAge(birthDate);
        AgeData age = new AgeData(currentAge.getNumberOfMonths());
        IncomeData incomeData = accessor.getIncomeData(currentAge);
        double balance = incomeData.getBalance();
        assertEquals(balance, 1000.0, 0);

        age = age.add(1);
        incomeData = accessor.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 1000.0, 0);

        age = new AgeData(currentAge.getNumberOfMonths());
        age = age.add(12);
        incomeData = accessor.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 1000.0, 0);

        age = new AgeData(currentAge.getNumberOfMonths());
        monthlyAddition = "100";
        startAge = age.add(1);
        savingsData = new SavingsData(0, 0, "SAVINGS", startAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease, 0);
        savingsData.setRules(rules);
        accessor = savingsData.getIncomeDataAccessor();

        age = new AgeData(currentAge.getNumberOfMonths());
        incomeData = accessor.getIncomeData(age);
        assertEquals(incomeData.getBalance(), 1000.0, 0);

        age = age.add(1);
        incomeData = accessor.getIncomeData(age);
        balance = incomeData.getBalance();
        assertEquals(balance, 1100.0, 0);
        assertEquals(incomeData.getMonthlyAmount(), 0.0, 0);

        age = new AgeData(currentAge.getNumberOfMonths());
        age = age.add(12);
        incomeData = accessor.getIncomeData(age);
        assertEquals(incomeData.getBalance(), 2200.0, 0);
        assertEquals(incomeData.getMonthlyAmount(), 0.0, 0);

        age = new AgeData(currentAge.getNumberOfMonths());
        age = age.add(12*20);
        incomeData = accessor.getIncomeData(age);
        assertEquals(incomeData.getBalance(), 2800.0, 0);
        assertEquals(incomeData.getMonthlyAmount(), 0.0, 0);
    }

    @Test
    public void testMonthlyWithdraw() {
        String birthDate = "01-01-1960";
        AgeData endAge = new AgeData(90, 0);
        AgeData startAge = new AgeData(65, 0);
        AgeData stopMonthlyAdditionAge = new AgeData(60, 0);
        String startBalance = "1000";
        String interest = "0";
        String monthlyAddition = "100";
        String initWithdrawPercent = "10";
        String annualPercentIncrease = "0";

        SavingsIncomeRules rules = new SavingsIncomeRules(birthDate, endAge);
        SavingsData savingsData = new SavingsData(0, 0, "SAVINGS", startAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease, 0);
        savingsData.setRules(rules);
        IncomeDataAccessor accessor = savingsData.getIncomeDataAccessor();

        AgeData currentAge = AgeUtils.getAge(birthDate);
        AgeData age = new AgeData(currentAge.getNumberOfMonths());

        IncomeData incomeData = accessor.getIncomeData(currentAge);
        assertEquals(incomeData.getBalance(), 1000.0, 0);
        assertEquals(incomeData.getMonthlyAmount(), 0.0, 0);

        startAge = age.add(1);
        savingsData = new SavingsData(0, 0, "SAVINGS", startAge, startBalance, interest, monthlyAddition,
                stopMonthlyAdditionAge, initWithdrawPercent, annualPercentIncrease, 0);
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
}
