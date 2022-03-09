package de.kontranik.freebudget.fragment

import de.kontranik.freebudget.model.Transaction.amount_fact
import de.kontranik.freebudget.model.Transaction.category
import de.kontranik.freebudget.model.Transaction.date_edit
import de.kontranik.freebudget.model.Transaction.id
import de.kontranik.freebudget.service.PlanRegular.setRegularToPlanned
import de.kontranik.freebudget.model.Category.name
import de.kontranik.freebudget.model.Transaction.amount_planned
import de.kontranik.freebudget.model.Category.weight
import de.kontranik.freebudget.model.RegularTransaction.id
import de.kontranik.freebudget.model.RegularTransaction.date_start
import de.kontranik.freebudget.model.RegularTransaction.date_end
import de.kontranik.freebudget.model.RegularTransaction.amount
import de.kontranik.freebudget.activity.MainActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.kontranik.freebudget.adapter.TransactionAdapter
import android.os.Bundle
import de.kontranik.freebudget.R
import android.annotation.SuppressLint
import android.widget.AdapterView.OnItemClickListener
import androidx.constraintlayout.widget.ConstraintLayout
import de.kontranik.freebudget.service.OnSwipeTouchListener
import android.view.View.OnDragListener
import android.view.View.OnTouchListener
import android.content.ClipData
import android.view.View.DragShadowBuilder
import android.os.Build
import de.kontranik.freebudget.fragment.AllTransactionFragment
import android.view.ContextMenu.ContextMenuInfo
import android.widget.AdapterView.AdapterContextMenuInfo
import de.kontranik.freebudget.database.DatabaseAdapter
import android.content.Intent
import android.view.*
import de.kontranik.freebudget.activity.TransactionActivity
import de.kontranik.freebudget.service.PlanRegular
import de.kontranik.freebudget.adapter.CategoryAdapter
import android.widget.*
import de.kontranik.freebudget.fragment.OverviewFragment
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import de.kontranik.freebudget.model.RegularTransaction
import de.kontranik.freebudget.adapter.RegularTransactionAdapter
import de.kontranik.freebudget.activity.RegularTransactionActivity
import de.kontranik.freebudget.fragment.RegularFragment
import de.kontranik.freebudget.model.Transaction
import java.util.*

