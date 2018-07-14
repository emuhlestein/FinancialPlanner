package com.intelliviz.retirementhelper.ui;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.income.ui.AgeDialog;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;
import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.viewmodel.RetirementOptionsViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Dialog for gathering user-selected retirement options.
 * Created by Ed Muhlestein on 5/15/2017.
 */

public class RetirementOptionsDialog extends AppCompatActivity implements AgeDialog.OnAgeEditListener {
    private RetirementOptionsEntity mROE;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.end_age_text_view)
    TextView mEndAgeTextVIew;

    @BindView(R.id.retirement_parms_ok)
    Button mOk;

    @BindView(R.id.retirement_parms_cancel)
    Button mCancel;

    @OnClick(R.id.edit_end_age_button) void onEditEndAge() {
        AgeData startAge = mROE.getEndAge();
        FragmentManager fm = getSupportFragmentManager();
        AgeDialog dialog = AgeDialog.newInstance(""+startAge.getYear(), ""+startAge.getMonth());
        dialog.show(fm, "");
    }

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
        mEndAgeTextVIew.setText(roe.getEndAge().toString());
    }

    private void sendData() {

        Intent returnIntent = new Intent();
        String value;

        value = mEndAgeTextVIew.getText().toString();
        AgeData endAge = new AgeData(value);
        if (!endAge.isValid()) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.age_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        returnIntent.putExtra(RetirementConstants.EXTRA_RETIREMENT_INCOME_SUMMARY_AGE, endAge);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        overridePendingTransition(0, R.anim.slide_right_out);
    }

    @Override
    public void onEditAge(String year, String month) {
        AgeData age = new AgeData(year, month);
        mEndAgeTextVIew.setText(age.toString());
    }
}
