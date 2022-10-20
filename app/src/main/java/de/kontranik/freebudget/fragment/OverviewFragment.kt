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
import androidx.lifecycle.ViewModelProvider
import de.kontranik.freebudget.R
import de.kontranik.freebudget.activity.MainActivity
import de.kontranik.freebudget.activity.TransactionActivity
import de.kontranik.freebudget.adapter.CategoryAdapter
import de.kontranik.freebudget.database.viewmodel.TransactionViewModel
import de.kontranik.freebudget.databinding.FragmentOverviewBinding
import de.kontranik.freebudget.model.Category
import de.kontranik.freebudget.model.Transaction
import de.kontranik.freebudget.service.Constant
import de.kontranik.freebudget.service.OnSwipeTouchListener
import java.util.*
import kotlin.math.abs

class OverviewFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentOverviewBinding
    private lateinit var mTransactionViewModel: TransactionViewModel

    private var main: MainActivity? = null

    // private int year, month;
    private lateinit var months: Array<String>
    private val categoryList: MutableList<Category> = ArrayList()
    private var categoryAdapter: CategoryAdapter? = null
    private var isMove: Boolean? = null
    private var amountPlanned = 0.0
    private var amountFact = 0.0
    private var receiptsPlanned = 0.0
    private var receiptsFactPlanned = 0.0
    private var receiptsFactUnplanned = 0.0
    private var spendingPlanned = 0.0
    private var spendingFactPlanned = 0.0
    private var spendingFactUnplanned = 0.0
    private var totalPlanned = 0.0
    private var totalFact = 0.0
    private var receiptsPlannedRest = 0.0
    private var spendingPlannedRest = 0.0
    private var totalDiff = 0.0

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

        mTransactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]


        val wm = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        maxWidth = size.x



        val buttonAlltransactions = view.findViewById<Button>(R.id.button_AllTransactions)
        val buttonRegular = view.findViewById<Button>(R.id.button_Regular)
        months = resources.getStringArray(R.array.months)

        main = activity as MainActivity?
        setMonthTextView()
        buttonAlltransactions.setOnClickListener(this)
        buttonRegular.setOnClickListener(this)
        binding.btnPrevMonth.setOnClickListener(this)
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
        binding.listViewCategoryList.setOnDragListener { _, event ->
            when (event.action) {
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
        binding.listViewCategoryList.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> binding.fabAdd.hide()
                MotionEvent.ACTION_UP -> binding.fabAdd.show()
                MotionEvent.ACTION_CANCEL -> binding.fabAdd.show()
            }
            false
        }
        binding.listViewCategoryList.onItemClickListener =
            OnItemClickListener { _, _, position, _ -> //If you wanna send any data to nextActicity.class you can use
                main!!.category = categoryList[position].name
                main!!.selectItem(MainActivity.INDEX_DRAWER_ALLTRANSACTION)
            }
        binding.fabAdd.setOnTouchListener { v, motionEvent ->
            isMove = false
            if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                val data = ClipData.newPlainText("", "")
                binding.fabAdd.setImageResource(R.drawable.ic_baseline_euro_symbol_24)
                val shadowBuilder = DragShadowBuilder(v)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    v.startDragAndDrop(data, shadowBuilder, v, 0)
                } else {
                    v.startDrag(data, shadowBuilder, v, 0)
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
        binding.fabAddPlus.setOnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {}
                DragEvent.ACTION_DRAG_ENTERED -> {}
                DragEvent.ACTION_DRAG_EXITED -> {}
                DragEvent.ACTION_DROP -> add(Constant.TRANS_STAT_PLUS, Constant.TRANS_TYP_FACT)
                DragEvent.ACTION_DRAG_ENDED -> {}
                else -> {}
            }
            true
        }
        binding.fabAddMinus.setOnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {}
                DragEvent.ACTION_DRAG_ENTERED -> {}
                DragEvent.ACTION_DRAG_EXITED -> {}
                DragEvent.ACTION_DROP -> add(Constant.TRANS_STAT_MINUS, Constant.TRANS_TYP_FACT)
                DragEvent.ACTION_DRAG_ENDED -> {}
                else -> {}
            }
            true
        }
        binding.fabAddPlusPlanned.setOnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {}
                DragEvent.ACTION_DRAG_ENTERED -> {}
                DragEvent.ACTION_DRAG_EXITED -> {}
                DragEvent.ACTION_DROP -> add(Constant.TRANS_STAT_PLUS, Constant.TRANS_TYP_PLANNED)
                DragEvent.ACTION_DRAG_ENDED -> {}
                else -> {}
            }
            true
        }
        binding.fabAddMinusPlanned.setOnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {}
                DragEvent.ACTION_DRAG_ENTERED -> {}
                DragEvent.ACTION_DRAG_EXITED -> {}
                DragEvent.ACTION_DROP -> add(Constant.TRANS_STAT_MINUS, Constant.TRANS_TYP_PLANNED)
                DragEvent.ACTION_DRAG_ENDED -> {}
                else -> {}
            }
            true
        }

        mTransactionViewModel.dataByYearAndMonth.observe(viewLifecycleOwner) {
            categoryList.clear()
            maxCategoryWeight = 0.0

            // als erstes komplett alle bewegungen für den Monat lesen
            clearSummen()

            buildSumAndCategoryList(it)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                categoryList.sortWith(Category.CategoryWeightComparator)
            }
            totalDiff = totalFact + receiptsPlannedRest - spendingPlannedRest
            setSummen()
            // und den Adapter aktualisieren
            categoryAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        main!!.updatePosition(MainActivity.INDEX_DRAWER_OVERVIEW)
        mTransactionViewModel.loadTransactions(main!!.year, main!!.month, false)
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


    fun prevMonth() {
        main!!.prevMonth()
        setMonthTextView()
        mTransactionViewModel.loadTransactions(main!!.year, main!!.month, false)
    }

    fun nextMonth() {
        main!!.nextMonth()
        setMonthTextView()
        mTransactionViewModel.loadTransactions(main!!.year, main!!.month, false)
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
        binding.fabAdd.setImageResource(R.drawable.ic_baseline_add_24)
        binding.fabAdd.visibility = View.VISIBLE
        binding.fabAddPlus.visibility = View.INVISIBLE
        binding.fabAddMinus.visibility = View.INVISIBLE
        binding.fabAddPlusPlanned.visibility = View.INVISIBLE
        binding.fabAddMinusPlanned.visibility = View.INVISIBLE
    }

    private fun buildSumAndCategoryList(list: List<Transaction>?) {
        if (list == null) return
        for (transaction in list) {
            amountPlanned = transaction.amountPlanned
            if (amountPlanned > 0) receiptsPlanned += amountPlanned else spendingPlanned += abs(
                amountPlanned
            )
            totalPlanned += amountPlanned
            amountFact = transaction.amountFact
            if (amountFact > 0) {
                if (amountPlanned > 0) receiptsFactPlanned += amountFact else receiptsFactUnplanned += amountFact
            } else if (amountFact < 0) {
                if (amountPlanned < 0) spendingFactPlanned += abs(amountFact) else spendingFactUnplanned += abs(
                    amountFact
                )
            } else if (amountFact == 0.0) {
                if (amountPlanned > 0) receiptsPlannedRest += amountPlanned
                if (amountPlanned < 0) spendingPlannedRest += abs(amountPlanned)
            }
            totalFact += amountFact
            var categoryName = transaction.category.trim()
            if (categoryName.isEmpty()) categoryName =
                resources.getString(R.string.activity_transaction_not_define)
            if (amountFact < 0) {
                var ix = false
                for (i in categoryList.indices) {
                    val category = categoryList[i]
                    if (categoryName == category.name) {
                        ix = true
                        category.weight = category.weight + abs(amountFact)
                        if (category.weight > maxCategoryWeight) maxCategoryWeight =
                            category.weight
                        break
                    }
                }
                if (!ix) {
                    val newCat = Category(0, categoryName, abs(amountFact))
                    categoryList.add(newCat)
                    if (newCat.weight > maxCategoryWeight) maxCategoryWeight = newCat.weight
                }
            }
        }
    }

    private fun setSummen() {
        binding.textViewSpendingPlanned.text =
            String.format(Locale.getDefault(), "%1$,.2f", spendingPlanned)
        binding.textViewSpendingPlanned.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorRed
            )
        )
        binding.textViewSpendingFactPlanned.text =
            String.format(Locale.getDefault(), "%1$,.2f", spendingFactPlanned)
        binding.textViewSpendingFactPlanned.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorRed
            )
        )
        binding.textViewSpendingFactUnplanned.text =
            String.format(Locale.getDefault(), "%1$,.2f", spendingFactUnplanned)
        binding.textViewSpendingFactUnplanned.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorRed
            )
        )
        binding.textViewReceiptsPlanned.text =
            String.format(Locale.getDefault(), "%1$,.2f", receiptsPlanned)
        binding.textViewReceiptsPlanned.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorGreen
            )
        )
        binding.textViewReceiptsFactPlanned.text =
            String.format(Locale.getDefault(), "%1$,.2f", receiptsFactPlanned)
        binding.textViewReceiptsFactPlanned.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorGreen
            )
        )
        binding.textViewReceiptsFactUnplanned.text =
            String.format(Locale.getDefault(), "%1$,.2f", receiptsFactUnplanned)
        binding.textViewReceiptsFactUnplanned.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorGreen
            )
        )
        binding.textViewTotalFact.text = String.format(Locale.getDefault(), "%1$,.2f", totalFact)
        binding.textViewTotalPlanned.text =
            String.format(Locale.getDefault(), "%1$,.2f", totalPlanned)
        binding.textViewReceiptsPlannedRest.text =
            String.format(Locale.getDefault(), "%1$,.2f", receiptsPlannedRest)
        binding.textViewReceiptsPlannedRest.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorGreen
            )
        )
        binding.textViewSpendingPlannedRest.text =
            String.format(Locale.getDefault(), "%1$,.2f", spendingPlannedRest)
        binding.textViewSpendingPlannedRest.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorRed
            )
        )
        binding.textViewTotalDiff.text =
            String.format(Locale.getDefault(), "%1$,.2f", totalDiff)
        if (totalPlanned > 0) {
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
        if (totalFact > 0) {
            binding.textViewTotalFact.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorGreen
                )
            )
        } else {
            binding.textViewTotalFact.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorRed))
        }
        if (totalDiff > 0) {
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
        amountPlanned = 0.0
        amountFact = 0.0
        totalPlanned = 0.0
        totalFact = 0.0
        receiptsFactPlanned = 0.0
        receiptsFactUnplanned = 0.0
        receiptsPlanned = 0.0
        spendingFactPlanned = 0.0
        spendingFactUnplanned = 0.0
        spendingPlanned = 0.0
        receiptsPlannedRest = 0.0
        spendingPlannedRest = 0.0
        totalDiff = 0.0
    }

    companion object {
        @JvmField
        var maxCategoryWeight = 0.0
        @JvmField
        var maxWidth = 0
    }
}