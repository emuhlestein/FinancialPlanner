package com.intelliviz.retirementhelper;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.intelliviz.retirementhelper.data.AgeData;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.intelliviz.retirementhelper.free", appContext.getPackageName());

    }

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

    }
}
