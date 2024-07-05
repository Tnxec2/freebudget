package de.kontranik.freebudget.fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.DragShadowBuilder
import android.view.ViewGroup
import androidx.compose.runtime.livedata.observeAsState
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.kontranik.freebudget.R
import de.kontranik.freebudget.activity.MainActivity
import de.kontranik.freebudget.activity.RegularTransactionActivity
import de.kontranik.freebudget.database.viewmodel.RegularTransactionViewModel
import de.kontranik.freebudget.databinding.FragmentRegularBinding
import de.kontranik.freebudget.service.Constant
import de.kontranik.freebudget.service.OnSwipeTouchListener
import de.kontranik.freebudget.ui.components.RegularTransactionScreen
import de.kontranik.freebudget.ui.theme.AppTheme

class RegularFragment : Fragment() {
    private lateinit var binding: FragmentRegularBinding
    private lateinit var mRegularTransactionViewModel: RegularTransactionViewModel

    private var isMove: Boolean? = null

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

        binding.composeRegularTransactionScreen.setOnTouchListener(object : OnSwipeTouchListener(context) {
            override fun onSwipeLeft() {
                mRegularTransactionViewModel.nextMonth()
            }

            override fun onSwipeRight() {
                mRegularTransactionViewModel.prevMonth()
            }
        })

        binding.composeRegularTransactionScreen.setContent {
            AppTheme {
                RegularTransactionScreen(
                    month = mRegularTransactionViewModel.getMonth().observeAsState(),
                    transactions = mRegularTransactionViewModel.regularTransactionByMonth.observeAsState(listOf()),
                    onClickTransaction = { pos, entry ->
                        val intent = Intent(context, RegularTransactionActivity::class.java)
                        intent.putExtra(Constant.TRANS_ID, entry.id)
                        intent.putExtra("click", 25)
                        startActivity(intent)
                    },
                    onPrevMonth = { mRegularTransactionViewModel.prevMonth() },
                    onNextMonth = { mRegularTransactionViewModel.nextMonth() },
                    onAdd = { add() }
                )
            }
        }

        binding.composeRegularTransactionScreen.setOnTouchListener { view, motionEvent ->
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
        binding.composeRegularTransactionScreen.setOnDragListener { v, event ->
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
        mRegularTransactionViewModel.loadRegularTransactionsByMonth(0)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)!!.updatePosition(MainActivity.INDEX_DRAWER_REGULAR)

    }

    fun add() {
        setNormalStat()
        val intent = Intent(context, RegularTransactionActivity::class.java)
        intent.putExtra(Constant.TRANS_STAT, transStat)
        intent.putExtra(RegularTransactionActivity.MONTH, mRegularTransactionViewModel.getMonth().value)
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