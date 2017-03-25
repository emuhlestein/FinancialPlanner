package com.intelliviz.retirementhelper.ui;

import android.os.Bundle;
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
    }

    public void registerUser(View view) {
    }
}
