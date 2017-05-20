package com.intelliviz.retirementhelper.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.PersonalInfoData;
import com.intelliviz.retirementhelper.util.RetirementConstants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PersonalInfoDialog extends AppCompatActivity {
    private String mPassword;
    private String mPIN;
    private String mEmail;
    @Bind(R.id.name_edit_text) EditText mNameEditText;
    @Bind(R.id.birthdate_edit_text) EditText mBirthDateateEditText;
    @Bind(R.id.email_edit_text) TextView mEmailTextView;
    @Bind(R.id.change_email) Button mChangeEmailButton;
    @Bind(R.id.change_password) Button mChangePasswordButton;
    @Bind(R.id.change_pin) Button mChangePinButton;
    @Bind(R.id.personal_info_ok) Button mOk;
    @Bind(R.id.personal_info_cancel) Button mCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info_dialog);
        ButterKnife.bind(this);

        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIncomeSourceData();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        mChangeEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mChangePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mChangePinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        updateUI();
    }

    private void updateUI() {

        Intent intent = getIntent();
        PersonalInfoData pid = intent.getParcelableExtra(RetirementConstants.EXTRA_PERSONALINFODATA);
        mNameEditText.setText(pid.getName());
        mBirthDateateEditText.setText(pid.getBirthdate());
        mEmailTextView.setText(pid.getEmail());

        mPassword = pid.getPassword();
        mPIN = pid.getPIN();
        mEmail = pid.getEmail();
    }

    private void sendIncomeSourceData() {
        String name = mNameEditText.getText().toString();
        String birthdate = mBirthDateateEditText.getText().toString();
        // TODO need to validate birth date

        PersonalInfoData pid = new PersonalInfoData(name, birthdate, mEmail, mPIN, mPassword);

        Intent returnIntent = new Intent();
        returnIntent.putExtra(RetirementConstants.EXTRA_PERSONALINFODATA, pid);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
