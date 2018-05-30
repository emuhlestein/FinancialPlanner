package com.intelliviz.retirementhelper.data;
import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;

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
        GovPensionEntity principleSpouse = new GovPensionEntity(0, 0, "SS1", fullMonthlyBenefit, startAge, 0);
        SocialSecurityRules ssr1 = new SocialSecurityRules(endAge, principleSpouseBirthdate,
                null, null, null, false, false);
        principleSpouse.setRules(ssr1);
        double monthlyBenefit = principleSpouse.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 1000, 1);

        startAge = new AgeData(61, 11);
        principleSpouse = new GovPensionEntity(0, 0, "SS1", fullMonthlyBenefit, startAge, 0);
        principleSpouse.setRules(ssr1);
        monthlyBenefit = principleSpouse.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 0, 0);

        startAge = new AgeData(66, 7);
        principleSpouse = new GovPensionEntity(0, 0, "SS1", fullMonthlyBenefit, startAge, 0);
        principleSpouse.setRules(ssr1);
        monthlyBenefit = principleSpouse.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 994.44, 0.1);

        startAge = new AgeData(62, 0);
        fullMonthlyBenefit = "2859";
        principleSpouse = new GovPensionEntity(0, 0, "SS1", fullMonthlyBenefit, startAge, 0);
        principleSpouse.setRules(ssr1);
        monthlyBenefit = principleSpouse.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 2048.93, 0.1);

        startAge = new AgeData(70, 0);
        fullMonthlyBenefit = "2859";
        principleSpouse = new GovPensionEntity(0, 0, "SS1", fullMonthlyBenefit, startAge, 0);
        principleSpouse.setRules(ssr1);
        monthlyBenefit = principleSpouse.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 3621.49, 0.1);

        startAge = new AgeData(70, 1);
        fullMonthlyBenefit = "2859";
        principleSpouse = new GovPensionEntity(0, 0, "SS1", fullMonthlyBenefit, startAge, 0);
        principleSpouse.setRules(ssr1);
        monthlyBenefit = principleSpouse.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 3621.49, 0.1);
    }

    /**
     * Test benefits where the principle spouse's benefits is more than twice
     * the spouse's benefits.
     */
    @Test
    public void testSpousalBenefits() {
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

        GovPensionEntity spouse1 = new GovPensionEntity(0, 0, "SS1", spouse1FullMonthlyBenefit, spouse1StartAge, 0);
        SocialSecurityRules ssr1 = new SocialSecurityRules(endAge, spouse1Birthdate,
                spouse2Birthdate, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        double monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 700, 0.1);

        GovPensionEntity spouse2 = new GovPensionEntity(0, 0, "SS2", spouse2FullMonthlyBenefit, spouse2StartAge, 0);
        SocialSecurityRules ssr2 = new SocialSecurityRules(endAge, spouse2Birthdate,
                spouse1Birthdate, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 350, 0.1);


        spouse1StartAge = new AgeData(65, 0);
        spouse2StartAge = new AgeData(65, 0);
        spouse1 = new GovPensionEntity(0, 0, "SS1", spouse1FullMonthlyBenefit, spouse1StartAge, 0);
        ssr1 = new SocialSecurityRules(endAge, spouse1Birthdate,
                spouse2Birthdate, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 866.7, 0.1);

        spouse2 = new GovPensionEntity(0, 0, "SS2", spouse2FullMonthlyBenefit, spouse2StartAge, 0);
        ssr2 = new SocialSecurityRules(endAge, spouse2Birthdate,
                spouse1Birthdate, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 433.33, 0.1);

        spouse1StartAge = new AgeData(67, 0);
        spouse2StartAge = new AgeData(67, 0);
        spouse1 = new GovPensionEntity(0, 0, "SS1", spouse1FullMonthlyBenefit, spouse1StartAge, 0);
        ssr1 = new SocialSecurityRules(endAge, spouse1Birthdate,
                spouse2Birthdate, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 1000, 0.1);

        spouse2 = new GovPensionEntity(0, 0, "SS2", spouse2FullMonthlyBenefit, spouse2StartAge, 0);
        ssr2 = new SocialSecurityRules(endAge, spouse2Birthdate,
                spouse1Birthdate, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 500, 0.1);

        spouse1StartAge = new AgeData(70, 0);
        spouse2StartAge = new AgeData(70, 0);
        spouse1 = new GovPensionEntity(0, 0, "SS1", spouse1FullMonthlyBenefit, spouse1StartAge, 0);
        ssr1 = new SocialSecurityRules(endAge, spouse1Birthdate,
                spouse2Birthdate, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 1000, 0.1);

        spouse2 = new GovPensionEntity(0, 0, "SS2", spouse2FullMonthlyBenefit, spouse2StartAge, 0);
        ssr2 = new SocialSecurityRules(endAge, spouse2Birthdate,
                spouse1Birthdate, spouse1FullMonthlyBenefit, spouse1StartAge, true, true);
        spouse2.setRules(ssr2);
        monthlyBenefit = spouse2.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 500, 0.1);

        spouse1StartAge = new AgeData(62, 0);
        spouse1 = new GovPensionEntity(0, 0, "SS1", spouse1FullMonthlyBenefit, spouse1StartAge, 0);
        ssr1 = new SocialSecurityRules(endAge, spouse2Birthdate,
                spouse2Birthdate, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 716.66, 0.1);

        spouse1StartAge = new AgeData(70, 0);
        spouse1 = new GovPensionEntity(0, 0, "SS1", spouse1FullMonthlyBenefit, spouse1StartAge, 0);
        ssr1 = new SocialSecurityRules(endAge, spouse2Birthdate,
                spouse2Birthdate, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 1266.7, 0.1);

        spouse1StartAge = new AgeData(70, 1);
        spouse1 = new GovPensionEntity(0, 0, "SS1", spouse1FullMonthlyBenefit, spouse1StartAge, 0);
        ssr1 = new SocialSecurityRules(endAge, spouse2Birthdate,
                spouse2Birthdate, spouse2FullMonthlyBenefit, spouse2StartAge, true, true);
        spouse1.setRules(ssr1);
        monthlyBenefit = spouse1.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 1266.7, 0.1);


        spouse2Birthdate = "03-13-1957";
        spouse2StartAge = new AgeData(66, 6);
        spouse2FullMonthlyBenefit = "0";

        spouse2Birthdate = "11-05-1958";
        spouse1StartAge = new AgeData(66, 8);
        spouse1FullMonthlyBenefit = "1000";

        GovPensionEntity spouse = new GovPensionEntity(0, 0, "SS2", spouse2FullMonthlyBenefit, spouse2StartAge,
                1);
        ssr2 = new SocialSecurityRules(endAge, spouse2Birthdate,
                spouse2Birthdate, spouse1FullMonthlyBenefit, spouse1StartAge, true, false);
        spouse.setRules(ssr2);

        monthlyBenefit = spouse.getMonthlyBenefit();
        AgeData age = spouse.getActualStartAge();
        AgeData actualAge = new AgeData(68, 4);
        assertEquals(age, actualAge);
        assertEquals(monthlyBenefit, 500, 1);

        spouse2Birthdate = "03-13-1965";
        spouse2StartAge = new AgeData(65, 0);
        spouse2FullMonthlyBenefit = "0";

        spouse2Birthdate = "11-05-1938";
        spouse1StartAge = new AgeData(66, 8);
        spouse1FullMonthlyBenefit = "1000";

        spouse = new GovPensionEntity(0, 0, "SS2", spouse2FullMonthlyBenefit, spouse2StartAge,
                1);
        ssr2 = new SocialSecurityRules(endAge, spouse2Birthdate,
                spouse2Birthdate, spouse1FullMonthlyBenefit, spouse1StartAge, true, false);
        spouse.setRules(ssr2);

        monthlyBenefit = spouse.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 433.35, .01);
    }

    /**
     * Test benefits where the spouse's benefits is more than half
     * the principle spouse's benefits.
     */
    @Test
    public void testSpouseBenefits() {
        String principleSpouseFullMonthlyBenefit = "1000";
        AgeData principleSpouseStartAge = new AgeData(66, 8);
        AgeData endAge = new AgeData(90, 0);
        String spouseBirthdate = "03-13-1957";
        String spouseFullBenefit = "0";
        AgeData spouseStartAge;
        String princpleSpouseBirthdate = "11-05-1958";


        principleSpouseFullMonthlyBenefit = "900";
        spouseFullBenefit = "500";
        spouseStartAge = new AgeData(66, 6);

        GovPensionEntity principleSpouse = new GovPensionEntity(0, 0, "SS1", principleSpouseFullMonthlyBenefit, principleSpouseStartAge,
                0);
        SocialSecurityRules ssr1 = new SocialSecurityRules(endAge, princpleSpouseBirthdate,
                spouseBirthdate, spouseFullBenefit, spouseStartAge, true, true);
        principleSpouse.setRules(ssr1);
        double monthlyBenefit = principleSpouse.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 900, 1);


        GovPensionEntity spouse = new GovPensionEntity(0, 0, "SS2", spouseFullBenefit, spouseStartAge,
                1);

        SocialSecurityRules ssr2 = new SocialSecurityRules(endAge, spouseBirthdate,
                princpleSpouseBirthdate, principleSpouseFullMonthlyBenefit, principleSpouseStartAge, true, false);

        spouse.setRules(ssr2);

        monthlyBenefit = spouse.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 500, 1);

        spouseStartAge = new AgeData(66, 0);
        spouse = new GovPensionEntity(0, 0, "SS2", spouseFullBenefit, spouseStartAge,
                1);

        ssr2 = new SocialSecurityRules(endAge, spouseBirthdate,
                princpleSpouseBirthdate, principleSpouseFullMonthlyBenefit, principleSpouseStartAge, true, false);

        spouse.setRules(ssr2);

        monthlyBenefit = spouse.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 483.33, 0.1);

        spouseStartAge = new AgeData(66, 0);
        principleSpouseFullMonthlyBenefit = "900";
        spouseFullBenefit = "0";
        spouse = new GovPensionEntity(0, 0, "SS2", spouseFullBenefit, spouseStartAge,
                1);

        ssr2 = new SocialSecurityRules(endAge, spouseBirthdate,
                princpleSpouseBirthdate, principleSpouseFullMonthlyBenefit, principleSpouseStartAge, true, false);

        spouse.setRules(ssr2);

        monthlyBenefit = spouse.getMonthlyBenefit();
        assertEquals(monthlyBenefit, 483.33, 0.1);
    }
}
