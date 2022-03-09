package de.kontranik.freebudget.model

import java.util.*

class RegularTransaction {
    var id: Long? = null
    var month = 0
    var day = 0
    var description: String? = null
    var category: String? = null
    var amount = 0.0
    var date_create: Long = 0
    var date_start: Long = 0
    var date_end: Long = 0

    constructor(id: Long) {
        this.id = id
    }

    constructor(month: Int, day: Int, description: String?, category: String?, amount: Double) {
        this.month = month
        this.day = day
        this.description = description
        this.category = category
        this.amount = amount
        date_create = Date().time
        date_start = 0
        date_end = 0
    }

    constructor(
        id: Long,
        month: Int,
        day: Int,
        description: String?,
        category: String?,
        amount: Double
    ) {
        this.id = id
        this.month = month
        this.day = day
        this.description = description
        this.category = category
        this.amount = amount
        date_create = Date().time
        date_start = 0
        date_end = 0
    }

    constructor(
        id: Long, month: Int, day: Int, description: String?, category: String?, amount: Double,
        date_start: Long, date_end: Long, date_create: Long
    ) {
        this.id = id
        this.month = month
        this.day = day
        this.description = description
        this.category = category
        this.amount = amount
        this.date_create = date_create
        this.date_start = date_start
        this.date_end = date_end
    }

    constructor(
        month: Int,
        day: Int,
        description: String?,
        category: String?,
        amount: Double,
        date_create: Long
    ) {
        this.month = month
        this.day = day
        this.description = description
        this.category = category
        this.amount = amount
        this.date_create = date_create
        date_start = 0
        date_end = 0
    }
}