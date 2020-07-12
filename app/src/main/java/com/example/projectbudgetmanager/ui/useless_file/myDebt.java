package com.example.projectbudgetmanager.ui.useless_file;

import com.example.projectbudgetmanager.ui.objectsTransaction.myTransaction;

public class myDebt extends myTransaction {
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    private String phoneNumber;
    private String borrowerName;

    public myDebt() {
        super();
    }
}
