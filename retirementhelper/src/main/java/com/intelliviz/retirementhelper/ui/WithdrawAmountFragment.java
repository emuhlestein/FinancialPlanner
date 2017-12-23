package com.intelliviz.retirementhelper.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.util.SaveRetirementOptionEntityListener;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.viewmodel.RetirementOptionsViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_ADD_AGE;
import static com.intelliviz.retirementhelper.util.SystemUtils.getFloatValue;

public class WithdrawAmountFragment extends Fragment {
    private SaveRetirementOptionEntityListener mListener;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.end_age)
    TextView mEndAge;

    @BindView(R.id.withdraw_amount_edit_text)
    EditText mWithdrawAmount;

    @BindView(R.id.annual_percent_increase_edit_text)
    EditText mPercentIncrease;

    @OnClick(R.id.retirement_options_ok) void onClickOk() {
        saveData();
    }

    @OnClick(R.id.retirement_options_cancel) void onClickCancel() {}

    @OnClick(R.id.edit_end_age_button) void editAge() {
        AgeData endAge = mROE.getEndAge();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        AgeDialog dialog = AgeDialog.newInstance(""+endAge.getYear(), ""+endAge.getMonth());
        dialog.setTargetFragment(WithdrawAmountFragment.this, REQUEST_ADD_AGE);
        dialog.show(fm, "");
    }

    private RetirementOptionsViewModel mViewModel;
    private RetirementOptionsEntity mROE;

    public WithdrawAmountFragment() {
        // Required empty public constructor
    }

    public static WithdrawAmountFragment newInstance() {
        WithdrawAmountFragment fragment = new WithdrawAmountFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_withdraw_amount, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(RetirementOptionsViewModel.class);
        mViewModel.get().observe(this, new Observer<RetirementOptionsEntity>() {
            @Override
            public void onChanged(@Nullable RetirementOptionsEntity roe) {
                updateUI(roe);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof SaveRetirementOptionEntityListener) {
            mListener = (SaveRetirementOptionEntityListener)context;
        } else {
            throw new ClassCastException(context.toString() + " must implement SaveRetirementOptionEntityListener");
        }
    }

    private void updateUI(RetirementOptionsEntity roe) {
        AgeData endAge =  roe.getEndAge();
        mEndAge.setText(endAge.toString());

        String amount = roe.getWithdrawAmount();
        mWithdrawAmount.setText(SystemUtils.getFormattedCurrency(amount));

        String percent = roe.getPercentIncrease() + "%";
        mPercentIncrease.setText(percent);
    }

    private void saveData() {
        String endAge = SystemUtils.trimAge(mEndAge.getText().toString());
        AgeData age = SystemUtils.parseAgeString(endAge);
        if(age == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.enter_birthdate), Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        String value = mPercentIncrease.getText().toString();
        String percentIncrease = getFloatValue(value);
        if(percentIncrease == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.value_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        value = mWithdrawAmount.getText().toString();
        String withdrawAmount = getFloatValue(value);
        if(withdrawAmount == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.value_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        mROE.setEndAge(age);
        mROE.setPercentIncrease(percentIncrease);
        mROE.setWithdrawAmount(withdrawAmount);
        if(mListener != null) {
            mListener.onSaveRetirementOptionEntity(mROE);
        }
    }
}
