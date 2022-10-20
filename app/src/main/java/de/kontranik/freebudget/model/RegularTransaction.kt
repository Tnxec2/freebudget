package de.kontranik.freebudget.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import de.kontranik.freebudget.database.DatabaseHelper
import java.util.*

@Entity(tableName = DatabaseHelper.TABLE_REGULAR)
class RegularTransaction {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DatabaseHelper.COLUMN_ID) var id: Long? = null
    @ColumnInfo(name = DatabaseHelper.COLUMN_MONTH) var month = 0
    @ColumnInfo(name = DatabaseHelper.COLUMN_DAY) var day = 0
    @ColumnInfo(name = DatabaseHelper.COLUMN_DESCRIPTION) var description: String = ""
    @ColumnInfo(name = DatabaseHelper.COLUMN_NOTE) var note: String? = null
    @ColumnInfo(name = DatabaseHelper.COLUMN_CATEGORY_NAME) var category: String = ""
    @ColumnInfo(name = DatabaseHelper.COLUMN_AMOUNT) var amount = 0.0
    @ColumnInfo(name = DatabaseHelper.COLUMN_DATE_CREATE) var dateCreate: Long = System.nanoTime()
    @ColumnInfo(name = DatabaseHelper.COLUMN_DATE_START) var dateStart: Long? = null
    @ColumnInfo(name = DatabaseHelper.COLUMN_DATE_END) var dateEnd: Long? = null

    constructor() {}

    @Ignore
    constructor(id: Long) {
        this.id = id
    }
    @Ignore
    constructor(month: Int, day: Int, description: String, category: String, amount: Double, note: String?) {
        this.month = month
        this.day = day
        this.description = description
        this.category = category
        this.amount = amount
        dateCreate = System.nanoTime()
        dateStart = null
        dateEnd = null
        this.note = note
    }
    @Ignore
    constructor(
        id: Long?,
        month: Int,
        day: Int,
        description: String,
        category: String,
        amount: Double,
        note: String?
    ) {
        this.id = id
        this.month = month
        this.day = day
        this.description = description
        this.category = category
        this.amount = amount
        dateCreate = System.nanoTime()
        dateStart = null
        dateEnd = null
        this.note = note
    }
    @Ignore
    constructor(
        id: Long, month: Int, day: Int, description: String, category: String, amount: Double,
        date_start: Long?, date_end: Long?, date_create: Long, note: String?
    ) {
        this.id = id
        this.month = month
        this.day = day
        this.description = description
        this.category = category
        this.amount = amount
        this.dateCreate = date_create
        this.dateStart = date_start
        this.dateEnd = date_end
        this.note = note
    }
    @Ignore
    constructor(
        month: Int,
        day: Int,
        description: String,
        category: String,
        amount: Double,
        date_create: Long,
        note: String?
    ) {
        this.month = month
        this.day = day
        this.description = description
        this.category = category
        this.amount = amount
        this.dateCreate = date_create
        dateStart = null
        dateEnd = null
        this.note = note
    }
}