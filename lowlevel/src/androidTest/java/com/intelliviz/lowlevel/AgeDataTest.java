package com.intelliviz.lowlevel;

import com.intelliviz.lowlevel.data.AgeData;

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
}
