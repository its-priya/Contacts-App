package com.example.phonebook;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.phonebook.ContactModel.Contact;
import com.example.phonebook.Data.DbHandler;

public class ContactPage extends AppCompatActivity {
    private TextView contactName, contactWorkplace, contactNumber;
    private Button backToContacts, editContact;
    private TextView pageTitle;
    private LinearLayout contactPageLayout;
    private Contact savedContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_page);

        //Toolbar
        backToContacts = findViewById(R.id.left);
        pageTitle = findViewById(R.id.title);
        editContact = findViewById(R.id.right);

        contactPageLayout = findViewById(R.id.contactPageLayout);

        contactName = findViewById(R.id.contactName);
        contactWorkplace = findViewById(R.id.contactWorkplace);
        contactNumber = findViewById(R.id.contactNumber);

        backToContacts.setText(R.string.contacts);
        pageTitle.setText(R.string.contactDetails);
        editContact.setText(R.string.edit);

        savedContact = (Contact) getIntent().getSerializableExtra("contactObject");
        setDetails(savedContact);

        backToContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactPage.this, MainActivity.class);
                startActivity(intent);
            }
        });

        editContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editPage = new Intent(ContactPage.this, NewContact.class);
                editPage.putExtra("savedContactObject", savedContact);
                startActivityForResult(editPage, 2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if(resultCode == RESULT_OK) {
                Contact updatedContact = (Contact) data.getSerializableExtra("updatedContactObject");
                setDetails(updatedContact);
            }
            else if(resultCode == RESULT_CANCELED) {
                setDetails(savedContact);
            }
        }
    }
    protected void setDetails(Contact contact) {
        String nameText = contact.getName().trim();
        String workplaceText = contact.getWorkplace().trim();
        String numberText = contact.getPhoneNumber().trim();

        if (numberText.isEmpty()) {
            contactPageLayout.getChildAt(2).setVisibility(View.GONE);
        }
        if (nameText.isEmpty() && workplaceText.isEmpty()) {
            nameText += "Contact " + numberText;
        }
        contactName.setText(nameText);
        contactWorkplace.setText(workplaceText);
        contactNumber.setText(numberText);
    }
}