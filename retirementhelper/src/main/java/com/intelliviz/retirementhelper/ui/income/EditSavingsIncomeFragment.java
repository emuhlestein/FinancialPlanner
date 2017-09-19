package com.intelliviz.retirementhelper.ui.income;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.SavingsIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.services.SavingsDataService;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SavingsIncomeHelper;
import com.intelliviz.retirementhelper.util.SystemUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intelliviz.retirementhelper.ui.income.ViewTaxDeferredIncomeFragment.ID_ARGS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_SAVINGS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_TAX_DEFERRED;
import static com.intelliviz.retirementhelper.util.SystemUtils.getFloatValue;

/**
 * Fragment used for adding and editing (savings) income sources.
 *
 * @author Ed Muhlestein
 */
public class EditSavingsIncomeFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = EditSavingsIncomeFragment.class.getSimpleName();
    public static final String EDIT_SAVINGS_INCOME_FRAG_TAG = "edit savings income frag tag";
    private static final String EXTRA_INTENT = "extra intent";
    private SavingsIncomeData mSID;
    public static final int SID_LOADER = 0;
    public static final int STATUS_LOADER = 1;
    private long mId;

    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @Bind(R.id.name_edit_text)
    EditText mIncomeSourceName;

    @Bind(R.id.balance_text)
    EditText mBalance;

    @Bind(R.id.annual_interest_text)
    EditText mAnnualInterest;

    @Bind(R.id.monthly_increase_text)
    EditText mMonthlyIncrease;

    @Bind(R.id.add_income_source_button)
    Button mAddIncomeSource;

    @OnClick(R.id.add_income_source_button) void onAddIncomeSource() {
        updateIncomeSourceData();
        getActivity().finish();
    }

    public EditSavingsIncomeFragment() {
        // Required empty public constructor
    }

    public static EditSavingsIncomeFragment newInstance(Intent intent) {
        EditSavingsIncomeFragment fragment = new EditSavingsIncomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_edit_savings_income, container, false);
        ButterKnife.bind(this, view);

        if(mId != -1) {
            Bundle bundle = new Bundle();
            bundle.putString(ID_ARGS, Long.toString(mId));
            getLoaderManager().initLoader(SID_LOADER, bundle, this);
        }

        getLoaderManager().initLoader(STATUS_LOADER, null, this);

        mBalance.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String formattedString;
                    String str = textView.getText().toString();
                    String value = getFloatValue(str);
                    formattedString = SystemUtils.getFormattedCurrency(value);
                    if(formattedString != null) {
                        mBalance.setText(formattedString);
                    }
                }
            }
        });

        mMonthlyIncrease.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String formattedString;
                    String str = textView.getText().toString();
                    String value = getFloatValue(str);
                    formattedString = SystemUtils.getFormattedCurrency(value);
                    if(formattedString != null) {
                        mMonthlyIncrease.setText(formattedString);
                    }
                }
            }
        });

        mAnnualInterest.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String interest = textView.getText().toString();
                    interest = SystemUtils.getFloatValue(interest);
                    if(interest != null) {
                        interest += "%";
                        mAnnualInterest.setText(interest);
                    } else {
                        mAnnualInterest.setText("");
                    }
                }
            }
        });

        return view;
    }

    private void updateUI() {
        if(mSID == null || mSID.getId() == -1) {
            return;
        }

        String incomeSourceName = mSID.getName();
        int type = mSID.getType();
        String incomeSourceTypeString = SystemUtils.getIncomeSourceTypeString(getContext(), type);
        SystemUtils.setToolbarSubtitle(getActivity(), incomeSourceTypeString);

        String balanceString = SystemUtils.getFormattedCurrency(mSID.getBalance());
        String monthlyIncreaseString = SystemUtils.getFormattedCurrency(mSID.getMonthlyIncrease());
        String interestString = String.valueOf(mSID.getInterest());

        mIncomeSourceName.setText(incomeSourceName);
        mBalance.setText(balanceString);
        mAnnualInterest.setText(interestString);
        mMonthlyIncrease.setText(monthlyIncreaseString);
    }

    public void updateIncomeSourceData() {
        String balance = SystemUtils.getFloatValue(mBalance.getText().toString());
        if(balance == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout,getString(R.string.balance_not_valid), Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        String value = mAnnualInterest.getText().toString();
        String interest = SystemUtils.getFloatValue(value);
        if(interest == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout,  getString(R.string.interest_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        value = mMonthlyIncrease.getText().toString();
        String monthlyIncrease = SystemUtils.getFloatValue(value);
        if(monthlyIncrease == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.value_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        String name = mIncomeSourceName.getText().toString();
        double dbalance = Double.parseDouble(balance);
        double dinterest = Double.parseDouble(interest);
        double dmonthlyIncrease = Double.parseDouble(monthlyIncrease);

        SavingsIncomeData sid = new SavingsIncomeData(mId, name, INCOME_TYPE_SAVINGS, dinterest, dmonthlyIncrease, dbalance);
        updateSID(sid);
    }

    private void updateSID(SavingsIncomeData sid) {
        Intent intent = new Intent(getContext(), SavingsDataService.class);
        intent.putExtra(EXTRA_DB_DATA, sid);
        if(sid.getId() == -1) {
            intent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ACTION, RetirementConstants.INCOME_ACTION_ADD);
        } else {
            intent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ACTION, RetirementConstants.INCOME_ACTION_UPDATE);
        }

        getActivity().startService(intent);
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
            case STATUS_LOADER:
                loader = new CursorLoader(getActivity(),
                        RetirementContract.TransactionStatusEntry.CONTENT_URI,
                        null,
                        RetirementContract.TransactionStatusEntry.COLUMN_TYPE + " =? ",
                        new String[]{Integer.toString(INCOME_TYPE_TAX_DEFERRED)},
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
            case STATUS_LOADER:
                if(cursor.moveToFirst()) {
                    int statusIndex = cursor.getColumnIndex(RetirementContract.TransactionStatusEntry.COLUMN_STATUS);
                    int resultIndex = cursor.getColumnIndex(RetirementContract.TransactionStatusEntry.COLUMN_RESULT);
                    int actionIndex = cursor.getColumnIndex(RetirementContract.TransactionStatusEntry.COLUMN_ACTION);
                    if(statusIndex != -1) {
                        int status = cursor.getInt(statusIndex);
                        if(status == RetirementContract.TransactionStatusEntry.STATUS_UPDATED) {
                            if(actionIndex != -1 && resultIndex != -1) {
                                int action = cursor.getInt(actionIndex);
                                int numRows;
                                switch(action) {
                                    case RetirementContract.TransactionStatusEntry.ACTION_DELETE:
                                        numRows = Integer.parseInt(cursor.getString(resultIndex));
                                        Log.d(TAG, numRows + " deleted");
                                        break;
                                    case RetirementContract.TransactionStatusEntry.ACTION_UPDATE:
                                        numRows = Integer.parseInt(cursor.getString(resultIndex));
                                        Log.d(TAG, numRows + " update");
                                        break;
                                    case RetirementContract.TransactionStatusEntry.ACTION_INSERT:
                                        String uri = cursor.getString(resultIndex);
                                        Log.d(TAG, uri + " inserted");
                                        break;
                                }
                            }
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
