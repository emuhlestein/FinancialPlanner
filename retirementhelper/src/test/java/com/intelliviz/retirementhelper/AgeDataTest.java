package com.intelliviz.retirementhelper;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.util.AgeUtils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class AgeDataTest {

    @Test
    public void testAgeData() {
        AgeData age = new AgeData(60, 5);

        assertEquals(age.getYear(), 60);

        age.addMonths(30);

        assertEquals(age.getYear(), 62);
        assertEquals(age.getMonth(), 11);

        age.addMonths(1);

        assertEquals(age.getYear(), 63);
        assertEquals(age.getMonth(), 0);
    }

    @Test
    public void testAddAge() {
        AgeData age1 = new AgeData(60, 8);
        AgeData age2 = new AgeData(59, 0);

        AgeData ageDiff = age1.subtract(age2);

        assertEquals(ageDiff.getYear(), 1);
        assertEquals(ageDiff.getMonth(), 8);

        age1 = new AgeData(60, 2);
        age2 = new AgeData(59, 11);

        ageDiff = age1.subtract(age2);

        assertEquals(ageDiff.getYear(), 0);
        assertEquals(ageDiff.getMonth(), 3);

        age1 = new AgeData(58, 2);
        age2 = new AgeData(59, 11);

        ageDiff = age1.subtract(age2);

        assertEquals(ageDiff.getYear(), -1);
        assertEquals(ageDiff.getMonth(), -9);
    }

    @Test
    public void testSubtractAge() {
        AgeData age1 = new AgeData(62, 10);
        AgeData age2 = new AgeData(58, 8);
        AgeData age = age1.subtract(age2);
        verifyAge(age, new AgeData(4, 2));

        age1 = new AgeData(58, 8);
        age2 = new AgeData(62, 10);
        age = age1.subtract(age2);
        verifyAge(age, new AgeData(-4, -2));
    }

    @Test
    public void testSpouseAge() {
        AgeData age = new AgeData(62, 0);
        String birthdate = "1-1-1960";
        String spousBirthdate = "1-1-1930";
        AgeData spouseAge = AgeUtils.getSpouseAge(birthdate, spousBirthdate, age);
        assertEquals(spouseAge.getYear(), 92);
        assertEquals(spouseAge.getMonth(), 0);

        age = new AgeData(62, 0);
        birthdate = "1-1-1960";
        spousBirthdate = "1-1-1970";
        spouseAge = AgeUtils.getSpouseAge(birthdate, spousBirthdate, age);
        assertEquals(spouseAge.getYear(), 52);
        assertEquals(spouseAge.getMonth(), 0);

        age = new AgeData(67, 0);
        birthdate = "10-8-1960";
        spousBirthdate = "5-11-1958";
        spouseAge = AgeUtils.getSpouseAge(birthdate, spousBirthdate, age);
        assertEquals(spouseAge.getYear(), 68);
        assertEquals(spouseAge.getMonth(), 9);

        age = new AgeData(71, 0);
        birthdate = "10-8-1960";
        spousBirthdate = "31-7-1968";
        spouseAge = AgeUtils.getSpouseAge(birthdate, spousBirthdate, age);
        verifyAge(spouseAge, new AgeData(63, 1));

        age = new AgeData(71, 0);
        birthdate = "10-8-1960";
        spousBirthdate = "9-12-1968";
        spouseAge = AgeUtils.getSpouseAge(birthdate, spousBirthdate, age);
        verifyAge(spouseAge, new AgeData(62, 8));
    }

    private void verifyAge(AgeData age, AgeData desiredAge) {
        assertEquals(age.getYear(), desiredAge.getYear());
        assertEquals(age.getMonth(), desiredAge.getMonth());
    }
}