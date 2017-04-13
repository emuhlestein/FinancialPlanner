package com.intelliviz.retirementhelper.ui;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.adapter.IncomeSourceAdapter;
import com.intelliviz.retirementhelper.db.RetirementContract;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IncomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private IncomeSourceAdapter mIncomeSourceAdapter;
    private static final int INCOME_TYPE_LOADER = 0;
    @Bind(R.id.gridView) RecyclerView mRecyclerView;
    @Bind(R.id.emptyView) TextView mEmptyView;
    @Bind(R.id.progressBar) ProgressBar mProgressBar;
    private OnSelectIncomeSourceListener mListener;

    public interface OnSelectIncomeSourceListener {

        /**
         * Callback for when an income source is selected..
         * @param id The id of the selected income source.
         */
        void onSelectIncomeSource(long id);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> loader;
        Uri uri = RetirementContract.IncomeSourceEntry.CONTENT_URI;
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

    public IncomeFragment() {
        // Required empty public constructor
    }

    public static IncomeFragment newInstance() {
        IncomeFragment fragment = new IncomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_income_layout, container, false);
        ButterKnife.bind(this, view);

        mIncomeSourceAdapter = new IncomeSourceAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mIncomeSourceAdapter);
        mIncomeSourceAdapter.setOnSelectIncomeSourceListener(mListener);
        getLoaderManager().initLoader(INCOME_TYPE_LOADER, null, this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnSelectIncomeSourceListener) {
            mListener = (OnSelectIncomeSourceListener)context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
