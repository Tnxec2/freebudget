package de.kontranik.freebudget.fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.View.DragShadowBuilder
import android.widget.AdapterView.OnItemClickListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.kontranik.freebudget.R
import de.kontranik.freebudget.activity.MainActivity
import de.kontranik.freebudget.activity.RegularTransactionActivity
import de.kontranik.freebudget.adapter.RegularTransactionAdapter
import de.kontranik.freebudget.database.viewmodel.RegularTransactionViewModel

import de.kontranik.freebudget.databinding.FragmentRegularBinding
import de.kontranik.freebudget.model.RegularTransaction
import de.kontranik.freebudget.service.Constant
import de.kontranik.freebudget.service.OnSwipeTouchListener
import java.util.*

class RegularFragment : Fragment() {
    private lateinit var binding: FragmentRegularBinding
    private lateinit var mRegularTransactionViewModel: RegularTransactionViewModel

    private var month = 0
    private lateinit var months: Array<String>
    var isMove: Boolean? = null
    var transactionList: MutableList<RegularTransaction> = ArrayList()
    var transactionAdapter: RegularTransactionAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRegularBinding.inflate(inflater, container, false)
        return binding.root
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Setup any handles to view objects here
        mRegularTransactionViewModel = ViewModelProvider(this)[RegularTransactionViewModel::class.java]

        binding.btnPrevMonth.setOnClickListener { prevMonth() }
        binding.btnNextMonth.setOnClickListener { nextMonth() }
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
        binding.textViewMonthRegular.text = months[month]
        binding.listViewRegularTransactions.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                val entry = transactionAdapter!!.getItem(position)
                if (entry != null) {
                    val intent = Intent(context, RegularTransactionActivity::class.java)
                    intent.putExtra(Constant.TRANS_ID, entry.id)
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
        binding.listViewRegularTransactions.adapter = transactionAdapter
        binding.listViewRegularTransactions.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> binding.fabAddRegular.hide()
                MotionEvent.ACTION_UP -> binding.fabAddRegular.show()
                MotionEvent.ACTION_CANCEL -> binding.fabAddRegular.show()
            }
            false
        }
        binding.fabAddRegular.setOnTouchListener { view, motionEvent ->
            isMove = false
            if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                val data = ClipData.newPlainText("", "")
                binding.fabAddRegular.setImageResource(R.drawable.ic_baseline_euro_symbol_24)
                val shadowBuilder = DragShadowBuilder(view)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view.startDragAndDrop(data, shadowBuilder, view, 0)
                } else {
                    view.startDrag(data, shadowBuilder, view, 0)
                }
                binding.fabAddPlusRegular.visibility = View.VISIBLE
                binding.fabAddMinusRegular.visibility = View.VISIBLE
                binding.fabAddRegular.visibility = View.INVISIBLE
                isMove = true
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (!isMove!!) {
                    transStat = "minus"
                    add()
                }
            } else if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                binding.fabAddPlusRegular.visibility = View.VISIBLE
                binding.fabAddMinusRegular.visibility = View.VISIBLE
                binding.fabAddRegular.visibility = View.VISIBLE
            }
            true
        }
        binding.listViewRegularTransactions.setOnDragListener { v, event ->
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
        binding.fabAddPlusRegular.setOnDragListener { v, event ->
            when (event.action) {
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
        binding.fabAddMinusRegular.setOnDragListener { v, event ->
            when (event.action) {
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

        mRegularTransactionViewModel.regularTransactionByMonth.observe(viewLifecycleOwner) {
            transactionList.clear()
            transactionAdapter!!.clear()
            transactionList.addAll(it)
            val today = Calendar.getInstance().timeInMillis
            var amount: Double
            var receipts = 0.0
            var spending = 0.0
            var total = 0.0
            for (transaction in transactionList) {
                if (transaction.dateStart == 0L && transaction.dateEnd == 0L
                    ||
                    transaction.dateStart != null && today >= transaction.dateStart!!
                    ||
                    transaction.dateEnd != null && today <= transaction.dateEnd!!
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
            binding.textViewSpendingRegular.text =
                String.format(Locale.getDefault(), "%1$,.2f", spending)
            binding.textViewSpendingRegular.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorRed
                )
            )
            binding.textViewReceiptsRegular.text =
                String.format(Locale.getDefault(), "%1$,.2f", receipts)
            binding.textViewReceiptsRegular.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorGreen
                )
            )
            binding.textViewTotalRegular.text = String.format(Locale.getDefault(), "%1$,.2f", total)
            if (total > 0) {
                binding.textViewTotalRegular.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorGreen
                    )
                )
            } else {
                binding.textViewTotalRegular.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorRed
                    )
                )
            }
            transactionAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)!!.updatePosition(MainActivity.INDEX_DRAWER_REGULAR)
        mRegularTransactionViewModel.loadRegularTransactionsByMonth(month)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(PREFS_KEY_LISTPOSITION, binding.listViewRegularTransactions.firstVisiblePosition)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            val listpos = savedInstanceState.getInt(PREFS_KEY_LISTPOSITION)
            binding.listViewRegularTransactions.setSelection(listpos)
        }
    }

    fun prevMonth() {
        month = if (month == 0) {
            12
        } else {
            month - 1
        }
        binding.textViewMonthRegular.text = months[month]
        mRegularTransactionViewModel.loadRegularTransactionsByMonth(month)
    }

    fun nextMonth() {
        month = if (month == 12) {
            0
        } else {
            month + 1
        }
        binding.textViewMonthRegular.text = months[month]
        mRegularTransactionViewModel.loadRegularTransactionsByMonth(month)
    }



    fun add() {
        setNormalStat()
        val intent = Intent(context, RegularTransactionActivity::class.java)
        intent.putExtra(Constant.TRANS_STAT, transStat)
        intent.putExtra(RegularTransactionActivity.MONTH, month)
        startActivity(intent)
    }

    private fun setNormalStat() {
        binding.fabAddRegular.setImageResource(R.drawable.ic_baseline_add_24)
        binding.fabAddRegular.visibility = View.VISIBLE
        binding.fabAddPlusRegular.visibility = View.INVISIBLE
        binding.fabAddMinusRegular.visibility = View.INVISIBLE
    }

    companion object {
        private const val PREFS_KEY_LISTPOSITION = "LISTPOS"
        var transStat: String? = null
    }
}