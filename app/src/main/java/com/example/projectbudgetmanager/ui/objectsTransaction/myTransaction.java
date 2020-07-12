package com.example.projectbudgetmanager.ui.objectsTransaction;

import java.io.Serializable;

public class myTransaction implements Serializable {
    private int typeOfTransaction = 0; // Income = 1, Expense = 2, Debt = 3;
    private int amount, imgId;
    private String category;
    private String note = "";
    private int day;
    private int month;
    private int year;
    private String payee;
    private String phoneNumber = "";
    private String id;

    @Override
    public String toString() {
        return "myTransaction{" +
                "amount=" + amount +
                ", category='" + category + '\'' +
                ", note='" + note + '\'' +
                ", day= '"+ day + '\'' +
                ", month= '"+ month + '\'' +
                ", year= '"+ year + '\'' +
                ", note= '"+ note + '\'' +
                ", phone= '"+ phoneNumber + '\'' +
                '}';
    }

    public myTransaction(int typeOfTransaction, int amount, String category, int day, int month, int year) {
        this.typeOfTransaction = typeOfTransaction;
        this.amount = amount;
        this.category = category;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public myTransaction(int typeOfTransaction, int amount, String category) {
        this.typeOfTransaction = typeOfTransaction;
        this.amount = amount;
        this.category = category;
    }

    public myTransaction(int typeOfTransaction,int amount, String category, String id) {
        this.typeOfTransaction = typeOfTransaction;
        this.amount = amount;
        this.category = category;
        this.id = id;
    }

    public myTransaction(int typeOfTransaction,int amount, String category, String phoneNumber, String note) {
        this.typeOfTransaction = typeOfTransaction;
        this.amount = amount;
        this.category = category;
        this.phoneNumber = phoneNumber;
        this.note = note;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public myTransaction() {
    }


    public int getTypeOfTransaction() {
        return typeOfTransaction;
    }

    public void setTypeOfTransaction(int typeOfTransaction) {
        this.typeOfTransaction = typeOfTransaction;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int  getImg() {
        return imgId;
    }

    public void setImg(int imgId) {
        this.imgId = imgId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
