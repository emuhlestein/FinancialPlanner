package com.intelliviz.retirementhelper.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.intelliviz.retirementhelper.db.entity.MilestoneAgeEntity;
import com.intelliviz.retirementhelper.util.AgeUtils;
import com.intelliviz.retirementhelper.util.SelectMilestoneAgeListener;
import com.intelliviz.retirementhelper.viewmodel.MilestoneAgeViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_MENU_ITEM_LIST;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_MONTH;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_SELECTED_MENU_ITEM;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_YEAR;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_ACTION_MENU;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_ADD_AGE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_YES_NO;

/**
 * Fragment for milestone retirement ages.
 *
 * @author Ed Muhlestein
 */
public class MilestoneAgesFragment extends Fragment implements SelectMilestoneAgeListener {
    private MilestoneAgeEntity mSelectedAge = null;
    private MilestoneAgeAdapter mAdapter = null;
    private static final String DIALOG_YESNO = "DialogYesNo";
    private static final String DIALOG_INPUT_TEXT = "DialogInputText";
    private MilestoneAgeViewModel mViewModel;
    private List<MilestoneAgeEntity> mMilestoneAges = new ArrayList<>();
    private AgeData mNewAge;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.emptyView)
    TextView mEmptyView;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.add_milestone_age_fab)
    FloatingActionButton mAddMilestoneAgeFAB;

    public MilestoneAgesFragment() {
        // Required empty public constructor
    }

    public static MilestoneAgesFragment newInstance() {
        return new MilestoneAgesFragment();
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


        mAdapter = new MilestoneAgeAdapter(mMilestoneAges);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnSelectMilestoneAgeListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                linearLayoutManager.getOrientation()));

        // The FAB will pop up an activity to allow a new retirement age to be created.
        mAddMilestoneAgeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, R.string.add_age, Snackbar.LENGTH_LONG);
                snackbar.show();

                FragmentManager fm = getFragmentManager();
                AgeDialog dialog = AgeDialog.newInstance("", "");
                dialog.setTargetFragment(MilestoneAgesFragment.this, REQUEST_ADD_AGE);
                dialog.show(fm, DIALOG_INPUT_TEXT);
            }
        });

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(getString(R.string.milestone_age_subtitle));
        }

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
                    onHandleYesNo();
                    break;
                case REQUEST_ADD_AGE:
                    onHandleAddAge(intent);
                    break;
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MilestoneAgeViewModel.class);

        mViewModel.getData().observe(this, new Observer<List<MilestoneAgeEntity>>() {
            @Override
            public void onChanged(@Nullable List<MilestoneAgeEntity> milestoneAges) {
                mMilestoneAges = milestoneAges;
                mAdapter.update(milestoneAges);
            }
        });

        mViewModel.getStatus().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer status) {
                onStatusChanged(status);
            }
        });
    }

    @Override
    public void onSelectMilestoneAge(MilestoneAgeEntity age) {
        mSelectedAge = age;
        Intent intent = new Intent(getContext(), ListMenuActivity.class);
        String[] incomeActions = getResources().getStringArray(R.array.age_actions);
        intent.putExtra(EXTRA_MENU_ITEM_LIST, incomeActions);
        startActivityForResult(intent, REQUEST_ACTION_MENU);
    }

    private void onHandleAction(Intent resultIntent) {
        int action = resultIntent.getIntExtra(EXTRA_SELECTED_MENU_ITEM, -1);

        if (action == 0) {
            FragmentManager fm = getFragmentManager();
            YesNoDialog dialog = YesNoDialog.newInstance(getString(R.string.delete_age));
            dialog.setTargetFragment(this, REQUEST_YES_NO);
            dialog.show(fm, DIALOG_YESNO);
        }
    }

    private void onHandleYesNo() {
        mViewModel.deleteAge(mSelectedAge);
    }

    private void onHandleAddAge(Intent intent) {
        String year = intent.getStringExtra(EXTRA_YEAR);
        String month = intent.getStringExtra(EXTRA_MONTH);
        if(year == null || month == null) {
            return;
        }

        mNewAge = AgeUtils.parseAgeString(year + " " + month);
        if(mNewAge == null) {
            return;
        }

        mViewModel.addAge(mNewAge);
    }

    private void onStatusChanged(int status) {
        String message;

        switch(status) {
            case MilestoneAgeViewModel.VALUE_DUPLICATE:
                message = getString(R.string.duplicate_age);
                message += mNewAge.toString();
                final Snackbar dupSnackbar = Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_INDEFINITE);
                dupSnackbar.setAction(R.string.dismiss, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dupSnackbar.dismiss();
                    }
                });
                dupSnackbar.show();
                return;
            case MilestoneAgeViewModel.VALUE_BEFORE:
                message = getString(R.string.invalid_age);
                final Snackbar existsSnackbar = Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_INDEFINITE);
                existsSnackbar.setAction(R.string.dismiss, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        existsSnackbar.dismiss();
                    }
                });
                existsSnackbar.show();
                return;
            case MilestoneAgeViewModel.VALUE_GOOD:
                break;
        }
    }
}