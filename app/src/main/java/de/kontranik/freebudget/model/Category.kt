package de.kontranik.freebudget.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import de.kontranik.freebudget.database.DatabaseHelper

@Entity(tableName = DatabaseHelper.TABLE_CATEGORY)
class Category : Comparable<Category> {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DatabaseHelper.COLUMN_ID) var id: Long? = null
    @ColumnInfo(name = DatabaseHelper.COLUMN_CATEGORY_NAME) var name: String
    @Ignore var weight = 0.0

    constructor(id: Long, name: String) {
        this.id = id
        this.name = name
        weight = 0.0
    }
    @Ignore
    constructor(id: Long, name: String, weight: Double) {
        this.id = id
        this.name = name
        this.weight = weight
    }
    @Ignore
    constructor(name: String) {
        this.name = name
    }

    override fun compareTo(other: Category): Int {
        val compareWeight = other.weight

        //ascending order
        return (weight - compareWeight).toInt()

        //descending order
        //return compareWeight - this.weight;
    }

    override fun toString(): String {
        return name
    }

    companion object {
        @JvmField
        var CategoryWeightComparator = java.util.Comparator<Category> { fruit1, fruit2 ->
            val catWeight1 = fruit1.weight
            val catWeight2 = fruit2.weight

            //ascending order
            //return (int) ( catWeight1 - catWeight2 );

            //descending order
            (catWeight2 - catWeight1).toInt()
        }
    }
}