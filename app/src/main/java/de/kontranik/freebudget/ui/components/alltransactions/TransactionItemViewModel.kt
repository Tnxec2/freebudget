package de.kontranik.freebudget.ui.components.alltransactions

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.kontranik.freebudget.database.repository.TransactionRepository
import de.kontranik.freebudget.model.Transaction
import de.kontranik.freebudget.ui.components.shared.TransactionType
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.abs

class TransactionItemViewModel(
    savedStateHandle: SavedStateHandle,
    private val mRepository: TransactionRepository
) : ViewModel() {

    fun delete(id: Long) {
        mRepository.delete(id)
    }

    var transactionItemUiState by mutableStateOf(TransactionItemUiState())
        private set

    private val itemId: String? = savedStateHandle[TransactionItemDestination.ITEM_ID_ARG]
    private val itemType: String? = savedStateHandle[TransactionItemDestination.ITEM_TYPE_ARG]

    init {
        viewModelScope.launch {

            transactionItemUiState = if (itemId != null) {
                mRepository.getTransactionByID(itemId.toLong())
                    .filterNotNull()
                    .first()
                    .toItemUiState()
            } else {
                TransactionItemUiState(TransactionItemDetails(
                    isIncome = itemType == TransactionType.INCOME_REGULAR.name || itemType == TransactionType.INCOME.name,
                    isPlanned = itemType == TransactionType.INCOME_REGULAR.name || itemType == TransactionType.BILLS_REGULAR.name
                ))
            }
        }
    }

    fun insert(transaction: Transaction) {
        mRepository.insert(transaction)
    }

    fun update(transaction: Transaction) {
        mRepository.update(transaction)
    }

    fun save(transaction: Transaction) {
        if (transaction.id == null)
            insert(transaction)
        else
            update(transaction)
    }

    fun updateUiState(itemDetails: TransactionItemDetails) {
        transactionItemUiState =
            TransactionItemUiState(itemDetails = itemDetails)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}


data class TransactionItemUiState(
    val itemDetails: TransactionItemDetails = TransactionItemDetails()
)


data class TransactionItemDetails(
    val id: Long? = null,
    val regularCreateTime: Long? = null,
    val description: String = "",
    val category: String = "",
    val note: String? = "",

    val amountPlanned: String = "",
    val amountFact: String = "",

    val date: Long = Calendar.getInstance().timeInMillis,
    val dateCreate: Long = Calendar.getInstance().timeInMillis,
    val dateEdit: Long = Calendar.getInstance().timeInMillis,

    val isIncome: Boolean = false,
    val isPlanned: Boolean = false,
)


fun TransactionItemDetails.toTransaction(): Transaction = Transaction(
    id = id,
    regularCreateTime = regularCreateTime,
    description = description,
    category = category,
    date = date,
    amountPlanned = if (isIncome) abs(amountPlanned.toDoubleOrNull() ?: 0.0) else -(abs(amountPlanned.toDoubleOrNull() ?: 0.0)),
    amountFact = if (isIncome) abs(amountFact.toDoubleOrNull() ?: 0.0) else -(abs(amountFact.toDoubleOrNull() ?: 0.0)),
    dateCreate = dateCreate,
    dateEdit = Calendar.getInstance().timeInMillis,
    note = note
)


fun Transaction.toItemUiState(): TransactionItemUiState = TransactionItemUiState(
    itemDetails = this.toItemDetails()
)

fun Transaction.toItemDetails(): TransactionItemDetails = TransactionItemDetails(
    id = id,
    regularCreateTime = regularCreateTime,
    description = description,
    category = category,
    note = note,
    amountPlanned = abs(amountPlanned).toString(),
    amountFact = abs(amountFact).toString(),
    date = date,
    dateCreate = dateCreate,
    dateEdit = dateEdit,
    isIncome = ( amountFact == 0.0 && amountPlanned > 0) || (amountFact > 0),
    isPlanned = false
)