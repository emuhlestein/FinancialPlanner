package com.intelliviz.retirementhelper.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.income.ui.BirthdateDialog;
import com.intelliviz.income.util.BirthdateDialogAction;
import com.intelliviz.lowlevel.util.AgeUtils;
import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.PersonalInfoDialogAction;
import com.intelliviz.retirementhelper.viewmodel.PersonalInfoViewModel;

import java.util.Arrays;
import java.util.List;

import static com.intelliviz.lowlevel.util.AgeUtils.DATE_FORMAT;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_BIRTHDATE;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCLUDE_SPOUSE;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_SPOUSE_BIRTHDATE;


/**
 * Activity for personal information.
 *
 * @author Ed Muhlestein
 */
public class PersonalInfoDialog extends DialogFragment implements AdapterView.OnItemSelectedListener{
    private PersonalInfoViewModel mViewModel;
    private RetirementOptions mROE;
    private PersonalInfoDialogAction mPersonalInfoDialogAction;
    private PersonalInfoAdvancedFragment mFragment;
    private CoordinatorLayout mCoordinatorLayout;
    private TextView mNameTextView;
    private TextView mEmailTextView;
    private TextView mBirthDateViewText;
    private Button mBirthdateButton;
    private Spinner mCountrySpinner;
    //private TextView mSpouseBirthdateTextView;
    //private Button mSpouseBirthdateButton;
    //private CheckBox mIncludeSpouseCheckBox;
    private Button mOk;
    private Button mCancel;

    public static PersonalInfoDialog getInstance(String birthdate, int includeSpouse, String spouseBirhtdate, PersonalInfoDialogAction personalInfoDialogAction) {
        PersonalInfoDialog fragment = new PersonalInfoDialog();
        fragment.mPersonalInfoDialogAction = personalInfoDialogAction;
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_BIRTHDATE, birthdate);
        bundle.putString(EXTRA_SPOUSE_BIRTHDATE, spouseBirhtdate);
        bundle.putInt(EXTRA_INCLUDE_SPOUSE, includeSpouse);
        fragment.setArguments(bundle);
        return fragment;
    }

    public PersonalInfoDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_personal_info_dialog, container, false);

        setCancelable(false);

        mCoordinatorLayout = view.findViewById(R.id.coordinatorLayout);


        mNameTextView = view.findViewById(R.id.name_edit_text);
        mEmailTextView = view.findViewById(R.id.email_edit_text);
        mBirthDateViewText = view.findViewById(R.id.birthdate_text_view);
        mBirthdateButton = view.findViewById(R.id.birthdate_button);
        mCountrySpinner = view.findViewById(R.id.country_spinner);
        mOk = view.findViewById(R.id.personal_info_ok);
        mCancel = view.findViewById(R.id.personal_info_cancel);

        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //overridePendingTransition(0, R.anim.slide_right_out);
                dismiss();
            }
        });
        mBirthdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String birthdate = mBirthDateViewText.getText().toString();
                showDialog(birthdate, new BirthdateDialogAction() {
                    @Override
                    public void onGetBirthdate(String birthdate) {
                        if (AgeUtils.validateBirthday(birthdate)) {
                            mBirthDateViewText.setText(birthdate);
                        } else {
                            String message;
                            String errMsg = getResources().getString(R.string.birthdate_not_valid);
                            message = errMsg + " (" + DATE_FORMAT + ").";
                            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                });
            }
        });
/*
        mSpouseBirthdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String birthdate = mSpouseBirthdateTextView.getText().toString();
                showDialog(birthdate, new BirthdateDialogAction() {
                    @Override
                    public void onGetBirthdate(String birthdate) {
                        if (AgeUtils.validateBirthday(birthdate)) {
                            mSpouseBirthdateTextView.setText(birthdate);
                        } else {
                            String message;
                            String errMsg = getResources().getString(R.string.birthdate_not_valid);
                            message = errMsg + " (" + DATE_FORMAT + ").";
                            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                });
            }
        });
        */

        mViewModel = ViewModelProviders.of(this).
                get(PersonalInfoViewModel.class);

        mViewModel.get().observe(this, new Observer<RetirementOptions>() {
            @Override
            public void onChanged(@Nullable RetirementOptions roe) {
                mROE = roe;
                updateUI(roe);
            }
        });
