package com.chintans.venturebox.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.chintans.venturebox.R;
import com.chintans.venturebox.fragments.KernelConfigurationFragment;
import com.chintans.venturebox.fragments.MainFragment;
import com.chintans.venturebox.fragments.VentureTweaksFragment;
import com.heinrichreimersoftware.materialdrawer.DrawerView;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerProfile;

public class MainActivity extends ActionBarActivity {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private DrawerView mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Initialize some variables
        mTitle = getTitle();
        mToolbar = (Toolbar) findViewById(R.id.action_bar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer = (DrawerView) findViewById(R.id.drawer);

        setSupportActionBar(mToolbar);
        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.toolbar_tint));

        // Set up drawer
        // Set up custom shadow to overlay main content when drawer is open
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolbar, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Set up the header image
        mDrawer.setProfile(
                new DrawerProfile()
                        .setAvatar(getResources().getDrawable(R.drawable.ic_launcher))
                        .setBackground(getResources().getDrawable(R.drawable.venture_logo))
                        .setName(getString(R.string.app_name))
                        .setDescription(getString(R.string.drawer_welcome))
        );

        // Add in drawer items
        mDrawer.addItem(
                new DrawerItem()
                        .setImage(getResources().getDrawable(android.R.drawable.ic_lock_lock))
                        .setTextPrimary(getString(R.string.home))
                        .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                            @Override
                            public void onClick(DrawerItem drawerItem, int id, int position) {
                                selectItem(position);
                            }
                        })
        );
        mDrawer.addDivider();
        mDrawer.addItem(
                new DrawerItem()
                        .setImage(getResources().getDrawable(android.R.drawable.ic_lock_lock))
                        .setTextPrimary(getString(R.string.venture_tweaks))
                        .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                            @Override
                            public void onClick(DrawerItem drawerItem, int id, int position) {
                                selectItem(position);
                            }
                        })
        );
        mDrawer.addItem(
                new DrawerItem()
                        .setImage(getResources().getDrawable(android.R.drawable.ic_lock_lock))
                        .setTextPrimary(getString(R.string.kernel_configuration))
                        .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                            @Override
                            public void onClick(DrawerItem drawerItem, int id, int position) {
                                selectItem(position);
                            }
                        })
        );
        mDrawer.addDivider();
        mDrawer.addItem(
                new DrawerItem()
                        .setImage(getResources().getDrawable(android.R.drawable.ic_lock_lock))
                        .setTextPrimary(getString(R.string.settings))
                        .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                            @Override
                            public void onClick(DrawerItem drawerItem, int id, int position) {
                                selectItem(position);
                            }
                        })
        );

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstance state has occurred
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.syncState();
    }

    private void selectItem(int position) {
        Fragment fragment = null;
        FragmentManager fragmentManager = getFragmentManager();

        // We skip some numbers because dividers also count in position
        switch (position) {
            case 0:
                getSupportActionBar().setTitle(getResources().getString(R.string.home));
                fragment = new MainFragment();
                break;
            case 2:
                getSupportActionBar().setTitle(getResources().getString(R.string.venture_tweaks));
                fragment = new VentureTweaksFragment();
                break;
            case 3:
                getSupportActionBar().setTitle(getResources().getString(R.string.kernel_configuration));
                fragment = new KernelConfigurationFragment();
                break;
            case 5:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                break;
        }

        if (fragment != null) {
            // Insert the fragment by replacing any existing fragment
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
        mDrawer.selectItem(position);
        mDrawerLayout.closeDrawer(mDrawer);
    }
}


