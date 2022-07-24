package com.example.bookshare.model;

import java.io.Serializable;

public class Order implements Serializable {
    public int orderId;
    public int userId;
    public int ownerId;
    public int bookId;
    public String bookPicture;
    public int orderType;
    public int dayCount=0;
    public double totalBill=0.00;
    public String orderStartDate;
    public String orderEndDate;
    public int orderStatusId;
    public String bookTitle;
    public String userName;
    public String userFirstName;
    public String userLastName;
    public String userFullName;

    public Order(){

    }

    public Order(int orderId, int userId, int ownerId, int bookId, int orderType, int dayCount, double totalBill, String orderStartDate, String orderEndDate, int orderStatusId, String bookTitle, String userName, String userFullName, String bookPicture) {
        this.orderId = orderId;
        this.userId = userId;
        this.ownerId = ownerId;
        this.bookId = bookId;
        this.orderType = orderType;
        this.dayCount = dayCount;
        this.totalBill = totalBill;
        this.orderStartDate = orderStartDate;
        this.orderEndDate = orderEndDate;
        this.orderStatusId = orderStatusId;
        this.bookTitle = bookTitle;
        this.userName = userName;
        this.userFullName = userFullName;
        this.bookPicture = bookPicture;
    }


    public int getOrderId() {
        return orderId;
    }

    public int getUserId() {
        return userId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getBookId() {
        return bookId;
    }

    public int getOrderType() {
        return orderType;
    }

    public int getDayCount() {
        return dayCount;
    }

    public double getTotalBill() {
        return totalBill;
    }

    public String getOrderStartDate() {
        return orderStartDate;
    }

    public String getOrderEndDate() {
        return orderEndDate;
    }

    public int getOrderStatusId() {
        return orderStatusId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public String getBookPicture() {
        return bookPicture;
    }
}
