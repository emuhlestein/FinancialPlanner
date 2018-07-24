package com.intelliviz.retirementhelper.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.intelliviz.income.ui.BirthdateDialog;
import com.intelliviz.lowlevel.util.AgeUtils;
import com.intelliviz.retirementhelper.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.intelliviz.lowlevel.util.AgeUtils.DATE_FORMAT;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_BIRTHDATE;
import static com.intelliviz.lowlevel.util.RetirementConstants.REQUEST_SPOUSE_BIRTHDATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalInfoAdvancedFragment extends Fragment implements BirthdateDialog.BirthdateDialogListener {
    private CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.include_spouse_check_box)
    CheckBox mIncludeSpouse;

    @BindView(R.id.spouse_birthdate_button)
    Button mSpouseBirthdateButton;

    @OnClick(R.id.spouse_birthdate_button)
    void editSpouseBirthdate() {
        if (mCoordinatorLayout == null) {
            return;
        }

        String birthdate = mSpouseBirthdateTextView.getText().toString();

        showSpouseBirthdateDialog(birthdate);
    }

    @BindView(R.id.spouse_birthdate_text_view)
    TextView mSpouseBirthdateTextView;

    public PersonalInfoAdvancedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_personal_info_advanced, container, false);
            ButterKnife.bind(this, view);

            mIncludeSpouse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        mSpouseBirthdateButton.setEnabled(true);
                        mSpouseBirthdateTextView.setEnabled(true);
                    } else {
                        mSpouseBirthdateButton.setEnabled(false);
                        mSpouseBirthdateTextView.setEnabled(false);
                    }
                }
            });
            return view;
        } catch (Exception e) {
            Log.e("TAG", "onCreateView", e);
            throw e;
        }
    }

    public void setCoordinatorLayout(CoordinatorLayout coordinatorLayout) {
        mCoordinatorLayout = coordinatorLayout;
    }

    public void setIncludeSpouse(boolean includeSpouse) {
        mIncludeSpouse.setChecked(includeSpouse);
        if(includeSpouse) {
            mSpouseBirthdateButton.setEnabled(true);
            mSpouseBirthdateTextView.setEnabled(true);
        } else {
            mSpouseBirthdateButton.setEnabled(false);
            mSpouseBirthdateTextView.setEnabled(false);
        }
    }

    public boolean getIncludeSpouse() {
        return mIncludeSpouse.isChecked();
    }

    public void setSpouseBirthdate(String birthdate) {
        mSpouseBirthdateTextView.setText(birthdate);
    }

    public String getSpouseBirthdate() {
        return mSpouseBirthdateTextView.getText().toString();
    }

    private void showSpouseBirthdateDialog(String birthdate) {
        FragmentManager fm = this.getFragmentManager();
        BirthdateDialog birthdateDialog = BirthdateDialog.getInstance(birthdate);
        birthdateDialog.setTargetFragment(this, REQUEST_SPOUSE_BIRTHDATE);
        birthdateDialog.show(fm, "birhtdate");
    }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SPOUSE_BIRTHDATE:
                    String birthdate = intent.getStringExtra(EXTRA_BIRTHDATE);
                    mSpouseBirthdateTextView.setText(birthdate);
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, intent);
            }
        }
    }
}