/*
        mIncludeSpouseCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    mSpouseBirthdateButton.setEnabled(true);
                    mSpouseBirthdateTextView.setEnabled(true);
                } else {
                    mSpouseBirthdateButton.setEnabled(false);
                    mSpouseBirthdateTextView.setEnabled(false);
                }
            }
        });
        */

        String[] countries = getResources().getStringArray(R.array.country_array);
        List<String> countryList = Arrays.asList(countries);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_layout, countryList);
        mCountrySpinner.setAdapter(adapter);
        mCountrySpinner.setOnItemSelectedListener(this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null) {
            //String birthdate = bundle.getString(EXTRA_BIRTHDATE);
            //String spouseBirthdate = bundle.getString(EXTRA_SPOUSE_BIRTHDATE);
            //int includeSpouse = bundle.getInt(EXTRA_INCLUDE_SPOUSE, 0);
            //updateUI(birthdate, includeSpouse, spouseBirthdate);
        }

        FragmentManager fm = getChildFragmentManager();
        Fragment fragment = fm.findFragmentByTag("PersonalInfoTag");
        if(fragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            mFragment = new PersonalInfoAdvancedFragment();
            ft.replace(R.id.personal_info_fragment, mFragment, "PersonalInfoTag");
            ft.commit();
        } else {
            mFragment = (PersonalInfoAdvancedFragment)fragment;
        }

        setCoordinatorLayout(mCoordinatorLayout);

    }

    private void updateUI(RetirementOptions roe) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String email = "";
        String displayName = "";
        if (user != null) {
            email = user.getEmail();
            if(email != null) {
                email = email.trim();
            }
            displayName = user.getDisplayName();
        }

        mCountrySpinner.setSelection(1);

        setIncludeSpouse(roe.getIncludeSpouse() == 1);
        setSpouseBirthdate(roe.getSpouseBirthdate());
        /*
        if(roe.getIncludeSpouse() == 1) {
            mSpouseBirthdateButton.setEnabled(true);
            mSpouseBirthdateTextView.setEnabled(true);
        } else {
            mSpouseBirthdateButton.setEnabled(false);
            mSpouseBirthdateTextView.setEnabled(false);
        }
        */

        mNameTextView.setText(displayName);

        mEmailTextView.setText(email);
        mBirthDateViewText.setText(roe.getBirthdate());
        //mSpouseBirthdateTextView.setText(roe.getSpouseBirthdate());

        String countryCode = roe.getCountryCode();
        if(countryCode.equals("US")) {
            mCountrySpinner.setSelection(0);
        } else {
            mCountrySpinner.setSelection(1);
        }

        if(mROE.isCountryAvailable()) {
            mCountrySpinner.setEnabled(true);
        } else {
            mCountrySpinner.setEnabled(false);
        }
    }

    private void saveData() {
        String birthdate = mBirthDateViewText.getText().toString();
        if(!AgeUtils.validateBirthday(birthdate)) {
            String message;
            String errMsg = getResources().getString(R.string.birthdate_not_valid);
            message = errMsg + " (" + DATE_FORMAT + ").";
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        int includeSpouse = getIncludeSpouse() ? 1 : 0;
        String spouseBirthdate = "";

        if(includeSpouse == 1) {
            spouseBirthdate = getSpouseBirthdate();
            if (!AgeUtils.validateBirthday(spouseBirthdate)) {
                String message;
                String errMsg = getResources().getString(R.string.spouse_birthdate_not_valid);
                message = errMsg + " (" + DATE_FORMAT + ").";
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG);
                snackbar.show();
                return;
            }
        }

        if(mPersonalInfoDialogAction != null) {
            mPersonalInfoDialogAction.onGetPersonalInfo(birthdate, includeSpouse, spouseBirthdate);
        }

        int i = mCountrySpinner.getSelectedItemPosition();
        String[] countries = getResources().getStringArray(R.array.country_array);
        Log.d("PeronsalInfoDialog", countries[i] + " is selected");
        if(i == 0) {
            mROE.setCountryCode("US");
        } else {
            mROE.setCountryCode(countries[1]);
        }

        mROE.setIncludeSpouse(includeSpouse);
        mROE.setSpouseBirthdate(spouseBirthdate);
        mROE.setBirthdate(birthdate);
        mViewModel.update(mROE);

        dismiss();
    }


    private String getSpouseBirthdate() {
        if(mFragment != null) {
            return mFragment.getSpouseBirthdate();
        } else {
            return "";
        }
    }

    private void setSpouseBirthdate(String spouseBirthdate) {
        if(mFragment != null) {
            mFragment.setSpouseBirthdate(spouseBirthdate);
        }
    }

    private boolean getIncludeSpouse() {
        if(mFragment != null) {
            return mFragment.getIncludeSpouse();
        } else {
            return false;
        }
    }

    private void setIncludeSpouse(boolean includeSpouse) {
        if(mFragment != null) {
            mFragment.setIncludeSpouse(includeSpouse);
        }
    }

    private void setCoordinatorLayout(CoordinatorLayout coordinatorLayout) {
        if(mFragment != null) {
            mFragment.setCoordinatorLayout(coordinatorLayout);
        }
    }

    private void showDialog(String birthdate, BirthdateDialogAction birthdateDialogAction) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        BirthdateDialog birthdateDialog = BirthdateDialog.getInstance(birthdate, birthdateDialogAction);
        birthdateDialog.show(fm, "birhtdate");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d("PersonalInfoDialog", "Here");
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
