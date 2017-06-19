package com.intelliviz.retirementhelper.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.PersonalInfoData;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PersonalInfoDialog extends AppCompatActivity {
    private PersonalInfoData mPID;
    @Bind(R.id.coordinatorLayout) CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.name_edit_text) EditText mNameEditText;
    @Bind(R.id.birthdate_edit_text) EditText mBirthDateateEditText;
    @Bind(R.id.email_edit_text) TextView mEmailTextView;
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
                sendData();
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

        Intent intent = getIntent();
        mPID = intent.getParcelableExtra(RetirementConstants.EXTRA_PERSONALINFODATA);

        updateUI();
    }

    private void updateUI() {

        Intent intent = getIntent();
        PersonalInfoData pid = intent.getParcelableExtra(RetirementConstants.EXTRA_PERSONALINFODATA);
        mNameEditText.setText(pid.getName());
        mBirthDateateEditText.setText(pid.getBirthdate());
        mEmailTextView.setText(pid.getEmail());
    }

    private void sendData() {
        String name = mNameEditText.getText().toString();
        String birthday = mBirthDateateEditText.getText().toString();
        if(!SystemUtils.validateBirthday(birthday)) {
            String errMsg = getResources().getString(R.string.birthday_not_valid);
            String yearFormat = getResources().getString(R.string.year_format);
            StringBuilder sb = new StringBuilder();
            sb.append(errMsg);
            sb.append(" ");
            sb.append(yearFormat);
            sb.append(".");

            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, sb.toString(), Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        PersonalInfoData pid = new PersonalInfoData(name, birthday, "", "", "");

        Intent returnIntent = new Intent();
        returnIntent.putExtra(RetirementConstants.EXTRA_PERSONALINFODATA, pid);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
