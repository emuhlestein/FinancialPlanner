package com.intelliviz.retirementhelper;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.SocialSecurityRules;
import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Created by edm on 2/5/2018.
 */

@RunWith(AndroidJUnit4.class)
public class SocialSecurityRulesTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.intelliviz.retirementhelper.free", appContext.getPackageName());
    }

    @Test
    public void testSpousalBenefits() {
        String fullMonthlyBenefit = "1000";
        AgeData startAge = new AgeData(66, 8);
        AgeData endAge = new AgeData(90, 0);
        String spouseBirhtdate = "03-13-1957";
        String spouseFullBenefits = "0";
        AgeData spouseStartAge = new AgeData(66, 6);
        String birhtdate = "11-05-1958";
        GovPensionEntity ss1 = new GovPensionEntity(0, 0, "SS1", fullMonthlyBenefit, startAge,
        0, spouseBirhtdate);

        GovPensionEntity ss2 = new GovPensionEntity(0, 0, "SS2", "0", spouseStartAge,
                1, birhtdate);

        SocialSecurityRules ssr1 = new SocialSecurityRules(birhtdate, endAge,
                Double.parseDouble(spouseFullBenefits), spouseStartAge, true);
        SocialSecurityRules ssr2 = new SocialSecurityRules(birhtdate, endAge,
                Double.parseDouble(fullMonthlyBenefit), startAge, true);
        ss1.setRules(ssr1);
        ss2.setRules(ssr2);

        double monthlyBenefit = ss1.getMonthlyBenfit();
        assertEquals(monthlyBenefit, 1000, 1);

        monthlyBenefit = ss2.getMonthlyBenfit();
        assertEquals(monthlyBenefit, 500, 1);
    }
}
