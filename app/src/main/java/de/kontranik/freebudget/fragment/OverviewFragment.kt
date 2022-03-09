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
import android.content.Context
import android.view.View.DragShadowBuilder
import android.os.Build
import de.kontranik.freebudget.fragment.AllTransactionFragment
import android.view.ContextMenu.ContextMenuInfo
import android.widget.AdapterView.AdapterContextMenuInfo
import de.kontranik.freebudget.database.DatabaseAdapter
import android.content.Intent
import android.graphics.Point
import android.view.*
import android.widget.*
import de.kontranik.freebudget.activity.TransactionActivity
import de.kontranik.freebudget.service.PlanRegular
import de.kontranik.freebudget.adapter.CategoryAdapter
import de.kontranik.freebudget.fragment.OverviewFragment
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import de.kontranik.freebudget.model.RegularTransaction
import de.kontranik.freebudget.adapter.RegularTransactionAdapter
import de.kontranik.freebudget.activity.RegularTransactionActivity
import de.kontranik.freebudget.fragment.RegularFragment
import de.kontranik.freebudget.model.Category
import java.util.*

class OverviewFragment : Fragment(), View.OnClickListener {
    var main: MainActivity? = null
    private var listView_categoryList: ListView? = null
    private var textView_Month: TextView? = null
    private var textView_receipts_planned: TextView? = null
    private var textView_spending_planned: TextView? = null
    private var textView_total_planned: TextView? = null
    private var textView_receipts_fact_planned: TextView? = null
    private var textView_receipts_fact_unplanned: TextView? = null
    private var textView_spending_fact_planned: TextView? = null
    private var textView_spending_fact_unplanned: TextView? = null
    private var textView_total_fact: TextView? = null
    private var textView_receipts_planned_rest: TextView? = null
    private var textView_spending_planned_rest: TextView? = null
    private var textView_total_diff: TextView? = null
    private var btn_prevMonth: ImageButton? = null
    private var btn_nextMonth: ImageButton? = null
    private var fab_add: FloatingActionButton? = null
    private var fab_add_plus: FloatingActionButton? = null
    private var fab_add_minus: FloatingActionButton? = null
    private var fab_add_plus_planned: FloatingActionButton? = null
    private var fab_add_minus_planned: FloatingActionButton? = null

