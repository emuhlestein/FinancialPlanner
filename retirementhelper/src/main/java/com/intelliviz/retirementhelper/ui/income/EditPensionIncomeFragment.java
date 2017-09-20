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
import com.intelliviz.retirementhelper.data.PensionIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.services.PensionDataService;
import com.intelliviz.retirementhelper.util.PensionHelper;
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
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_TAX_DEFERRED_RESULT;
import static com.intelliviz.retirementhelper.util.SystemUtils.getFloatValue;

/**
 * Fragment used for adding and editing government pension income sources.
 *
 * @author Ed Muhlestein
 */
public class EditPensionIncomeFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = EditPensionIncomeFragment.class.getSimpleName();
    public static final String EDIT_PENSION_INCOME_FRAG_TAG = "edit pension income frag tag";
    private PensionIncomeData mPID;
    private long mId;
    public static final int PID_LOADER = 0;

    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @Bind(R.id.name_edit_text)
    EditText mIncomeSourceName;

    @Bind(R.id.age_text)
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
        View view = inflater.inflate(R.layout.fragment_edit_pension_income, container, false);
        ButterKnife.bind(this, view);

        mPID = null;

        if(mId != -1) {
            Bundle bundle = new Bundle();
            bundle.putString(ID_ARGS, Long.toString(mId));
            getLoaderManager().initLoader(PID_LOADER, bundle, this);
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
        if(mPID == null) {
            return;
        }
        String name = mPID.getName();
        String monthlyBenefit = SystemUtils.getFormattedCurrency(mPID.getFullMonthlyBenefit());
        String age = mPID.getStartAge();

        mIncomeSourceName.setText(name);
        mMinAge.setText(age);
        mMonthlyBenefit.setText(monthlyBenefit);

        int type = mPID.getType();
        String incomeSourceTypeString = SystemUtils.getIncomeSourceTypeString(getContext(), type);
        SystemUtils.setToolbarSubtitle(getActivity(), incomeSourceTypeString);
    }

    public void updateIncomeSourceData() {
        String name = mIncomeSourceName.getText().toString();
        String age = mMinAge.getText().toString();
        String value = mMonthlyBenefit.getText().toString();
        String benefit = getFloatValue(value);
        if(benefit == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.value_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        age = SystemUtils.trimAge(age);
        AgeData minAge = SystemUtils.parseAgeString(age);
        if(minAge == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.age_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        double dbenefit = Double.parseDouble(benefit);
        PensionIncomeData pid = new PensionIncomeData(mId, name, INCOME_TYPE_PENSION, age, dbenefit);
        updatePID(pid);
    }

    private void updatePID(PensionIncomeData pid) {
        Intent intent = new Intent(getContext(), PensionDataService.class);
        intent.putExtra(RetirementConstants.EXTRA_DB_ID, pid.getId());
        intent.putExtra(EXTRA_DB_DATA, pid);
        if(pid.getId() == -1) {
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
            case PID_LOADER:
                String id = args.getString(ID_ARGS);
                uri = RetirementContract.PensionIncomeEntry.CONTENT_URI;
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
            case PID_LOADER:
                mPID = PensionHelper.extractData(cursor);
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
