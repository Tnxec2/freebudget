package de.kontranik.freebudget.fragment

import de.kontranik.freebudget.service.PlanRegular.setRegularToPlanned
import de.kontranik.freebudget.activity.MainActivity
import android.os.Bundle
import de.kontranik.freebudget.R
import android.annotation.SuppressLint
import de.kontranik.freebudget.service.OnSwipeTouchListener
import android.content.ClipData
import android.content.Context
import android.view.View.DragShadowBuilder
import android.os.Build
import android.view.ContextMenu.ContextMenuInfo
import android.widget.AdapterView.AdapterContextMenuInfo
import android.content.Intent
import android.view.*
import androidx.compose.runtime.livedata.observeAsState
import de.kontranik.freebudget.activity.TransactionActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import de.kontranik.freebudget.config.Config
import de.kontranik.freebudget.database.viewmodel.TransactionViewModel
import de.kontranik.freebudget.databinding.FragmentAlltransactionBinding
import de.kontranik.freebudget.model.Transaction
import de.kontranik.freebudget.service.Constant
import de.kontranik.freebudget.ui.components.AllTransactionList
import de.kontranik.freebudget.ui.components.AllTransactionScreen
import java.util.*

class AllTransactionFragment : Fragment() {
    private lateinit var binding: FragmentAlltransactionBinding
    private lateinit var mTransactionViewModel: TransactionViewModel

    private lateinit var main: MainActivity

    private val transactions: MutableList<Transaction> = ArrayList()

    private var isMove = false
    private var showOnlyPlanned = false

    private var lastEditedId = MutableLiveData<Long?>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAlltransactionBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Setup any handles to view objects here

        main = requireActivity() as MainActivity

        mTransactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        val settings = requireContext().getSharedPreferences(Config.PREFS_FILE, Context.MODE_PRIVATE)
        val markLastEdited = settings.getBoolean(Config.PREF_MARK_LAST_EDITED, false)

        binding.composeTransactionScreen.setContent {
            AllTransactionScreen(
                mainActivity = requireActivity() as MainActivity,
                transactions = mTransactionViewModel.dataByYearAndMonth.observeAsState(listOf()),
                markLastEdited = markLastEdited,
                lastEditedId = lastEditedId.observeAsState(),
                onClickTransaction = { pos, entry -> editTransaction(entry, Constant.TRANS_TYP_FACT)},
                onPlanRegularClick = { planRegular() },
                onPrevMonth = { prevMonth() },
                onNextMonth = { nextMonth() },
                onAdd = { add(Constant.TRANS_STAT_MINUS, Constant.TRANS_TYP_FACT) }
            )
        }

        // Register the ListView  for Context menu
        registerForContextMenu(binding.composeTransactionScreen)

        binding.mainlayoutAlltransaction.setOnTouchListener(object : OnSwipeTouchListener(context) {
            override fun onSwipeLeft() {
                nextMonth()
            }

            override fun onSwipeRight() {
                prevMonth()
            }
        })
        binding.composeTransactionScreen.setOnDragListener { v, event ->
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
        binding.composeTransactionScreen.setOnTouchListener { _, motionEvent ->
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
            transactions.clear()
            lastEditedId.value = 0L
            var lastEditDate: Long = 0
            if (it != null) {
                for (transaction in it) {
                    if (!showOnlyPlanned || transaction.amountFact == 0.0) {
                        transactions.add(transaction)
                        if (transaction.amountFact != 0.0 && transaction.dateEdit > lastEditDate) {
                            lastEditDate = transaction.dateEdit
                            lastEditedId.value = transaction.id
                        }
                    }
                }
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
        main.updatePosition(MainActivity.INDEX_DRAWER_ALLTRANSACTION)
        getTransactions()
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putInt(
//            PREFS_KEY_LISTPOSITION,
//            binding.listViewTransactions.firstVisiblePosition
//        )
//    }
//
//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//        if (savedInstanceState != null) {
//            val listpos = savedInstanceState.getInt(PREFS_KEY_LISTPOSITION)
//            binding.listViewTransactions.setSelection(listpos)
//        }
//    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = requireActivity().menuInflater
        inflater.inflate(R.menu.transaction_list_popup_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterContextMenuInfo
        val listPosition = info.position
        val transaction = transactions[listPosition]
        when (item.itemId) {
            R.id.popup_edit -> editTransaction(transaction, Constant.TRANS_TYP_FACT)
            R.id.popup_edit_planned -> editTransaction(transaction, Constant.TRANS_TYP_PLANNED)
            R.id.popup_delete -> deleteTransaction(transaction)
        }
        return true
    }

    private fun getTransactions() {
        mTransactionViewModel.loadTransactions(main.year, main.month, main.category, showOnlyPlanned)
    }

    fun prevMonth() {
        main.prevMonth()
        getTransactions()
    }

    fun nextMonth() {
        main.nextMonth()
        getTransactions()
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
        setRegularToPlanned(requireContext(), main.year, main.month)
        getTransactions()
    }

    companion object {
        private const val PREFS_KEY_LISTPOSITION = "LISTPOS"
    }
}