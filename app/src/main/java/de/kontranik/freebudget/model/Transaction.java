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
    private long date_planed;
    private long date_fact;
    private double amount_planed;
    private double amount_fact;
    private long date_create;
    private RegularTransaction regular;

    public Transaction(long id, long regular_id, String description, String category, long date_planed, long date_fact, double amount_planed, double amount_fact, long date_create) {
        this.id = id;
        this.regular_id = regular_id;
        this.description = description;
        this.category = category;
        this.date_planed = date_planed;
        this.date_fact = date_fact;
        this.amount_planed = amount_planed;
        this.amount_fact = amount_fact;
        this.date_create = date_create;

        if ( date_fact > 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(date_fact);
            this.year  = cal.get(Calendar.YEAR);
            this.month = cal.get(Calendar.MONTH);
            this.day = cal.get(Calendar.DAY_OF_MONTH);
        } else if ( date_planed > 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(date_planed);
            this.year  = cal.get(Calendar.YEAR);
            this.day = cal.get(Calendar.DAY_OF_MONTH);
        }
    }

    public Transaction(long id, long regular_id, String description, String category, long date_planed, long date_fact, double amount_planed, double amount_fact) {
        this.id = id;
        this.regular_id = regular_id;
        this.description = description;
        this.category = category;
        this.date_planed = date_planed;
        this.date_fact = date_fact;
        this.amount_planed = amount_planed;
        this.amount_fact = amount_fact;
        this.date_create = new Date().getTime();

        if ( date_fact > 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(date_fact);
            this.year  = cal.get(Calendar.YEAR);
            this.month = cal.get(Calendar.MONTH);
            this.day = cal.get(Calendar.DAY_OF_MONTH);
        } else if ( date_planed > 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(date_planed);
            this.year  = cal.get(Calendar.YEAR);
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

    public long getDate_planed() {
        return date_planed;
    }

    public void setDate_planed(long date_planed) {
        this.date_planed = date_planed;
    }

    public long getDate_fact() {
        return date_fact;
    }

    public void setDate_fact(long date_fact) {
        this.date_fact = date_fact;
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

    public RegularTransaction getRegular() {
        return regular;
    }

    public void setRegular(RegularTransaction regular) {
        this.regular = regular;
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
}
