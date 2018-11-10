package com.intelliviz.db;


import com.intelliviz.data.GovPension;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.data.SocialSecurityRules;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;

import org.junit.Test;

import static com.intelliviz.lowlevel.util.RetirementConstants.OWNER_PRIMARY;
import static com.intelliviz.lowlevel.util.RetirementConstants.OWNER_SPOUSE;
import static org.junit.Assert.assertEquals;

/**
 * Created by edm on 2/5/2018.
 */

public class SocialSecurityRulesTest {
    private final static int INCLUDED = 1;
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

        RetirementOptions ro = new RetirementOptions(endAge, null, principleSpouseBirthdate, null);

        GovPension principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, INCLUDED, fullMonthlyBenefit, startAge);
        SocialSecurityRules ssr1 = new SocialSecurityRules(ro);
        principleSpouse.setRules(ssr1);
        double monthlyBenefit = principleSpouse.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 1000, 1);

        startAge = new AgeData(61, 11);
        principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, INCLUDED, fullMonthlyBenefit, startAge);
        principleSpouse.setRules(ssr1);
        monthlyBenefit = principleSpouse.getMonthlyBenefit();
        assertEquals(0, monthlyBenefit, 0);

        startAge = new AgeData(66, 7);
        principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, INCLUDED, fullMonthlyBenefit, startAge);
        principleSpouse.setRules(ssr1);
        monthlyBenefit = principleSpouse.getMonthlyBenefit();
        assertEquals(994.44, monthlyBenefit, 0.1);

        startAge = new AgeData(62, 0);
        fullMonthlyBenefit = "2859";
        principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, INCLUDED, fullMonthlyBenefit, startAge);
        principleSpouse.setRules(ssr1);
        monthlyBenefit = principleSpouse.getMonthlyBenefit();
        assertEquals(2048.93, monthlyBenefit, 0.1);

        startAge = new AgeData(70, 0);
        fullMonthlyBenefit = "2859";
        principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, INCLUDED, fullMonthlyBenefit, startAge);
        principleSpouse.setRules(ssr1);
        monthlyBenefit = principleSpouse.getMonthlyBenefit();
        assertEquals(3621.49, monthlyBenefit, 0.1);

        startAge = new AgeData(70, 1);
        fullMonthlyBenefit = "2859";
        principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, INCLUDED, fullMonthlyBenefit, startAge);
        principleSpouse.setRules(ssr1);
        monthlyBenefit = principleSpouse.getMonthlyBenefit();
        assertEquals(3621.49, monthlyBenefit, 0.1);
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
        AgeData spouseEndAge;
        String spouse2FullMonthlyBenefit;
        String spouse2Birthdate;
        AgeData spouse2StartAge;

        endAge = new AgeData(90, 0);
        spouseEndAge = new AgeData(90, 0);

        spouse1FullMonthlyBenefit = "1000";
        spouse1StartAge = new AgeData(62, 0);
        spouse1Birthdate = "1-1-1960";
        spouse2Birthdate = "1-1-1960";
        spouse2FullMonthlyBenefit = "0";
        spouse2StartAge = new AgeData(62, 0);

        RetirementOptions ro = new RetirementOptions(endAge, spouseEndAge, spouse1Birthdate, spouse2Birthdate);

        GovPension spouse1 = new GovPension(0, 0, "SS1", OWNER_PRIMARY, INCLUDED, spouse1FullMonthlyBenefit, spouse1StartAge);
        SocialSecurityRules ssr1 = new SocialSecurityRules(ro, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        double monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(700, monthlyBenefit, 0.1);

        GovPension spouse2 = new GovPension(0, 0, "SS2", OWNER_SPOUSE, INCLUDED, spouse2FullMonthlyBenefit, spouse2StartAge);
        SocialSecurityRules ssr2 = new SocialSecurityRules(ro, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(350, monthlyBenefit, 0.1);

        spouse1StartAge = new AgeData(65, 0);
        spouse2StartAge = new AgeData(65, 0);
        spouse1 = new GovPension(0, 0, "SS1", OWNER_PRIMARY, INCLUDED, spouse1FullMonthlyBenefit, spouse1StartAge);
        ssr1 = new SocialSecurityRules(ro, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 866.7, 0.1);

        spouse2 = new GovPension(0, 0, "SS2", OWNER_SPOUSE, INCLUDED, spouse2FullMonthlyBenefit, spouse2StartAge);
        ssr2 = new SocialSecurityRules(ro, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(433.33, monthlyBenefit, 0.1);

        spouse1StartAge = new AgeData(67, 0);
        spouse2StartAge = new AgeData(67, 0);
        spouse1 = new GovPension(0, 0, "SS1", OWNER_PRIMARY, INCLUDED, spouse1FullMonthlyBenefit, spouse1StartAge);
        ssr1 = new SocialSecurityRules(ro, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(1000, monthlyBenefit, 0.1);

        spouse2 = new GovPension(0, 0, "SS2", OWNER_SPOUSE, INCLUDED, spouse2FullMonthlyBenefit, spouse2StartAge);
        ssr2 = new SocialSecurityRules(ro, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(500, monthlyBenefit, 0.1);

        spouse1StartAge = new AgeData(70, 0);
        spouse2StartAge = new AgeData(70, 0);
        spouse1 = new GovPension(0, 0, "SS1", OWNER_PRIMARY, INCLUDED, spouse1FullMonthlyBenefit, spouse1StartAge);
        ssr1 = new SocialSecurityRules(ro, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(1240, monthlyBenefit, 0.1);

        spouse2 = new GovPension(0, 0, "SS2", OWNER_SPOUSE, INCLUDED, spouse2FullMonthlyBenefit, spouse2StartAge);
        ssr2 = new SocialSecurityRules(ro, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(500, monthlyBenefit, 0.1);
    }

    @Test
    public void testSpousalBenefitsSameAgeDiffStartAge() {
        String spouse1FullMonthlyBenefit;
        AgeData spouse1StartAge;
        String spouse1Birthdate;
        AgeData endAge;
        AgeData spouseEndAge;
        String spouse2FullMonthlyBenefit;
        String spouse2Birthdate;
        AgeData spouse2StartAge;

        endAge = new AgeData(90, 0);
        spouseEndAge = new AgeData(90, 0);

        spouse1FullMonthlyBenefit = "1000";
        spouse2FullMonthlyBenefit = "0";
        spouse1Birthdate = "1-1-1960";
        spouse2Birthdate = "1-1-1960";
        spouse1StartAge = new AgeData(65, 0);
        spouse2StartAge = new AgeData(62, 0);

        RetirementOptions ro = new RetirementOptions(endAge, spouseEndAge, spouse1Birthdate, spouse2Birthdate);

        GovPension spouse1 = new GovPension(0, 0, "SS1", OWNER_PRIMARY, INCLUDED, spouse1FullMonthlyBenefit, spouse1StartAge);
        SocialSecurityRules ssr1 = new SocialSecurityRules(ro, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        double monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(866.7, monthlyBenefit, 0.1);

        GovPension spouse2 = new GovPension(0, 0, "SS2", OWNER_SPOUSE, INCLUDED, spouse2FullMonthlyBenefit, spouse2StartAge);
        SocialSecurityRules ssr2 = new SocialSecurityRules(ro, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(433.35, monthlyBenefit, 0.1);
        AgeData actualStartAge = spouse2.getActualStartAge();
        if(actualStartAge != null) {
            assertEquals(actualStartAge.getNumberOfMonths(), spouse1StartAge.getNumberOfMonths());
        }

        spouse1StartAge = new AgeData(70, 0);
        spouse2StartAge = new AgeData(65, 0);

        spouse1 = new GovPension(0, 0, "SS1", OWNER_PRIMARY, INCLUDED, spouse1FullMonthlyBenefit, spouse1StartAge);
        ssr1 = new SocialSecurityRules(ro, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(1240, monthlyBenefit, 0.1);

        spouse2 = new GovPension(0, 0, "SS2", OWNER_SPOUSE, INCLUDED, spouse2FullMonthlyBenefit, spouse2StartAge);
        ssr2 = new SocialSecurityRules(ro, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(500, monthlyBenefit, 0.1);
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
        AgeData spouseEndAge;
        String spouse2FullMonthlyBenefit;
        String spouse2Birthdate;
        AgeData spouse2StartAge;

        endAge = new AgeData(90, 0);
        spouseEndAge = new AgeData(90, 0);

        spouse1FullMonthlyBenefit = "1000";
        spouse2FullMonthlyBenefit = "0";
        spouse1Birthdate = "1-1-1960";
        spouse2Birthdate = "1-1-1930";

        spouse1StartAge = new AgeData(62, 0);
        spouse2StartAge = new AgeData(62, 0);

        RetirementOptions ro = new RetirementOptions(endAge, spouseEndAge, spouse1Birthdate, spouse2Birthdate);

        GovPension spouse1 = new GovPension(0, 0, "SS1", OWNER_PRIMARY, INCLUDED, spouse1FullMonthlyBenefit, spouse1StartAge);
        SocialSecurityRules ssr1 = new SocialSecurityRules(ro, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        double monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(700, monthlyBenefit, 0.1);

        GovPension spouse2 = new GovPension(0, 0, "SS2", OWNER_SPOUSE, INCLUDED, spouse2FullMonthlyBenefit, spouse2StartAge);
        SocialSecurityRules ssr2 = new SocialSecurityRules(ro, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(350, monthlyBenefit, 0.1);
        AgeData actualStartAge = spouse2.getActualStartAge();
        if(actualStartAge != null) {
            assertEquals(actualStartAge.getNumberOfMonths(), spouse1StartAge.getNumberOfMonths());
        }

        spouse1StartAge = new AgeData(65, 0);
        spouse2StartAge = new AgeData(62, 0);

        spouse1 = new GovPension(0, 0, "SS1", OWNER_PRIMARY, INCLUDED, spouse1FullMonthlyBenefit, spouse1StartAge);
        ssr1 = new SocialSecurityRules(ro, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(866.66, monthlyBenefit, 0.1);

        spouse2 = new GovPension(0, 0, "SS2", OWNER_SPOUSE, INCLUDED, spouse2FullMonthlyBenefit, spouse2StartAge);
        ssr2 = new SocialSecurityRules(ro, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(350, monthlyBenefit, 0.1);
        actualStartAge = spouse2.getActualStartAge();
        if(actualStartAge != null) {
            assertEquals(actualStartAge.getNumberOfMonths(), spouse1StartAge.getNumberOfMonths());
        }

        spouse1StartAge = new AgeData(70, 0);
        spouse2StartAge = new AgeData(62, 0);

        spouse1 = new GovPension(0, 0, "SS1", OWNER_PRIMARY, INCLUDED, spouse1FullMonthlyBenefit, spouse1StartAge);
        ssr1 = new SocialSecurityRules(ro, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(1240.0, monthlyBenefit, 0.1);

        spouse2 = new GovPension(0, 0, "SS2", OWNER_SPOUSE, INCLUDED, spouse2FullMonthlyBenefit, spouse2StartAge);
        ssr2 = new SocialSecurityRules(ro, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(350, monthlyBenefit, 0.1);
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
        AgeData spouseEndAge;
        String spouse2FullMonthlyBenefit;
        String spouse2Birthdate;
        AgeData spouse2StartAge;

        endAge = new AgeData(90, 0);
        spouseEndAge = new AgeData(90, 0);

        spouse1FullMonthlyBenefit = "1000";
        spouse2FullMonthlyBenefit = "0";
        spouse1Birthdate = "1-1-1960";
        spouse2Birthdate = "1-1-1930";
        spouse1StartAge = new AgeData(62, 0);
        spouse2StartAge = new AgeData(62, 0);

        RetirementOptions ro = new RetirementOptions(endAge, spouseEndAge, spouse1Birthdate, spouse2Birthdate);

        GovPension spouse1 = new GovPension(0, 0, "SS1", OWNER_PRIMARY, INCLUDED, spouse1FullMonthlyBenefit, spouse1StartAge);
        SocialSecurityRules ssr1 = new SocialSecurityRules(ro, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        double monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(700, monthlyBenefit, 0.1);

        GovPension spouse2 = new GovPension(0, 0, "SS2", OWNER_SPOUSE, INCLUDED, spouse2FullMonthlyBenefit, spouse2StartAge);
        SocialSecurityRules ssr2 = new SocialSecurityRules(ro, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(350, monthlyBenefit, 0.1);
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
        ro = new RetirementOptions(endAge, spouseEndAge, spouse1Birthdate, spouse2Birthdate);

        spouse1 = new GovPension(0, 0, "SS1", OWNER_PRIMARY, INCLUDED, spouse1FullMonthlyBenefit, spouse1StartAge);
        ssr1 = new SocialSecurityRules(ro, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(866.66, monthlyBenefit, 0.1);

        spouse2 = new GovPension(0, 0, "SS2", OWNER_SPOUSE, INCLUDED, spouse2FullMonthlyBenefit, spouse2StartAge);
        ssr2 = new SocialSecurityRules(ro, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(380, monthlyBenefit, 0.1);
        actualStartAge = spouse2.getActualStartAge();
        if(actualStartAge != null) {
            //assertEquals(actualStartAge.getNumberOfMonths(), spouse1StartAge.getNumberOfMonths());
        }

        spouse1StartAge = new AgeData(70, 0);
        spouse2StartAge = new AgeData(62, 0);
        spouse1FullMonthlyBenefit = "1000";
        spouse2FullMonthlyBenefit = "0";
        spouse1Birthdate = "1-1-1960";
        spouse2Birthdate = "1-1-1965";
        ro = new RetirementOptions(endAge, spouseEndAge, spouse1Birthdate, spouse2Birthdate);

        spouse1 = new GovPension(0, 0, "SS1", OWNER_PRIMARY, INCLUDED, spouse1FullMonthlyBenefit, spouse1StartAge);
        ssr1 = new SocialSecurityRules(ro, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(1240.0, monthlyBenefit, 0.1);

        spouse2 = new GovPension(0, 0, "SS2", OWNER_SPOUSE, INCLUDED, spouse2FullMonthlyBenefit, spouse2StartAge);
        ssr2 = new SocialSecurityRules(ro, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(433.35, monthlyBenefit, 0.1);
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
        AgeData spouseEndAge;
        String spouse2FullMonthlyBenefit;
        String spouse2Birthdate;
        AgeData spouse2StartAge;

        endAge = new AgeData(90, 0);
        spouseEndAge = new AgeData(90, 0);

        spouse1FullMonthlyBenefit = "900";
        spouse2FullMonthlyBenefit = "500";
        spouse1Birthdate = "1-1-1960";
        spouse2Birthdate = "1-1-1965";
        spouse1StartAge = new AgeData(62, 0);
        spouse2StartAge = new AgeData(62, 0);

        RetirementOptions ro = new RetirementOptions(endAge, spouseEndAge, spouse1Birthdate, spouse2Birthdate);

        GovPension spouse1 = new GovPension(0, 0, "SS1", OWNER_PRIMARY, INCLUDED, spouse1FullMonthlyBenefit, spouse1StartAge);
        SocialSecurityRules ssr1 = new SocialSecurityRules(ro, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        double monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(630, monthlyBenefit, 1);

        GovPension spouse2 = new GovPension(0, 0, "SS2", OWNER_SPOUSE, INCLUDED, spouse2FullMonthlyBenefit, spouse2StartAge);

        SocialSecurityRules ssr2 = new SocialSecurityRules(ro, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);

        spouse2.setRules(ssr2);

        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(350, monthlyBenefit, 1);

        spouse1StartAge = new AgeData(66, 0);
        spouse1 = new GovPension(0, 0, "SS1", OWNER_PRIMARY, INCLUDED, spouse1FullMonthlyBenefit, spouse1StartAge);
        ssr1 = new SocialSecurityRules(ro, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(840, monthlyBenefit, 1);

        spouse2 = new GovPension(0, 0, "SS2", OWNER_SPOUSE, INCLUDED, spouse2FullMonthlyBenefit, spouse2StartAge);

        ssr2 = new SocialSecurityRules(ro, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);

        spouse2.setRules(ssr2);

        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(350, monthlyBenefit, 1);
    }
}
