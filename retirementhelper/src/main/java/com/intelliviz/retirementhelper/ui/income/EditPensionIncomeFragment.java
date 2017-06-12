package com.intelliviz.retirementhelper.ui.income;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.PensionIncomeData;
import com.intelliviz.retirementhelper.services.PensionDataService;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.Intent.EXTRA_INTENT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.SystemUtils.getFloatValue;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditPensionIncomeFragment extends Fragment {
    public static final String EDIT_PENSION_INCOME_FRAG_TAG = "edit pension income frag tag";
    private PensionIncomeData mPID;
    @Bind(R.id.coordinatorLayout) CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.name_edit_text) EditText mIncomeSourceName;
    @Bind(R.id.age_text) EditText mMinAge;
    @Bind(R.id.monthly_benefit_text) EditText mMonthlyBenefit;
    @Bind(R.id.add_income_source_button) Button mAddIncomeSource;

    public static EditPensionIncomeFragment newInstance(Intent intent) {
        EditPensionIncomeFragment fragment = new EditPensionIncomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_INTENT, intent);
        fragment.setArguments(args);
        return fragment;
    }

    public EditPensionIncomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Intent intent = getArguments().getParcelable(EXTRA_INTENT);
            mPID = intent.getParcelableExtra(RetirementConstants.EXTRA_INCOME_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_pension_income, container, false);
        ButterKnife.bind(this, view);

        ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();

        if(mPID.getId() == -1) {
            ab.setSubtitle("Savings");
        } else {
            updateUI();
        }

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
        mAddIncomeSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIncomeSourceData();
            }
        });

        return view;
    }

    private void updateUI() {
        String name = mPID.getName();
        String monthlyBenefit = SystemUtils.getFormattedCurrency(mPID.getMonthlyBenefit(0));
        String age = mPID.getStartAge();

        mIncomeSourceName.setText(name);
        mMinAge.setText(age);
        mMonthlyBenefit.setText(monthlyBenefit);
    }

    public void sendIncomeSourceData() {
        String name = mIncomeSourceName.getText().toString();
        String age = mMinAge.getText().toString();
        String value = mMonthlyBenefit.getText().toString();
        String benefit = getFloatValue(value);
        if(benefit == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Monthly benefit value is not valid " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        // TODO need to validate age

        double dbenefit = Double.parseDouble(benefit);
        PensionIncomeData pid = new PensionIncomeData(mPID.getId(), name, mPID.getType(), age, dbenefit);
        updatePID(pid);
    }

    private void updatePID(PensionIncomeData pid) {
        Intent intent = new Intent(getContext(), PensionDataService.class);
        intent.putExtra(RetirementConstants.EXTRA_DB_ID, pid.getId());
        intent.putExtra(EXTRA_DB_DATA, pid);
        intent.putExtra(RetirementConstants.EXTRA_DB_ACTION, RetirementConstants.SERVICE_DB_UPDATE);
        getActivity().startService(intent);
    }
}
