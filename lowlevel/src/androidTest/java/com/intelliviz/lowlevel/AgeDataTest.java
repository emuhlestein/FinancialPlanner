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
        String birthDate = "01-01-1960";
        AgeData age = AgeUtils.getAge(birthDate);

        // TODO this is not a good test as it will fail when the month changes
        assertTrue(age.getYear() == 58);
        assertTrue(age.getMonth() == 8);

        AgeData age1 = new AgeData(70, 0);
        String birthDate2 = "01-01-1962";
        AgeData age2 = AgeUtils.getAge(birthDate, birthDate2, age1);
        assertTrue(age2.getYear() == 72);
        assertTrue(age2.getMonth() == 0);

        age1 = new AgeData(70, 0);
        birthDate2 = "01-01-1958";
        age2 = AgeUtils.getAge(birthDate, birthDate2, age1);
        assertTrue(age2.getYear() == 68);
        assertTrue(age2.getMonth() == 0);

        birthDate2 = "01-05-1958";
        age2 = AgeUtils.getAge(birthDate, birthDate2, age1);
        assertTrue(age2.getYear() == 68);
        assertTrue(age2.getMonth() == 4);

        age1 = new AgeData(70, 0);
        birthDate2 = "01-01-1962";
        age2 = AgeUtils.getAge(birthDate, birthDate2, age1);
        assertTrue(age2.getYear() == 72);
        assertTrue(age2.getMonth() == 0);
    }
}
