package de.kontranik.freebudget.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import de.kontranik.freebudget.database.DatabaseHelper
import de.kontranik.freebudget.ui.helpers.DateUtils
import java.io.Serializable


@Entity(tableName = DatabaseHelper.TABLE_TRANSACTION)
class Transaction : Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DatabaseHelper.COLUMN_ID) var id: Long? = null
    @ColumnInfo(name = DatabaseHelper.COLUMN_REGULAR_CREATE_DATE) var regularCreateTime: Long? = null
    @ColumnInfo(name = DatabaseHelper.COLUMN_DESCRIPTION) var description: String = ""
    @ColumnInfo(name = DatabaseHelper.COLUMN_NOTE) var note: String? = null
    @ColumnInfo(name = DatabaseHelper.COLUMN_CATEGORY_NAME) var category: String = ""
    @ColumnInfo(name = DatabaseHelper.COLUMN_DATE) var date: Long = DateUtils.now()
    @ColumnInfo(name = DatabaseHelper.COLUMN_AMOUNT_PLANNED) var amountPlanned: Double = 0.0
    @ColumnInfo(name = DatabaseHelper.COLUMN_AMOUNT_FACT) var amountFact: Double = 0.0
    @ColumnInfo(name = DatabaseHelper.COLUMN_DATE_CREATE) var dateCreate: Long = DateUtils.now()
    @ColumnInfo(name = DatabaseHelper.COLUMN_DATE_EDIT) var dateEdit: Long = DateUtils.now()

    constructor() {}

    @Ignore
    constructor(
        id: Long?,
        regularCreateTime: Long?,
        description: String,
        category: String,
        date: Long,
        amountPlanned: Double,
        amountFact: Double,
        dateCreate: Long,
        dateEdit: Long,
        note: String?
    ) {
        this.id = id
        this.regularCreateTime = regularCreateTime
        this.description = description
        this.category = category
        this.date = date
        this.amountPlanned = amountPlanned
        this.amountFact = amountFact
        this.dateCreate = dateCreate
        this.dateEdit = dateEdit
        this.note = note
    }

    @Ignore
    constructor(
        id: Long?,
        regularCreateTime: Long?,
        description: String,
        category: String,
        date: Long,
        amountPlanned: Double,
        amountFact: Double,
        note: String?
    ) {
        this.id = id
        this.regularCreateTime = regularCreateTime
        this.description = description
        this.category = category
        this.date = date
        this.amountPlanned = amountPlanned
        this.amountFact = amountFact
        this.dateCreate = DateUtils.now()
        this.dateEdit = DateUtils.now()
        this.note = note
    }

}