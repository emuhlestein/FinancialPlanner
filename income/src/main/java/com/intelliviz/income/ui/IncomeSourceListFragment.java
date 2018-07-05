package com.intelliviz.income.ui;


import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.transition.Slide;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelliviz.db.entity.AbstractIncomeSource;
import com.intelliviz.income.R;
import com.intelliviz.income.adapter.IncomeSourceAdapter;
import com.intelliviz.income.util.SelectIncomeSourceListener;
import com.intelliviz.income.viewmodel.IncomeSourceListViewModel;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_SOURCE_ACTION;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_MENU_ITEM_LIST;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_SELECTED_MENU_ITEM;
import static com.intelliviz.lowlevel.util.RetirementConstants.INCOME_ACTION_DELETE;
import static com.intelliviz.lowlevel.util.RetirementConstants.INCOME_ACTION_EDIT;
import static com.intelliviz.lowlevel.util.RetirementConstants.INCOME_ACTION_VIEW;
import static com.intelliviz.lowlevel.util.RetirementConstants.REQUEST_INCOME_MENU;
import static com.intelliviz.lowlevel.util.RetirementConstants.REQUEST_YES_NO;

/**
 * CLass for handling the list of income sources.
 */
public class IncomeSourceListFragment extends Fragment implements SelectIncomeSourceListener {
    public static final String TAG = IncomeSourceListFragment.class.getSimpleName();
    private static final int REQUEST_INCOME_SOURCE_MENU = 20;
    private IncomeSourceAdapter mIncomeSourceAdapter;
    private static final String DIALOG_YESNO = "DialogYesNo";
    private IncomeSource mSelectedIncomeSource;
    private int mIncomeAction;
    private List<AbstractIncomeSource> mIncomeSources = new ArrayList<>();
    private IncomeSourceListViewModel mViewModel;

    private RecyclerView mRecyclerView;
    private TextView mEmptyView;
    private CoordinatorLayout mCoordinatorLayout;
    private FloatingActionButton mAddIncomeSourceFAB;

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

        mRecyclerView = view.findViewById(R.id.recyclerview);
        mEmptyView = view.findViewById(R.id.emptyView);
        mCoordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        mAddIncomeSourceFAB = view.findViewById(R.id.addIncomeTypeFAB);

        mIncomeSourceAdapter = new IncomeSourceAdapter(mIncomeSources);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mIncomeSourceAdapter);
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
        //        linearLayoutManager.getOrientation()));
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

        mSelectedIncomeSource = null;
        mIncomeAction = -1;

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(getString(R.string.income_source_subtitle));
        }


        mViewModel = ViewModelProviders.of(getActivity()).get(IncomeSourceListViewModel.class);

        mViewModel.get().observe(this, new Observer< List<AbstractIncomeSource>>() {
            @Override
            public void onChanged(@Nullable List<AbstractIncomeSource> incomeSources) {
                mIncomeSourceAdapter.update(incomeSources);
            }
        });


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
    public void onSelectIncomeSource(AbstractIncomeSource incomeSource, boolean showMenu) {
        mSelectedIncomeSource = IncomeSourceFactory.createIncomeSource(incomeSource);
        if(showMenu) {
            // show edit/delete menu
            Slide slide = new Slide(Gravity.RIGHT);
            slide.setDuration(1000);


            Intent intent = new Intent(getContext(), IncomeSourceListMenuFragment.class);
            startActivityForResult(intent, REQUEST_INCOME_MENU);
            getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        } else {
            if(mSelectedIncomeSource != null) {
                mSelectedIncomeSource.startDetailsActivity(getContext());
            }
        }
    }

    // Add new income source
    private void onHandleIncomeSourceSelection(Intent resultIntent) {

        int item = resultIntent.getIntExtra(EXTRA_SELECTED_MENU_ITEM, -1);
        mSelectedIncomeSource = IncomeSourceFactory.createIncomeSource(item);
        if(mSelectedIncomeSource != null) {
            mSelectedIncomeSource.startAddActivity(getActivity());
        }
    }

    private void onHandleIncomeMenuSourceAction(Intent resultIntent) {
        int action = resultIntent.getIntExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_VIEW);
        if (mSelectedIncomeSource.getId() == 0) {
            mSelectedIncomeSource = null;
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
            mSelectedIncomeSource.startEditActivity(getActivity());
            getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        }
    }

    private void onHandleYesNo() {
        if (mIncomeAction == INCOME_ACTION_DELETE && mSelectedIncomeSource.getId() != 0) {
            mViewModel.delete(mSelectedIncomeSource.getIncomeSourceEntity());
            //mViewModel.updateAppWidget();
        }
    }


    public static class MyAlertDialog extends DialogFragment {
        public static MyAlertDialog newInstance(String title, String message) {
            MyAlertDialog fragment = new MyAlertDialog();
            Bundle args = new Bundle();
            args.putString("title", title);
            args.putString("message", message);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String title = getArguments().getString("title");
            String message = getArguments().getString("message");
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.dismiss();
                }
            });

            return alertDialogBuilder.create();
        }
    }
}
