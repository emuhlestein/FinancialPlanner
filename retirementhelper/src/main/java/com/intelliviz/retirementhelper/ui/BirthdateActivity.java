package com.intelliviz.retirementhelper.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.SystemUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.intelliviz.retirementhelper.R.id.edit_text_day;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_BIRTHDATE;

public class BirthdateActivity extends AppCompatActivity {
    @Bind(edit_text_day)
    EditText mEditTextDay;

    @Bind(R.id.edit_text_month)
    EditText mEditTextMonth;

    @Bind(R.id.edit_text_year)
    EditText mEditTextYear;

    @Bind(R.id.save_birthdate_button)
    Button mSaveBirthdateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthdate);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String birthdate = intent.getStringExtra(EXTRA_BIRTHDATE);
        updateUI(birthdate);

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
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
                sendResult(birthdate);
                finish();
            }
        });
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

    private void sendResult(String birthdate) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_BIRTHDATE, birthdate);
        setResult(Activity.RESULT_OK, intent);
    }
}
