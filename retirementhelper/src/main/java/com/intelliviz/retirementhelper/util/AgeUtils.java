package com.intelliviz.retirementhelper.util;

import com.intelliviz.retirementhelper.data.AgeData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.intelliviz.retirementhelper.util.RetirementConstants.DATE_FORMAT;
import static java.lang.Integer.parseInt;

/**
 * Created by edm on 5/28/2018.
 */

public class AgeUtils {

    public static int getBirthYear(String birthdate) {
        if(birthdate == null || birthdate.isEmpty()) {
            return 0;
        }
        String[] birthTokens = birthdate.split("-");
        if(birthTokens.length != 3) {
            return 0;
        }
        return Integer.parseInt(birthTokens[2]);
    }

    public static int getBirthMonth(String birthdate) {
        String[] birthTokens = birthdate.split("-");
        return parseInt(birthTokens[1]);
    }

    public static int getBirthDay(String birthdate) {
        String[] birthTokens = birthdate.split("-");
        return parseInt(birthTokens[0]);
    }

    public static AgeData getAge(String birthdate) {
        String[] birthTokens = birthdate.split("-");
        if(birthTokens.length != 3) {
            return new AgeData();
        }

        int birthMonth = parseInt(birthTokens[0]);
        int birthDay = parseInt(birthTokens[1]);
        int birthYear = parseInt(birthTokens[2]);

        String today = getTodaysDate();

        String[] nowTokens = today.split("-");

        int nowDay = parseInt(nowTokens[0]);
        int nowMonth = parseInt(nowTokens[1]);
        int nowYear = parseInt(nowTokens[2]);

        int years = nowYear - birthYear;

        int months = nowMonth - birthMonth;
        if(months < 0) {
            years--;
            months += 12;
        } else if(months == 0) {
            int dayDiff = nowDay - birthDay;
            if(dayDiff < 0) {
                years--;
                months = 11;
            }
        }
        return new AgeData(years, months);
    }


    /**
     * The format for age is "Y M", where Y is an integer that is the year, and M
     * is an integer that is the month.
     *
     * @param age The age.
     * @return The AgeData;
     */
    // TODO make sure all callers check for invalid age
    public static AgeData parseAgeString(String age) {
        if(age == null || age.isEmpty()) {
            return null;
        }

        String[] tokens = age.split(" ");
        int year;
        int month = 0;
        if(tokens.length == 1) {
            try {
                year = parseInt(tokens[0]);
            } catch (NumberFormatException e) {
                return null;
            }
            return new AgeData(year, month);
        } else if(tokens.length == 2) {
            try {
                year = parseInt(tokens[0]);
                month = parseInt(tokens[1]);
            } catch (NumberFormatException e) {
                return null;
            }
            return new AgeData(year, month);
        }
        return new AgeData();
    }

    public static AgeData parseAgeString(String year, String month) {
        StringBuilder sb = new StringBuilder();
        sb.append(year);
        sb.append(" ");
        sb.append(month);
        return parseAgeString(sb.toString());
    }

    public static String trimAge(String age) {
        age = age.replace("y", "");
        age = age.replace("m", "");
        return age;
    }


    public static boolean validateBirthday(String birthdate) {
        if(birthdate == null || birthdate.isEmpty()) {
            return false;
        }
        String[] tokens = birthdate.split("-");
        if(tokens.length != 3) {
            return false;
        }

        // Validate day
        if(tokens[0].length() == 1) {
            if(tokens[0].matches("[1-9]")) {
                return true;
            } else {
                return false;
            }
        } else if(tokens[0].length() == 2) {
            if (tokens[0].matches("[0-9]{2}")) {
                return true;
            } else {
                return false;
            }
        }

        // Validate month
        if(tokens[1].length() == 1) {
            if(tokens[1].matches("[1-9]")) {
                return true;
            } else {
                return false;
            }
        } else if(tokens[1].length() == 2) {
            if(tokens[1].matches("[0-9]{2}")) {
                return true;
            } else {
                return false;
            }
        }

        // Validate year
        if(tokens[2].length() != 4) {
            return false;
        }

        if(!tokens[2].matches("[0-9]{4}")) {
            return false;
        }

        String date = tokens[0]+"-"+tokens[1]+"-"+tokens[2];

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        try {
            dateFormat.setLenient(false);
            dateFormat.parse(date);
        } catch(ParseException e) {
            return false;
        }

        return true;
    }

    public static String getTodaysDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        return sdf.format(date);
    }
    public static String getFormattedAge(AgeData ageData) {
        String year = Integer.toString(ageData.getYear());
        String month = Integer.toString(ageData.getMonth());
        StringBuilder sb = new StringBuilder();
        sb.append(year);
        sb.append("y ");
        sb.append(month);
        sb.append("m");
        return sb.toString();
    }
    /**
     * Get the age for the spouse, given the principle spouse's age.
     * @param birthdate The birthdate of the principle spouse.
     * @param spouseBirthdate The spouse's birthdate.
     * @param age The age of tghe principle spouse.
     * @return The spouse start age.
     */
    public static AgeData getSpouseAge(String birthdate, String spouseBirthdate, AgeData age) {
        AgeData currentAge = AgeUtils.getAge(birthdate);
        AgeData spouseAge =  AgeUtils.getAge(spouseBirthdate);
        int numMonths = currentAge.diff(spouseAge);

        if(spouseAge.isBefore(currentAge)) {
            // spouse is younger
            return age.subtract(numMonths);
        } else {
            // spouse is older
            return age.add(numMonths);
        }
    }


    /**
     * Get the age for the spouse, given the other spouse's age.
     * @param birthdate The birthdate.
     * @param spouseBirthdate The spouse's birthdate.
     * @param spouseAge The age.
     * @return The spouse start age.
     */
    public static AgeData getAge(String birthdate, String spouseBirthdate, AgeData spouseAge) {
        AgeData currentAge = AgeUtils.getAge(birthdate);
        AgeData spouseCurrentAge = AgeUtils.getAge(spouseBirthdate);
        int numMonths = spouseCurrentAge.diff(currentAge);

        if(currentAge.isBefore(spouseCurrentAge)) {
            // spouse is younger
            return spouseAge.subtract(numMonths);
        } else {
            // spouse is older
            return spouseAge.add(numMonths);
        }
    }


}
