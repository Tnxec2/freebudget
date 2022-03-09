package de.kontranik.freebudget.model

import java.util.*

class Transaction {
    var id: Long
    var regular_id: Long
    var year = 0
    var month = 0
    var day = 0
    var description: String
    var category: String
    var date: Long
    var amount_planned: Double
    var amount_fact: Double
    var date_create: Long
    var date_edit: Long

    constructor(
        id: Long,
        regular_id: Long,
        description: String,
        category: String,
        date: Long,
        amount_planned: Double,
        amount_fact: Double,
        date_create: Long,
        date_edit: Long
    ) {
        this.id = id
        this.regular_id = regular_id
        this.description = description
        this.category = category
        this.date = date
        this.amount_planned = amount_planned
        this.amount_fact = amount_fact
        this.date_create = date_create
        this.date_edit = date_edit
        if (date > 0) {
            val cal = Calendar.getInstance()
            cal.timeInMillis = date
            year = cal[Calendar.YEAR]
            month = cal[Calendar.MONTH] + 1
            day = cal[Calendar.DAY_OF_MONTH]
        }
    }

    constructor(
        id: Long,
        regular_id: Long,
        description: String,
        category: String,
        date: Long,
        amount_planned: Double,
        amount_fact: Double,
        date_create: Long
    ) {
        this.id = id
        this.regular_id = regular_id
        this.description = description
        this.category = category
        this.date = date
        this.amount_planned = amount_planned
        this.amount_fact = amount_fact
        this.date_create = date_create
        if (date > 0) {
            val cal = Calendar.getInstance()
            cal.timeInMillis = date
            year = cal[Calendar.YEAR]
            month = cal[Calendar.MONTH] + 1
            day = cal[Calendar.DAY_OF_MONTH]
        }
        date_edit = Date().time
    }

    constructor(
        id: Long,
        regular_id: Long,
        description: String,
        category: String,
        date: Long,
        amount_planned: Double,
        amount_fact: Double
    ) {
        this.id = id
        this.regular_id = regular_id
        this.description = description
        this.category = category
        this.date = date
        this.amount_planned = amount_planned
        this.amount_fact = amount_fact
        date_create = Date().time
        date_edit = Date().time
        if (date > 0) {
            val cal = Calendar.getInstance()
            cal.timeInMillis = date
            year = cal[Calendar.YEAR]
            month = cal[Calendar.MONTH] + 1
            day = cal[Calendar.DAY_OF_MONTH]
        }
    }
}