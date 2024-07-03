package de.kontranik.freebudget.fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.view.View.DragShadowBuilder
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.AdapterView.OnItemClickListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.kontranik.freebudget.R
import de.kontranik.freebudget.activity.MainActivity
import de.kontranik.freebudget.activity.TransactionActivity
import de.kontranik.freebudget.adapter.TransactionSeparatedAdapter
import de.kontranik.freebudget.database.viewmodel.TransactionViewModel
import de.kontranik.freebudget.databinding.FragmentAlltransactionSeparateBinding
import de.kontranik.freebudget.model.Transaction
import de.kontranik.freebudget.service.Constant
import de.kontranik.freebudget.service.OnSwipeTouchListener
import de.kontranik.freebudget.service.PlanRegular.setRegularToPlanned
import java.util.*
import kotlin.math.abs


class AllTransactionSeparatedFragment : Fragment() {
    private lateinit var binding: FragmentAlltransactionSeparateBinding
    private lateinit var mTransactionViewModel: TransactionViewModel

    private var main: MainActivity? = null

    private val transactionsIn: MutableList<Transaction> = ArrayList()
    private val transactionsOut: MutableList<Transaction> = ArrayList()

    private var transactionInAdapter: TransactionSeparatedAdapter? = null
    private var transactionOutAdapter: TransactionSeparatedAdapter? = null

    private lateinit var months: Array<String>

    private var isMove = false
    private var showOnlyPlanned = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAlltransactionSeparateBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Setup any handles to view objects here

        mTransactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        binding.btnPlanRegular.setOnClickListener { planRegular() }
        binding.btnPrevMonth.setOnClickListener { prevMonth() }
        binding.btnNextMonth.setOnClickListener { nextMonth() }
        binding.listViewTransactionsIn.onItemClickListener =
            OnItemClickListener { _, _, position, _ ->
                val entry = transactionInAdapter!!.getItem(position)
                editTransaction(entry, Constant.TRANS_TYP_FACT)
            }
        binding.listViewTransactionsOut.onItemClickListener =
            OnItemClickListener { _, _, position, _ ->
                val entry = transactionOutAdapter!!.getItem(position)
                editTransaction(entry, Constant.TRANS_TYP_FACT)
            }
        // set list adapter
        transactionInAdapter =
            TransactionSeparatedAdapter(requireContext(), transactionsIn)
        transactionOutAdapter =
            TransactionSeparatedAdapter(requireContext(),  transactionsOut)
        // set adapter
        binding.listViewTransactionsIn.adapter = transactionInAdapter
        binding.listViewTransactionsOut.adapter = transactionOutAdapter
        // Register the ListView  for Context menu
        registerForContextMenu(binding.listViewTransactionsIn)
        registerForContextMenu(binding.listViewTransactionsOut)
        months = resources.getStringArray(R.array.months)

        main = activity as MainActivity?
        setMonthTextView()
        binding.textViewSummeIn.text = ""
        binding.textViewSummeOut.text = ""
        binding.textViewSummeDiff.text = ""

        binding.mainlayoutAlltransaction.setOnTouchListener(object : OnSwipeTouchListener(context) {
            override fun onSwipeLeft() {
                nextMonth()
            }

            override fun onSwipeRight() {
                prevMonth()
            }
        })
        binding.mainlayoutAlltransaction.setOnDragListener { v, event ->
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
        binding.mainlayoutAlltransaction.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> binding.fabAdd.hide()
                MotionEvent.ACTION_UP -> binding.fabAdd.show()
                MotionEvent.ACTION_CANCEL -> binding.fabAdd.show()
            }
            false
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
                if (!isMove) {
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
        mTransactionViewModel.dataByYearAndMonth.observe(viewLifecycleOwner) {
            transactionsIn.clear()
            transactionsOut.clear()
            var summeIn: Double = 0.0
            var summeOut: Double = 0.0
            if (it != null) {
                for (transaction in it) {
                    val amount = if (transaction.amountFact != 0.0) transaction.amountFact else transaction.amountPlanned
                    if (amount > 0) {
                        transactionsIn.add(transaction)
                        summeIn+= abs(amount)
                    } else {
                        transactionsOut.add(transaction)
                        summeOut+= abs(amount)
                    }
                }
            }

            if (it.isEmpty() && !showOnlyPlanned) {
                binding.btnPlanRegular.visibility = View.VISIBLE
            } else {
                binding.btnPlanRegular.visibility = View.GONE
            }
            transactionInAdapter!!.notifyDataSetChanged()
            transactionOutAdapter!!.notifyDataSetChanged()

            val summeDiff = summeIn - summeOut
            binding.textViewSummeIn.text = String.format(Locale.getDefault(), "%1$,.2f", summeIn)
            binding.textViewSummeOut.text = String.format(Locale.getDefault(), "%1$,.2f", summeOut)
            binding.textViewSummeDiff.text = String.format(Locale.getDefault(), "%1$,.2f", summeDiff)
            if (summeDiff < 0) {
                binding.textViewSummeDiff.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.colorRed)
                )
            } else {
                binding.textViewSummeDiff.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.colorGreen)
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.all_transaction_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuitem_load_regular -> planRegular()
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        main!!.updatePosition(MainActivity.INDEX_DRAWER_ALLTRANSACTION_SEPARATED)
        getTransactions()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = requireActivity().menuInflater
        inflater.inflate(R.menu.transaction_list_popup_menu, menu)
        if (v.id == R.id.listView_transactions_in) { //For first listview
            inflater.inflate(R.menu.transaction_list_popup_menu, menu)
        }
        if (v.id == R.id.listView_transactions_out) { //For second listview
            inflater.inflate(R.menu.transaction_list_popup_menu, menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterContextMenuInfo
        val listPosition = info.position
        val view = info.targetView

        val transaction = when (view.id) {
            R.id.listView_transactions_in -> transactionsIn[listPosition]
            R.id.listView_transactions_out -> transactionsOut[listPosition]
            else -> return true
        }

        when (item.itemId) {
            R.id.popup_edit -> editTransaction(transaction, Constant.TRANS_TYP_FACT)
            R.id.popup_edit_planned -> editTransaction(transaction, Constant.TRANS_TYP_PLANNED)
            R.id.popup_delete -> deleteTransaction(transaction)
        }
        return true
    }

    private fun getTransactions() {
        mTransactionViewModel.loadTransactions(main!!.year, main!!.month, false)
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
        binding.textViewMonth.text = String.format(
            Locale.getDefault(), "%d / %s", main!!.year, months[main!!.month]
        )
    }

    fun add(transStat: String?, planned: String?) {
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

    fun changeShowOnlyPlanned(b: Boolean) {
        showOnlyPlanned = b
        getTransactions()
    }

    private fun editTransaction(entry: Transaction?, planned: String) {
        if (entry != null) {
            val intent = Intent(context, TransactionActivity::class.java)
            intent.putExtra(Constant.TRANS_ID, entry.id)
            // intent.putExtra("click", 25);
            intent.putExtra(Constant.TRANS_TYP, planned)
            startActivity(intent)
        }
    }

    private fun deleteTransaction(entry: Transaction) {
        entry.id?.let { mTransactionViewModel.delete(it) }
        getTransactions()
    }

    private fun planRegular() {
        setRegularToPlanned(requireContext(), main!!.year, main!!.month)
        getTransactions()
    }

    companion object {
        private const val PREFS_KEY_LISTPOSITION = "LISTPOS"
        @JvmField
        var lastEditedId: Long? = null
    }
}