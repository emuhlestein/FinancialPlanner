package com.intelliviz.retirementhelper.ui.income;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.adapter.SummaryMilestoneAdapter;
import com.intelliviz.retirementhelper.data.BalanceData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.data.TaxDeferredIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.ui.MilestoneDetailsDialog;
import com.intelliviz.retirementhelper.util.BenefitHelper;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.RetirementOptionsHelper;
import com.intelliviz.retirementhelper.util.SelectionMilestoneListener;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.util.TaxDeferredHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.Intent.EXTRA_INTENT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;

/**
 * Fragment used for viewing tax deferred income sources.
 *
 * @author Ed Muhlestein
 */
public class ViewTaxDeferredIncomeFragment extends Fragment implements
        SelectionMilestoneListener,
        LoaderManager.LoaderCallbacks<Cursor>{
    public static final String VIEW_TAXDEF_INCOME_FRAG_TAG = "view taxdef income frag tag";
    public static final String ID_ARGS = "id";
    public static final int ROD_LOADER = 0;
    public static final int TDID_LOADER = 1;
    private long mId;
    private TaxDeferredIncomeData mTDID;
    private RetirementOptionsData mROD;
    private SummaryMilestoneAdapter mMilestoneAdapter;

    @Bind(R.id.name_text_view)
    TextView mIncomeSourceName;

    @Bind(R.id.annual_interest_text_view)
    TextView mAnnualInterest;

    @Bind(R.id.monthly_increase_text_view)
    TextView mMonthlyIncrease;

    @Bind(R.id.current_balance_text_view)
    TextView mCurrentBalance;

    @Bind(R.id.minimum_age_text_view)
    TextView mMinimumAge;

    @Bind(R.id.penalty_amount_text_view)
    TextView mPenaltyAmount;

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;

    public ViewTaxDeferredIncomeFragment() {
        // Required empty public constructor
    }

    public static ViewTaxDeferredIncomeFragment newInstance(Intent intent) {
        ViewTaxDeferredIncomeFragment fragment = new ViewTaxDeferredIncomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_view_tax_deferred_income, container, false);
        ButterKnife.bind(this, view);

        List<MilestoneData> milestones = new ArrayList<>();
        mMilestoneAdapter = new SummaryMilestoneAdapter(getContext(), milestones);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mMilestoneAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation()));
        mMilestoneAdapter.setOnSelectionMilestoneListener(this);

        mROD = null;
        mTDID = null;
        Bundle bundle = new Bundle();
        bundle.putString(ID_ARGS, Long.toString(mId));
        getLoaderManager().initLoader(ROD_LOADER, null, this);
        getLoaderManager().initLoader(TDID_LOADER, bundle, this);

        return view;
    }

    private void updateUI() {
        if(mTDID == null) {
            return;
        }

        mIncomeSourceName.setText(mTDID.getName());
        String subTitle = SystemUtils.getIncomeSourceTypeString(getContext(), mTDID.getType());
        SystemUtils.setToolbarSubtitle(getActivity(), subTitle);

        String interest = mTDID.getInterestRate()+"%";
        mAnnualInterest.setText(interest);
        mMonthlyIncrease.setText(SystemUtils.getFormattedCurrency(mTDID.getMonthAddition()));
        mMinimumAge.setText(mTDID.getMinimumAge());

        String penalty = mTDID.getPenalty() +"%";
        mPenaltyAmount.setText(penalty);

        List<BalanceData> bd = mTDID.getBalanceData();
        String formattedAmount = "$0.00";
        if(bd != null && !bd.isEmpty()) {
            formattedAmount = SystemUtils.getFormattedCurrency(bd.get(0).getBalance());
        }

        mCurrentBalance.setText(String.valueOf(formattedAmount));
    }

    @Override
    public void onSelectMilestone(MilestoneData msd) {
        Intent intent = new Intent(getContext(), MilestoneDetailsDialog.class);
        intent.putExtra(RetirementConstants.EXTRA_MILESTONEDATA, msd);
        startActivity(intent);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        Loader<Cursor> loader;
        Uri uri;
        switch (loaderId) {
            case ROD_LOADER:
                uri = RetirementContract.RetirementParmsEntry.CONTENT_URI;
                loader = new CursorLoader(getActivity(),
                        uri,
                        null,
                        null,
                        null,
                        null);
                break;
            case TDID_LOADER:
                String id = args.getString(ID_ARGS);
                uri = RetirementContract.TaxDeferredIncomeEntry.CONTENT_URI;
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
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        switch(loader.getId()) {
            case ROD_LOADER:
                mROD = RetirementOptionsHelper.extractData(cursor);
                break;
            case TDID_LOADER:
                mTDID = TaxDeferredHelper.extractData(cursor);
                updateUI();
                break;
        }

        if(mROD != null && mTDID != null) {
            List<MilestoneData> milestones = BenefitHelper.getMilestones(getContext(), mTDID, mROD);
            mMilestoneAdapter.update(milestones);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
    }
}
