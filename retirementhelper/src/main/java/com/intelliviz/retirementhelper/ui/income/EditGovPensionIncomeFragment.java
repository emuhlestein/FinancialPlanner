package com.intelliviz.retirementhelper.ui.income;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.GovPensionIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.services.GovPensionDataService;
import com.intelliviz.retirementhelper.util.GovPensionHelper;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Intent.EXTRA_INTENT;
import static com.intelliviz.retirementhelper.ui.income.ViewTaxDeferredIncomeFragment.ID_ARGS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_RESULT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_GOV_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_TAX_DEFERRED_RESULT;
import static com.intelliviz.retirementhelper.util.SystemUtils.getFloatValue;

/**
 * Fragment used for adding and editing government pension income sources.
 *
 * @author Ed Muhlestein
 */
public class EditGovPensionIncomeFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = EditGovPensionIncomeFragment.class.getSimpleName();
    public static final String EDIT_GOVPENSION_INCOME_FRAG_TAG = "edit govpension income frag tag";
    private GovPensionIncomeData mGPID;
    public static final int GPID_LOADER = 0;
    private long mId;

    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @Bind(R.id.name_edit_text)
    EditText mIncomeSourceName;

    @Bind(R.id.min_age_text)
    EditText mMinAge;

    @Bind(R.id.monthly_benefit_text)
    EditText mMonthlyBenefit;

    private BroadcastReceiver mResultsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long result = intent.getLongExtra(EXTRA_DB_RESULT, -1);
            // TODO check result
        }
    };

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
            mId = -1;
            if(intent != null) {
                mId = intent.getLongExtra(EXTRA_INCOME_SOURCE_ID, -1);
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

        if(mId != -1) {
            Bundle bundle = new Bundle();
            bundle.putString(ID_ARGS, Long.toString(mId));
            getLoaderManager().initLoader(GPID_LOADER, bundle, this);
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

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        registerReceivers();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceivers();
    }

    private void updateUI() {
        if(mGPID == null || mGPID.getId() == -1) {
            return;
        }
        String name = mGPID.getName();
        String monthlyBenefit = SystemUtils.getFormattedCurrency(mGPID.getFullMonthlyBenefit());
        String age = mGPID.getStartAge();

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
        /*
        String birthDate, minAge, new AgeData(70, 0), dbenefit);
        SocialSecurityRules ssr = new SocialSecurityRules();
        */
        GovPensionIncomeData gpid = new GovPensionIncomeData(mId, name, INCOME_TYPE_GOV_PENSION, minimumAge, dbenefit);
        updateGPID(gpid);
    }

    private void updateGPID(GovPensionIncomeData gpid) {
        Intent intent = new Intent(getContext(), GovPensionDataService.class);
        intent.putExtra(RetirementConstants.EXTRA_DB_ID, gpid.getId());
        intent.putExtra(EXTRA_DB_DATA, gpid);
        if(gpid.getId() == -1) {
            intent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ACTION, RetirementConstants.INCOME_ACTION_ADD);
        } else {
            intent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ACTION, RetirementConstants.INCOME_ACTION_UPDATE);
        }
        getActivity().startService(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        Loader<Cursor> loader;
        Uri uri;
        switch (loaderId) {
            case GPID_LOADER:
                String id = args.getString(ID_ARGS);
                uri = RetirementContract.GovPensionIncomeEntry.CONTENT_URI;
                if(uri != null) {
                    uri = Uri.withAppendedPath(uri, id);
                }

                loader = new CursorLoader(getActivity(),
                        uri,
                        null,
                        null,
                        null,
                        null);
                break;
            default:
                loader = null;
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch(loader.getId()) {
            case GPID_LOADER:
                mGPID = GovPensionHelper.extractData(cursor);
                updateUI();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void registerReceivers() {
        registerMilestoneReceiver();
    }

    private void unregisterReceivers() {
        unregisterMilestoneReceiver();
    }

    private void registerMilestoneReceiver() {
        IntentFilter filter = new IntentFilter(LOCAL_TAX_DEFERRED_RESULT);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mResultsReceiver, filter);
    }

    private void unregisterMilestoneReceiver() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mResultsReceiver);
    }
}
