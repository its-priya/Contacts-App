package com.example.phonebook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.phonebook.ContactModel.Contact;
import com.example.phonebook.Data.DbHandler;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DbHandler db= new DbHandler(MainActivity.this);

        Contact priya= new Contact();          // Creating a Contact object.
        priya.setName("Priya Nandi");
        priya.setPhoneNumber("1234567815");
        db.addContact(priya);                  // Adding Contact object in database.

        List<Contact> contactList= db.getContactList();        // Get the Contact List
        for(Contact contact: contactList){
            Log.d("dbContacts", "Id: " + contact.getId() + "\n" +
                    "Name: " + contact.getName() + "\n" +
                    "Phone Number " + contact.getPhoneNumber());
        }

        Log.d("dbContacts", "You have " + db.countContacts() + " contacts");
    }
}