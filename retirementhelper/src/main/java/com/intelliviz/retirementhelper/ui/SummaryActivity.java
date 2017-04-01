package com.intelliviz.retirementhelper.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.intelliviz.retirementhelper.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SummaryActivity extends AppCompatActivity {
    @Bind(R.id.navigation_view) NavigationView mNavigationView;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        ButterKnife.bind(this);

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        ActionBar ab = getSupportActionBar();
        ab.setSubtitle("Summary");
        createNavDrawer();

    }

    private void createNavDrawer() {
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.item1:
                        break;
                    case R.id.item2:
                        break;
                }
                mDrawer.closeDrawers();
                return false;
            }
        });
/*
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };
        */
    }


}
