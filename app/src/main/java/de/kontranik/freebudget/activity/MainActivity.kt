package de.kontranik.freebudget.activity

import de.kontranik.freebudget.model.Category.name
import de.kontranik.freebudget.database.DatabaseAdapter.open
import de.kontranik.freebudget.database.DatabaseAdapter.getCategory
import de.kontranik.freebudget.database.DatabaseAdapter.close
import de.kontranik.freebudget.database.DatabaseAdapter.allCategory
import de.kontranik.freebudget.model.Category.id
import de.kontranik.freebudget.database.DatabaseAdapter.update
import de.kontranik.freebudget.database.DatabaseAdapter.insert
import de.kontranik.freebudget.database.DatabaseAdapter.deleteCategory
import de.kontranik.freebudget.fragment.AllTransactionFragment.changeShowOnlyPlanned
import de.kontranik.freebudget.service.SoftKeyboard.showKeyboard
import de.kontranik.freebudget.database.DatabaseAdapter.getRegularById
import de.kontranik.freebudget.model.RegularTransaction.description
import de.kontranik.freebudget.model.RegularTransaction.category
import de.kontranik.freebudget.model.RegularTransaction.day
import de.kontranik.freebudget.model.RegularTransaction.amount
import de.kontranik.freebudget.model.RegularTransaction.month
import de.kontranik.freebudget.model.RegularTransaction.date_start
import de.kontranik.freebudget.model.RegularTransaction.date_end
import de.kontranik.freebudget.service.SoftKeyboard.hideKeyboard
import de.kontranik.freebudget.database.DatabaseAdapter.deleteRegularTransaction
import de.kontranik.freebudget.service.FileService.exportFileRegular
import de.kontranik.freebudget.service.FileService.exportFileTransaction
import de.kontranik.freebudget.service.BackupAndRestore.exportDB
import de.kontranik.freebudget.service.BackupAndRestore.importDB
import de.kontranik.freebudget.service.FileService.importFileRegular
import de.kontranik.freebudget.service.FileService.importFileTransaction
import de.kontranik.freebudget.database.DatabaseAdapter.getTransaction
import de.kontranik.freebudget.model.Transaction.description
import de.kontranik.freebudget.model.Transaction.category
import de.kontranik.freebudget.model.Transaction.amount_fact
import de.kontranik.freebudget.model.Transaction.amount_planned
import de.kontranik.freebudget.model.Transaction.date
import de.kontranik.freebudget.model.Transaction.regular_id
import de.kontranik.freebudget.database.DatabaseAdapter.deleteTransaction
import androidx.appcompat.app.AppCompatActivity
import de.kontranik.freebudget.database.DatabaseAdapter
import android.os.Bundle
import de.kontranik.freebudget.R
import android.widget.AdapterView.OnItemClickListener
import android.content.Intent
import de.kontranik.freebudget.activity.CategoryListActivity
import android.app.Activity
import androidx.drawerlayout.widget.DrawerLayout
import de.kontranik.freebudget.fragment.AllTransactionFragment
import de.kontranik.freebudget.model.DrawerItem
import de.kontranik.freebudget.activity.MainActivity
import de.kontranik.freebudget.adapter.DrawerItemCustomAdapter
import de.kontranik.freebudget.activity.MainActivity.DrawerItemClickListener
import de.kontranik.freebudget.fragment.OverviewFragment
import de.kontranik.freebudget.fragment.RegularFragment
import de.kontranik.freebudget.activity.ToolsActivity
import de.kontranik.freebudget.activity.SettingsActivity
import android.os.Build
import android.os.Environment
import de.kontranik.freebudget.activity.OpenFileActivity
import de.kontranik.freebudget.service.SoftKeyboard
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import de.kontranik.freebudget.model.RegularTransaction
import de.kontranik.freebudget.activity.RegularTransactionActivity
import android.content.SharedPreferences
import de.kontranik.freebudget.service.FileService
import de.kontranik.freebudget.service.BackupAndRestore
import android.content.DialogInterface
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import de.kontranik.freebudget.activity.TransactionActivity
import java.util.*

