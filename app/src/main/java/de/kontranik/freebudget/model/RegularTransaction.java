package de.kontranik.freebudget.model;

import java.util.Date;

public class RegularTransaction {

    private Long id;
    private int month;
    private int day;
    private String description;
    private String category;
    private double amount;
    private long date_create;
    private long date_start;
    private long date_end;

    public RegularTransaction(long id) {
        this.id = id;
    }

    public RegularTransaction(int month, int day, String description, String category, double amount) {
        this.month = month;
        this.day = day;
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.date_create = new Date().getTime();
        this.date_start = 0;
        this.date_end = 0;
    }

    public RegularTransaction(long id, int month, int day, String description, String category, double amount) {
        this.id = id;
        this.month = month;
        this.day = day;
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.date_create = new Date().getTime();
        this.date_start = 0;
        this.date_end = 0;
    }

    public RegularTransaction(
            long id, int month, int day, String description, String category, double amount,
            long date_start, long date_end, long date_create) {
        this.id = id;
        this.month = month;
        this.day = day;
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.date_create = date_create;
        this.date_start = date_start;
        this.date_end = date_end;
    }

    public RegularTransaction(int month, int day, String description, String category, double amount, long date_create) {
        this.month = month;
        this.day = day;
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.date_create = date_create;
        this.date_start = 0;
        this.date_end = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getDate_create() {
        return date_create;
    }

    public void setDate_create(long date_create) {
        this.date_create = date_create;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public long getDate_start() {
        return date_start;
    }

    public void setDate_start(long date_start) {
        this.date_start = date_start;
    }

    public long getDate_end() {
        return date_end;
    }

    public void setDate_end(long date_end) {
        this.date_end = date_end;
    }
}
