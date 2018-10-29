package com.intelliviz.db;

import com.intelliviz.data.GovPension;
import com.intelliviz.data.IncomeData;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.data.SocialSecurityRules;
import com.intelliviz.lowlevel.data.AgeData;

import org.junit.Test;

import static com.intelliviz.lowlevel.util.RetirementConstants.OWNER_PRIMARY;
import static com.intelliviz.lowlevel.util.RetirementConstants.OWNER_SPOUSE;
import static org.junit.Assert.assertEquals;

public class NewSocialSecurityRulesTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        //Context appContext = InstrumentationRegistry.getTargetContext();

        //assertEquals("com.intelliviz.retirementhelper.paid", appContext.getPackageName());
    }

    @Test
    public void testEarlyRetirementPenalty() {
        String fullMonthlyBenefit = "1000";
        AgeData startAge = new AgeData(66, 8);
        AgeData endAge = new AgeData(90, 0);
        String principleSpouseBirthdate = "11-05-1958";

        RetirementOptions ro = new RetirementOptions(endAge, null, principleSpouseBirthdate, null);

        GovPension principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, fullMonthlyBenefit, startAge);
        SocialSecurityRules ssr1 = new SocialSecurityRules(ro);
        principleSpouse.setRules(ssr1);

        IncomeData incomeData = principleSpouse.getIncomeData(new AgeData(66, 8));
        assertEquals(1000, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(66, 7));
        assertEquals(994.444, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(66, 6));
        assertEquals(988.889, incomeData.getMonthlyAmount(), .00001); // TODO expected value should be 988.8888

        incomeData = principleSpouse.getIncomeData(new AgeData(65, 8));
        assertEquals(933.333, incomeData.getMonthlyAmount(), .00001); // TODO expected value should be 933.3333

        incomeData = principleSpouse.getIncomeData(new AgeData(63, 8));
        assertEquals(800, incomeData.getMonthlyAmount(), .00001);

        incomeData = principleSpouse.getIncomeData(new AgeData(63, 7));
        assertEquals(795.833, incomeData.getMonthlyAmount(), .00001); // TODO expected value should be 795.8333

        incomeData = principleSpouse.getIncomeData(new AgeData(62, 0));
        assertEquals(716.667, incomeData.getMonthlyAmount(), .00001); // TODO expected value should be 716.6666

        incomeData = principleSpouse.getIncomeData(new AgeData(61, 11));
        assertEquals(0, incomeData.getMonthlyAmount(), 0);

        principleSpouseBirthdate = "11-05-1962";
        ro = new RetirementOptions(endAge, null, principleSpouseBirthdate, null);
        principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, fullMonthlyBenefit, startAge);
        ssr1 = new SocialSecurityRules(ro);
        principleSpouse.setRules(ssr1);

        incomeData = principleSpouse.getIncomeData(new AgeData(62, 0));
        assertEquals(700, incomeData.getMonthlyAmount(), .00001); // TO
    }

    @Test
    public void testDelayedRetirementCredit() {
        String fullMonthlyBenefit = "1000";
        AgeData startAge = new AgeData(66, 8);
        AgeData endAge = new AgeData(90, 0);
        String principleSpouseBirthdate = "01-01-1917";

        RetirementOptions ro = new RetirementOptions(endAge, null, principleSpouseBirthdate, null);

        GovPension principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, fullMonthlyBenefit, startAge);
        SocialSecurityRules ssr1 = new SocialSecurityRules(ro);
        principleSpouse.setRules(ssr1);

        IncomeData incomeData = principleSpouse.getIncomeData(new AgeData(65, 0));
        assertEquals(1000, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(65, 1));
        assertEquals(1002.5, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 0));
        assertEquals(1150, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 1));
        assertEquals(1150, incomeData.getMonthlyAmount(), .001);

        principleSpouseBirthdate = "01-01-1925";
        ro = new RetirementOptions(endAge, null, principleSpouseBirthdate, null);
        principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, fullMonthlyBenefit, startAge);
        ssr1 = new SocialSecurityRules(ro);
        principleSpouse.setRules(ssr1);

        incomeData = principleSpouse.getIncomeData(new AgeData(65, 0));
        assertEquals(1000, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(65, 1));
        assertEquals(1002.92, incomeData.getMonthlyAmount(), .001); // TODO expected value should be 1002.9166

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 0));
        assertEquals(1175, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 1));
        assertEquals(1175, incomeData.getMonthlyAmount(), .001);

        principleSpouseBirthdate = "01-01-1927";
        ro = new RetirementOptions(endAge, null, principleSpouseBirthdate, null);
        principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, fullMonthlyBenefit, startAge);
        ssr1 = new SocialSecurityRules(ro);
        principleSpouse.setRules(ssr1);

        incomeData = principleSpouse.getIncomeData(new AgeData(65, 0));
        assertEquals(1000, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(65, 1));
        assertEquals(1003.33, incomeData.getMonthlyAmount(), .001); // TODO expected value should be 1003.3333

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 0));
        assertEquals(1200, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 1));
        assertEquals(1200, incomeData.getMonthlyAmount(), .001);

        principleSpouseBirthdate = "01-01-1929";
        ro = new RetirementOptions(endAge, null, principleSpouseBirthdate, null);
        principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, fullMonthlyBenefit, startAge);
        ssr1 = new SocialSecurityRules(ro);
        principleSpouse.setRules(ssr1);

        incomeData = principleSpouse.getIncomeData(new AgeData(65, 0));
        assertEquals(1000, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(65, 1));
        assertEquals(1003.75, incomeData.getMonthlyAmount(), .001); // TODO expected value should be 1003.3333

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 0));
        assertEquals(1225, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 1));
        assertEquals(1225, incomeData.getMonthlyAmount(), .001);

        principleSpouseBirthdate = "01-01-1931";
        ro = new RetirementOptions(endAge, null, principleSpouseBirthdate, null);
        principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, fullMonthlyBenefit, startAge);
        ssr1 = new SocialSecurityRules(ro);
        principleSpouse.setRules(ssr1);

        incomeData = principleSpouse.getIncomeData(new AgeData(65, 0));
        assertEquals(1000, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(65, 1));
        assertEquals(1004.17, incomeData.getMonthlyAmount(), .001); // TODO expected value should be 1004.1666

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 0));
        assertEquals(1250, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 1));
        assertEquals(1250, incomeData.getMonthlyAmount(), .001);

        principleSpouseBirthdate = "01-01-1933";
        ro = new RetirementOptions(endAge, null, principleSpouseBirthdate, null);
        principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, fullMonthlyBenefit, startAge);
        ssr1 = new SocialSecurityRules(ro);
        principleSpouse.setRules(ssr1);

        incomeData = principleSpouse.getIncomeData(new AgeData(65, 0));
        assertEquals(1000, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(65, 1));
        assertEquals(1004.58, incomeData.getMonthlyAmount(), .001); // TODO expected value should be 1004.5833

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 0));
        assertEquals(1275, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 1));
        assertEquals(1275, incomeData.getMonthlyAmount(), .001);

        principleSpouseBirthdate = "01-01-1935";
        ro = new RetirementOptions(endAge, null, principleSpouseBirthdate, null);
        principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, fullMonthlyBenefit, startAge);
        ssr1 = new SocialSecurityRules(ro);
        principleSpouse.setRules(ssr1);

        incomeData = principleSpouse.getIncomeData(new AgeData(65, 0));
        assertEquals(1000, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(65, 1));
        assertEquals(1005, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 0));
        assertEquals(1300, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 1));
        assertEquals(1300, incomeData.getMonthlyAmount(), .001);

        principleSpouseBirthdate = "01-01-1937";
        ro = new RetirementOptions(endAge, null, principleSpouseBirthdate, null);
        principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, fullMonthlyBenefit, startAge);
        ssr1 = new SocialSecurityRules(ro);
        principleSpouse.setRules(ssr1);

        incomeData = principleSpouse.getIncomeData(new AgeData(65, 0));
        assertEquals(1000, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(65, 1));
        assertEquals(1005.42, incomeData.getMonthlyAmount(), .001); // TODO expected value should be 1005.4166

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 0));
        assertEquals(1325, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 1));
        assertEquals(1325, incomeData.getMonthlyAmount(), .001);

        principleSpouseBirthdate = "01-01-1938";
        ro = new RetirementOptions(endAge, null, principleSpouseBirthdate, null);
        principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, fullMonthlyBenefit, startAge);
        ssr1 = new SocialSecurityRules(ro);
        principleSpouse.setRules(ssr1);

        AgeData fra = ssr1.getFullRetirementAge();

        incomeData = principleSpouse.getIncomeData(fra);
        assertEquals(1000, incomeData.getMonthlyAmount(), .001);

        AgeData age = new AgeData(fra.getNumberOfMonths()+1);
        incomeData = principleSpouse.getIncomeData(age);
        assertEquals(1005.42, incomeData.getMonthlyAmount(), .001); // TODO expected value should be 1005.4166

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 0));
        assertEquals(1314.17, incomeData.getMonthlyAmount(), .001); // TODO expected value should be 1314.1666

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 1));
        assertEquals(1314.17, incomeData.getMonthlyAmount(), .001);

        principleSpouseBirthdate = "01-01-1939";
        ro = new RetirementOptions(endAge, null, principleSpouseBirthdate, null);
        principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, fullMonthlyBenefit, startAge);
        ssr1 = new SocialSecurityRules(ro);
        principleSpouse.setRules(ssr1);

        fra = ssr1.getFullRetirementAge();

        incomeData = principleSpouse.getIncomeData(fra);
        assertEquals(1000, incomeData.getMonthlyAmount(), .001);

        age = new AgeData(fra.getNumberOfMonths()+1);
        incomeData = principleSpouse.getIncomeData(age);
        assertEquals(1005.83, incomeData.getMonthlyAmount(), .001); // TODO expected value should be 1005.8333

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 0));
        assertEquals(1326.67, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 1));
        assertEquals(1326.67, incomeData.getMonthlyAmount(), .001);

        principleSpouseBirthdate = "01-01-1941";
        ro = new RetirementOptions(endAge, null, principleSpouseBirthdate, null);
        principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, fullMonthlyBenefit, startAge);
        ssr1 = new SocialSecurityRules(ro);
        principleSpouse.setRules(ssr1);

        fra = ssr1.getFullRetirementAge();

        incomeData = principleSpouse.getIncomeData(fra);
        assertEquals(1000, incomeData.getMonthlyAmount(), .001);

        age = new AgeData(fra.getNumberOfMonths()+1);
        incomeData = principleSpouse.getIncomeData(age);
        assertEquals(1006.25, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 0));
        assertEquals(1325, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 1));
        assertEquals(1325, incomeData.getMonthlyAmount(), .001);

        principleSpouseBirthdate = "01-01-1942";
        ro = new RetirementOptions(endAge, null, principleSpouseBirthdate, null);
        principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, fullMonthlyBenefit, startAge);
        ssr1 = new SocialSecurityRules(ro);
        principleSpouse.setRules(ssr1);

        fra = ssr1.getFullRetirementAge();

        incomeData = principleSpouse.getIncomeData(fra);
        assertEquals(1000, incomeData.getMonthlyAmount(), .001);

        age = new AgeData(fra.getNumberOfMonths()+1);
        incomeData = principleSpouse.getIncomeData(age);
        assertEquals(1006.25, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 0));
        assertEquals(1312.5, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 1));
        assertEquals(1312.5, incomeData.getMonthlyAmount(), .001);

        principleSpouseBirthdate = "01-01-1943";
        ro = new RetirementOptions(endAge, null, principleSpouseBirthdate, null);
        principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, fullMonthlyBenefit, startAge);
        ssr1 = new SocialSecurityRules(ro);
        principleSpouse.setRules(ssr1);

        fra = ssr1.getFullRetirementAge();

        incomeData = principleSpouse.getIncomeData(fra);
        assertEquals(1000, incomeData.getMonthlyAmount(), .001);

        age = new AgeData(fra.getNumberOfMonths()+1);
        incomeData = principleSpouse.getIncomeData(age);
        assertEquals(1006.67, incomeData.getMonthlyAmount(), .001); // TODO expected value should be 1006.6666

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 0));
        assertEquals(1320, incomeData.getMonthlyAmount(), .001);

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 1));
        assertEquals(1320, incomeData.getMonthlyAmount(), .001);
    }

    @Test
    public void testSpouseSameBenefitSameAge() {
        String fullMonthlyBenefit = "1000";
        String fullSpouseMonthlyBenefit = "1000";
        AgeData startAge = new AgeData(66, 8);
        AgeData spouseStartAge = new AgeData(66, 8);
        AgeData endAge = new AgeData(90, 0);
        AgeData spouseEndAge = new AgeData(90, 0);
        String principleSpouseBirthdate = "01-01-1958";
        String spouseBirthdate = "01-01-1958";

        RetirementOptions ro = new RetirementOptions(endAge, spouseEndAge, principleSpouseBirthdate, spouseBirthdate);
        SocialSecurityRules ssr1 = new SocialSecurityRules(ro, fullSpouseMonthlyBenefit, spouseStartAge, true);

        GovPension principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, fullMonthlyBenefit, startAge);
        principleSpouse.setRules(ssr1);

        SocialSecurityRules ssr2 = new SocialSecurityRules(ro, fullSpouseMonthlyBenefit,
                spouseStartAge, true);
        GovPension spouse = new GovPension(0, 0, "SS2", OWNER_SPOUSE, fullSpouseMonthlyBenefit, spouseStartAge);
        spouse.setRules(ssr2);

        IncomeData incomeData = principleSpouse.getIncomeData(new AgeData(62, 0));
        assertEquals(716.667, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(new AgeData(62, 0));
        assertEquals(716.667, incomeData.getMonthlyAmount(), 0.001);

        incomeData = principleSpouse.getIncomeData(new AgeData(65, 0));
        assertEquals(888.889, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(new AgeData(65, 0));
        assertEquals(888.889, incomeData.getMonthlyAmount(), 0.001);

        AgeData age = ssr1.getFullRetirementAge();
        incomeData = principleSpouse.getIncomeData(age);
        assertEquals(1000, incomeData.getMonthlyAmount(), 0.001);

        age = ssr2.getFullRetirementAge();
        incomeData = spouse.getIncomeData(age);
        assertEquals(1000, incomeData.getMonthlyAmount(), 0.001);

        incomeData = principleSpouse.getIncomeData(new AgeData(70, 0));
        assertEquals(1266.67, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(new AgeData(70, 0));
        assertEquals(1266.67, incomeData.getMonthlyAmount(), 0.001);
    }

    @Test
    public void testYoungerSpouseSameBenefit() {
        String fullMonthlyBenefit = "1000";
        String fullSpouseMonthlyBenefit = "1000";
        AgeData startAge = new AgeData(66, 8);
        AgeData spouseStartAge = new AgeData(66, 8);
        AgeData endAge = new AgeData(90, 0);
        AgeData spouseEndAge = new AgeData(90, 0);
        String principleSpouseBirthdate = "01-01-1958";
        String spouseBirthdate = "01-01-1960";

        RetirementOptions ro = new RetirementOptions(endAge, spouseEndAge, principleSpouseBirthdate, spouseBirthdate);
        GovPension principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, fullMonthlyBenefit, startAge);
        SocialSecurityRules ssr1 = new SocialSecurityRules(ro, fullSpouseMonthlyBenefit, spouseStartAge, true);
        principleSpouse.setRules(ssr1);

        GovPension spouse = new GovPension(0, 0, "SS2", OWNER_SPOUSE, fullSpouseMonthlyBenefit, spouseStartAge);
        SocialSecurityRules ssr2 = new SocialSecurityRules(ro, fullMonthlyBenefit, startAge, true);
        spouse.setRules(ssr2);

        AgeData primaryAge = new AgeData(62, 0);
        IncomeData incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(716.667, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(63, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(766.667, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(64, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(822.222, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(700, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = ssr1.getFullRetirementAge();
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1000, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(844.444, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(70, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1266.67, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(1080, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(71, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1266.67, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(1160.0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(72, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1266.67, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(1240.0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(73, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1266.67, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(1240.0, incomeData.getMonthlyAmount(), 0.001);
    }

    @Test
    public void testOlderSpouseSameBenefit() {
        String fullMonthlyBenefit = "1000";
        String fullSpouseMonthlyBenefit = "1000";
        AgeData startAge = new AgeData(66, 8);
        AgeData spouseStartAge = new AgeData(66, 8);
        AgeData endAge = new AgeData(90, 0);
        AgeData spouseEndAge = new AgeData(90, 0);
        String principleSpouseBirthdate = "01-01-1964";
        String spouseBirthdate = "01-01-1960";

        RetirementOptions ro = new RetirementOptions(endAge, spouseEndAge, principleSpouseBirthdate, spouseBirthdate);
        SocialSecurityRules ssr1 = new SocialSecurityRules(ro, fullSpouseMonthlyBenefit, spouseStartAge, true);

        GovPension principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, fullMonthlyBenefit, startAge);
        principleSpouse.setRules(ssr1);

        SocialSecurityRules ssr2 = new SocialSecurityRules(ro, fullSpouseMonthlyBenefit,
                spouseStartAge, true);
        GovPension spouse = new GovPension(0, 0, "SS2", OWNER_SPOUSE, fullSpouseMonthlyBenefit, spouseStartAge);
        spouse.setRules(ssr2);

        AgeData primaryAge = new AgeData(62, 0);
        IncomeData incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(700, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(933.333, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(63, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(750.0, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(1000, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(64, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(800.0, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(1080.0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = ssr1.getFullRetirementAge();
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1000, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(1240.0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(70, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1240.0, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(1240, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(71, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1240.0, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(1240.0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(72, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1240.0, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(1240.0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(73, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1240, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(1240.0, incomeData.getMonthlyAmount(), 0.001);
    }

    @Test
    public void testYoungerSpousalBenefits() {
        String fullMonthlyBenefit = "1000";
        String fullSpouseMonthlyBenefit = "0";
        AgeData startAge = new AgeData(66, 8);
        AgeData spouseStartAge = new AgeData(66, 8);
        AgeData endAge = new AgeData(90, 0);
        AgeData spouseEndAge = new AgeData(90, 0);
        String principleSpouseBirthdate = "01-01-1960";
        String spouseBirthdate = "01-01-1962";

        RetirementOptions ro = new RetirementOptions(endAge, spouseEndAge, principleSpouseBirthdate, spouseBirthdate);

        GovPension principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, fullMonthlyBenefit, startAge);
        SocialSecurityRules ssr1 = new SocialSecurityRules(ro, fullSpouseMonthlyBenefit, spouseStartAge, true);
        principleSpouse.setRules(ssr1);

        GovPension spouse = new GovPension(0, 0, "SS2", OWNER_SPOUSE, fullSpouseMonthlyBenefit, spouseStartAge);
        SocialSecurityRules ssr2 = new SocialSecurityRules(ro, fullMonthlyBenefit, startAge, true);
        spouse.setRules(ssr2);

        AgeData primaryAge = new AgeData(62, 0);
        IncomeData incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(700, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(63, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(750.0, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(64, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(800.0, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(350, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = ssr1.getFullRetirementAge();
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1000, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(433.334, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(70, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1240.0, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(500.0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(71, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1240.0, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(500.0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(72, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1240.0, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(500.0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(73, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1240, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(500.0, incomeData.getMonthlyAmount(), 0.001);
    }

    @Test
    public void testSpousalBenefitsSameAge() {
        String fullMonthlyBenefit = "1000";
        String fullSpouseMonthlyBenefit = "0";
        AgeData startAge = new AgeData(66, 8);
        AgeData spouseStartAge = new AgeData(66, 8);
        AgeData endAge = new AgeData(90, 0);
        AgeData spouseEndAge = new AgeData(90, 0);
        String principleSpouseBirthdate = "01-01-1960";
        String spouseBirthdate = "01-01-1960";

        RetirementOptions ro = new RetirementOptions(endAge, spouseEndAge, principleSpouseBirthdate, spouseBirthdate);

        GovPension principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, fullMonthlyBenefit, startAge);
        SocialSecurityRules ssr1 = new SocialSecurityRules(ro, fullSpouseMonthlyBenefit, spouseStartAge, true);
        principleSpouse.setRules(ssr1);

        GovPension spouse = new GovPension(0, 0, "SS2", OWNER_SPOUSE, fullSpouseMonthlyBenefit, spouseStartAge);
        SocialSecurityRules ssr2 = new SocialSecurityRules(ro, fullMonthlyBenefit, startAge, true);
        spouse.setRules(ssr2);

        AgeData primaryAge = new AgeData(62, 0);
        IncomeData incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(700, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(350, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(63, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(750.0, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(375, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(64, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(800.0, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(400, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = ssr1.getFullRetirementAge();
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1000, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(500.0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(70, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1240.0, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(500.0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(71, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1240.0, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(500.0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(72, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1240.0, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(500.0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(73, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1240, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(500.0, incomeData.getMonthlyAmount(), 0.001);
    }

    @Test
    public void testOlderSpousalBenefits() {
        String fullMonthlyBenefit = "1000";
        String fullSpouseMonthlyBenefit = "0";
        AgeData startAge = new AgeData(66, 8);
        AgeData spouseStartAge = new AgeData(66, 8);
        AgeData endAge = new AgeData(90, 0);
        AgeData spouseEndAge = new AgeData(90, 0);
        String principleSpouseBirthdate = "01-01-1962";
        String spouseBirthdate = "01-01-1960";

        RetirementOptions ro = new RetirementOptions(endAge, spouseEndAge, principleSpouseBirthdate, spouseBirthdate);

        GovPension principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, fullMonthlyBenefit, startAge);
        SocialSecurityRules ssr1 = new SocialSecurityRules(ro, fullSpouseMonthlyBenefit, spouseStartAge, true);
        principleSpouse.setRules(ssr1);

        GovPension spouse = new GovPension(0, 0, "SS2", OWNER_SPOUSE, fullSpouseMonthlyBenefit, spouseStartAge);
        SocialSecurityRules ssr2 = new SocialSecurityRules(ro, fullMonthlyBenefit, startAge, true);
        spouse.setRules(ssr2);

        AgeData primaryAge = new AgeData(62, 0);
        IncomeData incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(700, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(400, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(63, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(750.0, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(433.334, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(64, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(800.0, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(466.667, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = ssr1.getFullRetirementAge();
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1000, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(500.0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(70, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1240.0, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(500.0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(71, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1240.0, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(500.0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(72, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1240.0, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(500.0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(73, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1240, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(500.0, incomeData.getMonthlyAmount(), 0.001);
    }

    @Test
    public void testYoungerSpouseOverride() {
        String fullMonthlyBenefit = "1000";
        String fullSpouseMonthlyBenefit = "0";
        AgeData startAge = new AgeData(66, 8);
        AgeData spouseStartAge = new AgeData(66, 8);
        AgeData endAge = new AgeData(90, 0);
        AgeData spouseEndAge = new AgeData(90, 0);
        String principleSpouseBirthdate = "01-01-1958";
        String spouseBirthdate = "01-01-1960";

        RetirementOptions ro = new RetirementOptions(endAge, spouseEndAge, principleSpouseBirthdate, spouseBirthdate);

        GovPension principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, fullMonthlyBenefit, startAge);
        SocialSecurityRules ssr1 = new SocialSecurityRules(ro, fullSpouseMonthlyBenefit, spouseStartAge, true, true);
        principleSpouse.setRules(ssr1);

        GovPension spouse = new GovPension(0, 0, "SS2", OWNER_SPOUSE, fullSpouseMonthlyBenefit, spouseStartAge);
        SocialSecurityRules ssr2 = new SocialSecurityRules(ro, fullMonthlyBenefit, startAge, true, true);
        spouse.setRules(ssr2);

        AgeData primaryAge = new AgeData(62, 0);
        IncomeData incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(0, incomeData.getMonthlyAmount(), 0.001);

        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(65, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(0, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = ssr1.getFullRetirementAge();
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1000, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(68, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1000, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(68, 8);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1000, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(488.889, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(70, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1000, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(500, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(70, 8);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1000, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(500, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(72, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1000, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(500, incomeData.getMonthlyAmount(), 0.001);
    }

    @Test
    public void testOlderSpouseOverride() {
        String fullMonthlyBenefit = "1000";
        String fullSpouseMonthlyBenefit = "0";
        AgeData startAge = new AgeData(66, 8);
        AgeData spouseStartAge = new AgeData(66, 8);
        AgeData endAge = new AgeData(90, 0);
        AgeData spouseEndAge = new AgeData(90, 0);
        String principleSpouseBirthdate = "01-01-1962";
        String spouseBirthdate = "01-01-1960";

        RetirementOptions ro = new RetirementOptions(endAge, spouseEndAge, principleSpouseBirthdate, spouseBirthdate);

        GovPension principleSpouse = new GovPension(0, 0, "SS1", OWNER_PRIMARY, fullMonthlyBenefit, startAge);
        SocialSecurityRules ssr1 = new SocialSecurityRules(ro, fullSpouseMonthlyBenefit, spouseStartAge, true, true);
        principleSpouse.setRules(ssr1);

        GovPension spouse = new GovPension(0, 0, "SS2", OWNER_SPOUSE, fullSpouseMonthlyBenefit, spouseStartAge);
        SocialSecurityRules ssr2 = new SocialSecurityRules(ro, fullMonthlyBenefit, startAge, true, true);
        spouse.setRules(ssr2);

        AgeData primaryAge = new AgeData(62, 0);
        IncomeData incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(0, incomeData.getMonthlyAmount(), 0.001);

        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(65, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(0, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = ssr1.getFullRetirementAge();
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1000, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(500, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(68, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1000, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(0, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(68, 8);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1000, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(488.889, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(70, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1000, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(500, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(70, 8);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1000, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(500, incomeData.getMonthlyAmount(), 0.001);

        primaryAge = new AgeData(72, 0);
        incomeData = principleSpouse.getIncomeData(primaryAge);
        assertEquals(1000, incomeData.getMonthlyAmount(), 0.001);

        incomeData = spouse.getIncomeData(primaryAge);
        assertEquals(500, incomeData.getMonthlyAmount(), 0.001);
    }
}