class AllTransactionFragment : Fragment() {
    var main: MainActivity? = null
    private var textView_Month: TextView? = null
    private var listView_transactionsList: ListView? = null
    private var btn_planRegular: Button? = null
    private var btn_prevMonth: ImageButton? = null
    private var btn_nextMonth: ImageButton? = null
    private var fab_add: FloatingActionButton? = null
    private var fab_add_plus: FloatingActionButton? = null
    private var fab_add_minus: FloatingActionButton? = null
    private var fab_add_plus_planned: FloatingActionButton? = null
    private var fab_add_minus_planned: FloatingActionButton? = null
    private val transactions: MutableList<Transaction> = ArrayList()
    var transactionAdapter: TransactionAdapter? = null
    private var months: Array<String>
    var isMove = false
    var showOnlyPlanned = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_alltransaction, container, false)
        setHasOptionsMenu(true)
        return v
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Setup any handles to view objects here
        textView_Month = view.findViewById<View>(R.id.textView_Month) as TextView
        btn_prevMonth = view.findViewById<View>(R.id.btn_prevMonth) as ImageButton
        btn_nextMonth = view.findViewById<View>(R.id.btn_nextMonth) as ImageButton
        fab_add = view.findViewById<View>(R.id.fab_add) as FloatingActionButton
        fab_add_plus = view.findViewById<View>(R.id.fab_add_plus) as FloatingActionButton
        fab_add_minus = view.findViewById<View>(R.id.fab_add_minus) as FloatingActionButton
        fab_add_plus_planned =
            view.findViewById<View>(R.id.fab_add_plus_planned) as FloatingActionButton
        fab_add_minus_planned =
            view.findViewById<View>(R.id.fab_add_minus_planned) as FloatingActionButton
        btn_planRegular = view.findViewById<View>(R.id.btn_planRegular) as Button
        btn_planRegular!!.setOnClickListener { planRegular() }
        btn_prevMonth!!.setOnClickListener { prevMonth() }
        btn_nextMonth!!.setOnClickListener { nextMonth() }
        listView_transactionsList = view.findViewById<View>(R.id.listView_transactions) as ListView
        listView_transactionsList!!.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                val entry = transactionAdapter!!.getItem(position)
                editTransaction(entry, TRANS_TYP_FACT)
            }

        // set list adapter
        transactionAdapter =
            TransactionAdapter(context, R.layout.list_view_item_transaction_item, transactions)
        // set adapter
        listView_transactionsList!!.adapter = transactionAdapter
        // Register the ListView  for Context menu
        registerForContextMenu(listView_transactionsList!!)
        months = resources.getStringArray(R.array.months)
        val date = Calendar.getInstance()
        main = activity as MainActivity?
        setMonthTextView()
        val mainLayout = view.findViewById<View>(R.id.mainlayout_alltransaction) as ConstraintLayout
        mainLayout.setOnTouchListener(object : OnSwipeTouchListener(context) {
            override fun onSwipeLeft() {
                nextMonth()
            }

            override fun onSwipeRight() {
                prevMonth()
            }
        })
        listView_transactionsList!!.setOnDragListener { v, event ->
            val action = event.action
            when (action) {
                DragEvent.ACTION_DRAG_STARTED -> {}
                DragEvent.ACTION_DRAG_ENTERED -> {}
                DragEvent.ACTION_DRAG_EXITED -> {}
                DragEvent.ACTION_DROP -> setNormalStat()
                DragEvent.ACTION_DRAG_ENDED -> setNormalStat()
                else -> {}
            }
            true
        }
        listView_transactionsList!!.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> fab_add!!.hide()
                MotionEvent.ACTION_UP -> fab_add!!.show()
                MotionEvent.ACTION_CANCEL -> fab_add!!.show()
            }
            false
        }
        fab_add!!.setOnTouchListener { view, motionEvent ->
            isMove = false
            if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                val data = ClipData.newPlainText("", "")
                fab_add!!.setImageResource(R.drawable.ic_euro_symbol_white_24dp)
                val shadowBuilder = DragShadowBuilder(view)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view.startDragAndDrop(data, shadowBuilder, view, 0)
                } else {
                    view.startDrag(data, shadowBuilder, view, 0)
                }
                fab_add_plus!!.visibility = View.VISIBLE
                fab_add_minus!!.visibility = View.VISIBLE
                fab_add_plus_planned!!.visibility = View.VISIBLE
                fab_add_minus_planned!!.visibility = View.VISIBLE
                fab_add!!.visibility = View.INVISIBLE
                isMove = true
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (!isMove) {
                    add(TRANS_STAT_MINUS, TRANS_TYP_FACT)
                }
            } else if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                fab_add_plus!!.visibility = View.VISIBLE
                fab_add_minus!!.visibility = View.VISIBLE
                fab_add_plus_planned!!.visibility = View.VISIBLE
                fab_add_minus_planned!!.visibility = View.VISIBLE
                fab_add!!.visibility = View.VISIBLE
            }
            true
        }
        fab_add_plus!!.setOnDragListener { v, event ->
            val action = event.action
            when (action) {
                DragEvent.ACTION_DRAG_STARTED -> {}
                DragEvent.ACTION_DRAG_ENTERED -> {}
                DragEvent.ACTION_DRAG_EXITED -> {}
                DragEvent.ACTION_DROP -> add(TRANS_STAT_PLUS, TRANS_TYP_FACT)
                DragEvent.ACTION_DRAG_ENDED -> {}
                else -> {}
            }
            true
        }
        fab_add_minus!!.setOnDragListener { v, event ->
            val action = event.action
            when (action) {
                DragEvent.ACTION_DRAG_STARTED -> {}
                DragEvent.ACTION_DRAG_ENTERED -> {}
                DragEvent.ACTION_DRAG_EXITED -> {}
                DragEvent.ACTION_DROP -> add(TRANS_STAT_MINUS, TRANS_TYP_FACT)
                DragEvent.ACTION_DRAG_ENDED -> {}
                else -> {}
            }
            true
        }
        fab_add_plus_planned!!.setOnDragListener { v, event ->
            val action = event.action
            when (action) {
                DragEvent.ACTION_DRAG_STARTED -> {}
                DragEvent.ACTION_DRAG_ENTERED -> {}
                DragEvent.ACTION_DRAG_EXITED -> {}
                DragEvent.ACTION_DROP -> add(TRANS_STAT_PLUS, TRANS_TYP_PLANNED)
                DragEvent.ACTION_DRAG_ENDED -> {}
                else -> {}
            }
            true
        }
        fab_add_minus_planned!!.setOnDragListener { v, event ->
            val action = event.action
            when (action) {
                DragEvent.ACTION_DRAG_STARTED -> {}
                DragEvent.ACTION_DRAG_ENTERED -> {}
                DragEvent.ACTION_DRAG_EXITED -> {}
                DragEvent.ACTION_DROP -> add(TRANS_STAT_MINUS, TRANS_TYP_PLANNED)
                DragEvent.ACTION_DRAG_ENDED -> {}
                else -> {}
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.all_transaction_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.menuitem_load_regular -> planRegular()
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        main!!.setPosition(MainActivity.INDEX_DRAWER_ALLTRANSACTION)
        getTransactions()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (outState != null) {
            outState.putInt(
                PREFS_KEY_LISTPOSITION,
                listView_transactionsList!!.firstVisiblePosition
            )
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            val listpos = savedInstanceState.getInt(PREFS_KEY_LISTPOSITION)
            listView_transactionsList!!.setSelection(listpos)
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = activity!!.menuInflater
        inflater.inflate(R.menu.transaction_list_popup_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterContextMenuInfo
        val listPosition = info.position
        val transaction = transactions[listPosition]
        when (item.itemId) {
            R.id.popup_edit -> editTransaction(transaction, TRANS_TYP_FACT)
            R.id.popup_edit_planned -> editTransaction(transaction, TRANS_TYP_PLANNED)
            R.id.popup_delete -> deleteTransaction(transaction)
        }
        return true
    }

    private fun getTransactions() {
        val databaseAdapter = DatabaseAdapter(context)
        databaseAdapter.open()
        transactions.clear()
        //transactions.addAll(databaseAdapter.getTransactions(getContext(), this.year, this.month, showOnlyPlanned));
        lastEditedId = 0
        lastEditedId = 0
        var lastEditDate: Long = 0
        val dbTransactions =
            databaseAdapter.getTransactions(context, main!!.year, main!!.month, false)
        for (transaction in dbTransactions) {
            if (!showOnlyPlanned || transaction.amount_fact == 0.0) {
                if (main!!.category == null || main!!.category != null && main!!.category == transaction.category) {
                    transactions.add(transaction)
                    if (transaction.amount_fact != 0.0 && transaction.date_edit > lastEditDate) {
                        lastEditDate = transaction.date_edit
                        lastEditedId = transaction.id
                    }
                }
            }
        }

        //
        if (transactions.size == 0 && !showOnlyPlanned) {
            btn_planRegular!!.visibility = View.VISIBLE
        } else {
            btn_planRegular!!.visibility = View.GONE
        }
        transactionAdapter!!.notifyDataSetChanged()
        databaseAdapter.close()
    }

    fun prevMonth() {
        main!!.prevMonth()
        setMonthTextView()
        getTransactions()
    }

    fun nextMonth() {
        main!!.nextMonth()
        setMonthTextView()
        getTransactions()
    }

    private fun setMonthTextView() {
        textView_Month!!.text = String.format(
            Locale.getDefault(), "%d / %s", main!!.year, months[main!!.month]
        )
    }

    fun add(transStat: String?, planned: String?) {
        setNormalStat()
        val intent = Intent(context, TransactionActivity::class.java)
        intent.putExtra(TRANS_STAT, transStat)
        intent.putExtra(TRANS_TYP, planned)
        startActivity(intent)
    }

    private fun setNormalStat() {
        fab_add!!.setImageResource(R.drawable.ic_add_white_24dp)
        fab_add!!.visibility = View.VISIBLE
        fab_add_plus!!.visibility = View.INVISIBLE
        fab_add_minus!!.visibility = View.INVISIBLE
        fab_add_plus_planned!!.visibility = View.INVISIBLE
        fab_add_minus_planned!!.visibility = View.INVISIBLE
    }

    fun changeShowOnlyPlanned(b: Boolean) {
        showOnlyPlanned = b
        getTransactions()
    }

    private fun editTransaction(entry: Transaction?, planned: String) {
        if (entry != null) {
            val intent = Intent(context, TransactionActivity::class.java)
            intent.putExtra(TRANS_ID, entry.id)
            // intent.putExtra("click", 25);
            intent.putExtra(TRANS_TYP, planned)
            startActivity(intent)
        }
    }

    private fun deleteTransaction(entry: Transaction) {
        val databaseAdapter = DatabaseAdapter(context)
        databaseAdapter.open()
        databaseAdapter.deleteTransaction(entry.id)
        databaseAdapter.close()
        getTransactions()
    }

    private fun planRegular() {
        setRegularToPlanned(context, main!!.year, main!!.month)
        getTransactions()
    }

    companion object {
        private const val PREFS_KEY_LISTPOSITION = "LISTPOS"
        var lastEditedId: Long = 0
    }
}