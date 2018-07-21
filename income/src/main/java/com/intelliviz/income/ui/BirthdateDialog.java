package com.intelliviz.income.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.intelliviz.income.R;
import com.intelliviz.lowlevel.util.AgeUtils;

import static com.intelliviz.lowlevel.util.AgeUtils.DATE_FORMAT;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_BIRTHDATE;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_DIALOG_INPUT_TEXT;


public class BirthdateDialog extends DialogFragment {

    public interface BirthdateDialogListener {
        void onGetBirthdate(String birthdate);
    }

    CoordinatorLayout mCoordinatorLayout;
    EditText mEditTextDay;
    EditText mEditTextMonth;
    EditText mEditTextYear;
    Button mSaveBirthdateButton;
    Button mCancelBirthdateButton;

    public static BirthdateDialog getInstance(String birthdate) {
        BirthdateDialog fragment = new BirthdateDialog();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_BIRTHDATE, birthdate);
        fragment.setArguments(bundle);
        return fragment;
    }

    public BirthdateDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_birthdate, container, false);

        setCancelable(false);

        mCoordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        mEditTextDay = view.findViewById(R.id.edit_text_day);
        mEditTextMonth = view.findViewById(R.id.edit_text_month);
        mEditTextYear = view.findViewById(R.id.edit_text_year);
        mCancelBirthdateButton = view.findViewById(R.id.cancel_birthdate_button);
        mCancelBirthdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResult(null); // TODO probably need to let caller know if dialog was cancelled
                dismiss();        // to allow the options of doing something. For now, just pass them a null
            }
        });

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

        mSaveBirthdateButton = view.findViewById(R.id.save_birthdate_button);
        mSaveBirthdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // extract date from gui
                String day = mEditTextDay.getText().toString();
                String month = mEditTextMonth.getText().toString();
                String year = mEditTextYear.getText().toString();

                String birthdate = day + "-" + month + "-" + year;
                if(!AgeUtils.validateBirthday(birthdate)) {
                    String message;
                    String errMsg = getResources().getString(R.string.birthdate_not_valid);
                    message = errMsg + " (" + DATE_FORMAT + ").";
                    Snackbar snackbar = Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }
                sendResult(birthdate);
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
        if(AgeUtils.validateBirthday(birthdate)) {
            String day = Integer.toString(AgeUtils.getBirthDay(birthdate));
            String month = Integer.toString(AgeUtils.getBirthMonth(birthdate));
            String year = Integer.toString(AgeUtils.getBirthYear(birthdate));
            mEditTextDay.setText(day);
            mEditTextMonth.setText(month);
            mEditTextYear.setText(year);
        } else {
            mEditTextDay.setText("");
            mEditTextMonth.setText("");
            mEditTextYear.setText("");
        }
    }

    private void sendResult(String birthdate) {
        if(getTargetFragment() != null) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DIALOG_INPUT_TEXT, EXTRA_BIRTHDATE);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent());
        } else {
            BirthdateDialog.BirthdateDialogListener response = (BirthdateDialog.BirthdateDialogListener) getActivity();
            response.onGetBirthdate(birthdate);
        }
    }
}
