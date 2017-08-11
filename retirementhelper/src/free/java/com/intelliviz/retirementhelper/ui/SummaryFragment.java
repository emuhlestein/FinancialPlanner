package com.intelliviz.retirementhelper.ui;


import com.google.android.gms.ads.AdRequest;
import com.intelliviz.retirementhelper.R;

import butterknife.Bind;

/**
 * The summary fragment.
 * @author Ed Muhlestein
 */
public class SummaryFragment extends BaseSummaryFragment {

    @Bind(R.id.adView)
    com.google.android.gms.ads.AdView mBannerAdd;

    @Override
    public void createBannerAdd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mBannerAdd.loadAd(adRequest);
    }

    public SummaryFragment() {
        // Required empty public constructor
    }

    public static SummaryFragment newInstance() {
        return new SummaryFragment();
    }
}
