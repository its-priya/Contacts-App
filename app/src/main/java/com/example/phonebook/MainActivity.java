package com.example.phonebook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    protected static RecyclerViewAdapter recyclerViewAdapter;
    protected static ArrayList<Contact> contactArrayList;
    private Button editContacts, addContact;

    private TextView pageTitleText;
    private SearchView searchView;
    protected static DbHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db= new DbHandler(MainActivity.this);
        contactArrayList= new ArrayList<>(db.getContactList());

        editContacts= findViewById(R.id.left);
        pageTitleText= findViewById(R.id.title);
        addContact= findViewById(R.id.right);
        searchView= findViewById(R.id.searchView);

        editContacts.setText(R.string.edit);
        pageTitleText.setText(R.string.contacts);
        addContact.setText("");
        addContact.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_person_add,0);

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newContactPage= new Intent(MainActivity.this, NewContact.class);
                startActivity(newContactPage);
            }
        });
        Log.d("dbContacts", "You have " + db.countContacts() + " contact(s)");

        // RecyclerView
        recyclerView= findViewById(R.id.recyclerView);            // RecyclerView Initialization
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter= new RecyclerViewAdapter(MainActivity.this, contactArrayList);
        recyclerView.setAdapter(recyclerViewAdapter);

        searchView.setQueryHint("Search in " + contactArrayList.size() + " Contact(s)");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerViewAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}