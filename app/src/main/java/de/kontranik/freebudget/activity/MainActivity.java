package de.kontranik.freebudget.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Calendar;

import de.kontranik.freebudget.R;
import de.kontranik.freebudget.adapter.DrawerItemCustomAdapter;
import de.kontranik.freebudget.fragment.OverviewFragment;
import de.kontranik.freebudget.fragment.AllTransactionFragment;
import de.kontranik.freebudget.fragment.RegularFragment;
import de.kontranik.freebudget.model.DrawerItem;

public class MainActivity extends AppCompatActivity {

    final static String FRAGMENT_POSITION_KEY = "FRAGMENT_POSITION_KEY";

    public final static int INDEX_DRAWER_OVERVIEW = 0;
    public final static int INDEX_DRAWER_ALLTRANSACTION = 1;
    public final static int INDEX_DRAWER_REGULAR = 2;

    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    Toolbar toolbar;
    Switch switchShowOnlyPlanned;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;

    public int month, year;

    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerItemTitles = getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        setupToolbar();

        switchShowOnlyPlanned = (Switch) findViewById(R.id.switchShowPlannedOnly);
        setTextSwitch(false);

        switchShowOnlyPlanned.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                setTextSwitch(isChecked);

                // an Fragment übergeben
                Fragment contentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
                if ((contentFragment != null) && (findViewById(R.id.mainlayout_alltransaction) != null)) {
                    ( (AllTransactionFragment) contentFragment ).changeShowOnlyPlanned(isChecked);
                }
            }
        });

        DrawerItem[] drawerItem = new DrawerItem[7];

        drawerItem[INDEX_DRAWER_OVERVIEW] = new DrawerItem(R.drawable.ic_assessment_black_24dp, mNavigationDrawerItemTitles[INDEX_DRAWER_OVERVIEW]);
        drawerItem[INDEX_DRAWER_ALLTRANSACTION] = new DrawerItem(R.drawable.ic_view_list_black_24dp, mNavigationDrawerItemTitles[INDEX_DRAWER_ALLTRANSACTION]);
        drawerItem[INDEX_DRAWER_REGULAR] = new DrawerItem(R.drawable.ic_repeat_black_24dp, mNavigationDrawerItemTitles[INDEX_DRAWER_REGULAR]);
        drawerItem[3] = new DrawerItem(0, null);
        drawerItem[4] = new DrawerItem(R.drawable.ic_folder_black_24dp, mNavigationDrawerItemTitles[3]);
        drawerItem[5] = new DrawerItem(R.drawable.ic_menu_manage, mNavigationDrawerItemTitles[4]);
        drawerItem[6] = new DrawerItem(R.drawable.ic_settings_black_24dp, mNavigationDrawerItemTitles[5]);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.list_view_item_navigationdrawer, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        setupDrawerToggle();

        Calendar date = Calendar.getInstance();
        if ( month == 0) {
            month = date.get(Calendar.MONTH) + 1;
        }
        if ( year == 0 ) {
            year = date.get(Calendar.YEAR);
        }

        // If turn the screen orientation then the savedInstanceState is not null.
        // In this condition, do not need to add new fragment again.
        if( savedInstanceState == null ) {
            position = 0;
            selectItem(0);
        } else {
            //Use exist fragment.
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

    }

    public void selectItem(int position) {

        Fragment fragment = null;

        switch (position) {
            case INDEX_DRAWER_OVERVIEW:
                fragment = new OverviewFragment();
                break;
            case INDEX_DRAWER_ALLTRANSACTION:
                fragment = new AllTransactionFragment();
                break;
            case INDEX_DRAWER_REGULAR:
                fragment = new RegularFragment();
                break;
            case 3:
                break;
            case 4:
                Intent open_category = new Intent(this, CategoryListActivity.class);
                this.startActivityForResult(open_category, 0);
                break;
            case 5:
                Intent open_tools = new Intent(this, ToolsActivity.class);
                this.startActivityForResult(open_tools, 0);
                break;
            case 6:
                Intent open_settings = new Intent(this, SettingsActivity.class);
                this.startActivityForResult(open_settings, 0);
                break;
            default:
                break;
        }

        if (fragment != null) {
            if (position == INDEX_DRAWER_ALLTRANSACTION) {
                switchShowOnlyPlanned.setVisibility(View.VISIBLE);
                switchShowOnlyPlanned.setEnabled(true);
            } else {
                switchShowOnlyPlanned.setVisibility(View.INVISIBLE);
                switchShowOnlyPlanned.setEnabled(false);
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            if ( position > 0 ) {
                fragmentTransaction.addToBackStack(null);
            } else {
                fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            fragmentTransaction.commit();

            this.position = position;
        }

        setTitle( this.position );

        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(int index) {
        TextView textViewTitle = (TextView) findViewById(R.id.title);
        textViewTitle.setText(mNavigationDrawerItemTitles[index]);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(FRAGMENT_POSITION_KEY, this.position);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            if ( savedInstanceState.containsKey(FRAGMENT_POSITION_KEY) ) {
                position = savedInstanceState.getInt(FRAGMENT_POSITION_KEY);
                setTitle( this.position );
            }
        }
    }

    private void setupToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setupDrawerToggle(){
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();
    }

    private void setTextSwitch(boolean isChecked) {
        if ( isChecked ) switchShowOnlyPlanned.setText(R.string.only_planned);
        else switchShowOnlyPlanned.setText(R.string.all);
    }

    public void setPosition(int index) {
        this.position = index;
        setDrawerSelection(index);
        setTitle(index);
    }

    public void setDrawerSelection(int index) {
        mDrawerList.setItemChecked( index, true );
        mDrawerList.setSelection( index );
    }

    public void prevMonth(){
        if (month == 1 ) {
            if ( year == 2000 ) return;
            month = 12;
            year--;
        } else {
            month = month - 1;
        }
    }

    public void nextMonth(){
        if (month == 12 ) {
            if ( year == 3000 ) return;
            month = 1;
            year++;
        } else {
            month = month + 1;
        }
    }
}

