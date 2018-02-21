package com.intelliviz.retirementhelper.ui.income;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.transition.Slide;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.adapter.IncomeSourceAdapter;
import com.intelliviz.retirementhelper.db.entity.IncomeSourceEntityBase;
import com.intelliviz.retirementhelper.ui.IncomeSourceListMenuFragment;
import com.intelliviz.retirementhelper.ui.ListMenuActivity;
import com.intelliviz.retirementhelper.ui.YesNoDialog;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SelectIncomeSourceListener;
import com.intelliviz.retirementhelper.viewmodel.IncomeSourceListViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_TYPE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_MENU_ITEM_LIST;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_SELECTED_MENU_ITEM;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_DELETE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_EDIT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_VIEW;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_GOV_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_SAVINGS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_401K;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_INCOME_MENU;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_YES_NO;

/**
 * CLass for handling the list of income sources.
 */
public class IncomeSourceListFragment extends Fragment implements SelectIncomeSourceListener {
    public static final String TAG = IncomeSourceListFragment.class.getSimpleName();
    private static final int REQUEST_INCOME_SOURCE_MENU = 20;
    private IncomeSourceAdapter mIncomeSourceAdapter;
    private static final String DIALOG_YESNO = "DialogYesNo";
    private IncomeSourceEntityBase mSelectedIncome;
    private int mIncomeAction;
    private List<IncomeSourceEntityBase> mIncomeSources = new ArrayList<>();
    private IncomeSourceListViewModel mViewModel;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.emptyView)
    TextView mEmptyView;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.addIncomeTypeFAB)
    FloatingActionButton mAddIncomeSourceFAB;

    /**
     * Default constructor.
     */
    public IncomeSourceListFragment() {
        // Required empty public constructor
    }

    /**
     * Create a new instance of the fragment.
     * @return The fragment.
     */
    public static IncomeSourceListFragment newInstance() {
        return new IncomeSourceListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_income_list_layout, container, false);
        ButterKnife.bind(this, view);

        mIncomeSourceAdapter = new IncomeSourceAdapter(mIncomeSources);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mIncomeSourceAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                linearLayoutManager.getOrientation()));
        mIncomeSourceAdapter.setOnSelectIncomeSourceListener(this);

        // The FAB will pop up an activity to allow a new income source to be created.
        mAddIncomeSourceFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = getString(R.string.add_income_source);
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG);
                snackbar.show();

                // pop up the menu for selecting an income source type
                final String[] incomeTypes = getResources().getStringArray(R.array.income_types);
                Intent intent = new Intent(getContext(), ListMenuActivity.class);
                intent.putExtra(EXTRA_MENU_ITEM_LIST, incomeTypes);
                startActivityForResult(intent, REQUEST_INCOME_SOURCE_MENU);
            }
        });

        mSelectedIncome = null;
        mIncomeAction = -1;

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(getString(R.string.income_source_subtitle));
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.update();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(IncomeSourceListViewModel.class);

        mViewModel.get().observe(this, new Observer< List<IncomeSourceEntityBase>>() {
            @Override
            public void onChanged(@Nullable List<IncomeSourceEntityBase> incomeSources) {
                mIncomeSourceAdapter.update(incomeSources);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_INCOME_SOURCE_MENU:
                    onHandleIncomeSourceSelection(intent);
                    break;
                case REQUEST_INCOME_MENU:
                    onHandleIncomeMenuSourceAction(intent);
                    break;
                case REQUEST_YES_NO:
                    onHandleYesNo();
                    break;
            }
        }
    }

    @Override
    public void onSelectIncomeSource(IncomeSourceEntityBase incomeSource, boolean showMenu) {
        mSelectedIncome = incomeSource;
        if(showMenu) {
            // show edit/delete menu
            Slide slide = new Slide(Gravity.RIGHT);
            slide.setDuration(1000);


            Intent intent = new Intent(getContext(), IncomeSourceListMenuFragment.class);
            startActivityForResult(intent, REQUEST_INCOME_MENU);
            getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);


        } else {
            Intent intent;
            switch(incomeSource.getType()) {
                case INCOME_TYPE_GOV_PENSION:
                    intent = new Intent(getContext(), GovPensionIncomeDetailsActivity.class);
                    intent.putExtra(EXTRA_INCOME_SOURCE_ID, mSelectedIncome.getId());
                    startActivity(intent);
                    break;
                case INCOME_TYPE_401K:
                    intent = new Intent(getContext(), SavingsIncomeDetailsActivity.class);
                    intent.putExtra(EXTRA_INCOME_SOURCE_ID, mSelectedIncome.getId());
                    startActivity(intent);
                    break;
                case INCOME_TYPE_SAVINGS:
                    intent = new Intent(getContext(), SavingsIncomeDetailsActivity.class);
                    intent.putExtra(EXTRA_INCOME_SOURCE_ID, mSelectedIncome.getId());
                    startActivity(intent);
                    break;
                case INCOME_TYPE_PENSION:
                    intent = new Intent(getContext(), PensionIncomeDetailsActivity.class);
                    intent.putExtra(EXTRA_INCOME_SOURCE_ID, mSelectedIncome.getId());
                    startActivity(intent);
                    break;
            }
        }
    }

    // Add new income source
    private void onHandleIncomeSourceSelection(Intent resultIntent) {
        int item = resultIntent.getIntExtra(EXTRA_SELECTED_MENU_ITEM, -1);
        switch (item) {
            case INCOME_TYPE_SAVINGS:
                startSavingsIncomeSourceActivity(0, RetirementConstants.INCOME_ACTION_ADD, RetirementConstants.INCOME_TYPE_SAVINGS);
                break;
            case INCOME_TYPE_401K:
                startSavingsIncomeSourceActivity(0, RetirementConstants.INCOME_ACTION_ADD, RetirementConstants.INCOME_TYPE_401K);
                break;
            case INCOME_TYPE_PENSION:
                startPensionIncomeSourceActivity(0, RetirementConstants.INCOME_ACTION_ADD);
                break;
            case INCOME_TYPE_GOV_PENSION:
                startGovPensionIncomeSourceActivity(0, RetirementConstants.INCOME_ACTION_ADD);
                break;
        }
    }

    private void onHandleIncomeMenuSourceAction(Intent resultIntent) {
        int action = resultIntent.getIntExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_VIEW);
        if (mSelectedIncome.getId() == 0) {
            mSelectedIncome = null;
            return;
        }

        if (action == INCOME_ACTION_DELETE) {
            mIncomeAction = INCOME_ACTION_DELETE;
            FragmentManager fm = getFragmentManager();
            YesNoDialog dialog = YesNoDialog.newInstance(getString(R.string.delete_income_source));
            dialog.setTargetFragment(this, REQUEST_YES_NO);
            dialog.show(fm, DIALOG_YESNO);
            return;
        }

        if (action == INCOME_ACTION_EDIT) {
            switch (mSelectedIncome.getType()) {
                case INCOME_TYPE_SAVINGS:
                    startSavingsIncomeSourceActivity(mSelectedIncome.getId(), RetirementConstants.INCOME_ACTION_EDIT, RetirementConstants.INCOME_TYPE_SAVINGS);
                    getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                    break;
                case INCOME_TYPE_401K:
                    startSavingsIncomeSourceActivity(mSelectedIncome.getId(), RetirementConstants.INCOME_ACTION_EDIT, RetirementConstants.INCOME_TYPE_401K);
                    getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                    break;
                case INCOME_TYPE_PENSION:
                    startPensionIncomeSourceActivity(mSelectedIncome.getId(), RetirementConstants.INCOME_ACTION_EDIT);
                    getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                    break;
                case INCOME_TYPE_GOV_PENSION:
                    startGovPensionIncomeSourceActivity(mSelectedIncome.getId(), RetirementConstants.INCOME_ACTION_EDIT);
                    getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                    break;
            }
        }
    }

    private void onHandleYesNo() {
        if (mIncomeAction == INCOME_ACTION_DELETE && mSelectedIncome.getId() != 0) {
            mViewModel.delete(mSelectedIncome);
            //mViewModel.updateAppWidget();
        }
    }

    private void startSavingsIncomeSourceActivity(long id, int action, int incomeType) {
        Intent intent = new Intent(getContext(), SavingsIncomeEditActivity.class);
        intent.putExtra(EXTRA_INCOME_SOURCE_ID, id);
        intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, action);
        intent.putExtra(EXTRA_INCOME_TYPE, incomeType);
        startActivity(intent);
    }

    private void startGovPensionIncomeSourceActivity(long id, int action) {
        Intent intent = new Intent(getContext(), GovPensionIncomeEditActivity.class);
        intent.putExtra(EXTRA_INCOME_SOURCE_ID, id);
        intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, action);
        startActivity(intent);
    }

    private void startPensionIncomeSourceActivity(long id, int action) {
        Intent intent = new Intent(getContext(), PensionIncomeEditActivity.class);
        intent.putExtra(EXTRA_INCOME_SOURCE_ID, id);
        intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, action);
        startActivity(intent);
    }
}
