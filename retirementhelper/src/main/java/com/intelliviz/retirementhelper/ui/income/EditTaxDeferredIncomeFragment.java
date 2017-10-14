package com.intelliviz.retirementhelper.ui.income;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.db.entity.TaxDeferredIncomeEntity;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.viewmodel.TaxDeferredViewModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Intent.EXTRA_INTENT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_TAX_DEFERRED;
import static com.intelliviz.retirementhelper.util.SystemUtils.getFloatValue;

/**
 * Fragment used for adding tax deferred income sources.
 *
 * @author Ed Muhlestein
 */
public class EditTaxDeferredIncomeFragment extends Fragment {
    private static final String TAG = EditTaxDeferredIncomeFragment.class.getSimpleName();
    public static final String EDIT_TAXDEF_INCOME_FRAG_TAG = "edit taxdef income frag tag";
    private TaxDeferredIncomeEntity mTDID;
    private long mId;
    private TaxDeferredViewModel mViewModel;

    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @Bind(R.id.name_edit_text)
    EditText mIncomeSourceName;

    @Bind(R.id.balance_text)
    EditText mBalance;

    @Bind(R.id.annual_interest_text)
    EditText mAnnualInterest;

    @Bind(R.id.monthly_increase_text)
    EditText mMonthlyIncrease;

    @Bind(R.id.penalty_age_text)
    EditText mPenaltyAge;

    @Bind(R.id.penalty_amount_text)
    EditText mPenaltyAmount;

    @OnClick(R.id.add_income_source_button) void onAddIncomeSource() {
        updateIncomeSourceData();
        getActivity().finish();
    }

    public EditTaxDeferredIncomeFragment() {
        // Required empty public constructor
    }

    public static EditTaxDeferredIncomeFragment newInstance(Intent intent) {
        EditTaxDeferredIncomeFragment fragment = new EditTaxDeferredIncomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_INTENT, intent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Intent intent = getArguments().getParcelable(EXTRA_INTENT);
            mId = 0;
            if(intent != null) {
                mId = intent.getLongExtra(EXTRA_INCOME_SOURCE_ID, 0);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_tax_deferred_income, container, false);
        ButterKnife.bind(this, view);

        mTDID = null;

        mBalance.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String formattedString;
                    String str = textView.getText().toString();
                    String value = getFloatValue(str);
                    formattedString = SystemUtils.getFormattedCurrency(value);
                    if(formattedString != null) {
                        mBalance.setText(formattedString);
                    }
                }
            }
        });

        mMonthlyIncrease.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String formattedString;
                    String str = textView.getText().toString();
                    String value = getFloatValue(str);
                    formattedString = SystemUtils.getFormattedCurrency(value);
                    if(formattedString != null) {
                        mMonthlyIncrease.setText(formattedString);
                    }
                }
            }
        });

        mAnnualInterest.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String interest = textView.getText().toString();
                    interest = getFloatValue(interest);
                    if(interest != null) {
                        interest += "%";
                        mAnnualInterest.setText(interest);
                    } else {
                        mAnnualInterest.setText("");
                    }
                }
            }
        });

        mPenaltyAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String interest = textView.getText().toString();
                    interest = getFloatValue(interest);
                    if(interest != null) {
                        interest += "%";
                        mPenaltyAmount.setText(interest);
                    } else {
                        mPenaltyAmount.setText("");
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TaxDeferredViewModel.Factory factory = new
                TaxDeferredViewModel.Factory(getActivity().getApplication(), mId);
        mViewModel = ViewModelProviders.of(this, factory).
                get(TaxDeferredViewModel.class);

        mViewModel.getData().observe(this, new Observer<TaxDeferredIncomeEntity>() {
            @Override
            public void onChanged(@Nullable TaxDeferredIncomeEntity data) {
                mTDID = data;
                updateUI();
            }
        });
    }

    private void updateUI() {
        if (mTDID == null || mTDID.getId() == 0) {
            return;
        }

        String incomeSourceName = mTDID.getName();
        int type = mTDID.getType();
        String incomeSourceTypeString = SystemUtils.getIncomeSourceTypeString(getContext(), type);
        SystemUtils.setToolbarSubtitle(getActivity(), incomeSourceTypeString);

        String balanceString;
        balanceString = mTDID.getBalance();
        balanceString = SystemUtils.getFormattedCurrency(balanceString);

        String monthlyIncreaseString = SystemUtils.getFormattedCurrency(mTDID.getMonthlyIncrease());
        String minimumAge = mTDID.getMinAge();
        AgeData age = SystemUtils.parseAgeString(minimumAge);
        minimumAge = SystemUtils.getFormattedAge(age);

        String penaltyAmount = mTDID.getPenalty() + "%";

        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setSubtitle(incomeSourceTypeString);
        }
        mIncomeSourceName.setText(incomeSourceName);
        mBalance.setText(balanceString);

        String interest = mTDID.getInterest()+"%";
        mAnnualInterest.setText(interest);
        mMonthlyIncrease.setText(monthlyIncreaseString);
        mPenaltyAge.setText(minimumAge);
        mPenaltyAmount.setText(penaltyAmount);
    }

    public void updateIncomeSourceData() {
        String value = mBalance.getText().toString();
        String balance = getFloatValue(value);
        if(balance == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.balance_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        value = mAnnualInterest.getText().toString();
        String interest = getFloatValue(value);
        if(interest == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.interest_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        value = mMonthlyIncrease.getText().toString();
        String monthlyIncrease = getFloatValue(value);
        if(monthlyIncrease == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.value_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        value = mPenaltyAmount.getText().toString();
        String penaltyAmount = getFloatValue(value);
        if(penaltyAmount == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.value_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        String minimumAge = mPenaltyAge.getText().toString();
        minimumAge = SystemUtils.trimAge(minimumAge);
        AgeData minAge = SystemUtils.parseAgeString(minimumAge);
        if(minAge == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.age_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        String name = mIncomeSourceName.getText().toString();
        TaxDeferredIncomeEntity tdid = new TaxDeferredIncomeEntity(mId, INCOME_TYPE_TAX_DEFERRED, name, minimumAge, interest, monthlyIncrease, penaltyAmount, 1, balance);
        mViewModel.setData(tdid);
    }
}
