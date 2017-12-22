package com.intelliviz.retirementhelper.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.intelliviz.retirementhelper.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RetirementOptionsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String WITHDRAW_PERCENT_FRAG_TAG = "withdraw percent frag tag";
    private static final String WITHDRAW_AMOUNT_FRAG_TAG = "withdraw amount frag tag";
    private static final String WHEN_REACH_PERCENT_INCOME_FRAG_TAG = "when reach percent income frag tag";
    private static final String WHEN_REACH_AMOUNT_FRAG_TAG = "when reach amount frag tag";

    @BindView(R.id.retirementModeSpinner)
    Spinner mRetirementModes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retirement_options);
        ButterKnife.bind(this);
        final String[] appModes = getResources().getStringArray(R.array.app_modes);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.layout_spinner_item, appModes);
        mRetirementModes.setAdapter(adapter);
        mRetirementModes.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Fragment fragment = null;
        String fragmentTag = null;

        switch(i) {
            case 0:
                fragment = WithdrawPercentFragment.newInstance();
                fragmentTag = WITHDRAW_PERCENT_FRAG_TAG;
                break;
            case 1:
                fragment = WithdrawAmountFragment.newInstance();
                fragmentTag = WITHDRAW_AMOUNT_FRAG_TAG;
                break;
            case 2:
                fragment = WhenReachPercentIncomeFragment.newInstance();
                fragmentTag = WHEN_REACH_PERCENT_INCOME_FRAG_TAG;
                break;
            case 3:
                fragment = WhenReachAmountFragment.newInstance();
                fragmentTag = WHEN_REACH_AMOUNT_FRAG_TAG;
                break;
            default:
                return;
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.options_frame);
        String oldTag = "";
        if(frag != null) {
            oldTag = frag.getTag();
        }
        if(oldTag.equals(fragmentTag)) {
            return;
        }
        FragmentTransaction ft;
        ft = fm.beginTransaction();
        ft.replace(R.id.options_frame, fragment, fragmentTag);
        ft.commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
