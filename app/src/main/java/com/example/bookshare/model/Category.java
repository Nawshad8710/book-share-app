package com.example.bookshare.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable {
    public int categoryId = 0;
    public String categoryName = "";

    public ArrayList<Category> cList = new ArrayList<Category>();

    public Category() {
    }

    public Category(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
}
