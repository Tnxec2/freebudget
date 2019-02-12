package de.kontranik.freebudget.model;

public class Category {
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
    public String toString() {
        return name;
    }
}
