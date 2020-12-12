package com.example.phonebook;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.phonebook.ContactModel.Contact;
import com.example.phonebook.Data.DbHandler;

public class NewContact extends AppCompatActivity {
    private Button cancelB, saveB;
    private TextView pageTitleText;
    private EditText addName, addWorkplace, addNumber;
    private String nameVal, workplaceVal, numberVal;
    private boolean existContact;
    protected Contact editContactObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        addName = findViewById(R.id.addName);
        addWorkplace = findViewById(R.id.addWorkplace);
        addNumber = findViewById(R.id.addNumber);

        //Toolbar
        cancelB = findViewById(R.id.left);
        pageTitleText = findViewById(R.id.title);
        saveB = findViewById(R.id.right);

        cancelB.setText(R.string.cancel);
        pageTitleText.setText(R.string.newContact);
        saveB.setText(R.string.save);

        // Editing a Contact
        existContact = false;
        editContactObject = (Contact) getIntent().getSerializableExtra("contactObject");
        if (editContactObject != null)
            existContact = true;

        if (existContact) {
            Log.d("dbContacts", editContactObject.getId()+"");
            addName.setText(editContactObject.getName());
            addWorkplace.setText(editContactObject.getWorkplace());
            addNumber.setText(editContactObject.getPhoneNumber());
            pageTitleText.setText(R.string.editContact);
        }

        // Disable Save button until any data entered.
        saveB.setEnabled(false);
        saveB.setTextColor(Color.parseColor("#D3D3D3"));

        TextWatcher saveTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nameVal = addName.getText().toString();
                workplaceVal = addWorkplace.getText().toString();
                numberVal = addNumber.getText().toString();
                boolean enable;
                if (existContact) {
                    //When editing an existing Contact, disable Save Button until any changes has been made in it.
                    //Sets true if all the field has same data as earlier (No changes made in any field)
                    boolean isChanged = (nameVal.equals(editContactObject.getName())
                            && workplaceVal.equals(editContactObject.getWorkplace())
                            && numberVal.equals(editContactObject.getPhoneNumber()));
                    enable = !isChanged;     // if no changes then set false for enable.
                } else {
                    // When creating a New Contact, disable Save Button until any data has been entered.
                    //Sets true if any one field is not Empty.
                    boolean hasData = (!nameVal.trim().isEmpty() || !workplaceVal.trim().isEmpty() || !numberVal.trim().isEmpty());
                    enable = hasData;
                }
                if (enable) {
                    saveB.setEnabled(true);
                    saveB.setTextColor(getColor(R.color.colorAccent));
                } else {
                    saveB.setEnabled(false);
                    saveB.setTextColor(Color.parseColor("#D3D3D3"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        addName.addTextChangedListener(saveTextWatcher);
        addWorkplace.addTextChangedListener(saveTextWatcher);
        addNumber.addTextChangedListener(saveTextWatcher);

        saveB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbHandler db = new DbHandler(NewContact.this);

                nameVal = addName.getText().toString();
                workplaceVal = addWorkplace.getText().toString();
                numberVal = addNumber.getText().toString();
                if (existContact) {
                    //Update Contact
                    editContactObject.setName(nameVal);
                    editContactObject.setWorkplace(workplaceVal);
                    editContactObject.setPhoneNumber(numberVal);

                    if(db.updateContact(editContactObject)!=0) {
                        Log.d("dbContacts", "Updated Successfully!");
                        Intent updatedContactIntent = new Intent();
                        updatedContactIntent.putExtra("updatedContactObject", editContactObject);
                        db.close();
                        setResult(Activity.RESULT_OK, updatedContactIntent);
                        finish();
                    }
                }
                else {
                    //New Contact
                    Contact newContact= new Contact(nameVal, workplaceVal, numberVal);
                    int newContactId= db.addContact(newContact);
                    newContact.setId(newContactId);

                    Intent contactPage = new Intent(NewContact.this, ContactPage.class);
                    contactPage.putExtra("contactObject", newContact);
                    db.close();
                    startActivity(contactPage);
                }
            }
        });
        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent;
                if (existContact) {
                    backIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED);
                } else {
                    backIntent = new Intent(NewContact.this, MainActivity.class);
                }
                startActivity(backIntent);
            }
        });
    }
}