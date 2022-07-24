package com.example.bookshare.model;

import java.io.Serializable;

public class Request implements Serializable {
    public int cartId;
    public int bookId;
    public String bookTitle;
    public double bookPrice;
    public String bookPicture;
    public int requesterId;
    public int ownerId;
    public int quantity;
    public double totalPrice;
    public String dateAdded;
    public int purpose;
    public int status;
    public String ownerName = "";
    public String ownerFirstName = "";
    public String ownerLastName = "";
    public String ownerFullName = "";
    public String requesterName = "";
    public String requesterFirstName = "";
    public String requesterLastName = "";
    public String requesterFullName = "";
    public int orderId = 0;
    public int bookConditionId = 0;
    public String bookCategoryName = "";
    public String bookAuthorName = "";

    public Request() {
    }

    public Request(int cartId, int bookId, String bookTitle, double bookPrice, String bookPicture, int requesterId, int ownerId, int quantity, double totalPrice, int purpose, int status, String dateAdded, String requesterName, String ownerName, String requesterFullName, String ownerFullName, int bookConditionId) {
        this.cartId = cartId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookPrice = bookPrice;
        this.bookPicture = bookPicture;
        this.requesterId = requesterId;
        this.ownerId = ownerId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.purpose = purpose;
        this.status = status;
        this.dateAdded = dateAdded;
        this.requesterName = requesterName;
        this.ownerName = ownerName;
        this.requesterFullName = requesterFullName;
        this.ownerFullName = ownerFullName;
        this.bookConditionId = bookConditionId;
    }

    public int getBookId() {
        return bookId;
    }

    public int getRequesterId() {
        return requesterId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public int getCartId() {
        return cartId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public double getBookPrice() {
        return bookPrice;
    }

    public String getBookPicture() {
        return bookPicture;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public int getPurpose() {
        return purpose;
    }

    public int getStatus() {
        return status;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public String getRequesterFirstName() {
        return requesterFirstName;
    }

    public String getRequesterLastName() {
        return requesterLastName;
    }

    public String getOwnerFirstName() {
        return ownerFirstName;
    }

    public String getOwnerLastName() {
        return ownerLastName;
    }

    public String getOwnerFullName() {
        return ownerFullName;
    }

    public String getRequesterFullName() {
        return requesterFullName;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getBookConditionId() {
        return bookConditionId;
    }

    public String getBookCategoryName() {
        return bookCategoryName;
    }

    public String getBookAuthorName() {
        return bookAuthorName;
    }
}
