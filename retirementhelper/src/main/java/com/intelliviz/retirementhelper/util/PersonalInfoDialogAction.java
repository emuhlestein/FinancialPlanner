package com.intelliviz.retirementhelper.util;

/**
 * Created by edm on 2/15/2018.
 */

public interface PersonalInfoDialogAction {
    void onGetPersonalInfo(String birthdate, int includeSpouse, String spouseBirthdate);
}
