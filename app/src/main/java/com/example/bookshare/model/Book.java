package com.example.bookshare.model;

import com.example.bookshare.R;

import java.io.Serializable;

public class Book implements Serializable {
    public int id;
    public String title;
    public double price;
    public String picture;
    public int minimumQuantity;
    public String link;
    public String edition;
    public String isbn;
    public int totalPage;
    public String publisher;
    public String country;
    public String language;
    public String description;
    public String category;
    public int idCategory;
    public String authorName;
    public String authorBio;
    public int purpose;
    public int idUser;
    public int idOwner;
    public String ownerFullName;
    public String ownerFirstName;
    public String ownerLastName;
    public String ownerUsername;
    public int userBookType;
    public int userBookStatus;
    public int idCondition;
    public String conditionName;

    public int imageTemp = R.drawable.single_book;
    public int cartValue = 0;
    public int totalSell = 0;
    public int totalRent = 0;


    public Book(){

    }

    public Book(int id, String title, double price, String picture, int minimumQuantity, int idOwner) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.picture = picture;
        this.minimumQuantity = minimumQuantity;
        this.idOwner = idOwner;
    }

    public Book(int id, String title, double price, int idOwner) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.idOwner = idOwner;
    }

    public int getIdOwner() {
        return idOwner;
    }

    public void setIdOwner(int idOwner) {
        this.idOwner = idOwner;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public String getPicture() {
        return picture;
    }

    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    public String getLink() {
        return link;
    }

    public String getEdition() {
        return edition;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getCountry() {
        return country;
    }

    public String getLanguage() {
        return language;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorBio() {
        return authorBio;
    }

    public int getImageTemp() {
        return imageTemp;
    }

    public int getCartValue() {
        return cartValue;
    }

    public double getTotalPriceForCart(){
        return price*minimumQuantity;
    }

    public double getUpdateTotalPriceForCart(){
        return price*cartValue;
    }

    public int getPurpose() {
        return purpose;
    }

    public int getIdUser() {
        return idUser;
    }

    public String getOwnerFullName() {
        return ownerFullName;
    }

    public String getOwnerFirstName() {
        return ownerFirstName;
    }

    public String getOwnerLastName() {
        return ownerLastName;
    }

    public int getUserBookType() {
        return userBookType;
    }

    public int getUserBookStatus() {
        return userBookStatus;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public int getIdCondition() {
        return idCondition;
    }

    public String getConditionName() {
        return conditionName;
    }
}
