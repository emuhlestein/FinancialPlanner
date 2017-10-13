package com.intelliviz.retirementhelper.ui.income;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.viewmodel.GovPensionViewModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Intent.EXTRA_INTENT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_GOV_PENSION;
import static com.intelliviz.retirementhelper.util.SystemUtils.getFloatValue;

/**
 * Fragment used for adding and editing government pension income sources.
 *
 * @author Ed Muhlestein
 */
public class EditGovPensionIncomeFragment extends Fragment {
    private static final String TAG = EditGovPensionIncomeFragment.class.getSimpleName();
    public static final String EDIT_GOVPENSION_INCOME_FRAG_TAG = "edit govpension income frag tag";
    private GovPensionEntity mGPID;
    private long mId;
    private GovPensionViewModel mViewModel;

    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @Bind(R.id.name_edit_text)
    EditText mIncomeSourceName;

    @Bind(R.id.min_age_text)
    EditText mMinAge;

    @Bind(R.id.monthly_benefit_text)
    EditText mMonthlyBenefit;

    @OnClick(R.id.add_income_source_button) void onAddIncomeSource() {
        updateIncomeSourceData();
        getActivity().finish();
    }

    public static EditGovPensionIncomeFragment newInstance(Intent intent) {
        EditGovPensionIncomeFragment fragment = new EditGovPensionIncomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_INTENT, intent);
        fragment.setArguments(args);
        return fragment;
    }

    public EditGovPensionIncomeFragment() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.fragment_edit_gov_pension_income, container, false);
        ButterKnife.bind(this, view);

        mGPID = null;

        mMonthlyBenefit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String str = textView.getText().toString();
                    String value = getFloatValue(str);
                    String formattedString = SystemUtils.getFormattedCurrency(value);
                    if(formattedString != null) {
                        mMonthlyBenefit.setText(formattedString);
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GovPensionViewModel.Factory factory = new
                GovPensionViewModel.Factory(getActivity().getApplication(), mId);
        mViewModel = ViewModelProviders.of(this, factory).
                get(GovPensionViewModel.class);

        mViewModel.getData().observe(this, new Observer<GovPensionEntity>() {
            @Override
            public void onChanged(@Nullable GovPensionEntity govPensionIncomeData) {
                mGPID = govPensionIncomeData;
                updateUI();
            }
        });
    }

    private void updateUI() {
        if(mGPID == null || mGPID.getId() == 0) {
            return;
        }
        String name = mGPID.getName();
        String monthlyBenefit = SystemUtils.getFormattedCurrency(mGPID.getFullMonthlyBenefit());
        String age = mGPID.getMinAge();

        mIncomeSourceName.setText(name);
        mMinAge.setText(age);
        mMonthlyBenefit.setText(monthlyBenefit);

        int type = mGPID.getType();
        String incomeSourceTypeString = SystemUtils.getIncomeSourceTypeString(getContext(), type);
        SystemUtils.setToolbarSubtitle(getActivity(), incomeSourceTypeString);
    }

    private void updateIncomeSourceData() {
        String name = mIncomeSourceName.getText().toString();
        String minimumAge = mMinAge.getText().toString();
        String value = mMonthlyBenefit.getText().toString();
        String benefit = getFloatValue(value);
        if(benefit == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.monthly_benefit_not_valid) + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        minimumAge = SystemUtils.trimAge(minimumAge);
        AgeData minAge = SystemUtils.parseAgeString(minimumAge);
        if(minAge == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.age_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        double dbenefit = Double.parseDouble(benefit);
        GovPensionEntity gpid = new GovPensionEntity(mId, INCOME_TYPE_GOV_PENSION, name, minimumAge, benefit);
        mViewModel.setData(gpid);
    }
}
