package com.intelliviz.retirementhelper.ui.income;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.adapter.IncomeSourceAdapter;
import com.intelliviz.retirementhelper.data.GovPensionIncomeData;
import com.intelliviz.retirementhelper.data.PensionIncomeData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.data.SavingsIncomeData;
import com.intelliviz.retirementhelper.data.TaxDeferredIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.services.GovPensionDataService;
import com.intelliviz.retirementhelper.services.PensionDataService;
import com.intelliviz.retirementhelper.services.SavingsDataService;
import com.intelliviz.retirementhelper.services.TaxDeferredIntentService;
import com.intelliviz.retirementhelper.ui.IncomeSourceListMenuFragment;
import com.intelliviz.retirementhelper.ui.YesNoDialog;
import com.intelliviz.retirementhelper.util.GovPensionHelper;
import com.intelliviz.retirementhelper.util.PensionHelper;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SavingsHelper;
import com.intelliviz.retirementhelper.util.SelectIncomeSourceListener;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.util.TaxDeferredHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.intelliviz.retirementhelper.util.PensionHelper.addPensionData;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_EXTRA_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ROWS_UPDATED;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DIALOG_MESSAGE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_TYPE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_RETIREOPTIONS_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_SERVICE_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_DELETE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_EDIT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_VIEW;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_GOV_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_SAVINGS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_TAX_DEFERRED;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_GOV_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_SAVINGS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_TAX_DEFERRED;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_GOV_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_INCOME_MENU;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_YES_NO;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_QUERY;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_UPDATE;

/**
 * CLass for handling the list of income sources.
 */
public class IncomeSourceListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, SelectIncomeSourceListener {
    public static final String TAG = IncomeSourceListFragment.class.getSimpleName();
    private IncomeSourceAdapter mIncomeSourceAdapter;
    private static final int INCOME_TYPE_LOADER = 0;
    private long mSelectedId;
    private int mIncomeAction;
    private int mIncomeSourceType;

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @Bind(R.id.emptyView)
    TextView mEmptyView;

    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @Bind(R.id.addIncomeTypeFAB)
    FloatingActionButton mAddIncomeSourceFAB;