class MainActivity : AppCompatActivity() {
    private var mNavigationDrawerItemTitles: Array<String>
    private var mDrawerLayout: DrawerLayout? = null
    private var mDrawerList: ListView? = null
    var toolbar: Toolbar? = null
    var switchShowOnlyPlanned: Switch? = null
    var mDrawerToggle: ActionBarDrawerToggle? = null
    var month = 0
    var year = 0
    var category: String? = null
    var position = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mNavigationDrawerItemTitles =
            resources.getStringArray(R.array.navigation_drawer_items_array)
        mDrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        mDrawerList = findViewById<View>(R.id.left_drawer) as ListView
        setupToolbar()
        switchShowOnlyPlanned = findViewById<View>(R.id.switchShowPlannedOnly) as Switch
        setTextSwitch(false)
        switchShowOnlyPlanned!!.setOnCheckedChangeListener { buttonView, isChecked -> // do something, the isChecked will be
            // true if the switch is in the On position
            setTextSwitch(isChecked)

            // an Fragment übergeben
            val contentFragment = supportFragmentManager.findFragmentById(R.id.content_frame)
            if (contentFragment != null && findViewById<View?>(R.id.mainlayout_alltransaction) != null) {
                (contentFragment as AllTransactionFragment).changeShowOnlyPlanned(isChecked)
            }
        }
        val drawerItem = arrayOfNulls<DrawerItem>(7)
        drawerItem[INDEX_DRAWER_OVERVIEW] = DrawerItem(
            R.drawable.ic_assessment_black_24dp,
            mNavigationDrawerItemTitles[INDEX_DRAWER_OVERVIEW]
        )
        drawerItem[INDEX_DRAWER_ALLTRANSACTION] = DrawerItem(
            R.drawable.ic_view_list_black_24dp,
            mNavigationDrawerItemTitles[INDEX_DRAWER_ALLTRANSACTION]
        )
        drawerItem[INDEX_DRAWER_REGULAR] = DrawerItem(
            R.drawable.ic_repeat_black_24dp,
            mNavigationDrawerItemTitles[INDEX_DRAWER_REGULAR]
        )
        drawerItem[3] = DrawerItem(0, null)
        drawerItem[4] = DrawerItem(R.drawable.ic_folder_black_24dp, mNavigationDrawerItemTitles[3])
        drawerItem[5] = DrawerItem(R.drawable.ic_menu_manage, mNavigationDrawerItemTitles[4])
        drawerItem[6] =
            DrawerItem(R.drawable.ic_settings_black_24dp, mNavigationDrawerItemTitles[5])
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setHomeButtonEnabled(true)
        val adapter =
            DrawerItemCustomAdapter(this, R.layout.list_view_item_navigationdrawer, drawerItem)
        mDrawerList!!.adapter = adapter
        mDrawerList!!.onItemClickListener = DrawerItemClickListener()
        mDrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        mDrawerLayout!!.setDrawerListener(mDrawerToggle)
        setupDrawerToggle()
        val date = Calendar.getInstance()
        if (month == 0) {
            month = date[Calendar.MONTH] + 1
        }
        if (year == 0) {
            year = date[Calendar.YEAR]
        }

        // If turn the screen orientation then the savedInstanceState is not null.
        // In this condition, do not need to add new fragment again.
        if (savedInstanceState == null) {
            position = 0
            selectItem(0)
        } else {
            //Use exist fragment.
        }
    }

    private inner class DrawerItemClickListener : OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
            selectItem(position)
        }
    }

    fun selectItem(position: Int) {
        var fragment: Fragment? = null
        when (position) {
            INDEX_DRAWER_OVERVIEW -> fragment = OverviewFragment()
            INDEX_DRAWER_ALLTRANSACTION -> fragment = AllTransactionFragment()
            INDEX_DRAWER_REGULAR -> fragment = RegularFragment()
            3 -> {}
            4 -> {
                val open_category = Intent(this, CategoryListActivity::class.java)
                this.startActivityForResult(open_category, 0)
            }
            5 -> {
                val open_tools = Intent(this, ToolsActivity::class.java)
                this.startActivityForResult(open_tools, 0)
            }
            6 -> {
                val open_settings = Intent(this, SettingsActivity::class.java)
                this.startActivityForResult(open_settings, 0)
            }
            else -> {}
        }
        if (fragment != null) {
            if (position == INDEX_DRAWER_ALLTRANSACTION) {
                switchShowOnlyPlanned!!.visibility = View.VISIBLE
                switchShowOnlyPlanned!!.isEnabled = true
            } else {
                switchShowOnlyPlanned!!.visibility = View.INVISIBLE
                switchShowOnlyPlanned!!.isEnabled = false
            }
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.content_frame, fragment)
            if (position > 0) {
                fragmentTransaction.addToBackStack(null)
            } else {
                fragmentManager.popBackStackImmediate(
                    null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
            }
            fragmentTransaction.commit()
            this.position = position
        }
        setTitle(this.position)
        mDrawerLayout!!.closeDrawer(mDrawerList!!)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (mDrawerToggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(
            item
        )
    }

    override fun setTitle(index: Int) {
        val textViewTitle = findViewById<View>(R.id.title) as TextView
        var title: String? = mNavigationDrawerItemTitles[index]
        if (index == INDEX_DRAWER_ALLTRANSACTION) {
            title = this.category
        }
        textViewTitle.text = title
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mDrawerToggle!!.syncState()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(FRAGMENT_POSITION_KEY, position)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(FRAGMENT_POSITION_KEY)) {
                position = savedInstanceState.getInt(FRAGMENT_POSITION_KEY)
                setTitle(position)
            }
        }
    }

    private fun setupToolbar() {
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    private fun setupDrawerToggle() {
        mDrawerToggle = ActionBarDrawerToggle(
            this,
            mDrawerLayout,
            toolbar,
            R.string.app_name,
            R.string.app_name
        )
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle!!.syncState()
    }

    private fun setTextSwitch(isChecked: Boolean) {
        if (isChecked) switchShowOnlyPlanned!!.setText(R.string.only_planned) else switchShowOnlyPlanned!!.setText(
            R.string.all
        )
    }

    fun setPosition(index: Int) {
        position = index
        setDrawerSelection(index)
        setTitle(index)
    }

    fun setDrawerSelection(index: Int) {
        mDrawerList!!.setItemChecked(index, true)
        mDrawerList!!.setSelection(index)
    }

    fun prevMonth() {
        if (month == 1) {
            if (year == 2000) return
            month = 12
            year--
        } else {
            month = month - 1
        }
    }

    fun nextMonth() {
        if (month == 12) {
            if (year == 3000) return
            month = 1
            year++
        } else {
            month = month + 1
        }
    }

    companion object {
        const val FRAGMENT_POSITION_KEY = "FRAGMENT_POSITION_KEY"
        const val INDEX_DRAWER_OVERVIEW = 0
        const val INDEX_DRAWER_ALLTRANSACTION = 1
        const val INDEX_DRAWER_REGULAR = 2
    }
}