    // private int year, month;
    private var months: Array<String>
    private val categoryList: MutableList<Category> = ArrayList()
    var categoryAdapter: CategoryAdapter? = null
    var isMove: Boolean? = null
    var amount_planned = 0.0
    var amount_fact = 0.0
    var receipts_planned = 0.0
    var receipts_fact_planned = 0.0
    var receipts_fact_unplanned = 0.0
    var spending_planned = 0.0
    var spending_fact_planned = 0.0
    var spending_fact_unplanned = 0.0
    var total_planned = 0.0
    var total_fact = 0.0
    var receipts_planned_rest = 0.0
    var spending_planned_rest = 0.0
    var total_diff = 0.0
    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Defines the xml file for the fragment
        clearSummen()
        return inflater.inflate(R.layout.fragment_overview, parent, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val wm = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        maxWidth = size.x
        listView_categoryList = view.findViewById<View>(R.id.listView_categoryList) as ListView
        textView_Month = view.findViewById<View>(R.id.textView_Month) as TextView
        textView_receipts_planned =
            view.findViewById<View>(R.id.textView_receipts_planned) as TextView
        textView_receipts_fact_planned =
            view.findViewById<View>(R.id.textView_receipts_fact_planned) as TextView
        textView_receipts_fact_unplanned =
            view.findViewById<View>(R.id.textView_receipts_fact_unplanned) as TextView
        textView_spending_planned =
            view.findViewById<View>(R.id.textView_spending_planned) as TextView
        textView_spending_fact_planned =
            view.findViewById<View>(R.id.textView_spending_fact_planned) as TextView
        textView_spending_fact_unplanned =
            view.findViewById<View>(R.id.textView_spending_fact_unplanned) as TextView
        textView_total_planned = view.findViewById<View>(R.id.textView_total_planned) as TextView
        textView_total_fact = view.findViewById<View>(R.id.textView_total_fact) as TextView
        textView_receipts_planned_rest =
            view.findViewById<View>(R.id.textView_receipts_planned_rest) as TextView
        textView_spending_planned_rest =
            view.findViewById<View>(R.id.textView_spending_planned_rest) as TextView
        textView_total_diff = view.findViewById<View>(R.id.textView_total_diff) as TextView
        btn_prevMonth = view.findViewById<View>(R.id.btn_prevMonth) as ImageButton
        btn_nextMonth = view.findViewById<View>(R.id.btn_nextMonth) as ImageButton
        fab_add = view.findViewById<View>(R.id.fab_add) as FloatingActionButton
        fab_add_plus = view.findViewById<View>(R.id.fab_add_plus) as FloatingActionButton
        fab_add_minus = view.findViewById<View>(R.id.fab_add_minus) as FloatingActionButton
        fab_add_plus_planned =
            view.findViewById<View>(R.id.fab_add_plus_planned) as FloatingActionButton
        fab_add_minus_planned =
            view.findViewById<View>(R.id.fab_add_minus_planned) as FloatingActionButton
        val button_AllTransactions = view.findViewById<Button>(R.id.button_AllTransactions)
        val button_Regular = view.findViewById<Button>(R.id.button_Regular)
        months = resources.getStringArray(R.array.months)
        val date = Calendar.getInstance()
        main = activity as MainActivity?
        setMonthTextView()
        button_AllTransactions.setOnClickListener(this)
        button_Regular.setOnClickListener(this)
        btn_prevMonth!!.setOnClickListener(this)
        btn_nextMonth!!.setOnClickListener(this)
        val mainLayout = view.findViewById<View>(R.id.linearLayout_overview) as LinearLayout
        mainLayout.setOnTouchListener(object : OnSwipeTouchListener(context) {
            override fun onSwipeLeft() {
                nextMonth()
            }

            override fun onSwipeRight() {
                prevMonth()
            }
        })
        listView_categoryList!!.setOnDragListener { v, event ->
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
        categoryAdapter =
            CategoryAdapter(view.context, R.layout.list_view_item_categorygraph, categoryList)
        listView_categoryList!!.adapter = categoryAdapter
        listView_categoryList!!.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> fab_add!!.hide()
                MotionEvent.ACTION_UP -> fab_add!!.show()
                MotionEvent.ACTION_CANCEL -> fab_add!!.show()
            }
            false
        }
        listView_categoryList!!.onItemClickListener =
            OnItemClickListener { parent, view, position, id -> //If you wanna send any data to nextActicity.class you can use
                main!!.category = categoryList[position].name
                main!!.selectItem(MainActivity.INDEX_DRAWER_ALLTRANSACTION)
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
                if (!isMove!!) {
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

    override fun onResume() {
        super.onResume()
        main!!.setPosition(MainActivity.INDEX_DRAWER_OVERVIEW)
        transactions
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_prevMonth -> prevMonth()
            R.id.btn_nextMonth -> nextMonth()
            R.id.button_AllTransactions -> {
                main!!.category = null
                main!!.selectItem(MainActivity.INDEX_DRAWER_ALLTRANSACTION)
            }
            R.id.button_Regular -> {
                main!!.category = null
                main!!.selectItem(MainActivity.INDEX_DRAWER_REGULAR)
            }
            else -> {}
        }
    }
    /*
         * damit ListAdapter mitkriegt, dass die Liste geändert wurde,
         * muss diese Liste hier zuerst geputzt werden
         * und danach neue Liste ge-added werden
         */

    // als erstes komplett alle bewegungen für den Monat lesen
    val transactions: Unit
        // und den Adapter aktualisieren
        get() {
            val dbAdapter = DatabaseAdapter(context)
            dbAdapter.open()

            /*
                * damit ListAdapter mitkriegt, dass die Liste geändert wurde,
                * muss diese Liste hier zuerst geputzt werden
                * und danach neue Liste ge-added werden
                */categoryList.clear()
            maxCategoryWeight = 0.0

            // als erstes komplett alle bewegungen für den Monat lesen
            val dbTransactions =
                dbAdapter.getTransactions(context, main!!.year, main!!.month, false)
            clearSummen()
            for (transaction in dbTransactions) {
                amount_planned = transaction.amount_planned
                if (amount_planned > 0) receipts_planned += amount_planned else spending_planned += Math.abs(
                    amount_planned
                )
                total_planned = total_planned + amount_planned
                amount_fact = transaction.amount_fact
                if (amount_fact > 0) {
                    if (amount_planned > 0) receipts_fact_planned += amount_fact else receipts_fact_unplanned += amount_fact
                } else if (amount_fact < 0) {
                    if (amount_planned < 0) spending_fact_planned += Math.abs(amount_fact) else spending_fact_unplanned += Math.abs(
                        amount_fact
                    )
                } else if (amount_fact == 0.0) {
                    if (amount_planned > 0) receipts_planned_rest += amount_planned
                    if (amount_planned < 0) spending_planned_rest += Math.abs(amount_planned)
                }
                total_fact += amount_fact
                var categoryName = transaction.category.trim { it <= ' ' }
                if (categoryName.length == 0) categoryName =
                    resources.getString(R.string.activity_transaction_not_define)
                if (amount_fact < 0) {
                    var ix = false
                    for (i in categoryList.indices) {
                        val category = categoryList[i]
                        if (categoryName == category.name) {
                            ix = true
                            category.weight = category.weight + Math.abs(amount_fact)
                            if (category.weight > maxCategoryWeight) maxCategoryWeight =
                                category.weight
                            break
                        }
                    }
                    if (!ix) {
                        var newCat: Category
                        newCat = Category(0, categoryName, Math.abs(amount_fact))
                        categoryList.add(newCat)
                        if (newCat.weight > maxCategoryWeight) maxCategoryWeight = newCat.weight
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                categoryList.sort(Category.CategoryWeightComparator)
            }
            total_diff = total_fact + receipts_planned_rest - spending_planned_rest
            setSummen()
            dbAdapter.close()

            // und den Adapter aktualisieren
            categoryAdapter!!.notifyDataSetChanged()
        }

    fun prevMonth() {
        main!!.prevMonth()
        setMonthTextView()
        transactions
    }

    fun nextMonth() {
        main!!.nextMonth()
        setMonthTextView()
        transactions
    }

    private fun setMonthTextView() {
        textView_Month!!.text = String.format(
            Locale.getDefault(), "%d / %s", main!!.year, months[main!!.month]
        )
    }

    private fun add(transStat: String, planned: String) {
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

    private fun setSummen() {
        textView_spending_planned!!.text =
            String.format(Locale.getDefault(), "%1$,.2f", spending_planned)
        textView_spending_planned!!.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.colorRed
            )
        )
        textView_spending_fact_planned!!.text =
            String.format(Locale.getDefault(), "%1$,.2f", spending_fact_planned)
        textView_spending_fact_planned!!.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.colorRed
            )
        )
        textView_spending_fact_unplanned!!.text =
            String.format(Locale.getDefault(), "%1$,.2f", spending_fact_unplanned)
        textView_spending_fact_unplanned!!.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.colorRed
            )
        )
        textView_receipts_planned!!.text =
            String.format(Locale.getDefault(), "%1$,.2f", receipts_planned)
        textView_receipts_planned!!.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.colorGreen
            )
        )
        textView_receipts_fact_planned!!.text =
            String.format(Locale.getDefault(), "%1$,.2f", receipts_fact_planned)
        textView_receipts_fact_planned!!.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.colorGreen
            )
        )
        textView_receipts_fact_unplanned!!.text =
            String.format(Locale.getDefault(), "%1$,.2f", receipts_fact_unplanned)
        textView_receipts_fact_unplanned!!.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.colorGreen
            )
        )
        textView_total_fact!!.text = String.format(Locale.getDefault(), "%1$,.2f", total_fact)
        textView_total_planned!!.text =
            String.format(Locale.getDefault(), "%1$,.2f", total_planned)
        textView_receipts_planned_rest!!.text =
            String.format(Locale.getDefault(), "%1$,.2f", receipts_planned_rest)
        textView_receipts_planned_rest!!.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.colorGreen
            )
        )
        textView_spending_planned_rest!!.text =
            String.format(Locale.getDefault(), "%1$,.2f", spending_planned_rest)
        textView_spending_planned_rest!!.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.colorRed
            )
        )
        textView_total_diff!!.text =
            String.format(Locale.getDefault(), "%1$,.2f", total_diff)
        if (total_planned > 0) {
            textView_total_planned!!.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.colorGreen
                )
            )
        } else {
            textView_total_planned!!.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.colorRed
                )
            )
        }
        if (total_fact > 0) {
            textView_total_fact!!.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.colorGreen
                )
            )
        } else {
            textView_total_fact!!.setTextColor(ContextCompat.getColor(context!!, R.color.colorRed))
        }
        if (total_diff > 0) {
            textView_total_diff!!.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.colorGreen
                )
            )
        } else {
            textView_total_diff!!.setTextColor(ContextCompat.getColor(context!!, R.color.colorRed))
        }
    }

    private fun clearSummen() {
        amount_planned = 0.0
        amount_fact = 0.0
        total_planned = 0.0
        total_fact = 0.0
        receipts_fact_planned = 0.0
        receipts_fact_unplanned = 0.0
        receipts_planned = 0.0
        spending_fact_planned = 0.0
        spending_fact_unplanned = 0.0
        spending_planned = 0.0
        receipts_planned_rest = 0.0
        spending_planned_rest = 0.0
        total_diff = 0.0
    }

    companion object {
        var maxCategoryWeight = 0.0
        var maxWidth = 0
    }
}