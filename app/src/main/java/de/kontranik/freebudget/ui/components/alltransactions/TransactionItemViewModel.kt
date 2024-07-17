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
import de.kontranik.freebudget.ui.helpers.DateUtils
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
    private val editPlanned: String? = savedStateHandle[TransactionItemDestination.EDIT_PLANNED_ARG]

    init {
        viewModelScope.launch {

            transactionItemUiState = if (itemId != null) {
                mRepository.getTransactionByID(itemId.toLong())
                    .filterNotNull()
                    .first()
                    .toItemUiState(editPlanned.toBoolean())
            } else {
                TransactionItemUiState(TransactionItemDetails(
                    isIncome = itemType == TransactionType.INCOME_REGULAR.name || itemType == TransactionType.INCOME.name,
                    isPlanned = editPlanned.toBoolean() || itemType == TransactionType.INCOME_REGULAR.name || itemType == TransactionType.BILLS_REGULAR.name
                ))
            }
        }
    }

    fun save(transaction: Transaction) {
        transaction.dateEdit = DateUtils.now()
        if (transaction.id == null)
            mRepository.insert(transaction)
        else
            mRepository.update(transaction)
    }

    fun updateUiState(itemDetails: TransactionItemDetails) {
        transactionItemUiState =
            TransactionItemUiState(itemDetails = itemDetails)
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

    val date: Long = DateUtils.now(),
    val dateCreate: Long = DateUtils.now(),
    val dateEdit: Long = DateUtils.now(),

    val isIncome: Boolean = false,
    val isPlanned: Boolean = false,
)

fun TransactionItemDetails.canCopy(): Boolean{
    return  !isPlanned
            && amountPlanned.isNotEmpty()
            && amountPlanned != "0.0"
            && (amountFact == "0.0" || amountFact.isEmpty())
}


fun TransactionItemDetails.toTransaction(): Transaction = Transaction(
    id = id,
    regularCreateTime = regularCreateTime,
    description = description,
    category = category,
    date = date,
    amountPlanned = if (isIncome) abs(amountPlanned.toDoubleOrNull() ?: 0.0) else -(abs(amountPlanned.toDoubleOrNull() ?: 0.0)),
    amountFact = if (isIncome) abs(amountFact.toDoubleOrNull() ?: 0.0) else -(abs(amountFact.toDoubleOrNull() ?: 0.0)),
    dateCreate = dateCreate,
    dateEdit = DateUtils.now(),
    note = note
)


fun Transaction.toItemUiState(editPlanned: Boolean): TransactionItemUiState = TransactionItemUiState(
    itemDetails = this.toItemDetails(editPlanned)
)

fun Transaction.toItemDetails(editPlanned: Boolean): TransactionItemDetails = TransactionItemDetails(
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
    isPlanned = editPlanned
)
