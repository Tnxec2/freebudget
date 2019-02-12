package de.kontranik.freebudget.model;

import java.util.Comparator;

public class Category implements Comparable<Category> {
    private long id;
    private String name;
    private double weight;

    public Category(long id, String name) {
        this.id = id;
        this.name = name;
        this.weight = 0;
    }

    public Category(long id, String name, double weight) {
        this.id = id;
        this.name = name;
        this.weight = weight;
    }

    public Category(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public int compareTo(Category compareCategory) {

        double compareWeight = ((Category) compareCategory).getWeight();

        //ascending order
        return (int) ( this.weight - compareWeight);

        //descending order
        //return compareWeight - this.weight;
    }

    public static Comparator<Category> CategoryWeightComparator
            = new Comparator<Category>() {

        public int compare(Category fruit1, Category fruit2) {

            double catWeight1 = fruit1.getWeight();
            double catWeight2 = fruit2.getWeight();

            //ascending order
            //return (int) ( catWeight1 - catWeight2 );

            //descending order
            return (int) ( catWeight2 - catWeight1 );
        }

    };

    @Override
    public String toString() {
        return name;
    }
}
