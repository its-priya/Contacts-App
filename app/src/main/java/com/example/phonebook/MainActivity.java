package com.example.phonebook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.phonebook.Adapter.RecyclerViewAdapter;
import com.example.phonebook.ContactModel.Contact;
import com.example.phonebook.Data.DbHandler;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<Contact> contactArrayList;
    SearchView searchView;
    Button editContacts, addContact;
    TextView pageTitleText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DbHandler db= new DbHandler(MainActivity.this);

        editContacts= findViewById(R.id.left);
        pageTitleText= findViewById(R.id.title);
        addContact= findViewById(R.id.right);

        editContacts.setText(R.string.edit);
        pageTitleText.setText(R.string.contacts);
        addContact.setText(R.string.add);
        addContact.setTextSize(30);
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newContactPage= new Intent(MainActivity.this, NewContact.class);
                startActivity(newContactPage);
            }
        });

        searchView= findViewById(R.id.searchView);
        //searchView.setQueryHint("Search in " + db.countContacts() + " contact(s)");
        /*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                RecyclerViewAdapter.getFilter(query.toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });*/

        contactArrayList= new ArrayList<>();

        Contact priya= new Contact();          // Creating a Contact object.
        priya.setName("Priya Nandi");
        priya.setWorkplace("Earth");
        priya.setPhoneNumber("1234567815");
        db.addContact(priya);                  // Adding Contact object in database.

        db.addContact(new Contact("Aariya", "Mars","9984567815"));
        db.addContact(new Contact("Sara",  "","7539514268"));
        db.addContact(new Contact("Jiya",  "","4576514268"));
        db.addContact(new Contact("Riya",  "","5147514268"));
        db.addContact(new Contact("Piya",  "","3127514268"));
        db.addContact(new Contact("Shrey", "", "1234514268"));
        db.addContact(new Contact("Gian",  "","8899514268"));
        db.addContact(new Contact("Rehan", "", "6547514268"));


        /*
        aariya.setName("Aariya Sharma");
        aariya.setPhoneNumber("7777777890");
        int affectedRows= db.updateContactById(aariya, 2);        // Update Contact
        Log.d("dbContacts", "No. of rows affected are: "+affectedRows);

        db.deleteContact("Sara");                                 // Delete Contact
        */

        List<Contact> contactList= db.getContactList();        // Get the Contact List
        for(Contact contact: contactList){
            Log.d("dbContacts", "Id: " + contact.getId() + "\n" +
                    "Name: " + contact.getName()+
                    "Workplace: " + contact.getWorkplace()+
                    "Phone Number " + contact.getPhoneNumber());
            contactArrayList.add(contact);
        }
        Log.d("dbContacts", "You have " + db.countContacts() + " contacts");

        // RecyclerView
        recyclerView= findViewById(R.id.recyclerView);            // RecyclerView Initialization
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter= new RecyclerViewAdapter(MainActivity.this, contactArrayList);
        recyclerView.setAdapter(recyclerViewAdapter);

        //recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL));
    }
}