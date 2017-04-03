package com.intelliviz.retirementhelper.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.intelliviz.retirementhelper.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SummaryActivity extends AppCompatActivity {
    private static final String SUMMARY_FRAG_TAG = "summary frag tag";
    @Bind(R.id.summary_toolbar) Toolbar mToolbar;
    @Bind(R.id.bottom_navigation) BottomNavigationView mBottonNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setSubtitle("Summary");

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment;

        fragment = fm.findFragmentByTag(SUMMARY_FRAG_TAG);
        if (fragment == null) {
            fragment = SummaryFragment.newInstance();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.content_frame, fragment, SUMMARY_FRAG_TAG);
            ft.commit();
        }

        mBottonNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                }
                return false;
            }
        });
    }

    /**
     * This will add the option menu to the toolbar.
     * @param menu The menu.
     * @return If true, menu will be created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.summary_menu, menu);
        return true;
    }
}
