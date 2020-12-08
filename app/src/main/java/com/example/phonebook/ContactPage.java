package com.example.phonebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ContactPage extends AppCompatActivity {
    TextView contactName, contactWorkplace,contactNumber;
    Button backToContacts, editContact;
    TextView pageTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_page);

        //Toolbar
        backToContacts= findViewById(R.id.left);
        pageTitle= findViewById(R.id.title);
        editContact= findViewById(R.id.right);

        contactName= findViewById(R.id.contactName);
        contactWorkplace= findViewById(R.id.contactWorkplace);
        contactNumber= findViewById(R.id.contactNumber);

        backToContacts.setText(R.string.contacts);
        pageTitle.setText(R.string.contactDetails);
        editContact.setText(R.string.edit);

        Intent intent= getIntent();
        contactName.setText(intent.getStringExtra("contact_name"));
        contactWorkplace.setText(intent.getStringExtra("contact_workplace"));
        contactNumber.setText(intent.getStringExtra("contact_number"));

        backToContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(ContactPage.this, MainActivity.class);
                startActivity(intent);
            }
        });
        /*editContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contact= new Intent(ContactPage.this, NewContact.class);
                Bundle savedData= new Bundle();
                savedData.putString("firstName", );
            }
        });*/
    }
}