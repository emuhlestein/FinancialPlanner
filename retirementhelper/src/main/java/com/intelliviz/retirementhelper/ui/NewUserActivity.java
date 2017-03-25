package com.intelliviz.retirementhelper.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.intelliviz.retirementhelper.R;

import butterknife.ButterKnife;

public class NewUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        ButterKnife.bind(this);

        ActionBar ab = getSupportActionBar();
        ab.setSubtitle("New User");
    }

    public void registerUser(View view) {
        validateEmail();
        validatePassword();
    }

    private void validatePassword() {

    }

    private void validateEmail() {

    }
}
