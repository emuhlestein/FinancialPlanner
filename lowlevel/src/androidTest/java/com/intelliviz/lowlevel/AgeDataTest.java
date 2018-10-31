package com.intelliviz.lowlevel;

import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AgeDataTest {

    @Test
    public void testValidAge() {
        AgeData ageData = new AgeData();
        assertTrue(ageData.isValid());
        assertFalse(!ageData.isValid());

        ageData = new AgeData(0);
        assertTrue(ageData.isValid());

        ageData = new AgeData(200);
        assertTrue(ageData.isValid());

        ageData = new AgeData(-1);
        assertTrue(!ageData.isValid());

        ageData = new AgeData(201*12);
        assertTrue(!ageData.isValid());

        ageData = new AgeData("10 5");
        assertTrue(ageData.isValid());

        assertTrue(ageData.getYear() == 10);
        assertTrue(ageData.getMonth() == 5);

        ageData = new AgeData("60y 5m");
        assertTrue(ageData.isValid());

        assertTrue(ageData.getYear() == 60);
        assertTrue(ageData.getMonth() == 5);

        ageData = new AgeData("67", "11");
        assertTrue(ageData.isValid());

        assertTrue(ageData.getYear() == 67);
        assertTrue(ageData.getMonth() == 11);

        ageData = new AgeData("67", "12");
        assertTrue(ageData.isValid());

        assertTrue(ageData.getYear() == 68);
        assertTrue(ageData.getMonth() == 0);
    }

    @Test
    public void testAgeUtils() {
        AgeData testAge = new AgeData(60, 0);
        String birthdate = AgeUtils.getBirthdate(testAge);
        AgeData age = AgeUtils.getAge(birthdate);
        assertTrue(testAge.getYear() == age.getYear());
        assertTrue(testAge.getMonth() == age.getMonth());

        testAge = new AgeData(65, 6);
        birthdate = AgeUtils.getBirthdate(testAge);
        age = AgeUtils.getAge(birthdate);
        assertTrue(testAge.getYear() == age.getYear());
        assertTrue(testAge.getMonth() == age.getMonth());

        testAge = new AgeData(55, 11);
        birthdate = AgeUtils.getBirthdate(testAge);
        age = AgeUtils.getAge(birthdate);
        assertTrue(testAge.getYear() == age.getYear());
        assertTrue(testAge.getMonth() == age.getMonth());

        testAge = new AgeData(55, 4);
        birthdate = AgeUtils.getBirthdate(testAge);
        age = AgeUtils.getAge(birthdate);
        assertTrue(testAge.getYear() == age.getYear());
        assertTrue(testAge.getMonth() == age.getMonth());

        testAge = new AgeData(70, 10);
        birthdate = AgeUtils.getBirthdate(testAge);
        age = AgeUtils.getAge(birthdate);
        assertTrue(testAge.getYear() == age.getYear());
        assertTrue(testAge.getMonth() == age.getMonth());
    }

    @Test
    public void getBirthdateTest() {
        AgeData testAge = new AgeData(60, 0);
        String birthdate = AgeUtils.getBirthdate(testAge);
        AgeData age = AgeUtils.getAge(birthdate);
        assertTrue(testAge.getYear() == age.getYear());
        assertTrue(testAge.getMonth() == age.getMonth());
    }

    @Test
    public void testAgeDiff() {
        AgeData testAge = new AgeData(60, 0);
        AgeData age = AgeUtils.getAge("01-01-1960", "01-01-1960", testAge);
        assertTrue(age.diff(testAge) == 0);

        age = AgeUtils.getAge("01-01-1958", "01-01-1960", testAge);
        assertTrue(age.diff(new AgeData(58, 0)) == 0);

        age = AgeUtils.getAge("01-01-1960", "01-01-1958", testAge);
        assertTrue(age.diff(new AgeData(62, 0)) == 0);
    }
}
