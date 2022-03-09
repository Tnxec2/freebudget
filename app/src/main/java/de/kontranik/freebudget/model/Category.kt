package de.kontranik.freebudget.model

class Category : Comparable<Category> {
    var id: Long = 0
    var name: String
    var weight = 0.0

    constructor(id: Long, name: String) {
        this.id = id
        this.name = name
        weight = 0.0
    }

    constructor(id: Long, name: String, weight: Double) {
        this.id = id
        this.name = name
        this.weight = weight
    }

    constructor(name: String) {
        this.name = name
    }

    override fun compareTo(compareCategory: Category): Int {
        val compareWeight = compareCategory.weight

        //ascending order
        return (weight - compareWeight).toInt()

        //descending order
        //return compareWeight - this.weight;
    }

    override fun toString(): String {
        return name
    }

    companion object {
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