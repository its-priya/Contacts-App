package com.example.phonebook.ContactModel;

import android.graphics.Bitmap;
import java.io.Serializable;

public class Contact implements Serializable {
    private int id;
    private String name;
    private String workplace;
    private String phoneNumber;
    private String imageUri;

    // Constructors
    public Contact(int id, String name, String workplace,String phoneNumber, String imageUri) {
        this.id = id;
        this.name = name;
        this.workplace = workplace;
        this.phoneNumber = phoneNumber;
        this.imageUri= imageUri;
    }
    public Contact(String name, String workplace, String phoneNumber) {
        this.name = name;
        this.workplace = workplace;
        this.phoneNumber = phoneNumber;
    }
    public Contact(String name, String workplace, String phoneNumber, String imageUri) {
        this.name = name;
        this.workplace = workplace;
        this.phoneNumber = phoneNumber;
        this.imageUri= imageUri;
    }
    public Contact() {

    }

    // Getters and Setters for data members of this class.
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
    public String getImageUri() {
        return imageUri;
    }
}
