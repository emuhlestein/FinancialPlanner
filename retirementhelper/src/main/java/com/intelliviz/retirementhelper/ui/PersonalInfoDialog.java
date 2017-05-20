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
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.RetirementOptionsData;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PersonalInfoDialog extends AppCompatActivity {
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

        Intent intent = getIntent();
        RetirementOptionsData rod = intent.getParcelableExtra(RetirementConstants.EXTRA_PERSONALINFODATA);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });
    }

    private void sendIncomeSourceData() {

    }
}
