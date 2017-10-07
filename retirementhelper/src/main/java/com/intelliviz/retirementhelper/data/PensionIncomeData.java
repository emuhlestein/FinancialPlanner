package com.intelliviz.retirementhelper.data;

import android.arch.persistence.room.ColumnInfo;
import android.os.Parcel;
import android.os.Parcelable;

import com.intelliviz.retirementhelper.db.entity.MilestoneAgeEntity;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.retirementhelper.util.SystemUtils.parseAgeString;

/**
 * Class for pension income data.
 * Created by Ed Muhlestein on 5/11/2017.
 */
public class PensionIncomeData extends IncomeTypeData {
    public static final String TABLE_NAME = "pension_income";
    public static final String INCOME_TYPE_ID_FIELD = "income_type_id";
    public static final String MIN_AGE_FIELD = "min_age";
    public static final String MONTHLY_BENEFIT_FIELD = "monthly_benefit";

    @ColumnInfo(name = INCOME_TYPE_ID_FIELD)
    public long incomeTypeId;

    @ColumnInfo(name = MIN_AGE_FIELD)
    public String minAge;

    @ColumnInfo(name = MONTHLY_BENEFIT_FIELD)
    public String mStartAge;
    public double mMonthlyBenefit;

    public PensionIncomeData(long id, int type, String name, long income_type_id, String startAge, double monthlyBenefit) {
        super(id, type, name);
        mStartAge = startAge;
        mMonthlyBenefit = monthlyBenefit;
    }

    @Override
    public double getBalance() {
        return 0;
    }

    @Override
    public double getMonthlyBenefitForAge(AgeData age) {
        AgeData startAge = SystemUtils.parseAgeString(mStartAge);
        if(age.isBefore(startAge)) {
            return 0;
        } else {
            return mMonthlyBenefit;
        }
    }

    @Override
    public double getFullMonthlyBenefit() {
        return mMonthlyBenefit;
    }

    /**
     * Get the start age.
     * @return The start age.
     */
    public String getStartAge() {
        return mStartAge;
    }

    public List<MilestoneData> getMilestones(List<MilestoneAgeEntity> ages, RetirementOptionsData rod) {
        List<MilestoneData> milestones = new ArrayList<>();
        if(ages.isEmpty()) {
            return milestones;
        }

        AgeData minimumAge = parseAgeString(mStartAge);
        AgeData endAge = parseAgeString(rod.getEndAge());
        double monthlyBenefit = mMonthlyBenefit;

        MilestoneData milestone;
        for(MilestoneAgeEntity msad : ages) {
            AgeData age = msad.getAge();
            if(age.isBefore(minimumAge)) {
                milestone = new MilestoneData(age, endAge, minimumAge, 0, 0, 0, 0, 0);
            } else {
                AgeData diffAge = endAge.subtract(age);
                int numMonths = diffAge.getNumberOfMonths();

                milestone = new MilestoneData(age, endAge, minimumAge, monthlyBenefit, 0, 0, 0, numMonths);
            }
            milestones.add(milestone);
        }
        return milestones;
    }

    @Override
    public List<AgeData> getAges() {
        List<AgeData> ages = new ArrayList<>();
        ages.add(parseAgeString(mStartAge));
        return ages;
    }

    private PensionIncomeData(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mStartAge);
        dest.writeDouble(mMonthlyBenefit);
    }

    @Override
    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        mStartAge = in.readString();
        mMonthlyBenefit = in.readDouble();
    }

    public static final Parcelable.Creator<PensionIncomeData> CREATOR = new Parcelable.Creator<PensionIncomeData>()
    {
        @Override
        public PensionIncomeData createFromParcel(Parcel in) {
            return new PensionIncomeData(in);
        }

        @Override
        public PensionIncomeData[] newArray(int size) {
            return new PensionIncomeData[size];
        }
    };
}
