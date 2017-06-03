package com.intelliviz.retirementhelper.ui.income;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.adapter.IncomeSourceAdapter;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.ui.IncomeSourceListMenuFragment;
import com.intelliviz.retirementhelper.ui.YesNoDialog;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.RetirementOptionsData;
import com.intelliviz.retirementhelper.util.SavingsIncomeData;
import com.intelliviz.retirementhelper.util.SelectIncomeSourceListener;
import com.intelliviz.retirementhelper.util.TaxDeferredIncomeData;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_TYPE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_RETIREOPTIONS_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_DELETE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_EDIT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_VIEW;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_SAVINGS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_GOV_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_INCOME_MENU;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_SAVINGS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_TAX_DEFERRED;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_YES_NO;

public class IncomeSourceListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, SelectIncomeSourceListener {
    public static final String TAG = IncomeSourceListFragment.class.getSimpleName();
    private static final String DIALOG_MENU = "DialogIncomeMenu";
    private static final String DIALOG_YES_NO = "DialogYesNo";

    private IncomeSourceAdapter mIncomeSourceAdapter;
    private static final int INCOME_TYPE_LOADER = 0;
    @Bind(R.id.recyclerview) RecyclerView mRecyclerView;
    @Bind(R.id.emptyView) TextView mEmptyView;
    @Bind(R.id.coordinatorLayout) CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.addIncomeTypeFAB) FloatingActionButton mAddIncomeSourceFAB;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> loader;
        Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
        loader = new CursorLoader(getActivity(),
                uri, null, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mIncomeSourceAdapter.swapCursor(cursor);
        if (mIncomeSourceAdapter.getItemCount() == 0) {
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
        mIncomeSourceAdapter.swapCursor(null);
    }

    public IncomeSourceListFragment() {
        // Required empty public constructor
    }

    public static IncomeSourceListFragment newInstance() {
        IncomeSourceListFragment fragment = new IncomeSourceListFragment();
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
        View view = inflater.inflate(R.layout.fragment_income_list_layout, container, false);
        ButterKnife.bind(this, view);

        mIncomeSourceAdapter = new IncomeSourceAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mIncomeSourceAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation()));
        mIncomeSourceAdapter.setOnSelectIncomeSourceListener(this);
        getLoaderManager().initLoader(INCOME_TYPE_LOADER, null, this);

        // The FAB will pop up an activity to allow a new income source to be created.
        mAddIncomeSourceFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Add new income source", Snackbar.LENGTH_LONG);
                snackbar.show();

                final String[] incomeTypes = getResources().getStringArray(R.array.income_types);

                // TODO wrap in DialogFragment
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Select an account type");
                builder.setItems(incomeTypes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {
                        Toast.makeText(getContext(), "You selected " + incomeTypes[item], Toast.LENGTH_LONG).show();
                        dialogInterface.dismiss();
                        Intent intent = new Intent(getActivity(), IncomeSourceActivity.class);
                        intent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ID, -1);
                        intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, RetirementConstants.INCOME_ACTION_ADD);
                        intent.putExtra(EXTRA_INCOME_SOURCE_TYPE, item);
                        switch (item) {
                            case INCOME_TYPE_SAVINGS:
                                intent.putExtra(EXTRA_INCOME_DATA, new SavingsIncomeData(INCOME_TYPE_SAVINGS));
                                startActivityForResult(intent, REQUEST_SAVINGS);
                                break;
                            case RetirementConstants.INCOME_TYPE_TAX_DEFERRED:
                                intent.putExtra(EXTRA_INCOME_DATA, new TaxDeferredIncomeData(RetirementConstants.INCOME_TYPE_TAX_DEFERRED));
                                startActivityForResult(intent, REQUEST_TAX_DEFERRED);
                                break;
                            case RetirementConstants.INCOME_TYPE_PENSION:
                                startActivityForResult(intent, REQUEST_PENSION);
                                break;
                            case RetirementConstants.INCOME_TYPE_GOV_PENSION:
                                startActivityForResult(intent, REQUEST_GOV_PENSION);
                                break;
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setSubtitle("Income Source");

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SAVINGS:
                    saveSavingsIncomeData(intent);
                    break;
                case REQUEST_TAX_DEFERRED:
                    saveTaxDeferredData(intent);
                    break;
                case REQUEST_PENSION:
                    savePensionData(intent);
                    break;
                case REQUEST_GOV_PENSION:
                    saveGovPensionData(intent);
                    break;
                case REQUEST_INCOME_MENU:
                    onHandleAction(intent);
                    break;
                case REQUEST_YES_NO:
                    onHandleYesNo(intent);
                    break;
            }

            getLoaderManager().restartLoader(INCOME_TYPE_LOADER, null, this);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSelectIncomeSource(long id, int type, boolean showMenu) {
        final long incomeSourceId = id;
        final int incomeSourceType = type;
        if(showMenu) {

            FragmentManager fm = getActivity().getSupportFragmentManager();
            IncomeSourceListMenuFragment dialog = IncomeSourceListMenuFragment.newInstance(id, type);
            dialog.setTargetFragment(IncomeSourceListFragment.this, REQUEST_INCOME_MENU);
            dialog.show(fm, DIALOG_MENU);
/*
            // TODO wrap in DialogFragment
            final String[] incomeActions = getResources().getStringArray(R.array.income_source_actions);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setItems(incomeActions, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int item) {
                    Toast.makeText(getContext(), "You selected " + incomeActions[item], Toast.LENGTH_LONG).show();
                    dialogInterface.dismiss();
                    Intent intent = new Intent(getContext(), IncomeSourceActivity.class);

                    switch(incomeSourceType) {
                        case INCOME_TYPE_SAVINGS:
                        if(item == MENU_EDIT) {
                            SavingsIncomeData sid = DataBaseUtils.getSavingsIncomeData(getContext(), incomeSourceId);
                            intent.putExtra(EXTRA_INCOME_SOURCE_ID, incomeSourceId);
                            intent.putExtra(EXTRA_INCOME_DATA, sid);
                            intent.putExtra(EXTRA_INCOME_SOURCE_TYPE, incomeSourceType);
                            intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_EDIT);
                            startActivityForResult(intent, REQUEST_SAVINGS);
                        } else if(item == MENU_DELETE) {
                            int rowsDeleted = DataBaseUtils.deleteSavingsIncome(getContext(), incomeSourceId);
                        }
                        break;
                        case RetirementConstants.INCOME_TYPE_TAX_DEFERRED:
                        if(item == MENU_EDIT) {
                            TaxDeferredIncomeData tdid = DataBaseUtils.getTaxDeferredIncomeData(getContext(), incomeSourceId);
                            intent.putExtra(EXTRA_INCOME_SOURCE_ID, incomeSourceId);
                            intent.putExtra(EXTRA_INCOME_DATA, tdid);
                            intent.putExtra(EXTRA_INCOME_SOURCE_TYPE, incomeSourceType);
                            intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_EDIT);
                            startActivityForResult(intent, REQUEST_TAX_DEFERRED);
                        } else if(item == MENU_DELETE) {
                            int rowsDeleted = DataBaseUtils.deleteTaxDeferredIncome(getContext(), incomeSourceId);
                        }
                        break;
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            */
        } else {
            Intent intent;
            RetirementOptionsData rod;
            switch(incomeSourceType) {
                case INCOME_TYPE_SAVINGS:
                    SavingsIncomeData sid = DataBaseUtils.getSavingsIncomeData(getContext(), incomeSourceId);
                    rod = DataBaseUtils.getRetirementOptionsData(getContext());
                    intent = new Intent(getActivity(), IncomeSourceActivity.class);
                    intent.putExtra(EXTRA_INCOME_DATA, sid);
                    intent.putExtra(EXTRA_RETIREOPTIONS_DATA, rod);
                    intent.putExtra(EXTRA_INCOME_SOURCE_ID, incomeSourceId);
                    intent.putExtra(EXTRA_INCOME_SOURCE_TYPE, sid.getType());
                    intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_VIEW);
                    startActivityForResult(intent, REQUEST_SAVINGS);
                    break;
                case RetirementConstants.INCOME_TYPE_TAX_DEFERRED:
                    TaxDeferredIncomeData tdid = DataBaseUtils.getTaxDeferredIncomeData(getContext(), incomeSourceId);
                    rod = DataBaseUtils.getRetirementOptionsData(getContext());
                    intent = new Intent(getActivity(), IncomeSourceActivity.class);
                    intent.putExtra(EXTRA_INCOME_DATA, tdid);
                    intent.putExtra(EXTRA_RETIREOPTIONS_DATA, rod);
                    intent.putExtra(EXTRA_INCOME_SOURCE_ID, incomeSourceId);
                    intent.putExtra(EXTRA_INCOME_SOURCE_TYPE, tdid.getType());
                    intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_VIEW);
                    startActivityForResult(intent, REQUEST_TAX_DEFERRED);
                    break;
            }
        }
    }

    private void saveSavingsIncomeData(Intent intent) {
        SavingsIncomeData sid = intent.getParcelableExtra(EXTRA_INCOME_DATA);

        if (sid.getId() == -1) {
            String rc = DataBaseUtils.addSavingsIncome(getContext(), sid);
        } else {
            DataBaseUtils.saveSavingsIncomeData(getContext(), sid);
        }
    }

    private void saveTaxDeferredData(Intent intent) {
        TaxDeferredIncomeData tdid = intent.getParcelableExtra(EXTRA_INCOME_DATA);

        if (tdid.getId() == -1) {
            String rc = DataBaseUtils.addTaxDeferredIncome(getContext(), tdid);
        } else {
            DataBaseUtils.saveTaxDeferredData(getContext(), tdid);
        }
    }

    private void onHandleAction(Intent resultIntent) {
        int action = resultIntent.getIntExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_VIEW);
        int incomeSourceType = resultIntent.getIntExtra(EXTRA_INCOME_SOURCE_TYPE, INCOME_TYPE_SAVINGS);
        long incomeSourceId = resultIntent.getLongExtra(EXTRA_INCOME_SOURCE_ID, -1);
        if(incomeSourceId < 0) {
            return;
        }
        Intent intent = new Intent(getContext(), IncomeSourceActivity.class);

        switch(incomeSourceType) {
            case INCOME_TYPE_SAVINGS:
                if(action == INCOME_ACTION_EDIT) {
                    SavingsIncomeData sid = DataBaseUtils.getSavingsIncomeData(getContext(), incomeSourceId);
                    intent.putExtra(EXTRA_INCOME_SOURCE_ID, incomeSourceId);
                    intent.putExtra(EXTRA_INCOME_DATA, sid);
                    intent.putExtra(EXTRA_INCOME_SOURCE_TYPE, incomeSourceType);
                    intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_EDIT);
                    startActivityForResult(intent, REQUEST_SAVINGS);
                } else if(action == INCOME_ACTION_DELETE) {
                    int rowsDeleted = DataBaseUtils.deleteSavingsIncome(getContext(), incomeSourceId);
                }
                break;
            case RetirementConstants.INCOME_TYPE_TAX_DEFERRED:
                if(action == INCOME_ACTION_EDIT) {
                    TaxDeferredIncomeData tdid = DataBaseUtils.getTaxDeferredIncomeData(getContext(), incomeSourceId);
                    intent.putExtra(EXTRA_INCOME_SOURCE_ID, incomeSourceId);
                    intent.putExtra(EXTRA_INCOME_DATA, tdid);
                    intent.putExtra(EXTRA_INCOME_SOURCE_TYPE, incomeSourceType);
                    intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_EDIT);
                    startActivityForResult(intent, REQUEST_TAX_DEFERRED);
                } else if(action == INCOME_ACTION_DELETE) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    YesNoDialog dialog = YesNoDialog.newInstance(incomeSourceId);
                    dialog.setTargetFragment(IncomeSourceListFragment.this, REQUEST_YES_NO);
                    dialog.show(fm, DIALOG_YES_NO);

                }
                break;
        }
    }

    private void onHandleYesNo(Intent intent) {
        int action = intent.getIntExtra(EXTRA_INCOME_SOURCE_ACTION, -1);
        if(action == INCOME_ACTION_DELETE) {
            long incomeSourceId = intent.getLongExtra(EXTRA_INCOME_SOURCE_ID, -1);
            if(incomeSourceId != -1) {
                int rowsDeleted = DataBaseUtils.deleteTaxDeferredIncome(getContext(), incomeSourceId);
            }
        }
    }

    private void savePensionData(Intent intent) {
        /*
        long incomeTypeId = intent.getLongExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ID, -1);
        int incomeType = intent.getIntExtra(RetirementConstants.EXTRA_INCOME_SOURCE_TYPE, 0);
        String incomeSourceName = intent.getStringExtra(RetirementConstants.EXTRA_INCOME_SOURCE_NAME);
        String monthlyBenefit = intent.getStringExtra(RetirementConstants.EXTRA_INCOME_SOURCE_MONTHLY_BENEFIT);
        String age = intent.getStringExtra(RetirementConstants.EXTRA_INCOME_SOURCE_MINIMUM_AGE);
        IncomeTypeData isd = DataBaseUtils.getIncomeTypeData(getContext(), incomeTypeId);
        if (isd == null) {
            String sid = DataBaseUtils.addIncomeType(getContext(), incomeSourceName, incomeType);
            long id = Long.parseLong(sid);

            String sdid = DataBaseUtils.addPensionData(getContext(), id, monthlyBenefit, age);
        } else {
            String sid = Long.toString(incomeTypeId);

            int rowsUpdated = DataBaseUtils.saveIncomeType(getContext(), incomeTypeId, incomeSourceName, incomeType);
            if(rowsUpdated != 1) {
                // TODO handle error
            }

            rowsUpdated = DataBaseUtils.savePensionData(getContext(), id, monthlyBenefit, age);
            if(rowsUpdated != 1) {
                // TODO handle error
            }
        }
        */
    }

    private void saveGovPensionData(Intent intent) {
        /*
        long incomeTypeId = intent.getLongExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ID, -1);
        int incomeType = intent.getIntExtra(RetirementConstants.EXTRA_INCOME_SOURCE_TYPE, 0);
        String incomeSourceName = intent.getStringExtra(RetirementConstants.EXTRA_INCOME_SOURCE_NAME);
        String monthlyBenefit = intent.getStringExtra(RetirementConstants.EXTRA_INCOME_SOURCE_MONTHLY_BENEFIT);
        String age = intent.getStringExtra(RetirementConstants.EXTRA_INCOME_SOURCE_MINIMUM_AGE);
        IncomeTypeData isd = DataBaseUtils.getIncomeTypeData(getContext(), incomeTypeId);
        if (isd == null) {
            String sid = DataBaseUtils.addIncomeType(getContext(), incomeSourceName, incomeType);
            long id = Long.parseLong(sid);

            String sdid = DataBaseUtils.addGovPensionData(getContext(), id, monthlyBenefit, age);
        } else {
            String sid = Long.toString(incomeTypeId);

            int rowsUpdated = DataBaseUtils.saveIncomeType(getContext(), incomeTypeId, incomeSourceName, incomeType);
            if(rowsUpdated != 1) {
                // TODO handle error
            }

            rowsUpdated = DataBaseUtils.saveGovPensionData(getContext(), id, monthlyBenefit, age);
            if(rowsUpdated != 1) {
                // TODO handle error
            }
        }
        */
    }
}
