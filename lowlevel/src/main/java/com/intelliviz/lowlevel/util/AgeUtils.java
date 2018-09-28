package com.intelliviz.lowlevel.util;

import com.intelliviz.lowlevel.data.AgeData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.lang.Integer.parseInt;

/**
 * Created by edm on 5/28/2018.
 */

public class AgeUtils {
    public static final String DATE_FORMAT = "dd-MM-yyyy";

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

        int birthDay = parseInt(birthTokens[0]);
        int birthMonth = parseInt(birthTokens[1]);
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
            return tokens[0].matches("[1-9]");
        } else if(tokens[0].length() == 2) {
            return tokens[0].matches("[0-9]{2}");
        }

        // Validate month
        if(tokens[1].length() == 1) {
            return tokens[1].matches("[1-9]");
        } else if(tokens[1].length() == 2) {
            return tokens[1].matches("[0-9]{2}");
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
        return year + "y " + month + "m";
    }

    public static String getBirthdate(AgeData age) {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        String sdate = sdf.format(date);
        AgeData ageData = getAge(sdate);
        int numMonths = ageData.diff(age);
        if(age.isBefore(ageData)) {
            age.add(numMonths);
        } else {
            age.subtract(numMonths);
        }
        String[] tokens = sdate.split("-");
        int day = parseInt(tokens[0]);
        int month = parseInt(tokens[1]);
        int year = parseInt(tokens[2]);
        int newYear = year - age.getYear();
        int newMonth = month - age.getMonth();
        if(newMonth < 0) {
            newMonth += 12;
            newYear--;
        }
        return day + "-" + newMonth + "-" + newYear;
    }


    /**
     * Get the age for person2, given person1's age.
     * @param birthdate1 The birthdate for person1.
     * @param birthdate2 The birthdate for person2.
     * @param age1 The age of person1.
     * @return The age of person2.
     */
    public static AgeData getAge(String birthdate1, String birthdate2, AgeData age1) {
        if(birthdate1 == null || birthdate1.isEmpty() || birthdate2 == null || birthdate2.isEmpty()) {
            return new AgeData(age1);
        }
        AgeData currentAge1 = AgeUtils.getAge(birthdate1);
        AgeData currentAge2 = AgeUtils.getAge(birthdate2);
        int numMonths = currentAge2.diff(currentAge1);

        if(currentAge1.isBefore(currentAge2)) {
            // person1 is younger
            return age1.subtract(numMonths);
        } else {
            // person1 is older
            return age1.add(numMonths);
        }
    }

    public static AgeData ageDiff(String birthdate, String spouseBirthdate) {
        AgeData currentAge = AgeUtils.getAge(birthdate);
        AgeData spouseCurrentAge = AgeUtils.getAge(spouseBirthdate);
        int numMonths = spouseCurrentAge.diff(currentAge);
        return new AgeData(numMonths);
    }
}
