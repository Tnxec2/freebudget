package de.kontranik.freebudget.fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.View.DragShadowBuilder
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.kontranik.freebudget.R
import de.kontranik.freebudget.activity.MainActivity
import de.kontranik.freebudget.activity.TransactionActivity
import de.kontranik.freebudget.adapter.CategoryAdapter
import de.kontranik.freebudget.database.DatabaseAdapter
import de.kontranik.freebudget.databinding.FragmentOverviewBinding
import de.kontranik.freebudget.model.Category
import de.kontranik.freebudget.service.Constant
import de.kontranik.freebudget.service.OnSwipeTouchListener
import java.util.*

class OverviewFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentOverviewBinding

    private var main: MainActivity? = null

    // private int year, month;
    private lateinit var months: Array<String>
    private val categoryList: MutableList<Category> = ArrayList()
    private var categoryAdapter: CategoryAdapter? = null
    private var isMove: Boolean? = null
    private var amount_planned = 0.0
    private var amount_fact = 0.0
    private var receipts_planned = 0.0
    private var receipts_fact_planned = 0.0
    private var receipts_fact_unplanned = 0.0
    private var spending_planned = 0.0
    private var spending_fact_planned = 0.0
    private var spending_fact_unplanned = 0.0
    private var total_planned = 0.0
    private var total_fact = 0.0
    private var receipts_planned_rest = 0.0
    private var spending_planned_rest = 0.0
    private var total_diff = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Defines the xml file for the fragment
        clearSummen()
        binding = FragmentOverviewBinding.inflate(inflater, parent, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val wm = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        maxWidth = size.x

        val button_AllTransactions = view.findViewById<Button>(R.id.button_AllTransactions)
        val button_Regular = view.findViewById<Button>(R.id.button_Regular)
        months = resources.getStringArray(R.array.months)
        val date = Calendar.getInstance()
        main = activity as MainActivity?
        setMonthTextView()
        button_AllTransactions.setOnClickListener(this)
        button_Regular.setOnClickListener(this)
        binding.btnPrevMonth!!.setOnClickListener(this)
        binding.btnNextMonth.setOnClickListener(this)
        val mainLayout = view.findViewById<View>(R.id.linearLayout_overview) as LinearLayout
        mainLayout.setOnTouchListener(object : OnSwipeTouchListener(context) {
            override fun onSwipeLeft() {
                nextMonth()
            }

            override fun onSwipeRight() {
                prevMonth()
            }
        })
        binding.listViewCategoryList.setOnDragListener { v, event ->
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
        binding.listViewCategoryList.adapter = categoryAdapter
        binding.listViewCategoryList.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> binding.fabAdd.hide()
                MotionEvent.ACTION_UP -> binding.fabAdd.show()
                MotionEvent.ACTION_CANCEL -> binding.fabAdd.show()
            }
            false
        }
        binding.listViewCategoryList.onItemClickListener =
            OnItemClickListener { parent, view, position, id -> //If you wanna send any data to nextActicity.class you can use
                main!!.category = categoryList[position].name
                main!!.selectItem(MainActivity.INDEX_DRAWER_ALLTRANSACTION)
            }
        binding.fabAdd.setOnTouchListener { view, motionEvent ->
            isMove = false
            if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                val data = ClipData.newPlainText("", "")
                binding.fabAdd.setImageResource(R.drawable.ic_euro_symbol_white_24dp)
                val shadowBuilder = DragShadowBuilder(view)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view.startDragAndDrop(data, shadowBuilder, view, 0)
                } else {
                    view.startDrag(data, shadowBuilder, view, 0)
                }
                binding.fabAddPlus.visibility = View.VISIBLE
                binding.fabAddMinus.visibility = View.VISIBLE
                binding.fabAddPlusPlanned.visibility = View.VISIBLE
                binding.fabAddMinusPlanned.visibility = View.VISIBLE
                binding.fabAdd.visibility = View.INVISIBLE
                isMove = true
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (!isMove!!) {
                    add(Constant.TRANS_STAT_MINUS, Constant.TRANS_TYP_FACT)
                }
            } else if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                binding.fabAddPlus.visibility = View.VISIBLE
                binding.fabAddMinus.visibility = View.VISIBLE
                binding.fabAddPlusPlanned.visibility = View.VISIBLE
                binding.fabAddMinusPlanned.visibility = View.VISIBLE
                binding.fabAdd.visibility = View.VISIBLE
            }
            true
        }
        binding.fabAddPlus.setOnDragListener { v, event ->
            val action = event.action
            when (action) {
                DragEvent.ACTION_DRAG_STARTED -> {}
                DragEvent.ACTION_DRAG_ENTERED -> {}
                DragEvent.ACTION_DRAG_EXITED -> {}
                DragEvent.ACTION_DROP -> add(Constant.TRANS_STAT_PLUS, Constant.TRANS_TYP_FACT)
                DragEvent.ACTION_DRAG_ENDED -> {}
                else -> {}
            }
            true
        }
        binding.fabAddMinus.setOnDragListener { v, event ->
            val action = event.action
            when (action) {
                DragEvent.ACTION_DRAG_STARTED -> {}
                DragEvent.ACTION_DRAG_ENTERED -> {}
                DragEvent.ACTION_DRAG_EXITED -> {}
                DragEvent.ACTION_DROP -> add(Constant.TRANS_STAT_MINUS, Constant.TRANS_TYP_FACT)
                DragEvent.ACTION_DRAG_ENDED -> {}
                else -> {}
            }
            true
        }
        binding.fabAddPlusPlanned.setOnDragListener { v, event ->
            val action = event.action
            when (action) {
                DragEvent.ACTION_DRAG_STARTED -> {}
                DragEvent.ACTION_DRAG_ENTERED -> {}
                DragEvent.ACTION_DRAG_EXITED -> {}
                DragEvent.ACTION_DROP -> add(Constant.TRANS_STAT_PLUS, Constant.TRANS_TYP_PLANNED)
                DragEvent.ACTION_DRAG_ENDED -> {}
                else -> {}
            }
            true
        }
        binding.fabAddMinusPlanned.setOnDragListener { v, event ->
            val action = event.action
            when (action) {
                DragEvent.ACTION_DRAG_STARTED -> {}
                DragEvent.ACTION_DRAG_ENTERED -> {}
                DragEvent.ACTION_DRAG_EXITED -> {}
                DragEvent.ACTION_DROP -> add(Constant.TRANS_STAT_MINUS, Constant.TRANS_TYP_PLANNED)
                DragEvent.ACTION_DRAG_ENDED -> {}
                else -> {}
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        main!!.updatePosition(MainActivity.INDEX_DRAWER_OVERVIEW)
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
            val dbAdapter = DatabaseAdapter(requireContext())
            dbAdapter.open()

            /*
                * damit ListAdapter mitkriegt, dass die Liste geändert wurde,
                * muss diese Liste hier zuerst geputzt werden
                * und danach neue Liste ge-added werden
                */categoryList.clear()
            maxCategoryWeight = 0.0

            // als erstes komplett alle bewegungen für den Monat lesen
            val dbTransactions =
                dbAdapter.getTransactions(requireContext(), main!!.year, main!!.month, false)
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
                        val newCat: Category = Category(0, categoryName, Math.abs(amount_fact))
                        categoryList.add(newCat)
                        if (newCat.weight > maxCategoryWeight) maxCategoryWeight = newCat.weight
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                categoryList.sortWith(Category.CategoryWeightComparator)
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
        binding.textViewMonth.text = String.format(
            Locale.getDefault(), "%d / %s", main!!.year, months[main!!.month]
        )
    }

    private fun add(transStat: String, planned: String) {
        setNormalStat()
        val intent = Intent(context, TransactionActivity::class.java)
        intent.putExtra(Constant.TRANS_STAT, transStat)
        intent.putExtra(Constant.TRANS_TYP, planned)
        startActivity(intent)
    }

    private fun setNormalStat() {
        binding.fabAdd.setImageResource(R.drawable.ic_add_white_24dp)
        binding.fabAdd.visibility = View.VISIBLE
        binding.fabAddPlus.visibility = View.INVISIBLE
        binding.fabAddMinus.visibility = View.INVISIBLE
        binding.fabAddPlusPlanned.visibility = View.INVISIBLE
        binding.fabAddMinusPlanned.visibility = View.INVISIBLE
    }

    private fun setSummen() {
        binding.textViewSpendingPlanned.text =
            String.format(Locale.getDefault(), "%1$,.2f", spending_planned)
        binding.textViewSpendingPlanned.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorRed
            )
        )
        binding.textViewSpendingFactPlanned.text =
            String.format(Locale.getDefault(), "%1$,.2f", spending_fact_planned)
        binding.textViewSpendingFactPlanned.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorRed
            )
        )
        binding.textViewSpendingFactUnplanned.text =
            String.format(Locale.getDefault(), "%1$,.2f", spending_fact_unplanned)
        binding.textViewSpendingFactUnplanned.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorRed
            )
        )
        binding.textViewReceiptsPlanned.text =
            String.format(Locale.getDefault(), "%1$,.2f", receipts_planned)
        binding.textViewReceiptsPlanned.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorGreen
            )
        )
        binding.textViewReceiptsFactPlanned.text =
            String.format(Locale.getDefault(), "%1$,.2f", receipts_fact_planned)
        binding.textViewReceiptsFactPlanned.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorGreen
            )
        )
        binding.textViewReceiptsFactUnplanned.text =
            String.format(Locale.getDefault(), "%1$,.2f", receipts_fact_unplanned)
        binding.textViewReceiptsFactUnplanned.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorGreen
            )
        )
        binding.textViewTotalFact.text = String.format(Locale.getDefault(), "%1$,.2f", total_fact)
        binding.textViewTotalPlanned.text =
            String.format(Locale.getDefault(), "%1$,.2f", total_planned)
        binding.textViewReceiptsPlannedRest.text =
            String.format(Locale.getDefault(), "%1$,.2f", receipts_planned_rest)
        binding.textViewReceiptsPlannedRest!!.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorGreen
            )
        )
        binding.textViewSpendingPlannedRest.text =
            String.format(Locale.getDefault(), "%1$,.2f", spending_planned_rest)
        binding.textViewSpendingPlannedRest.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorRed
            )
        )
        binding.textViewTotalDiff.text =
            String.format(Locale.getDefault(), "%1$,.2f", total_diff)
        if (total_planned > 0) {
            binding.textViewTotalPlanned.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorGreen
                )
            )
        } else {
            binding.textViewTotalPlanned.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorRed
                )
            )
        }
        if (total_fact > 0) {
            binding.textViewTotalFact.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorGreen
                )
            )
        } else {
            binding.textViewTotalFact.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorRed))
        }
        if (total_diff > 0) {
            binding.textViewTotalDiff.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorGreen
                )
            )
        } else {
            binding.textViewTotalDiff.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorRed))
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
        @JvmField
        var maxCategoryWeight = 0.0
        @JvmField
        var maxWidth = 0
    }
}