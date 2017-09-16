package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.intelliviz.retirementhelper.util.SystemUtils;

/**
 * Created by edm on 8/14/2017.
 */

public class SocialSecurityRules implements IncomeTypeRules, Parcelable {
    private static final double MAX_SS_PENALTY = 30.0;
    private int mBirthYear;
    private AgeData mMinAge;
    private AgeData mMaxAge;
    private double mFullRetirementBenefit;

    public SocialSecurityRules(String birthDate, AgeData minAge, AgeData maxAge, double fullRetirementBenefit) {
        mBirthYear = SystemUtils.getBirthYear(birthDate);
        mMinAge = minAge;
        mMaxAge = maxAge;
        mFullRetirementBenefit = fullRetirementBenefit;
    }

    @Override
    public double getMonthlyBenefitForAge(AgeData startAge) {
        AgeData retireAge = getFullRetirementAge(mBirthYear);
        AgeData diffAge = retireAge.subtract(startAge);
        int numMonths = diffAge.getNumberOfMonths();
        if(numMonths > 0) {
            // this is early retirement; the adjustment will be a penalty.
            if(numMonths < 37) {
                return (numMonths * 5.0) / 9.0;
            } else {
                double penalty = (numMonths * 5.0) / 12.0;
                if(penalty > MAX_SS_PENALTY) {
                    penalty = MAX_SS_PENALTY;
                }
                return penalty;
            }
        } else if(numMonths < 0) {

        } else {
            return mFullRetirementBenefit;
        }
        return 0;
    }

    @Override
    public AgeData getFullRetirementAge() {
        return getFullRetirementAge(mBirthYear);
    }

    @Override
    public double getFullMonthlyBenefit() {
        return mFullRetirementBenefit;
    }

    @Override
    public AgeData getMinimumAge() {
        return mMinAge;
    }

    @Override
    public AgeData getMaximumAge() {
        return mMaxAge;
    }

    private AgeData getFullRetirementAge(int birthYear) {
        AgeData fullAge;
        if(birthYear <= 1937) {
            fullAge = new AgeData(65, 0);
        } else if(birthYear == 1938) {
            fullAge = new AgeData(65, 2);
        } else if(birthYear == 1939) {
            fullAge = new AgeData(65, 4);
        }else if(birthYear == 1940) {
            fullAge = new AgeData(65, 6);
        } else if(birthYear == 1941) {
            fullAge = new AgeData(65, 8);
        } else if(birthYear == 19342) {
            fullAge = new AgeData(65, 10);
        } else if(birthYear >= 1939 && birthYear < 1955) {
            fullAge = new AgeData(66, 0);
        } else if(birthYear == 1955) {
            fullAge = new AgeData(66, 2);
        } else if(birthYear == 1956) {
            fullAge = new AgeData(66, 4);
        } else if(birthYear == 1957) {
            fullAge = new AgeData(66, 6);
        } else if(birthYear == 1958) {
            fullAge = new AgeData(66, 8);
        } else if(birthYear == 1959) {
            fullAge = new AgeData(66, 10);
        } else {
            fullAge = new AgeData(67, 0);
        }

        return fullAge;
    }


    /**
     * Get the percent credit per year.
     * @param birthyear The birth year.
     * @return THe delayed credit.
     */
    private static double getDelayedCredit(int birthyear) {
        if(birthyear < 1925) {
            return 3;
        } else if(birthyear < 1927) {
            return 3.5;
        } else if(birthyear < 1929) {
            return 4.0;
        } else if(birthyear < 1931) {
            return 4.5;
        } else if(birthyear < 1933 ) {
            return 5.0;
        } else if(birthyear < 1935) {
            return 5.5;
        } else if(birthyear < 1937) {
            return 6.0;
        } else if(birthyear < 1939) {
            return 6.5;
        } else if(birthyear < 1941) {
            return 7.0;
        } else if(birthyear < 1943) {
            return 7.5;
        } else {
            return 8.0; // the max
        }
    }

    private double getSocialSecurityAdjustment(String birthDate, AgeData startAge) {
        int year = SystemUtils.getBirthYear(birthDate);
        AgeData retireAge = getFullRetirementAge(year);
        AgeData diffAge = retireAge.subtract(startAge);
        int numMonths = diffAge.getNumberOfMonths();
        if(numMonths > 0) {
            // this is early retirement; the adjustment will be a penalty.
            if(numMonths < 37) {
                return (numMonths * 5.0) / 9.0;
            } else {
                double penalty = (numMonths * 5.0) / 12.0;
                if(penalty > MAX_SS_PENALTY) {
                    penalty = MAX_SS_PENALTY;
                }
                return penalty;
            }
        } else if(numMonths < 0) {
            // this is delayed retirement; the adjustment is a credit.
            double annualCredit = getDelayedCredit(year);
            return numMonths * (annualCredit / 12.0);
        } else {
            return 0; // exact retirement age
        }
    }

    private SocialSecurityRules(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mBirthYear);
        dest.writeDouble(mFullRetirementBenefit);
        dest.writeParcelable(mMinAge, flags);
        dest.writeParcelable(mMaxAge, flags);
    }

    private void readFromParcel(Parcel in) {
        mBirthYear = in.readInt();
        mFullRetirementBenefit = in.readDouble();
        mMinAge = in.readParcelable(AgeData.class.getClassLoader());
        mMaxAge = in.readParcelable(AgeData.class.getClassLoader());
    }

    public static final Parcelable.Creator<SocialSecurityRules> CREATOR = new Parcelable.Creator<SocialSecurityRules>()
    {
        @Override
        public SocialSecurityRules createFromParcel(Parcel in) {
            return new SocialSecurityRules(in);
        }

        @Override
        public SocialSecurityRules[] newArray(int size) {
            return new SocialSecurityRules[size];
        }
    };
}
