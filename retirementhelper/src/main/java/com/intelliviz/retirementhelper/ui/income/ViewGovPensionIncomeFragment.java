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
import com.intelliviz.retirementhelper.adapter.SSMilestoneAdapter;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.GovPensionIncomeData;
import com.intelliviz.retirementhelper.data.MilestoneAgeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.GovPensionHelper;
import com.intelliviz.retirementhelper.util.RetirementOptionsHelper;
import com.intelliviz.retirementhelper.util.SelectionMilestoneDataListener;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.Intent.EXTRA_INTENT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;

/**
 * Fragment used for viewing government pension income sources.
 *
 * @author Ed Muhlestein
 */
public class ViewGovPensionIncomeFragment extends Fragment implements
        SelectionMilestoneDataListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    public static final String VIEW_GOV_PENSION_INCOME_FRAG_TAG = "view gov pension income frag tag";
    public static final String ID_ARGS = "id";
    public static final int ROD_LOADER = 0;
    public static final int GPID_LOADER = 1;
    private long mId;
    private GovPensionIncomeData mGPID;
    private RetirementOptionsData mROD;
    private SSMilestoneAdapter mMilestoneAdapter;

    @Bind(R.id.name_text_view)
    TextView mIncomeSourceName;

    @Bind(R.id.min_age_text_view)
    TextView mMinAge;

    @Bind(R.id.full_age_text_view)
    TextView mFullAge;

    @Bind(R.id.monthly_amount_text_view)
    TextView mMonthlyBenefit;

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;

    public static ViewGovPensionIncomeFragment newInstance(Intent intent) {
        ViewGovPensionIncomeFragment fragment = new ViewGovPensionIncomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_INTENT, intent);
        fragment.setArguments(args);
        return fragment;
    }
    public ViewGovPensionIncomeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_view_gov_pension_income, container, false);
        ButterKnife.bind(this, view);

        List<MilestoneData> milestones = new ArrayList<>();
        mMilestoneAdapter = new SSMilestoneAdapter(getContext(), milestones);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mMilestoneAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation()));
        mMilestoneAdapter.setOnSelectionMilestoneListener(this);

        mROD = null;
        mGPID = null;
        Bundle bundle = new Bundle();
        bundle.putString(ID_ARGS, Long.toString(mId));
        getLoaderManager().initLoader(ROD_LOADER, null, this);
        getLoaderManager().initLoader(GPID_LOADER, bundle, this);

        return view;
    }

    private void updateUI() {
        if(mGPID == null) {
            return;
        }

        mIncomeSourceName.setText(mGPID.getName());
        mMinAge.setText(mGPID.getStartAge());

        int birthYear = SystemUtils.getBirthYear(mROD.getBirthdate());
        AgeData fullAge = GovPensionHelper.getFullRetirementAge(birthYear);
        mFullAge.setText(fullAge.toString());

        String formattedValue = SystemUtils.getFormattedCurrency(mGPID.getMonthlyBenefit());
        mMonthlyBenefit.setText(formattedValue);

        int type = mGPID.getType();
        String incomeSourceTypeString = SystemUtils.getIncomeSourceTypeString(getContext(), type);
        SystemUtils.setToolbarSubtitle(getActivity(), incomeSourceTypeString);
    }

    @Override
    public void onSelectMilestone(MilestoneData msd) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
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
            case ROD_LOADER:
                mROD = RetirementOptionsHelper.extractData(cursor);
                mMilestoneAdapter.setROD(mROD);
                break;
            case GPID_LOADER:
                mGPID = GovPensionHelper.extractData(cursor);
                break;
        }

        if(mROD != null && mGPID != null) {
            List<MilestoneAgeData> ages = DataBaseUtils.getMilestoneAges(getContext(), mROD);
            List<MilestoneData> milestones = mGPID.getMilestones(getContext(), ages, mROD);
            mMilestoneAdapter.update(milestones);
            updateUI();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
