package com.intelliviz.retirementhelper.ui;

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
import com.intelliviz.income.ui.BirthdateActivity;
import com.intelliviz.income.util.AgeUtils;
import com.intelliviz.income.util.BirthdateDialogAction;
import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.PersonalInfoDialogAction;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intelliviz.income.util.RetirementConstants.DATE_FORMAT;
import static com.intelliviz.income.util.RetirementConstants.EXTRA_BIRTHDATE;
import static com.intelliviz.income.util.RetirementConstants.EXTRA_INCLUDE_SPOUSE;
import static com.intelliviz.income.util.RetirementConstants.EXTRA_SPOUSE_BIRTHDATE;


/**
 * Activity for personal information.
 *
 * @author Ed Muhlestein
 */
public class PersonalInfoDialog extends DialogFragment implements AdapterView.OnItemSelectedListener{
    //private String mBirthdate;
    //private String mSpouseBirthdate;
    //private boolean mIncludeSpouse;
    //private PersonalInfoViewModel mViewModel;
    //private RetirementOptionsEntity mROE;
    private PersonalInfoDialogAction mPersonalInfoDialogAction;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.name_edit_text)
    TextView mNameTextView;

    @BindView(R.id.email_edit_text)
    TextView mEmailTextView;

    @BindView(R.id.birthdate_text_view)
    TextView mBirthDateViewText;

    @BindView(R.id.birthdate_button)
    Button mBirthdateButton;

    @BindView(R.id.country_spinner)
    Spinner mCountrySpinner;

    //@BindView(R.id.spouse_birthdate_button)
    //Button mSpouseBirthdateButton;

    @OnClick(R.id.birthdate_button) void editBirthdate() {
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
/*
    @OnClick(R.id.spouse_birthdate_button) void editSpouseBirthdate() {
        String birthdate = mSpouseBirthDateViewText.getText().toString();
        showDialog(birthdate, new BirthdateDialogAction() {
            @Override
            public void onGetBirthdate(String birthdate) {
                if (SystemUtils.validateBirthday(birthdate)) {
                    mSpouseBirthDateViewText.setText(birthdate);
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
    */

    @BindView(R.id.personal_info_ok)
    Button mOk;

    @BindView(R.id.personal_info_cancel)
    Button mCancel;

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
        ButterKnife.bind(this, view);

        setCancelable(false);

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

        /*
        mViewModel = ViewModelProviders.of(this).
                get(PersonalInfoViewModel.class);

        mViewModel.get().observe(this, new Observer<RetirementOptionsEntity>() {
            @Override
            public void onChanged(@Nullable RetirementOptionsEntity roe) {
                mROE = roe;
                updateUI();
            }
        });
        */
/*
        mIncludeSpouseCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    mSpouseBirthdateButton.setEnabled(true);
                    mSpouseBirthDateViewText.setEnabled(true);
                } else {
                    mSpouseBirthdateButton.setEnabled(false);
                    mSpouseBirthDateViewText.setEnabled(false);
                }
            }
        });
        */

        String[] countries = getResources().getStringArray(R.array.country_array);
        List<String> countryList = Arrays.asList(countries);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, countryList);
        mCountrySpinner.setAdapter(adapter);
        mCountrySpinner.setOnItemSelectedListener(this);

        setCoordinatorLayout(mCoordinatorLayout);
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
            String birthdate = bundle.getString(EXTRA_BIRTHDATE);
            String spouseBirthdate = bundle.getString(EXTRA_SPOUSE_BIRTHDATE);
            int includeSpouse = bundle.getInt(EXTRA_INCLUDE_SPOUSE, 0);
            updateUI(birthdate, includeSpouse, spouseBirthdate);
        }

        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.personal_info_fragment, new PersonalInfoAdvancedFragment(), "PersonalInfoTag");
        ft.commit();
    }

    private void updateUI(String birthdate, int includeSpouse, String spouseBirhtdate) {

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
        /*
        if(includeSpouse == 1) {
            mSpouseBirthdateButton.setEnabled(true);
            mSpouseBirthDateViewText.setEnabled(true);
        } else {
            mSpouseBirthdateButton.setEnabled(false);
            mSpouseBirthDateViewText.setEnabled(false);
        }
        */

        mNameTextView.setText(displayName);

        mEmailTextView.setText(email);
        mBirthDateViewText.setText(birthdate);

        setSpouseBirthdate(spouseBirhtdate);
        //mSpouseBirthDateViewText.setText(spouseBirhtdate);
        setIncludeSpouse(includeSpouse == 1);
        //mIncludeSpouseCheckBox.setChecked(includeSpouse == 1);

        if(!AgeUtils.validateBirthday(birthdate)) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.enter_birthdate), Snackbar.LENGTH_LONG);
            snackbar.show();
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

        int includeSpouse = 0;
        String spouseBirthdate = "";

        if(getIncludeSpouse()) {
            spouseBirthdate = getSpouseBirthdate();
            if (!AgeUtils.validateBirthday(spouseBirthdate)) {
                String message;
                String errMsg = getResources().getString(R.string.spouse_birthdate_not_valid);
                message = errMsg + " (" + DATE_FORMAT + ").";
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG);
                snackbar.show();
                return;
            }

            includeSpouse = 1;
        }

        if(mPersonalInfoDialogAction != null) {
            mPersonalInfoDialogAction.onGetPersonalInfo(birthdate, includeSpouse, spouseBirthdate);
        }

        int i = mCountrySpinner.getSelectedItemPosition();
        String[] countries = getResources().getStringArray(R.array.country_array);
        Log.d("PeronsalInfoDialog", countries[i] + " is selected");

        /*
        mROE.setIncludeSpouse(includeSpouse);
        mROE.setSpouseBirthdate(spouseBirthday);
        mROE.setBirthdate(birthday);
        mViewModel.update(mROE);
        */

        /*
        Intent returnIntent = new Intent();
        returnIntent.putExtra(RetirementConstants.EXTRA_BIRTHDATE, birthday);
        returnIntent.putExtra(RetirementConstants.EXTRA_SPOUSE_BIRTHDATE, spouseBirthday);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCLUDE_SPOUSE, includeSpouse);
        setResult(Activity.RESULT_OK, returnIntent);
        */
        //finish();
        //overridePendingTransition(0, R.anim.slide_right_out);
        dismiss();
    }

    private String getSpouseBirthdate() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.personal_info_fragment);
        if(fragment != null && fragment instanceof PersonalInfoAdvancedFragment) {
            return ((PersonalInfoAdvancedFragment)fragment).getSpouseBirthdate();
        } else {
            return "";
        }
    }

    private void setSpouseBirthdate(String spouseBirthdate) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.personal_info_fragment);
        if(fragment != null && fragment instanceof PersonalInfoAdvancedFragment) {
            ((PersonalInfoAdvancedFragment)fragment).setSpouseBirthdate(spouseBirthdate);
        }
    }

    private boolean getIncludeSpouse() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.personal_info_fragment);
        if(fragment != null && fragment instanceof PersonalInfoAdvancedFragment) {
            return ((PersonalInfoAdvancedFragment)fragment).getIncludeSpouse();
        } else {
            return false;
        }
    }

    private void setIncludeSpouse(boolean includeSpouse) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.personal_info_fragment);
        if(fragment != null && fragment instanceof PersonalInfoAdvancedFragment) {
            ((PersonalInfoAdvancedFragment)fragment).setIncludeSpouse(includeSpouse);
        }
    }

    private void setCoordinatorLayout(CoordinatorLayout coordinatorLayout) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.personal_info_fragment);
        if(fragment != null && fragment instanceof PersonalInfoAdvancedFragment) {
            ((PersonalInfoAdvancedFragment)fragment).setCoordinatorLayout(coordinatorLayout);
        }
    }

    private void showDialog(String birthdate, BirthdateDialogAction birthdateDialogAction) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        BirthdateActivity birthdateDialog = BirthdateActivity.getInstance(birthdate, birthdateDialogAction);
        birthdateDialog.show(fm, "birhtdate");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
