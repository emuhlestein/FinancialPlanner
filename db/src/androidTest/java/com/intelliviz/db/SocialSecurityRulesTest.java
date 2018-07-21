package com.intelliviz.db;


import com.intelliviz.data.GovPension;
import com.intelliviz.data.SocialSecurityRules;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by edm on 2/5/2018.
 */

public class SocialSecurityRulesTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        //Context appContext = InstrumentationRegistry.getTargetContext();

        //assertEquals("com.intelliviz.retirementhelper.paid", appContext.getPackageName());
    }

    @Test
    public void testMonthlyBenefits() {
        String fullMonthlyBenefit = "1000";
        AgeData startAge = new AgeData(66, 8);
        AgeData endAge = new AgeData(90, 0);
        String principleSpouseBirthdate = "11-05-1958";
        GovPension principleSpouse = new GovPension(0, 0, "SS1", fullMonthlyBenefit, startAge, false);
        SocialSecurityRules ssr1 = new SocialSecurityRules(endAge, principleSpouseBirthdate,
                null, null, null, false, false);
        principleSpouse.setRules(ssr1);
        double monthlyBenefit = principleSpouse.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 1000, 1);

        startAge = new AgeData(61, 11);
        principleSpouse = new GovPension(0, 0, "SS1", fullMonthlyBenefit, startAge, false);
        principleSpouse.setRules(ssr1);
        monthlyBenefit = principleSpouse.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 0, 0);

        startAge = new AgeData(66, 7);
        principleSpouse = new GovPension(0, 0, "SS1", fullMonthlyBenefit, startAge, false);
        principleSpouse.setRules(ssr1);
        monthlyBenefit = principleSpouse.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 994.44, 0.1);

        startAge = new AgeData(62, 0);
        fullMonthlyBenefit = "2859";
        principleSpouse = new GovPension(0, 0, "SS1", fullMonthlyBenefit, startAge, false);
        principleSpouse.setRules(ssr1);
        monthlyBenefit = principleSpouse.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 2048.93, 0.1);

        startAge = new AgeData(70, 0);
        fullMonthlyBenefit = "2859";
        principleSpouse = new GovPension(0, 0, "SS1", fullMonthlyBenefit, startAge, false);
        principleSpouse.setRules(ssr1);
        monthlyBenefit = principleSpouse.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 3621.49, 0.1);

        startAge = new AgeData(70, 1);
        fullMonthlyBenefit = "2859";
        principleSpouse = new GovPension(0, 0, "SS1", fullMonthlyBenefit, startAge, false);
        principleSpouse.setRules(ssr1);
        monthlyBenefit = principleSpouse.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 3621.49, 0.1);
    }

    /**
     * Test benefits where the principle spouse's benefits is more than twice
     * the spouse's benefits.
     */
    @Test
    public void testSpousalBenefitsSameAgeSameStartAge() {
        String spouse1FullMonthlyBenefit;
        AgeData spouse1StartAge;
        String spouse1Birthdate;
        AgeData endAge;
        String spouse2FullMonthlyBenefit;
        String spouse2Birthdate;
        AgeData spouse2StartAge;

        endAge = new AgeData(90, 0);

        spouse1FullMonthlyBenefit = "1000";
        spouse1StartAge = new AgeData(62, 0);
        spouse1Birthdate = "1-1-1960";
        spouse2Birthdate = "1-1-1960";
        spouse2FullMonthlyBenefit = "0";
        spouse2StartAge = new AgeData(62, 0);

        GovPension spouse1 = new GovPension(0, 0, "SS1", spouse1FullMonthlyBenefit, spouse1StartAge, false);
        SocialSecurityRules ssr1 = new SocialSecurityRules(endAge, spouse1Birthdate,
                spouse2Birthdate, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        double monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 700, 0.1);

        GovPension spouse2 = new GovPension(0, 0, "SS2", spouse2FullMonthlyBenefit, spouse2StartAge, false);
        SocialSecurityRules ssr2 = new SocialSecurityRules(endAge, spouse2Birthdate,
                spouse1Birthdate, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 350, 0.1);

        spouse1StartAge = new AgeData(65, 0);
        spouse2StartAge = new AgeData(65, 0);
        spouse1 = new GovPension(0, 0, "SS1", spouse1FullMonthlyBenefit, spouse1StartAge, false);
        ssr1 = new SocialSecurityRules(endAge, spouse1Birthdate,
                spouse2Birthdate, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 866.7, 0.1);

        spouse2 = new GovPension(0, 0, "SS2", spouse2FullMonthlyBenefit, spouse2StartAge, false);
        ssr2 = new SocialSecurityRules(endAge, spouse2Birthdate,
                spouse1Birthdate, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 433.33, 0.1);

        spouse1StartAge = new AgeData(67, 0);
        spouse2StartAge = new AgeData(67, 0);
        spouse1 = new GovPension(0, 0, "SS1", spouse1FullMonthlyBenefit, spouse1StartAge, false);
        ssr1 = new SocialSecurityRules(endAge, spouse1Birthdate,
                spouse2Birthdate, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 1000, 0.1);

        spouse2 = new GovPension(0, 0, "SS2", spouse2FullMonthlyBenefit, spouse2StartAge, false);
        ssr2 = new SocialSecurityRules(endAge, spouse2Birthdate,
                spouse1Birthdate, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 500, 0.1);

        spouse1StartAge = new AgeData(70, 0);
        spouse2StartAge = new AgeData(70, 0);
        spouse1 = new GovPension(0, 0, "SS1", spouse1FullMonthlyBenefit, spouse1StartAge, false);
        ssr1 = new SocialSecurityRules(endAge, spouse1Birthdate,
                spouse2Birthdate, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 1240, 0.1);

        spouse2 = new GovPension(0, 0, "SS2", spouse2FullMonthlyBenefit, spouse2StartAge, false);
        ssr2 = new SocialSecurityRules(endAge, spouse2Birthdate,
                spouse1Birthdate, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 500, 0.1);
    }

    @Test
    public void testSpousalBenefitsSameAgeDiffStartAge() {
        String spouse1FullMonthlyBenefit;
        AgeData spouse1StartAge;
        String spouse1Birthdate;
        AgeData endAge;
        String spouse2FullMonthlyBenefit;
        String spouse2Birthdate;
        AgeData spouse2StartAge;

        endAge = new AgeData(90, 0);

        spouse1FullMonthlyBenefit = "1000";
        spouse2FullMonthlyBenefit = "0";
        spouse1Birthdate = "1-1-1960";
        spouse2Birthdate = "1-1-1960";
        spouse1StartAge = new AgeData(65, 0);
        spouse2StartAge = new AgeData(62, 0);

        GovPension spouse1 = new GovPension(0, 0, "SS1", spouse1FullMonthlyBenefit, spouse1StartAge, false);
        SocialSecurityRules ssr1 = new SocialSecurityRules(endAge, spouse1Birthdate,
                spouse2Birthdate, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        double monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 866.7, 0.1);

        GovPension spouse2 = new GovPension(0, 0, "SS2", spouse2FullMonthlyBenefit, spouse2StartAge, false);
        SocialSecurityRules ssr2 = new SocialSecurityRules(endAge, spouse2Birthdate,
                spouse1Birthdate, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 433.35, 0.1);
        AgeData actualStartAge = spouse2.getActualStartAge();
        if(actualStartAge != null) {
            assertEquals(actualStartAge.getNumberOfMonths(), spouse1StartAge.getNumberOfMonths());
        }

        spouse1StartAge = new AgeData(70, 0);
        spouse2StartAge = new AgeData(65, 0);

        spouse1 = new GovPension(0, 0, "SS1", spouse1FullMonthlyBenefit, spouse1StartAge, false);
        ssr1 = new SocialSecurityRules(endAge, spouse1Birthdate,
                spouse2Birthdate, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 1240, 0.1);

        spouse2 = new GovPension(0, 0, "SS2", spouse2FullMonthlyBenefit, spouse2StartAge, false);
        ssr2 = new SocialSecurityRules(endAge, spouse2Birthdate,
                spouse1Birthdate, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 500, 0.1);
        actualStartAge = spouse2.getActualStartAge();
        if(actualStartAge != null) {
            assertEquals(actualStartAge.getNumberOfMonths(), spouse1StartAge.getNumberOfMonths());
        }
    }

    @Test
    public void testSpousalBenefitsOlderSpouse1() {
        String spouse1FullMonthlyBenefit;
        AgeData spouse1StartAge;
        String spouse1Birthdate;
        AgeData endAge;
        String spouse2FullMonthlyBenefit;
        String spouse2Birthdate;
        AgeData spouse2StartAge;

        endAge = new AgeData(90, 0);

        spouse1FullMonthlyBenefit = "1000";
        spouse2FullMonthlyBenefit = "0";
        spouse1Birthdate = "1-1-1930";
        spouse2Birthdate = "1-1-1960";
        spouse1StartAge = new AgeData(62, 0);
        spouse2StartAge = new AgeData(62, 0);

        GovPension spouse1 = new GovPension(0, 0, "SS1", spouse1FullMonthlyBenefit, spouse1StartAge, false);
        SocialSecurityRules ssr1 = new SocialSecurityRules(endAge, spouse1Birthdate,
                spouse2Birthdate, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        double monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 800, 0.1);

        GovPension spouse2 = new GovPension(0, 0, "SS2", spouse2FullMonthlyBenefit, spouse2StartAge, false);
        SocialSecurityRules ssr2 = new SocialSecurityRules(endAge, spouse2Birthdate,
                spouse1Birthdate, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 350, 0.1);
        AgeData actualStartAge = spouse2.getActualStartAge();
        if(actualStartAge != null) {
            assertEquals(actualStartAge.getNumberOfMonths(), spouse1StartAge.getNumberOfMonths());
        }

        spouse1StartAge = new AgeData(65, 0);
        spouse2StartAge = new AgeData(62, 0);

        spouse1 = new GovPension(0, 0, "SS1", spouse1FullMonthlyBenefit, spouse1StartAge, false);
        ssr1 = new SocialSecurityRules(endAge, spouse1Birthdate,
                spouse2Birthdate, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 1000, 0.1);

        spouse2 = new GovPension(0, 0, "SS2", spouse2FullMonthlyBenefit, spouse2StartAge, false);
        ssr2 = new SocialSecurityRules(endAge, spouse2Birthdate,
                spouse1Birthdate, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 350, 0.1);
        actualStartAge = spouse2.getActualStartAge();
        if(actualStartAge != null) {
            assertEquals(actualStartAge.getNumberOfMonths(), spouse1StartAge.getNumberOfMonths());
        }

        spouse1StartAge = new AgeData(70, 0);
        spouse2StartAge = new AgeData(62, 0);

        spouse1 = new GovPension(0, 0, "SS1", spouse1FullMonthlyBenefit, spouse1StartAge, false);
        ssr1 = new SocialSecurityRules(endAge, spouse1Birthdate,
                spouse2Birthdate, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 1225.0, 0.1);

        spouse2 = new GovPension(0, 0, "SS2", spouse2FullMonthlyBenefit, spouse2StartAge, false);
        ssr2 = new SocialSecurityRules(endAge, spouse2Birthdate,
                spouse1Birthdate, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 350, 0.1);
        actualStartAge = spouse2.getActualStartAge();
        if(actualStartAge != null) {
            assertEquals(actualStartAge.getNumberOfMonths(), spouse1StartAge.getNumberOfMonths());
        }
    }

    @Test
    public void testSpousalBenefitsYoungerSpouse1() {
        String spouse1FullMonthlyBenefit;
        AgeData spouse1StartAge;
        String spouse1Birthdate;
        AgeData endAge;
        String spouse2FullMonthlyBenefit;
        String spouse2Birthdate;
        AgeData spouse2StartAge;

        endAge = new AgeData(90, 0);

        spouse1FullMonthlyBenefit = "1000";
        spouse2FullMonthlyBenefit = "0";
        spouse1Birthdate = "1-1-1960";
        spouse2Birthdate = "1-1-1930";
        spouse1StartAge = new AgeData(62, 0);
        spouse2StartAge = new AgeData(62, 0);

        GovPension spouse1 = new GovPension(0, 0, "SS1", spouse1FullMonthlyBenefit, spouse1StartAge, false);
        SocialSecurityRules ssr1 = new SocialSecurityRules(endAge, spouse1Birthdate,
                spouse2Birthdate, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        double monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 700, 0.1);

        GovPension spouse2 = new GovPension(0, 0, "SS2", spouse2FullMonthlyBenefit, spouse2StartAge, false);
        SocialSecurityRules ssr2 = new SocialSecurityRules(endAge, spouse2Birthdate,
                spouse1Birthdate, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 500, 0.1);
        AgeData actualStartAge = spouse2.getActualStartAge();
        if(actualStartAge != null) {
            AgeData diffAge = AgeUtils.ageDiff(spouse2Birthdate, spouse1Birthdate);
            assertEquals(actualStartAge.getNumberOfMonths(), spouse1StartAge.getNumberOfMonths() + diffAge.getNumberOfMonths());
        }

        spouse1StartAge = new AgeData(65, 0);
        spouse2StartAge = new AgeData(62, 0);
        spouse1FullMonthlyBenefit = "1000";
        spouse2FullMonthlyBenefit = "0";
        spouse1Birthdate = "1-1-1960";
        spouse2Birthdate = "1-1-1965";

        spouse1 = new GovPension(0, 0, "SS1", spouse1FullMonthlyBenefit, spouse1StartAge, false);
        ssr1 = new SocialSecurityRules(endAge, spouse1Birthdate,
                spouse2Birthdate, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 866.66, 0.1);

        spouse2 = new GovPension(0, 0, "SS2", spouse2FullMonthlyBenefit, spouse2StartAge, false);
        ssr2 = new SocialSecurityRules(endAge, spouse2Birthdate,
                spouse1Birthdate, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 350, 0.1);
        actualStartAge = spouse2.getActualStartAge();
        if(actualStartAge != null) {
            assertEquals(actualStartAge.getNumberOfMonths(), spouse1StartAge.getNumberOfMonths());
        }

        spouse1StartAge = new AgeData(70, 0);
        spouse2StartAge = new AgeData(62, 0);
        spouse1FullMonthlyBenefit = "1000";
        spouse2FullMonthlyBenefit = "0";
        spouse1Birthdate = "1-1-1960";
        spouse2Birthdate = "1-1-1965";

        spouse1 = new GovPension(0, 0, "SS1", spouse1FullMonthlyBenefit, spouse1StartAge, false);
        ssr1 = new SocialSecurityRules(endAge, spouse1Birthdate,
                spouse2Birthdate, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 1240.0, 0.1);

        spouse2 = new GovPension(0, 0, "SS2", spouse2FullMonthlyBenefit, spouse2StartAge, false);
        ssr2 = new SocialSecurityRules(endAge, spouse2Birthdate,
                spouse1Birthdate, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 433.35, 0.1);
        actualStartAge = spouse2.getActualStartAge();
        if(actualStartAge != null) {
            spouse1StartAge = new AgeData(65, 0);
            assertEquals(actualStartAge.getNumberOfMonths(), spouse1StartAge.getNumberOfMonths());
        }
    }

    /**
     * Test benefits where the spouse's benefits is more than half
     * the principle spouse's benefits.
     */
    @Test
    public void testSpouseBenefits() {
        String spouse1FullMonthlyBenefit;
        AgeData spouse1StartAge;
        String spouse1Birthdate;
        AgeData endAge;
        String spouse2FullMonthlyBenefit;
        String spouse2Birthdate;
        AgeData spouse2StartAge;

        endAge = new AgeData(90, 0);

        spouse1FullMonthlyBenefit = "900";
        spouse2FullMonthlyBenefit = "500";
        spouse1Birthdate = "1-1-1960";
        spouse2Birthdate = "1-1-1965";
        spouse1StartAge = new AgeData(62, 0);
        spouse2StartAge = new AgeData(62, 0);

        GovPension spouse1 = new GovPension(0, 0, "SS1", spouse1FullMonthlyBenefit, spouse1StartAge,
                false);
        SocialSecurityRules ssr1 = new SocialSecurityRules(endAge, spouse1Birthdate,
                spouse2Birthdate, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        double monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 630, 1);

        GovPension spouse2 = new GovPension(0, 0, "SS2", spouse2FullMonthlyBenefit, spouse2StartAge,
                true);

        SocialSecurityRules ssr2 = new SocialSecurityRules(endAge, spouse2Birthdate,
                spouse1Birthdate, spouse1FullMonthlyBenefit, spouse1StartAge, true, false);

        spouse2.setRules(ssr2);

        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 350, 1);

        spouse1StartAge = new AgeData(66, 0);
        spouse1 = new GovPension(0, 0, "SS1", spouse1FullMonthlyBenefit, spouse1StartAge,
                false);
        ssr1 = new SocialSecurityRules(endAge, spouse1Birthdate,
                spouse2Birthdate, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 840, 1);

        spouse2 = new GovPension(0, 0, "SS2", spouse2FullMonthlyBenefit, spouse2StartAge,
                true);

        ssr2 = new SocialSecurityRules(endAge, spouse2Birthdate,
                spouse1Birthdate, spouse1FullMonthlyBenefit, spouse1StartAge, true, false);

        spouse2.setRules(ssr2);

        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 350, 1);
    }
}
