package com.teyouale.smsspamblock;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.teyouale.smsspamblock.fragments.ContactsFragment;
import com.teyouale.smsspamblock.fragments.InformationFragment;
import com.teyouale.smsspamblock.fragments.JournalFragment;
import com.teyouale.smsspamblock.fragments.SMSConversatonListFragment;
import com.teyouale.smsspamblock.fragments.SettingFragment;
import com.teyouale.smsspamblock.utils.FragmentConstants;
import com.teyouale.smsspamblock.utils.Permissions;

public class MainActivity extends AppCompatActivity implements FragmentConstants {
    private String TAG = MainActivity.class.getName();
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;

    private ActionBarDrawerToggle drawerToggle;

    private String Current_Fragment = null;
    private String Current_Title = null;
    private Fragment selectedFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Requesting A Permission
        Permissions.checkAndRequest(this);
        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // This will display an Up icon (<-), we will replace it with hamburger later
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find our drawer layout
        mDrawerLayout = findViewById(R.id.drawer_layout);
        // Find our drawer view
        navigationView = findViewById(R.id.navigationView);
        //setup Drawer View
        setupDrawerContent(navigationView);

        FragmentManager fragmentManager = getSupportFragmentManager();

        // If there is Any Screen Rotation or Instance(State) Change
        if (savedInstanceState != null) {
            selectedFragment = getSupportFragmentManager().getFragment(savedInstanceState, Current_Fragment);
            Current_Title = savedInstanceState.getString(TITLE);
            setTitle(Current_Title);
            fragmentManager.beginTransaction().replace(R.id.flContent, selectedFragment).commit();
        } else {
            Class fragmentClass = SMSConversatonListFragment.class;
            try {
                Current_Title = getString(R.string.Message);
                setTitle(Current_Title);
                selectedFragment = (Fragment) fragmentClass.newInstance();
                fragmentManager.beginTransaction().replace(R.id.flContent, selectedFragment).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    // OnPermissions Result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // if it's Granted
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                // if Denied
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, Current_Fragment, selectedFragment);
        outState.putString(TITLE, Current_Title);
    }

    private void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass = null;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Bundle arguments = (bundle != null ? new Bundle(bundle) : new Bundle());
        switch (menuItem.getItemId()) {
            case R.id.nav_Message:
                // Passing Argument For New Fragment
                arguments.putString(TITLE, getString(R.string.Message));
                fragmentClass = SMSConversatonListFragment.class;
                break;
            case R.id.nav_BlackList:
                fragmentClass = ContactsFragment.class;
                arguments.putString(TITLE, getString(R.string.Black_list));
                arguments.putString(CONTACT_TYPE, getString(R.string.Black_list)); // Later it Will Be Changed By Constant Number
                break;
            case R.id.nav_WhiteList:
                fragmentClass = ContactsFragment.class;
                arguments.putString(TITLE, getString(R.string.White_list));
                arguments.putString(CONTACT_TYPE, getString(R.string.White_list));
                break;
            case R.id.nav_Information:
                fragmentClass = InformationFragment.class;
                arguments.putString(TITLE, getString(R.string.Information));
                break;
            case R.id.nav_Setting:
                fragmentClass = SettingFragment.class;
                arguments.putString(TITLE, getString(R.string.Setting));
                break;
            case R.id.nav_Journal:
                fragmentClass = JournalFragment.class;
                arguments.putString(TITLE, getString(R.string.Journal));
                break;
            case R.id.exit:
                System.exit(1);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + menuItem.getItemId());
        }

        try {
            Current_Title = arguments.getString(TITLE);
            selectedFragment = (Fragment) fragmentClass.newInstance();
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment.setArguments(arguments);
        fragmentManager.beginTransaction().replace(R.id.flContent, selectedFragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawerLayout.closeDrawers();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            // WE Will Implement this Later When We are Using Snack Bar
        }
    }
   /* Later We Will Implement This
        boolean onBackPressed() {
            return journalFragment.dismissSnackBar() ||
                    blackListFragment.dismissSnackBar() ||
                    whiteListFragment.dismissSnackBar();
        }
    */
}

