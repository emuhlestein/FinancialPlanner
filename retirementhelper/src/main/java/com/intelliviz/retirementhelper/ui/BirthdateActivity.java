package com.intelliviz.retirementhelper.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.intelliviz.retirementhelper.R;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.intelliviz.retirementhelper.R.id.edit_text_day;


public class BirthdateActivity extends AppCompatActivity {
    @Bind(edit_text_day)
    EditText mEditTextDay;

    @Bind(R.id.edit_text_month)
    EditText mEditTextMonth;

    @Bind(R.id.edit_text_year)
    EditText mEditTextYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthdate);
        ButterKnife.bind(this);

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
    }
}
