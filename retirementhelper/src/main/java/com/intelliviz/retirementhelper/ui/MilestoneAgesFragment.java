package com.intelliviz.retirementhelper.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
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
import com.intelliviz.retirementhelper.data.MilestoneAgeData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.db.RetirementOptionsDatabase;
import com.intelliviz.retirementhelper.services.MilestoneAgeIntentService;
import com.intelliviz.retirementhelper.services.RetirementOptionsService;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SelectMilestoneAgeListener;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.intelliviz.retirementhelper.util.DataBaseUtils.getMilestoneAges;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DIALOG_INPUT_TEXT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_MENU_ITEM_LIST;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_MILESTONEAGE_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_SELECTED_MENU_ITEM;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_MILESTONE_AGE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_RETIRE_OPTIONS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_ACTION_MENU;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_ADD_AGE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_YES_NO;
import static com.intelliviz.retirementhelper.util.SystemUtils.getAge;

/**
 * Fragment for milestone retirement ages.
 *
 * @author Ed Muhlestein
 */
public class MilestoneAgesFragment extends Fragment implements SelectMilestoneAgeListener {
    private MilestoneAgeData mSelectedAge = null;
    private MilestoneAgeAdapter mAdapter = null;
    private static final String DIALOG_YESNO = "DialogYesNo";
    private static final String DIALOG_INPUT_TEXT = "DialogInputText";
    private RetirementOptionsData mROD;

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @Bind(R.id.emptyView)
    TextView mEmptyView;

    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @Bind(R.id.add_milestone_age_fab)
    FloatingActionButton mAddMilestoneAgeFAB;

    private BroadcastReceiver mMilestoneAgeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<MilestoneAgeData> milestoneAges = intent.getParcelableArrayListExtra(EXTRA_MILESTONEAGE_DATA);
            mAdapter.update(milestoneAges);
        }
    };

    private BroadcastReceiver mRetirementOptionsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mROD = intent.getParcelableExtra(EXTRA_DB_DATA);
        }
    };

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

        List<MilestoneAgeData> milestoneAges = new ArrayList<>();
        mAdapter = new MilestoneAgeAdapter(milestoneAges);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnSelectMilestoneAgeListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                linearLayoutManager.getOrientation()));

        // The FAB will pop up an activity to allow a new income source to be created.
        mAddMilestoneAgeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, R.string.add_age, Snackbar.LENGTH_LONG);
                snackbar.show();

                FragmentManager fm = getFragmentManager();
                SimpleTextDialog dialog = SimpleTextDialog.newInstance(getString(R.string.add_age), "");
                dialog.setTargetFragment(MilestoneAgesFragment.this, REQUEST_ADD_AGE);
                dialog.show(fm, DIALOG_INPUT_TEXT);
            }
        });

        Intent intent = new Intent(getContext(), MilestoneAgeIntentService.class);
        intent.putExtra(RetirementConstants.EXTRA_DB_ACTION, RetirementConstants.SERVICE_DB_QUERY);
        getContext().startService(intent);

        intent = new Intent(getContext(), RetirementOptionsService.class);
        intent.putExtra(RetirementConstants.EXTRA_DB_ACTION, RetirementConstants.SERVICE_DB_QUERY);
        getContext().startService(intent);

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
    public void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    public void onSelectMilestoneAge(MilestoneAgeData age) {
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

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(LOCAL_MILESTONE_AGE);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMilestoneAgeReceiver, filter);
        filter = new IntentFilter(LOCAL_RETIRE_OPTIONS);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRetirementOptionsReceiver, filter);
    }

    private void unregisterReceiver() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMilestoneAgeReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mRetirementOptionsReceiver);
    }

    private void onHandleYesNo() {
        RetirementOptionsDatabase.getInstance(getContext()).deleteAge(mSelectedAge.getId());
        //RetirementOptionsHelper.deleteAge(getContext(), mSelectedAge.getId());
        SystemUtils.updateAppWidget(getContext());
        RetirementOptionsData rod = RetirementOptionsDatabase.getInstance(getContext()).get();
        List<MilestoneAgeData> milestoneAges = getMilestoneAges(getContext(), rod);
        mAdapter.update(milestoneAges);
    }

    private void onHandleAddAge(Intent intent) {
        String ageString = intent.getStringExtra(EXTRA_DIALOG_INPUT_TEXT);
        AgeData newAge;
        if(ageString != null) {
            newAge = SystemUtils.parseAgeString(ageString);
            if(newAge == null) {
                return;
            }
        } else {
            return;
        }

        AgeData nowAge = getAge(mROD.getBirthdate());
        if(nowAge == null) {
            return;
        }
        if(newAge.isBefore(nowAge)) {
            String message = getString(R.string.invalid_age);
            message += nowAge.toString();
            final Snackbar snackbar = Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(R.string.dismiss, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
            return;
        }
        List<MilestoneAgeData> milestoneAges = getMilestoneAges(getContext(), mROD);
        boolean foundAge = false;
        for(MilestoneAgeData msad : milestoneAges) {
            if(newAge.equals(msad.getAge())) {
                foundAge = true;
                break;
            }
        }

        if(foundAge) {
            String message = getString(R.string.duplicate_age);
            message += newAge.toString();
            final Snackbar snackbar = Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(R.string.dismiss, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
            return;
        }

        RetirementOptionsDatabase.getInstance(getContext()).addAge(newAge);
        //RetirementOptionsHelper.addAge(getContext(), newAge);
        SystemUtils.updateAppWidget(getContext());
        milestoneAges = getMilestoneAges(getContext(), mROD);
        mAdapter.update(milestoneAges);
    }
}