package com.intelliviz.retirementhelper.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.BirthdateDialogAction;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intelliviz.retirementhelper.R.id.edit_text_day;
import static com.intelliviz.retirementhelper.util.RetirementConstants.DATE_FORMAT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_BIRTHDATE;

public class BirthdateActivity extends DialogFragment {
    private BirthdateDialogAction mBirthdateDialogAction;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(edit_text_day)
    EditText mEditTextDay;

    @BindView(R.id.edit_text_month)
    EditText mEditTextMonth;

    @BindView(R.id.edit_text_year)
    EditText mEditTextYear;

    @BindView(R.id.save_birthdate_button)
    Button mSaveBirthdateButton;

    @OnClick(R.id.cancel_birthdate_button)  void cancelBirthdate() {
        dismiss();
    }

    public static BirthdateActivity getInstance(String birthdate, BirthdateDialogAction birthdateDialogAction) {
        BirthdateActivity fragment = new BirthdateActivity();
        fragment.mBirthdateDialogAction = birthdateDialogAction;
        Bundle bundle = new Bundle();
        bundle.putString(RetirementConstants.EXTRA_BIRTHDATE, birthdate);
        fragment.setArguments(bundle);
        return fragment;
    }

    public BirthdateActivity() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_birthdate, container, false);
        ButterKnife.bind(this, view);

        setCancelable(false);

        mEditTextDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mEditTextDay.length() == 2) {
                    mEditTextMonth.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mEditTextMonth, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        mEditTextMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mEditTextMonth.length() == 2) {
                    mEditTextYear.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mEditTextYear, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        mSaveBirthdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // extract date from gui
                String day = mEditTextDay.getText().toString();
                String month = mEditTextMonth.getText().toString();
                String year = mEditTextYear.getText().toString();

                String birthdate = day + "-" + month + "-" + year;
                if(!SystemUtils.validateBirthday(birthdate)) {
                    String message;
                    String errMsg = getResources().getString(R.string.birthdate_not_valid);
                    message = errMsg + " (" + DATE_FORMAT + ").";
                    Snackbar snackbar = Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }
                if(mBirthdateDialogAction != null) {
                    mBirthdateDialogAction.onGetBirthdate(birthdate);
                }
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null) {
            updateUI(bundle.getString(EXTRA_BIRTHDATE));
        }
    }

    private void updateUI(String birthdate) {
        if(SystemUtils.validateBirthday(birthdate)) {
            String day = Integer.toString(SystemUtils.getBirthDay(birthdate));
            String month = Integer.toString(SystemUtils.getBirthMonth(birthdate));
            String year = Integer.toString(SystemUtils.getBirthYear(birthdate));
            mEditTextDay.setText(day);
            mEditTextMonth.setText(month);
            mEditTextYear.setText(year);
        } else {
            mEditTextDay.setText("");
            mEditTextMonth.setText("");
            mEditTextYear.setText("");
        }
    }
}
