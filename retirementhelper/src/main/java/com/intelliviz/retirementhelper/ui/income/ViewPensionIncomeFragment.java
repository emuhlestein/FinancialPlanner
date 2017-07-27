package com.intelliviz.retirementhelper.ui.income;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.PensionIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.util.PensionHelper;
import com.intelliviz.retirementhelper.util.SystemUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.Intent.EXTRA_INTENT;
import static com.intelliviz.retirementhelper.ui.income.ViewTaxDeferredIncomeFragment.ID_ARGS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;

/**
 * Fragment used for viewing pension income sources.
 *
 * @author Ed Muhlestein
 */
public class ViewPensionIncomeFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    public static final String VIEW_PENSION_INCOME_FRAG_TAG = "view pension income frag tag";
    public static final int ROD_LOADER = 0;
    public static final int PID_LOADER = 1;
    private long mId;
    private PensionIncomeData mPID;

    @Bind(R.id.name_text_view)
    TextView mIncomeSourceName;

    @Bind(R.id.minimum_age_text_view)
    TextView mStartAge;

    @Bind(R.id.monthly_benefit_text_view)
    TextView mMonthlyBenefit;

    public static ViewPensionIncomeFragment newInstance(Intent intent) {
        ViewPensionIncomeFragment fragment = new ViewPensionIncomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_INTENT, intent);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewPensionIncomeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_view_pension_income, container, false);
        ButterKnife.bind(this, view);

        mPID = null;
        Bundle bundle = new Bundle();
        bundle.putString(ID_ARGS, Long.toString(mId));
        getLoaderManager().initLoader(ROD_LOADER, null, this);
        getLoaderManager().initLoader(PID_LOADER, bundle, this);

        return view;
    }

    private void updateUI() {
        if(mPID == null) {
            return;
        }

        mIncomeSourceName.setText(mPID.getName());
        mStartAge.setText(mPID.getStartAge());

        String monthlyIncreaseString = SystemUtils.getFormattedCurrency(mPID.getMonthlyBenefit(0));
        mMonthlyBenefit.setText(monthlyIncreaseString);

        int type = mPID.getType();
        String incomeSourceTypeString = SystemUtils.getIncomeSourceTypeString(getContext(), type);
        SystemUtils.setToolbarSubtitle(getActivity(), incomeSourceTypeString);
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
}
