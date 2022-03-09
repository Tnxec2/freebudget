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
import java.util.*

class RegularFragment : Fragment() {
    private var listView_Transactions: ListView? = null
    private var textView_Month: TextView? = null
    private var textView_receipts: TextView? = null
    private var textView_spending: TextView? = null
    private var textView_total: TextView? = null
    private var fab_add: FloatingActionButton? = null
    private var fab_add_plus: FloatingActionButton? = null
    private var fab_add_minus: FloatingActionButton? = null
    private var month = 0
    private var months: Array<String>
    var isMove: Boolean? = null
    var transactionList: MutableList<RegularTransaction> = ArrayList()
    var transactionAdapter: RegularTransactionAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_regular, container, false)
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Setup any handles to view objects here
        listView_Transactions =
            view.findViewById<View>(R.id.listView_regular_transactions) as ListView
        textView_Month = view.findViewById<View>(R.id.textView_Month_Regular) as TextView
        textView_receipts = view.findViewById<View>(R.id.textView_receipts_regular) as TextView
        textView_spending = view.findViewById<View>(R.id.textView_spending_regular) as TextView
        textView_total = view.findViewById<View>(R.id.textView_total_regular) as TextView
        fab_add = view.findViewById<View>(R.id.fab_add_regular) as FloatingActionButton
        fab_add_plus = view.findViewById<View>(R.id.fab_add_plus_regular) as FloatingActionButton
        fab_add_minus = view.findViewById<View>(R.id.fab_add_minus_regular) as FloatingActionButton
        val btn_prevMonth = view.findViewById<View>(R.id.btn_prevMonth) as ImageButton
        val btn_nextMonth = view.findViewById<View>(R.id.btn_nextMonth) as ImageButton
        btn_prevMonth.setOnClickListener { prevMonth() }
        btn_nextMonth.setOnClickListener { nextMonth() }
        val mainLayout = view.findViewById<View>(R.id.mainlayout_regular) as ConstraintLayout
        mainLayout.setOnTouchListener(object : OnSwipeTouchListener(context) {
            override fun onSwipeLeft() {
                nextMonth()
            }

            override fun onSwipeRight() {
                prevMonth()
            }
        })
        months = resources.getStringArray(R.array.months)
        month = 0
        textView_Month!!.text = months[month]
        listView_Transactions!!.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                val entry = transactionAdapter!!.getItem(position)
                if (entry != null) {
                    val intent = Intent(context, RegularTransactionActivity::class.java)
                    intent.putExtra("id", entry.id)
                    intent.putExtra("click", 25)
                    startActivity(intent)
                }
            }

        // set list adapter
        transactionAdapter = RegularTransactionAdapter(
            context,
            R.layout.list_view_item_regular_transaction_item,
            transactionList
        )
        // set adapter
        listView_Transactions!!.adapter = transactionAdapter
        listView_Transactions!!.setOnTouchListener { view, motionEvent ->
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
                fab_add!!.visibility = View.INVISIBLE
                isMove = true
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (!isMove!!) {
                    transStat = "minus"
                    add()
                }
            } else if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                fab_add_plus!!.visibility = View.VISIBLE
                fab_add_minus!!.visibility = View.VISIBLE
                fab_add!!.visibility = View.VISIBLE
            }
            true
        }
        listView_Transactions!!.setOnDragListener { v, event ->
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
        fab_add_plus!!.setOnDragListener { v, event ->
            val action = event.action
            when (action) {
                DragEvent.ACTION_DRAG_STARTED -> {}
                DragEvent.ACTION_DRAG_ENTERED -> {}
                DragEvent.ACTION_DRAG_EXITED -> {}
                DragEvent.ACTION_DROP -> {
                    transStat = "plus"
                    add()
                }
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
                DragEvent.ACTION_DROP -> {
                    transStat = "minus"
                    add()
                }
                DragEvent.ACTION_DRAG_ENDED -> {}
                else -> {}
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)!!.setPosition(MainActivity.INDEX_DRAWER_REGULAR)
        transactions
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (outState != null) {
            outState.putInt(PREFS_KEY_LISTPOSITION, listView_Transactions!!.firstVisiblePosition)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            val listpos = savedInstanceState.getInt(PREFS_KEY_LISTPOSITION)
            listView_Transactions!!.setSelection(listpos)
        }
    }

    fun prevMonth() {
        month = if (month == 0) {
            12
        } else {
            month - 1
        }
        textView_Month!!.text = months[month]
        transactions
    }

    fun nextMonth() {
        month = if (month == 12) {
            0
        } else {
            month + 1
        }
        textView_Month!!.text = months[month]
        transactions
    }

    val transactions: Unit
        get() {
            val databaseAdapter = DatabaseAdapter(context)
            databaseAdapter.open()
            transactionList.clear()
            transactionAdapter!!.clear()
            transactionList.addAll(databaseAdapter.getRegular(month))
            val today = Calendar.getInstance().timeInMillis
            var amount: Double
            var receipts = 0.0
            var spending = 0.0
            var total = 0.0
            for (transaction in transactionList) {
                if (transaction.date_start == 0L && transaction.date_end == 0L
                    ||
                    transaction.date_start > 0 && today >= transaction.date_start
                    ||
                    transaction.date_end > 0 && today <= transaction.date_end
                ) {
                    amount = transaction.amount
                    if (amount > 0) {
                        receipts += amount
                    } else {
                        spending += Math.abs(amount)
                    }
                    total += amount
                }
            }
            textView_spending!!.text = String.format(Locale.getDefault(), "%1$,.2f", spending)
            textView_spending!!.setTextColor(ContextCompat.getColor(context!!, R.color.colorRed))
            textView_receipts!!.text = String.format(Locale.getDefault(), "%1$,.2f", receipts)
            textView_receipts!!.setTextColor(ContextCompat.getColor(context!!, R.color.colorGreen))
            textView_total!!.text = String.format(Locale.getDefault(), "%1$,.2f", total)
            if (total > 0) {
                textView_total!!.setTextColor(ContextCompat.getColor(context!!, R.color.colorGreen))
            } else {
                textView_total!!.setTextColor(ContextCompat.getColor(context!!, R.color.colorRed))
            }
            databaseAdapter.close()
            transactionAdapter!!.notifyDataSetChanged()
        }

    fun add() {
        setNormalStat()
        val intent = Intent(context, RegularTransactionActivity::class.java)
        intent.putExtra(TRANS_STAT, transStat)
        intent.putExtra(RegularTransactionActivity.MONTH, month)
        startActivity(intent)
    }

    private fun setNormalStat() {
        fab_add!!.setImageResource(R.drawable.ic_add_white_24dp)
        fab_add!!.visibility = View.VISIBLE
        fab_add_plus!!.visibility = View.INVISIBLE
        fab_add_minus!!.visibility = View.INVISIBLE
    }

    companion object {
        private const val PREFS_KEY_LISTPOSITION = "LISTPOS"
        var transStat: String? = null
    }
}