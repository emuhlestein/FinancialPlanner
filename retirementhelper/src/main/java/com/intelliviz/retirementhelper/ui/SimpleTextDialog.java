package com.intelliviz.retirementhelper.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.intelliviz.retirementhelper.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_AGE_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_DELETE;

public class SimpleTextDialog extends AppCompatActivity {

    @Bind(R.id.age_text)
    EditText mAge;

    @OnClick(R.id.cancel_button) void onCancelClick(View view) {
        finish();
    }

    @OnClick(R.id.yes_button) void onYesClick(View view) {
        sendResult();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_text);
        ButterKnife.bind(this);
    }

    private void sendResult() {
        String age = mAge.getText().toString();

        // TODO validate age
        Intent intent = new Intent();
        intent.putExtra(EXTRA_AGE_DATA, age);
        setResult(Activity.RESULT_OK, intent);
    }
}
