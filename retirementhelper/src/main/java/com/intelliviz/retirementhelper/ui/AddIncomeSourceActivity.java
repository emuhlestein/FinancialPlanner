package com.intelliviz.retirementhelper.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.intelliviz.retirementhelper.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddIncomeSourceActivity extends AppCompatActivity {
    @Bind(R.id.testButton) Button mButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income_source);
        ButterKnife.bind(this);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", 333);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}
