package com.intelliviz.retirementhelper.ui;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.viewmodel.RetirementOptionsViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Dialog for gathering user-selected retirement options.
 * Created by Ed Muhlestein on 5/15/2017.
 */

public class RetirementOptionsDialog extends AppCompatActivity {
    private RetirementOptionsEntity mROE;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.income_summary_layout)
    LinearLayout mIncomeSummaryLayout;

    @BindView(R.id.end_age_edit_text)
    EditText mEndAgeEditText;

    @BindView(R.id.retirement_parms_ok)
    Button mOk;

    @BindView(R.id.retirement_parms_cancel)
    Button mCancel;

    @OnClick(R.id.retirement_parms_ok) void onClickOk() {
        sendData();
    }

    @OnClick(R.id.retirement_parms_cancel) void onClickCancel() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
        overridePendingTransition(0, R.anim.slide_right_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_retire_parms);
        ButterKnife.bind(this);

        RetirementOptionsViewModel mViewModel;
        mViewModel = ViewModelProviders.of(this).
                get(RetirementOptionsViewModel.class);

        mViewModel.get().observe(this, new Observer<RetirementOptionsEntity>() {
            @Override
            public void onChanged(@Nullable RetirementOptionsEntity roe) {
                updateUI(roe);
                mROE = roe;
            }
        });
    }

    private void updateUI(RetirementOptionsEntity roe) {
        mEndAgeEditText.setText(roe.getEndAge().toString());
    }

    private void sendData() {

        Intent returnIntent = new Intent();
        String value;

        value = mEndAgeEditText.getText().toString();
        value = SystemUtils.trimAge(value);
        AgeData endAge = SystemUtils.parseAgeString(value);
        if (endAge == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.age_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        returnIntent.putExtra(RetirementConstants.EXTRA_RETIREMENT_INCOME_SUMMARY_AGE, endAge);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        overridePendingTransition(0, R.anim.slide_right_out);
    }
}
