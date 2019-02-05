package de.kontranik.freebudget.model;

import java.util.Calendar;
import java.util.Date;

public class Transaction {

    private Long id;
    private Long regular_id;
    private int year;
    private int month;
    private int day;
    private String description;
    private String category;
    private long date;
    private double amount_planed;
    private double amount_fact;
    private long date_create;
    private long date_edit;

    public Transaction(long id, long regular_id, String description, String category, long date, double amount_planed, double amount_fact, long date_create, long date_edit) {
        this.id = id;
        this.regular_id = regular_id;
        this.description = description;
        this.category = category;
        this.date = date;
        this.amount_planed = amount_planed;
        this.amount_fact = amount_fact;
        this.date_create = date_create;
        this.date_edit = date_edit;

        if ( date > 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(date);
            this.year  = cal.get(Calendar.YEAR);
            this.month = cal.get(Calendar.MONTH) + 1;
            this.day = cal.get(Calendar.DAY_OF_MONTH);
        }
    }

    public Transaction(long id, long regular_id, String description, String category, long date, double amount_planed, double amount_fact, long date_create) {
        this.id = id;
        this.regular_id = regular_id;
        this.description = description;
        this.category = category;
        this.date = date;
        this.amount_planed = amount_planed;
        this.amount_fact = amount_fact;
        this.date_create = date_create;

        if ( date > 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(date);
            this.year  = cal.get(Calendar.YEAR);
            this.month = cal.get(Calendar.MONTH) + 1;
            this.day = cal.get(Calendar.DAY_OF_MONTH);
        }

        this.date_edit = new Date().getTime();
    }

    public Transaction(long id, long regular_id, String description, String category, long date, double amount_planed, double amount_fact) {
        this.id = id;
        this.regular_id = regular_id;
        this.description = description;
        this.category = category;
        this.date = date;
        this.amount_planed = amount_planed;
        this.amount_fact = amount_fact;
        this.date_create = new Date().getTime();
        this.date_edit = new Date().getTime();

        if ( date > 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(date);
            this.year  = cal.get(Calendar.YEAR);
            this.month = cal.get(Calendar.MONTH) + 1;
            this.day = cal.get(Calendar.DAY_OF_MONTH);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRegular_id() {
        return regular_id;
    }

    public void setRegular_id(Long regular_id) {
        this.regular_id = regular_id;
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

    public long getDate() {
        return date;
    }

    public double getAmount_planed() {
        return amount_planed;
    }

    public void setAmount_planed(double amount_planed) {
        this.amount_planed = amount_planed;
    }

    public double getAmount_fact() {
        return amount_fact;
    }

    public void setAmount_fact(double amount_fact) {
        this.amount_fact = amount_fact;
    }

    public long getDate_create() {
        return date_create;
    }

    public void setDate_create(long date_create) {
        this.date_create = date_create;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getDate_edit() {
        return date_edit;
    }

    public void setDate_edit(long date_edit) {
        this.date_edit = date_edit;
    }
}
