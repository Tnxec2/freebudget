package de.kontranik.freebudget.ui.components.regular

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.kontranik.freebudget.database.repository.RegularTransactionRepository
import de.kontranik.freebudget.model.RegularTransaction
import de.kontranik.freebudget.ui.components.shared.TransactionType
import de.kontranik.freebudget.ui.helpers.DateUtils
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.NumberFormat
import kotlin.math.abs

class RegularTransactionItemViewModel(
    savedStateHandle: SavedStateHandle,
    private val mRepository: RegularTransactionRepository
) : ViewModel() {

    fun delete(id: Long) {
        mRepository.delete(id)
    }

    var regularTransactionItemUiState by mutableStateOf(RegularTransactionItemUiState())
        private set

    private val monthParam: String? = savedStateHandle[RegularTransactionItemDestination.MONTH_ARG]
    private val itemIdParam: String? = savedStateHandle[RegularTransactionItemDestination.ITEM_ID_ARG]
    private val itemTypeParam: String? = savedStateHandle[RegularTransactionItemDestination.ITEM_TYPE_ARG]

    init {
        viewModelScope.launch {

            regularTransactionItemUiState = if (itemIdParam != null) {
                mRepository.getById(itemIdParam.toLong())
                    .filterNotNull()
                    .first()
                    .toItemUiState()
            } else {
                RegularTransactionItemUiState(RegularTransactionItem(
                    month = monthParam?.toIntOrNull() ?: 0,
                    isIncome = itemTypeParam == TransactionType.INCOME_REGULAR.name)
                )
            }
        }
    }

    fun insert(regularTransaction: RegularTransaction) {
        mRepository.insertRegularTransaction(regularTransaction)
    }

    fun update(regularTransaction: RegularTransaction) {
        mRepository.update(regularTransaction)
    }

    fun save(rt: RegularTransaction) {
        if (rt.id == null)
            insert(rt)
        else
            update(rt)
    }

    fun updateUiState(itemDetails: RegularTransactionItem) {
        regularTransactionItemUiState =
            RegularTransactionItemUiState(itemDetails = itemDetails)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class RegularTransactionItemUiState(
    val itemDetails: RegularTransactionItem = RegularTransactionItem()
)

data class RegularTransactionItem(
    val id: Long? = null,
    val month: Int = 0,
    val day: String = "1",
    val description: String = "",
    val category: String = "",
    val note: String = "",
    val amount: String = "",
    val dateCreate: Long = DateUtils.now(),
    val dateStart: Long? = null,
    val dateEnd: Long? = null,
    val isIncome: Boolean = false,
)


fun RegularTransactionItem.toRegularTransaction(): RegularTransaction = RegularTransaction(
    id = id,
    month = month,
    day = day.toIntOrNull() ?: 1,
    description = description,
    category = category,
    amount = if (isIncome) abs(amount.toDoubleOrNull() ?: 0.0) else -(abs(amount.toDoubleOrNull() ?: 0.0)),
    date_create = dateCreate,
    date_start = dateStart,
    date_end = dateEnd,
    note = note.ifEmpty { null }
)

fun RegularTransaction.formatedPrice(): String {
    return NumberFormat.getCurrencyInstance().format(amount)
}

fun RegularTransaction.toItemUiState(): RegularTransactionItemUiState = RegularTransactionItemUiState(
    itemDetails = this.toItemDetails()
)

fun RegularTransaction.toItemDetails(): RegularTransactionItem = RegularTransactionItem(
    id = id,
    month = month,
    day = day.toString(),
    description = description,
    category = category,
    note = note ?: "",
    amount = abs(amount).toString(),
    dateCreate = dateCreate,
    dateStart = dateStart,
    dateEnd = dateEnd,
    isIncome = amount > 0
)