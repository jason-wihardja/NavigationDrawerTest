package com.example.navigationdrawertest;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends AppCompatActivity {

    private NavigationDrawerFragment navigationDrawerFragment;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle.DrawerToggle drawerToggle;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FrameLayout contentLayout;

    private String current_fragment;
    private FirstFragment firstFragment = new FirstFragment();
    private SecondFragment secondFragment = new SecondFragment();

    private Runnable[] runnables = new Runnable[NavigationDrawerFragment.ANIMATION_DURATION + 1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationDrawerFragment.setup(drawerLayout, toolbar);
        navigationDrawerFragment.getActionBarDrawerToggle().setToolbarNavigationClickListener(navigationClicked);
        drawerToggle = navigationDrawerFragment.getActionBarDrawerToggle().getSlider();

        contentLayout = (FrameLayout) findViewById(R.id.content_layout);
        contentLayout.post(new Runnable() {
            @Override
            public void run() {
                fragmentManager.beginTransaction().replace(R.id.content_layout, firstFragment).commit();
                current_fragment = firstFragment.getClass().getSimpleName();
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        });

        for (int i = 0; i < runnables.length; i++) {
            final float position = i / (float) NavigationDrawerFragment.ANIMATION_DURATION;
            runnables[i] = new Runnable() {
                @Override
                public void run() {
                    drawerToggle.setPosition(position);
                }
            };
        }
    }

    @Override
    public void onBackPressed() {
        if (navigationDrawerFragment.isDrawerOpened()) {
            drawerLayout.closeDrawers();
        } else if (current_fragment.equals(secondFragment.getClass().getSimpleName())) {
            navigationClicked.onClick(contentLayout);
        } else {
            super.onBackPressed();
        }
    }

    public void onButtonClicked(View v) {
        switch (v.getId()) {
            case R.id.go_to_second_fragment_btn:
                navigationClicked.onClick(contentLayout);
                break;
        }
    }

    public View.OnClickListener navigationClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (current_fragment.equals(firstFragment.getClass().getSimpleName())) {
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_left_enter, R.anim.slide_left_exit)
                        .replace(R.id.content_layout, secondFragment)
                        .commit();
                current_fragment = secondFragment.getClass().getSimpleName();
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                for (int i = 0; i < runnables.length; i++)
                    drawerLayout.postDelayed(runnables[i], i);
                drawerLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        navigationDrawerFragment.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
                    }
                }, NavigationDrawerFragment.ANIMATION_DURATION);
            } else {
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_right_enter, R.anim.slide_right_exit)
                        .replace(R.id.content_layout, firstFragment)
                        .commit();
                current_fragment = firstFragment.getClass().getSimpleName();
                navigationDrawerFragment.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
                for (int i = 0; i < runnables.length; i++)
                    drawerLayout.postDelayed(runnables[runnables.length - i - 1], i);
                drawerLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    }
                }, NavigationDrawerFragment.ANIMATION_DURATION);
            }
        }
    };
}
