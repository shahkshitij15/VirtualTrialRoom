package com.example.arshop.ui.category;

import android.graphics.Bitmap;


import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;


public class Cloth {
    private String name;
    private String gender;
    private String[] categories;
    private String color;
    private String brand;
    private MatOfPoint2f wearablePoints;
    Bitmap image = null;

    public Cloth(String name, String[] categories, String gender, String brand, String color) {
        this.name = name;
        this.categories = categories;
        this.gender = gender;
        this.brand = brand;
        this.color = color;
    }

    public Cloth(String name, String[] categories, Bitmap image, String gender, String brand, String color) {
        this.name = name;
        this.categories = categories;
        this.image = image;
        this.gender = gender;
        this.brand = brand;
        this.color = color;
    }

    public Cloth(Bitmap image)
    {
        this.image = image;
    }
    public String getGender() {
        return gender;
    }

    public String[] getCategories() {
        return this.categories;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getBrand() {
        return this.brand;
    }

    public void setPoints(Point top_left, Point top_right, Point bottom_left, Point bottom_right) {
        wearablePoints = new MatOfPoint2f(top_left, bottom_right, bottom_left, top_right);
        //wearablePoints = new MatOfPoint2f(top_left, top_right, bottom_left);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory(int id) {
        return this.categories[id];
    }

    public Bitmap getImage() {
        return this.image;
    }

    public MatOfPoint2f getPoints() {
        return this.wearablePoints;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

}
