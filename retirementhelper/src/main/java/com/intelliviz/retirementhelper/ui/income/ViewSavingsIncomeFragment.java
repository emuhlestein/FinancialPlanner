package com.intelliviz.retirementhelper.ui.income;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.adapter.MilestoneDataAdapter;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.SavingsIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.services.SavingsDataService;
import com.intelliviz.retirementhelper.ui.MilestoneDetailsDialog;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SavingsIncomeHelper;
import com.intelliviz.retirementhelper.util.SelectionMilestoneDataListener;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.intelliviz.retirementhelper.ui.income.ViewTaxDeferredIncomeFragment.ID_ARGS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_MILESTONES;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_SAVINGS;

/**
 * Fragment used for viewing savings income sources.
 *
 * @author Ed Muhlestein
 */
public class ViewSavingsIncomeFragment extends Fragment implements
        SelectionMilestoneDataListener,
        LoaderManager.LoaderCallbacks<Cursor>{
    public static final String VIEW_SAVINGS_INCOME_FRAG_TAG = "view savings income frag tag";
    private static final String EXTRA_INTENT = "extra intent";
    public static final int SID_LOADER = 1;
    private SavingsIncomeData mSID;
    private long mId;
    private MilestoneDataAdapter mMilestoneDataAdapter;

    @Bind(R.id.name_text_view)
    TextView mIncomeSourceName;

    @Bind(R.id.annual_interest_text_view)
    TextView mAnnualInterest;

    @Bind(R.id.monthly_increase_text_view)
    TextView mMonthlyIncrease;

    @Bind(R.id.current_balance_text_view)
    TextView mCurrentBalance;

    @Bind(R.id.monthly_amount_text_view)
    TextView mMonthlyAmount;

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private BroadcastReceiver mMilestoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<MilestoneData> milestones = intent.getParcelableArrayListExtra(EXTRA_DB_MILESTONES);
            mMilestoneDataAdapter.update(milestones);
        }
    };

    public static ViewSavingsIncomeFragment newInstance(Intent intent) {
        ViewSavingsIncomeFragment fragment = new ViewSavingsIncomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_INTENT, intent);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewSavingsIncomeFragment() {
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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_savings_income, container, false);
        ButterKnife.bind(this, view);
        List<MilestoneData> milestones = new ArrayList<>();
        mMilestoneDataAdapter = new MilestoneDataAdapter(getContext(), milestones);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mMilestoneDataAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation()));
        mMilestoneDataAdapter.setOnSelectionMilestoneDataListener(this);

        setHasOptionsMenu(true);

        mSID = null;
        Bundle bundle = new Bundle();
        bundle.putString(ID_ARGS, Long.toString(mId));
        getLoaderManager().initLoader(SID_LOADER, bundle, this);

        Intent intent = new Intent(getContext(), SavingsDataService.class);
        intent.putExtra(EXTRA_INCOME_SOURCE_ID, mId);
        intent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ACTION, RetirementConstants.INCOME_ACTION_VIEW);
        getActivity().startService(intent);

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
        if(mSID == null) {
            return;
        }

        mIncomeSourceName.setText(mSID.getName());
        String subTitle = SystemUtils.getIncomeSourceTypeString(getContext(), mSID.getType());
        SystemUtils.setToolbarSubtitle(getActivity(), subTitle);

        String interest = mSID.getInterest() + "%";
        mAnnualInterest.setText(interest);
        mMonthlyIncrease.setText(SystemUtils.getFormattedCurrency(mSID.getMonthlyIncrease()));

        double balance = mSID.getBalance();
        String formattedAmount = SystemUtils.getFormattedCurrency(balance);

        mCurrentBalance.setText(String.valueOf(formattedAmount));
    }

    @Override
    public void onSelectMilestone(MilestoneData msd) {
        Intent intent = new Intent(getContext(), MilestoneDetailsDialog.class);
        intent.putExtra(RetirementConstants.EXTRA_MILESTONEDATA, msd);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        Loader<Cursor> loader;
        Uri uri;
        switch (loaderId) {
            case SID_LOADER:
                String id = args.getString(ID_ARGS);
                uri = RetirementContract.SavingsIncomeEntry.CONTENT_URI;
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
            case SID_LOADER:
                mSID = SavingsIncomeHelper.extractData(cursor);
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
        IntentFilter filter = new IntentFilter(LOCAL_SAVINGS);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMilestoneReceiver, filter);
    }

    private void unregisterMilestoneReceiver() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMilestoneReceiver);
    }
}
