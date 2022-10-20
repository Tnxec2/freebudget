package de.kontranik.freebudget.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import de.kontranik.freebudget.R
import de.kontranik.freebudget.adapter.DrawerItemCustomAdapter
import de.kontranik.freebudget.databinding.ActivityMainBinding
import de.kontranik.freebudget.fragment.AllTransactionFragment
import de.kontranik.freebudget.fragment.OverviewFragment
import de.kontranik.freebudget.fragment.RegularFragment
import de.kontranik.freebudget.model.DrawerItem
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var mNavigationDrawerItemTitles: Array<String>


    private lateinit var toolbar: Toolbar
    private lateinit var switchShowOnlyPlanned: SwitchCompat

    var mDrawerToggle: ActionBarDrawerToggle? = null
    var month = 0
    var year = 0
    var category: String? = null
    var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mNavigationDrawerItemTitles =
            resources.getStringArray(R.array.navigation_drawer_items_array)

        setupToolbar()
        switchShowOnlyPlanned = binding.toolbar.switchShowPlannedOnly
        setTextSwitch(false)
        switchShowOnlyPlanned.setOnCheckedChangeListener { buttonView, isChecked -> // do something, the isChecked will be
            // true if the switch is in the On position
            setTextSwitch(isChecked)

            // an Fragment übergeben
            val contentFragment = supportFragmentManager.findFragmentById(R.id.content_frame)
            if (contentFragment != null && findViewById<View?>(R.id.mainlayout_alltransaction) != null) {
                (contentFragment as AllTransactionFragment).changeShowOnlyPlanned(isChecked)
            }
        }
        val drawerItem = hashMapOf<Int, DrawerItem>()
        drawerItem[INDEX_DRAWER_OVERVIEW] = DrawerItem(
            R.drawable.ic_assessment_24dp,
            mNavigationDrawerItemTitles[INDEX_DRAWER_OVERVIEW]
        )
        drawerItem[INDEX_DRAWER_ALLTRANSACTION] = DrawerItem(
            R.drawable.ic_view_list_24dp,
            mNavigationDrawerItemTitles[INDEX_DRAWER_ALLTRANSACTION]
        )
        drawerItem[INDEX_DRAWER_REGULAR] = DrawerItem(
            R.drawable.ic_repeat_24dp,
            mNavigationDrawerItemTitles[INDEX_DRAWER_REGULAR]
        )
        drawerItem[3] = DrawerItem(0, "")
        drawerItem[4] = DrawerItem(R.drawable.ic_folder_24dp, mNavigationDrawerItemTitles[3])
        drawerItem[5] = DrawerItem(R.drawable.ic_menu_manage, mNavigationDrawerItemTitles[4])
        drawerItem[6] =
            DrawerItem(R.drawable.ic_settings_24dp, mNavigationDrawerItemTitles[5])
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setHomeButtonEnabled(true)
        val adapter =
            DrawerItemCustomAdapter(this, R.layout.list_view_item_navigationdrawer, drawerItem.values.toTypedArray())
        binding.leftDrawer.adapter = adapter
        binding.leftDrawer.onItemClickListener = DrawerItemClickListener()

        binding.drawerLayout.setDrawerListener(mDrawerToggle)
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
                val openCategory = Intent(this, CategoryListActivity::class.java)
                this.startActivityForResult(openCategory, 0)
            }
            5 -> {
                val openTools = Intent(this, ToolsActivity::class.java)
                this.startActivityForResult(openTools, 0)
            }
            6 -> {
                val openSettings = Intent(this, SettingsActivity::class.java)
                this.startActivityForResult(openSettings, 0)
            }
            else -> {}
        }
        if (fragment != null) {
            if (position == INDEX_DRAWER_ALLTRANSACTION) {
                switchShowOnlyPlanned.visibility = View.VISIBLE
                switchShowOnlyPlanned.isEnabled = true
            } else {
                switchShowOnlyPlanned.visibility = View.INVISIBLE
                switchShowOnlyPlanned.isEnabled = false
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
        binding.drawerLayout.closeDrawer(binding.leftDrawer)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (mDrawerToggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(
            item
        )
    }

    override fun setTitle(index: Int) {
        var title: String? = mNavigationDrawerItemTitles[index]
        if (index == INDEX_DRAWER_ALLTRANSACTION) {
            title = this.category
        }
        binding.toolbar.title.text = title
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
        if (savedInstanceState.containsKey(FRAGMENT_POSITION_KEY)) {
            position = savedInstanceState.getInt(FRAGMENT_POSITION_KEY)
            setTitle(position)
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
            binding.drawerLayout,
            toolbar,
            R.string.app_name,
            R.string.app_name
        )
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle!!.syncState()
    }

    private fun setTextSwitch(isChecked: Boolean) {
        if (isChecked) switchShowOnlyPlanned.setText(R.string.only_planned) else switchShowOnlyPlanned.setText(
            R.string.all
        )
    }

    fun updatePosition(index: Int) {
        position = index
        setDrawerSelection(index)
        setTitle(index)
    }

    private fun setDrawerSelection(index: Int) {
        binding.leftDrawer.setItemChecked(index, true)
        binding.leftDrawer.setSelection(index)
    }

    fun prevMonth() {
        if (month == 1) {
            if (year == 2000) return
            month = 12
            year--
        } else {
            month -= 1
        }
    }

    fun nextMonth() {
        if (month == 12) {
            if (year == 3000) return
            month = 1
            year++
        } else {
            month += 1
        }
    }

    companion object {
        const val FRAGMENT_POSITION_KEY = "FRAGMENT_POSITION_KEY"
        const val INDEX_DRAWER_OVERVIEW = 0
        const val INDEX_DRAWER_ALLTRANSACTION = 1
        const val INDEX_DRAWER_REGULAR = 2
    }
}