package com.intelliviz.retirementhelper.ui;


import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.BirthdateDialogAction;
import com.intelliviz.retirementhelper.util.SystemUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intelliviz.retirementhelper.util.RetirementConstants.DATE_FORMAT;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalInfoAdvancedFragment extends Fragment {
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

        showDialog(birthdate, new BirthdateDialogAction() {
            @Override
            public void onGetBirthdate(String birthdate) {
                if (SystemUtils.validateBirthday(birthdate)) {
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

    @BindView(R.id.spouse_birthdate_text_view)
    TextView mSpouseBirthdateTextView;

    public PersonalInfoAdvancedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personal_info_advanced, container, false);
        ButterKnife.bind(this, view);

        mIncludeSpouse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        return view;
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

    private void showDialog(String birthdate, BirthdateDialogAction birthdateDialogAction) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        BirthdateActivity birthdateDialog = BirthdateActivity.getInstance(birthdate, birthdateDialogAction);
        birthdateDialog.show(fm, "birhtdate");
    }
}
