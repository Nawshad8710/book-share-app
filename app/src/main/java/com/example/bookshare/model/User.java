package com.example.bookshare.model;

import java.io.Serializable;

public class User implements Serializable {

    public int idUser;
    public String username;
    public String firstName;
    public String lastName;
    public String phone;
    public String email;
    public int gender;
    public double latitude;
    public double longitude;
    public int bookCount;
    public int sellBookCount;
    public int rentBookCount;

    public User() {
    }

    public User(double latitude, double longitude, String lastName, int bookCount) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.lastName = lastName;
        this.bookCount = bookCount;
    }

    public User(int idUser, String username, String firstName, String lastName, String phone, String email, double latitude, double longitude, int bookCount, int sellBookCount, int rentBookCount) {
        this.idUser = idUser;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bookCount = bookCount;
        this.sellBookCount = sellBookCount;
        this.rentBookCount = rentBookCount;
    }

    public User(double latitude, double longitude, String lastName, int bookCount, int sellBookCount, int rentBookCount) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.lastName = lastName;
        this.bookCount = bookCount;
        this.sellBookCount = sellBookCount;
        this.rentBookCount = rentBookCount;
    }

    public User(int idUser, String username, String firstName, String lastName) {
        this.idUser = idUser;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getBookCount() {
        return bookCount;
    }

    public void setBookCount(int bookCount) {
        this.bookCount = bookCount;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIdUser() {
        return idUser;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public int getGender() {
        return gender;
    }

    public int getSellBookCount() {
        return sellBookCount;
    }

    public int getRentBookCount() {
        return rentBookCount;
    }
}
