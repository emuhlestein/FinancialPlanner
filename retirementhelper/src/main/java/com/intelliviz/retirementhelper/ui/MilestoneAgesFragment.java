package com.intelliviz.retirementhelper.ui;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.intelliviz.retirementhelper.adapter.MilestoneAgeAdapter;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.MilestoneAgeData;
import com.intelliviz.retirementhelper.data.PersonalInfoData;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.SelectMilestoneAgeListener;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_AGE_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_MILESONTE_AGE_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.MILESTONE_AGE_DELETE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.MILESTONE_AGE_EDIT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_ACTION_MENU;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_ADD_AGE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_YES_NO;

public class MilestoneAgesFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, SelectMilestoneAgeListener {
    private static final int MILESTONE_AGE_LOADER = 1;
    private MilestoneAgeData mSelectedAge = null;

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @Bind(R.id.emptyView)
    TextView mEmptyView;

    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @Bind(R.id.add_milestone_age_fab)
    FloatingActionButton mAddMilestoneAgeFAB;

    private MilestoneAgeAdapter mAdapter = null;

    public MilestoneAgesFragment() {
        // Required empty public constructor
    }

    public static MilestoneAgesFragment newInstance() {
        MilestoneAgesFragment fragment = new MilestoneAgesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_milestone_age_layout, container, false);
        ButterKnife.bind(this, view);

        PersonalInfoData perid = DataBaseUtils.getPersonalInfoData(getContext());
        List<MilestoneAgeData> milestoneAges = DataBaseUtils.getMilestoneAges(getContext(), perid);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                linearLayoutManager.getOrientation()));


        getLoaderManager().initLoader(MILESTONE_AGE_LOADER, null, this);

        // The FAB will pop up an activity to allow a new income source to be created.
        mAddMilestoneAgeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Add an age", Snackbar.LENGTH_LONG);
                snackbar.show();

                Intent intent = new Intent(getContext(), SimpleTextDialog.class);
                startActivityForResult(intent, REQUEST_ADD_AGE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ACTION_MENU:
                    onHandleAction(intent);
                    break;
                case REQUEST_YES_NO:
                    onHandleYesNo(intent);
                    break;
                case REQUEST_ADD_AGE:
                    onHandleAddAge(intent);
                    break;

            }

            getLoaderManager().restartLoader(MILESTONE_AGE_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> loader;
        Uri uri = RetirementContract.MilestoneEntry.CONTENT_URI;
        loader = new CursorLoader(getContext(),
                uri, null, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(mAdapter == null) {
            mAdapter = new MilestoneAgeAdapter(getContext(), cursor);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnSelectMilestoneAgeListener(this);
        } else {
            mAdapter.swapCursor(cursor);
        }
        if (mAdapter.getItemCount() == 0) {
            mEmptyView.setText(R.string.empty_list);
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onSelectMilestoneAge(MilestoneAgeData age) {
        mSelectedAge = age;
        Intent intent = new Intent(getContext(), ListMenuActivity.class);
        startActivityForResult(intent, REQUEST_ACTION_MENU);
    }

    private void onHandleAction(Intent resultIntent) {
        int action = resultIntent.getIntExtra(EXTRA_MILESONTE_AGE_ACTION, -1);

        switch(action) {
            case MILESTONE_AGE_EDIT:
                mSelectedAge = null;
                break;
            case MILESTONE_AGE_DELETE:
                Intent intent = new Intent(getContext(), YesNoDialog.class);
                startActivityForResult(intent, REQUEST_YES_NO);
                break;
        }
    }

    private void onHandleYesNo(Intent intent) {
        DataBaseUtils.deleteAge(getContext(), mSelectedAge.getId());
        SystemUtils.updateAppWidget(getContext());
        SystemUtils.updateAppWidget(getContext());
        getLoaderManager().initLoader(MILESTONE_AGE_LOADER, null, this);
        PersonalInfoData perid = DataBaseUtils.getPersonalInfoData(getContext());
        List<MilestoneAgeData> milestoneAges = DataBaseUtils.getMilestoneAges(getContext(), perid);
        mAdapter.update(milestoneAges);
    }

    private void onHandleAddAge(Intent intent) {
        String ageString = intent.getStringExtra(EXTRA_AGE_DATA);
        // TODO add age to DB
        AgeData age = SystemUtils.parseAgeString(ageString);
        DataBaseUtils.addAge(getContext(), age);
        SystemUtils.updateAppWidget(getContext());
        getLoaderManager().initLoader(MILESTONE_AGE_LOADER, null, this);
        PersonalInfoData perid = DataBaseUtils.getPersonalInfoData(getContext());
        List<MilestoneAgeData> milestoneAges = DataBaseUtils.getMilestoneAges(getContext(), perid);
        mAdapter.update(milestoneAges);
    }
}