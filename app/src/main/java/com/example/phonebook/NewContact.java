package com.example.phonebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.phonebook.Adapter.RecyclerViewAdapter;
import com.example.phonebook.ContactModel.Contact;
import com.example.phonebook.Data.DbHandler;

import org.w3c.dom.Text;

public class NewContact extends AppCompatActivity {
    private Button cancelB, saveB;
    private TextView pageTitleText;
    private EditText addName, addWorkplace, addNumber;
    private String nameVal, workplaceVal, numberVal;
    private Contact curContact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        addName= findViewById(R.id.addName);
        addWorkplace= findViewById(R.id.addWorkplace);
        addNumber= findViewById(R.id.addNumber);

        //Toolbar
        cancelB= findViewById(R.id.left);
        pageTitleText= findViewById(R.id.title);
        saveB= findViewById(R.id.right);

        cancelB.setText(R.string.cancel);
        pageTitleText.setText(R.string.newContact);
        saveB.setText(R.string.save);

        // Editing a Contact
        final Contact savedContact= (Contact)getIntent().getSerializableExtra("savedContactObject");

        // Disable Save button until any data entered.
        saveB.setEnabled(false);
        saveB.setTextColor(Color.parseColor("#D3D3D3"));

        TextWatcher saveTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nameVal= addName.getText().toString();
                workplaceVal= addWorkplace.getText().toString();
                numberVal= addNumber.getText().toString();
                boolean enable;
                if(savedContact!=null) {
                    //When editing an existing Contact, disable Save Button until any changes has been made in it.
                    //Sets true if all the field has same data as earlier (No changes made in any field)
                    boolean isChanged = (nameVal.equals(savedContact.getName())
                            && workplaceVal.equals(savedContact.getWorkplace())
                            && numberVal.equals(savedContact.getPhoneNumber()));
                    enable= !isChanged;     // if no changes then set false for enable.
                }
                else {
                    // When creating a New Contact, disable Save Button until any data has been entered.
                    //Sets true if any one field is not Empty.
                    boolean hasData = (!nameVal.trim().isEmpty() || !workplaceVal.trim().isEmpty() || !numberVal.trim().isEmpty());
                    enable= hasData;
                }
                if(enable){
                    saveB.setEnabled(true);
                    saveB.setTextColor(getColor(R.color.colorAccent));
                }
                else {
                    saveB.setEnabled(false);
                    saveB.setTextColor(Color.parseColor("#D3D3D3"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        if(savedContact!=null) {
            addName.setText(savedContact.getName());
            addWorkplace.setText(savedContact.getWorkplace());
            addNumber.setText(savedContact.getPhoneNumber());
            pageTitleText.setText(R.string.editContact);
        }
        addName.addTextChangedListener(saveTextWatcher);
        addWorkplace.addTextChangedListener(saveTextWatcher);
        addNumber.addTextChangedListener(saveTextWatcher);

        saveB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbHandler db= new DbHandler(NewContact.this);

                nameVal= addName.getText().toString();
                workplaceVal= addWorkplace.getText().toString();
                numberVal= addNumber.getText().toString();
                curContact= new Contact(nameVal, workplaceVal, numberVal);

                if(savedContact!=null){
                    //Update Contact
                    int affectedRow= db.updateContactById(curContact, savedContact.getId());
                    if(affectedRow!=0) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("updatedContactObject", db.getContact(savedContact.getId()));
                        db.close();
                        setResult(RESULT_OK, resultIntent);
                    }
                }
                else {
                    //New Contact
                    db.addContact(curContact);
                    db.close();

                    Intent contactPage = new Intent(getApplicationContext(), ContactPage.class);
                    contactPage.putExtra("contactObject", curContact);
                    startActivity(contactPage);
                }
            }
        });

        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent;
                if(savedContact==null) {
                    backIntent = new Intent(NewContact.this, MainActivity.class);
                }
                else{
                    backIntent = new Intent();
                    setResult(RESULT_CANCELED);
                }
                startActivity(backIntent);
            }
        });

    }
}