    private BroadcastReceiver mSavingsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int action = intent.getIntExtra(EXTRA_DB_ACTION, -1);
            if(action == SERVICE_DB_UPDATE) {
                int numRows = intent.getIntExtra(EXTRA_DB_ROWS_UPDATED, -1);
                if(numRows != 1) {
                    Log.e(TAG, "Savings table failed to update");
                }
            } else if(mIncomeAction == INCOME_ACTION_VIEW || mIncomeAction == INCOME_ACTION_EDIT) {
                SavingsIncomeData sid = intent.getParcelableExtra(EXTRA_DB_DATA);
                RetirementOptionsData rod = intent.getParcelableExtra(EXTRA_DB_EXTRA_DATA);
                Intent newIntent = new Intent(getContext(), IncomeSourceActivity.class);
                newIntent.putExtra(EXTRA_INCOME_SOURCE_ID, sid.getId());
                newIntent.putExtra(EXTRA_INCOME_SOURCE_TYPE, sid.getType());
                newIntent.putExtra(EXTRA_INCOME_DATA, sid);
                newIntent.putExtra(EXTRA_RETIREOPTIONS_DATA, rod);
                newIntent.putExtra(EXTRA_INCOME_SOURCE_ACTION, mIncomeAction);
                startActivity(newIntent);
            }
        }
    };

    private BroadcastReceiver mTaxDeferredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int action = intent.getIntExtra(EXTRA_DB_ACTION, -1);
            if(action == SERVICE_DB_UPDATE) {
                int numRows = intent.getIntExtra(EXTRA_DB_ROWS_UPDATED, -1);
                if(numRows != 1) {
                    Log.e(TAG, "Tax deferred table failed to update");
                }
            } else if(mIncomeAction == INCOME_ACTION_VIEW || mIncomeAction == INCOME_ACTION_EDIT) {
                TaxDeferredIncomeData tdid = intent.getParcelableExtra(EXTRA_DB_DATA);
                RetirementOptionsData rod = intent.getParcelableExtra(EXTRA_DB_EXTRA_DATA);
                Intent newIntent = new Intent(getContext(), IncomeSourceActivity.class);
                newIntent.putExtra(EXTRA_INCOME_SOURCE_ID, tdid.getId());
                newIntent.putExtra(EXTRA_INCOME_SOURCE_TYPE, tdid.getType());
                newIntent.putExtra(EXTRA_INCOME_DATA, tdid);
                newIntent.putExtra(EXTRA_RETIREOPTIONS_DATA, rod);
                newIntent.putExtra(EXTRA_INCOME_SOURCE_ACTION, mIncomeAction);
                startActivity(newIntent);
            }
        }
    };

    private BroadcastReceiver mPensionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int action = intent.getIntExtra(EXTRA_DB_ACTION, -1);
            if(action == SERVICE_DB_UPDATE) {
                int numRows = intent.getIntExtra(EXTRA_DB_ROWS_UPDATED, -1);
                if(numRows != 1) {
                    Log.e(TAG, "Pension table failed to update");
                }
            } else if(mIncomeAction == INCOME_ACTION_VIEW || mIncomeAction == INCOME_ACTION_EDIT) {
                PensionIncomeData pid = intent.getParcelableExtra(EXTRA_DB_DATA);
                RetirementOptionsData rod = intent.getParcelableExtra(EXTRA_DB_EXTRA_DATA);
                Intent newIntent = new Intent(getContext(), IncomeSourceActivity.class);
                newIntent.putExtra(EXTRA_INCOME_SOURCE_ID, pid.getId());
                newIntent.putExtra(EXTRA_INCOME_SOURCE_TYPE, pid.getType());
                newIntent.putExtra(EXTRA_INCOME_DATA, pid);
                newIntent.putExtra(EXTRA_RETIREOPTIONS_DATA, rod);
                newIntent.putExtra(EXTRA_INCOME_SOURCE_ACTION, mIncomeAction);
                startActivity(newIntent);
            }
        }
    };

    private BroadcastReceiver mGovPensionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int action = intent.getIntExtra(EXTRA_DB_ACTION, -1);
            if(action == SERVICE_DB_UPDATE) {
                int numRows = intent.getIntExtra(EXTRA_DB_ROWS_UPDATED, -1);
                if(numRows != 1) {
                    Log.e(TAG, "GOv pension table failed to update");
                }
            } else if(mIncomeAction == INCOME_ACTION_VIEW || mIncomeAction == INCOME_ACTION_EDIT) {
                GovPensionIncomeData gpid = intent.getParcelableExtra(EXTRA_DB_DATA);
                RetirementOptionsData rod = intent.getParcelableExtra(EXTRA_DB_EXTRA_DATA);
                Intent newIntent = new Intent(getContext(), IncomeSourceActivity.class);
                newIntent.putExtra(EXTRA_INCOME_SOURCE_ID, gpid.getId());
                newIntent.putExtra(EXTRA_INCOME_SOURCE_TYPE, gpid.getType());
                newIntent.putExtra(EXTRA_INCOME_DATA, gpid);
                newIntent.putExtra(EXTRA_RETIREOPTIONS_DATA, rod);
                newIntent.putExtra(EXTRA_INCOME_SOURCE_ACTION, mIncomeAction);
                startActivity(newIntent);
            }
        }
    };

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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> loader;
        Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
        loader = new CursorLoader(getContext(),
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_income_list_layout, container, false);
        ButterKnife.bind(this, view);

        mIncomeSourceAdapter = new IncomeSourceAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mIncomeSourceAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                linearLayoutManager.getOrientation()));
        mIncomeSourceAdapter.setOnSelectIncomeSourceListener(this);
        getLoaderManager().initLoader(INCOME_TYPE_LOADER, null, this);

        // The FAB will pop up an activity to allow a new income source to be created.
        mAddIncomeSourceFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = getString(R.string.add_income_source);
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG);
                snackbar.show();

                final String[] incomeTypes = getResources().getStringArray(R.array.income_types);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.select_income_source_type);
                builder.setItems(incomeTypes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {
                        dialogInterface.dismiss();
                        Intent intent = new Intent(getContext(), IncomeSourceActivity.class);
                        intent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ID, -1);
                        intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, RetirementConstants.INCOME_ACTION_ADD);
                        intent.putExtra(EXTRA_INCOME_SOURCE_TYPE, item);
                        switch (item) {
                            case INCOME_TYPE_SAVINGS:
                                intent.putExtra(EXTRA_INCOME_DATA, new SavingsIncomeData());
                                startActivity(intent);
                                break;
                            case INCOME_TYPE_TAX_DEFERRED:
                                intent.putExtra(EXTRA_INCOME_DATA, new TaxDeferredIncomeData());
                                startActivity(intent);
                                break;
                            case INCOME_TYPE_PENSION:
                                intent.putExtra(EXTRA_INCOME_DATA, new PensionIncomeData());
                                startActivity(intent);
                                break;
                            case INCOME_TYPE_GOV_PENSION:
                                intent.putExtra(EXTRA_INCOME_DATA, new GovPensionIncomeData());
                                startActivity(intent);
                                break;
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        mSelectedId = -1;
        mIncomeAction = -1;

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PENSION:
                    //savePensionData(intent);
                    break;
                case REQUEST_GOV_PENSION:
                    //saveGovPensionData(intent);
                    break;
                case REQUEST_INCOME_MENU:
                    onHandleMenuSelection(intent);
                    break;
                case REQUEST_YES_NO:
                    onHandleYesNo(intent);
                    break;
            }

            getLoaderManager().restartLoader(INCOME_TYPE_LOADER, null, this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    @Override
    public void onSelectIncomeSource(long id, int type, boolean showMenu) {
        if(showMenu) {
            // edit, delete
            Intent intent = new Intent(getContext(), IncomeSourceListMenuFragment.class);
            intent.putExtra(EXTRA_INCOME_SOURCE_ID, id);
            intent.putExtra(EXTRA_INCOME_SOURCE_TYPE, type);
            startActivityForResult(intent, REQUEST_INCOME_MENU);
        } else {
            // view
            Intent intent;
            RetirementOptionsData rod;
            mIncomeAction = INCOME_ACTION_VIEW;
            switch(type) {
                case INCOME_TYPE_SAVINGS:
                    intent = new Intent(getContext(), SavingsDataService.class);
                    intent.putExtra(RetirementConstants.EXTRA_DB_ID, id);
                    intent.putExtra(EXTRA_DB_ACTION, SERVICE_DB_QUERY);
                    getActivity().startService(intent);
                    break;
                case INCOME_TYPE_TAX_DEFERRED:
                    intent = new Intent(getContext(), TaxDeferredIntentService.class);
                    intent.putExtra(RetirementConstants.EXTRA_DB_ID, id);
                    intent.putExtra(EXTRA_DB_ACTION, SERVICE_DB_QUERY);
                    getActivity().startService(intent);
                    break;
                case INCOME_TYPE_PENSION:
                    /*
                    PensionIncomeData pid = getPensionIncomeData(getContext(), id);
                    rod = getRetirementOptionsData(getContext());
                    intent = new Intent(getContext(), IncomeSourceActivity.class);
                    intent.putExtra(EXTRA_INCOME_DATA, pid);
                    intent.putExtra(EXTRA_RETIREOPTIONS_DATA, rod);
                    intent.putExtra(EXTRA_INCOME_SOURCE_ID, id);
                    intent.putExtra(EXTRA_INCOME_SOURCE_TYPE, pid.getType());
                    intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_VIEW);
                    startActivityForResult(intent, REQUEST_TAX_DEFERRED);
                    */
                    intent = new Intent(getContext(), PensionDataService.class);
                    intent.putExtra(RetirementConstants.EXTRA_DB_ID, id);
                    intent.putExtra(EXTRA_DB_ACTION, SERVICE_DB_QUERY);
                    getActivity().startService(intent);
                    break;
                case INCOME_TYPE_GOV_PENSION:
                    /*
                    GovPensionIncomeData gpid = GovPensionHelper.getGovPensionIncomeData(getContext(), id);
                    rod = RetirementOptionsHelper.getRetirementOptionsData(getContext());
                    intent = new Intent(getContext(), IncomeSourceActivity.class);
                    intent.putExtra(EXTRA_INCOME_DATA, gpid);
                    intent.putExtra(EXTRA_RETIREOPTIONS_DATA, rod);
                    intent.putExtra(EXTRA_INCOME_SOURCE_ID, id);
                    intent.putExtra(EXTRA_INCOME_SOURCE_TYPE, gpid.getType());
                    intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_VIEW);
                    startActivityForResult(intent, REQUEST_TAX_DEFERRED);
                    */
                    intent = new Intent(getContext(), GovPensionDataService.class);
                    intent.putExtra(RetirementConstants.EXTRA_DB_ID, id);
                    intent.putExtra(EXTRA_DB_ACTION, SERVICE_DB_QUERY);
                    getActivity().startService(intent);
                    break;
            }
        }
    }

    private void registerReceiver() {
        registerSavingsReceiver();
        registerTaxDeferredReceiver();
        registerPensionReceiver();
        registerGovPensionReceiver();
    }

    private void unregisterReceiver() {
        unregisterSavingsReceiver();
        unregisterTaxDeferredReceiver();
        unregisterPensionReceiver();
        unregisterGovPensionReceiver();
    }

    private void registerSavingsReceiver() {
        IntentFilter filter = new IntentFilter(LOCAL_SAVINGS);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mSavingsReceiver, filter);
    }

    private void unregisterSavingsReceiver() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mSavingsReceiver);
    }

    private void registerTaxDeferredReceiver() {
        IntentFilter filter = new IntentFilter(LOCAL_TAX_DEFERRED);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mTaxDeferredReceiver, filter);
    }

    private void unregisterTaxDeferredReceiver() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mTaxDeferredReceiver);
    }

    private void registerPensionReceiver() {
        IntentFilter filter = new IntentFilter(LOCAL_PENSION);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mPensionReceiver, filter);
    }

    private void unregisterPensionReceiver() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mPensionReceiver);
    }

    private void registerGovPensionReceiver() {
        IntentFilter filter = new IntentFilter(LOCAL_GOV_PENSION);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mGovPensionReceiver, filter);
    }

    private void unregisterGovPensionReceiver() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mGovPensionReceiver);
    }

    private void onHandleMenuSelection(Intent resultIntent) {
        int action = resultIntent.getIntExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_VIEW);
        int incomeSourceType = resultIntent.getIntExtra(EXTRA_INCOME_SOURCE_TYPE, INCOME_TYPE_SAVINGS);
        long incomeSourceId = resultIntent.getLongExtra(EXTRA_INCOME_SOURCE_ID, -1);
        if(incomeSourceId < 0) {
            mSelectedId = -1;
            return;
        }
        Intent intent;

        mSelectedId = incomeSourceId;
        mIncomeSourceType = incomeSourceType;

        switch(incomeSourceType) {
            case INCOME_TYPE_SAVINGS:
                if(action == INCOME_ACTION_EDIT) {
                    mIncomeAction = INCOME_ACTION_EDIT;
                    Intent localIntent = new Intent(getContext(), SavingsDataService.class);
                    localIntent.putExtra(EXTRA_DB_ID, incomeSourceId);
                    localIntent.putExtra(EXTRA_SERVICE_ACTION, SERVICE_DB_QUERY);
                    getActivity().startService(localIntent);
                } else if(action == INCOME_ACTION_DELETE) {
                    mIncomeAction = INCOME_ACTION_DELETE;
                    intent = new Intent(getContext(), YesNoDialog.class);
                    intent.putExtra(EXTRA_DIALOG_MESSAGE, getString(R.string.delete_income_source));
                    startActivityForResult(intent, REQUEST_YES_NO);
                }
                break;
            case INCOME_TYPE_TAX_DEFERRED:
                if(action == INCOME_ACTION_EDIT) {
                    mIncomeAction = INCOME_ACTION_EDIT;
                    Intent localIntent = new Intent(getContext(), TaxDeferredIntentService.class);
                    localIntent.putExtra(EXTRA_DB_ID, incomeSourceId);
                    localIntent.putExtra(EXTRA_SERVICE_ACTION, SERVICE_DB_QUERY);
                    getActivity().startService(localIntent);
                } else if(action == INCOME_ACTION_DELETE) {
                    mIncomeAction = INCOME_ACTION_DELETE;
                    intent = new Intent(getContext(), YesNoDialog.class);
                    intent.putExtra(EXTRA_DIALOG_MESSAGE, getString(R.string.delete_income_source));
                    startActivityForResult(intent, REQUEST_YES_NO);
                }
                break;
            case INCOME_TYPE_PENSION:
                if(action == INCOME_ACTION_EDIT) {
                    mIncomeAction = INCOME_ACTION_EDIT;
                    Intent localIntent = new Intent(getContext(), PensionDataService.class);
                    localIntent.putExtra(EXTRA_DB_ID, incomeSourceId);
                    localIntent.putExtra(EXTRA_DB_ACTION, SERVICE_DB_QUERY);
                    getActivity().startService(localIntent);
                } else if(action == INCOME_ACTION_DELETE) {
                    mIncomeAction = INCOME_ACTION_DELETE;
                    intent = new Intent(getContext(), YesNoDialog.class);
                    intent.putExtra(EXTRA_DIALOG_MESSAGE, getString(R.string.delete_income_source));
                    startActivityForResult(intent, REQUEST_YES_NO);
                }
                break;
            case INCOME_TYPE_GOV_PENSION:
                if(action == INCOME_ACTION_EDIT) {
                    mIncomeAction = INCOME_ACTION_EDIT;
                    Intent localIntent = new Intent(getContext(), GovPensionDataService.class);
                    localIntent.putExtra(EXTRA_DB_ID, incomeSourceId);
                    localIntent.putExtra(EXTRA_DB_ACTION, SERVICE_DB_QUERY);
                    getActivity().startService(localIntent);
                } else if(action == INCOME_ACTION_DELETE) {
                    mIncomeAction = INCOME_ACTION_DELETE;
                    intent = new Intent(getContext(), YesNoDialog.class);
                    intent.putExtra(EXTRA_DIALOG_MESSAGE, getString(R.string.delete_income_source));
                    startActivityForResult(intent, REQUEST_YES_NO);
                }
                break;
        }
    }

    private void onHandleYesNo(Intent intent) {
        if (mIncomeAction == INCOME_ACTION_DELETE && mSelectedId != -1) {
            switch(mIncomeSourceType){
                case RetirementConstants.INCOME_TYPE_SAVINGS:
                    SavingsHelper.deleteSavingsIncome(getContext(), mSelectedId);
                    break;
                case INCOME_TYPE_TAX_DEFERRED:
                    TaxDeferredHelper.deleteTaxDeferredIncome(getContext(), mSelectedId);
                    break;
                case INCOME_TYPE_PENSION:
                    PensionHelper.deleteSavingsIncome(getContext(), mSelectedId);
                    break;
                case INCOME_TYPE_GOV_PENSION:
                    GovPensionHelper.deleteSavingsIncome(getContext(), mSelectedId);
                    break;
            }

            SystemUtils.updateAppWidget(getContext());
        }
    }

    private void savePensionData(Intent intent) {
        PensionIncomeData pid = intent.getParcelableExtra(EXTRA_INCOME_DATA);

        if (pid.getId() == -1) {
            String rc = addPensionData(getContext(), pid);
        } else {
            PensionHelper.savePensionData(getContext(), pid);
        }

        SystemUtils.updateAppWidget(getContext());
    }

    private void saveGovPensionData(Intent intent) {
        GovPensionIncomeData gpid = intent.getParcelableExtra(EXTRA_INCOME_DATA);

        if (gpid.getId() == -1) {
            String rc = GovPensionHelper.addGovPensionData(getContext(), gpid);
        } else {
            GovPensionHelper.saveGovPensionData(getContext(), gpid);
        }

        SystemUtils.updateAppWidget(getContext());
    }
}
