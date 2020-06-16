package com.teyouale.smsspamblock.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.teyouale.smsspamblock.R;

/**
 * Activity with arbitrary fragment inside
 */
public class CustomFragmentActivity extends AppCompatActivity {
    private static final String TAG = CustomFragmentActivity.class.getName();
    private static final String ACTIVITY_TITLE = "ACTIVITY_TITLE";
    private static final String FRAGMENT_ARGUMENTS = "FRAGMENT_ARGUMENTS";
    private static final String FRAGMENT_CLASS = "FRAGMENT_CLASS";

    // Opens activity with fragment and waiting for result
    public static void show(Activity context, String activityTitle,
                            Class<? extends Fragment> fragmentClass,
                            Bundle fragmentArguments, int requestCode) {
        Intent intent = getIntent(context, activityTitle, fragmentClass, fragmentArguments);
        context.startActivityForResult(intent, requestCode);
    }

    // Opens activity with fragment and waiting for result
    public static void show(Context context, Fragment parent, String activityTitle,
                            Class<? extends Fragment> fragmentClass,
                            Bundle fragmentArguments, int requestCode) {
        Intent intent = getIntent(context, activityTitle, fragmentClass, fragmentArguments);
        parent.startActivityForResult(intent, requestCode);
    }

    // Opens activity with fragment
    public static void show(Context context, String activityTitle,
                            Class<? extends Fragment> fragmentClass,
                            Bundle fragmentArguments) {
        Intent intent = getIntent(context, activityTitle, fragmentClass, fragmentArguments);
        context.startActivity(intent);
    }

    private static Intent getIntent(Context context, String activityTitle,
                                    Class<? extends Fragment> fragmentClass,
                                    Bundle fragmentArguments) {
        Intent intent = new Intent(context, CustomFragmentActivity.class);
        intent.putExtra(ACTIVITY_TITLE, activityTitle);
        intent.putExtra(FRAGMENT_CLASS, fragmentClass.getName());
        intent.putExtra(FRAGMENT_ARGUMENTS, fragmentArguments);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar);

        // get toolbar's title
        String title = getIntent().getStringExtra(ACTIVITY_TITLE);
        if (title != null) {
            // setup toolbar
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle(title);
            }

            // show custom toolbar shadow on pre LOLLIPOP devices
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                View view = findViewById(R.id.toolbar_shadow);
               if (view != null) {
                  view.setVisibility(View.VISIBLE);
                }
            }
        }

        // there is not just a screen rotation
        if (savedInstanceState == null) {
            // create fragment
            Fragment fragment;
            String fragmentClass = getIntent().getStringExtra(FRAGMENT_CLASS);
            try {
                Class<?> clazz = Class.forName(fragmentClass);
                fragment = (Fragment) clazz.newInstance();
            } catch (Exception ex) {
                Log.w(TAG, ex);
                finish();
                return;
            }
            // add arguments
            Bundle arguments = getIntent().getBundleExtra(FRAGMENT_ARGUMENTS);
            fragment.setArguments(arguments);
            // put fragment int activity
            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.frame_layout, fragment).
                    commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
