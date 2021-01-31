package com.example.phonebook;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.phonebook.ContactModel.Contact;
import com.example.phonebook.Data.DbHandler;

public class ContactPage extends AppCompatActivity {
    private TextView contactName, contactWorkplace, contactNumber;
    private Button backToContacts, editContact, deleteContact, shareContact;
    private ImageButton msgContact;
    private ImageView contactImage;
    private TextView pageTitle;
    private LinearLayout contactPageLayout;
    private Contact savedContact;
    public static final int EDIT_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_page);

        //Toolbar
        backToContacts = findViewById(R.id.left);
        pageTitle = findViewById(R.id.title);
        editContact = findViewById(R.id.right);
        deleteContact = findViewById(R.id.deleteContact);
        msgContact= findViewById(R.id.textSMS);
        shareContact= findViewById(R.id.shareContact);
        backToContacts.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_back,0,0,0);

        contactPageLayout = findViewById(R.id.contactPageLayout);

        contactName = findViewById(R.id.contactName);
        contactWorkplace = findViewById(R.id.contactWorkplace);
        contactNumber = findViewById(R.id.contactNumber);
        contactImage = findViewById(R.id.contactImage);

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
                editPage.putExtra("contactObject", savedContact);
                startActivityForResult(editPage, EDIT_REQUEST_CODE);
            }
        });

        deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog();
            }
        });
        contactNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent= new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + contactNumber.getText()));
                startActivity(callIntent);
            }
        });
        msgContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent msgIntent= new Intent();
                msgIntent.addCategory(Intent.CATEGORY_DEFAULT);
                msgIntent.setType("vnd.android-dir/mms-sms");
                msgIntent.setData(Uri.parse("sms:" + contactNumber.getText()));
                startActivity(msgIntent);
            }
        });
        shareContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent= new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                if(contactPageLayout.getChildAt(2).getVisibility()==View.GONE)
                    sendIntent.putExtra(Intent.EXTRA_TEXT, contactName.getText());
                else
                    sendIntent.putExtra(Intent.EXTRA_TEXT, contactNumber.getText());
                Intent appChooserIntent= Intent.createChooser(sendIntent, getString(R.string.shareContact));
                startActivity(appChooserIntent);
            }
        });
    }

    protected void showDeleteDialog() {
        LayoutInflater dialogInflator = LayoutInflater.from(this);
        View dialogView = dialogInflator.inflate(R.layout.alert_dialog, null);
        Button firstButton, secondButton, deleteButton, cancelButton;
        View dividerLine1, dividerLine2;
        dividerLine1= dialogView.findViewById(R.id.dialogDividerLine1);
        dividerLine2= dialogView.findViewById(R.id.dialogDividerLine2);
        dividerLine1.setVisibility(View.GONE);
        dividerLine2.setVisibility(View.GONE);
        firstButton = dialogView.findViewById(R.id.firstButton);
        secondButton= dialogView.findViewById(R.id.secondButton);
        deleteButton = dialogView.findViewById(R.id.thirdButton);
        cancelButton = dialogView.findViewById(R.id.cancelButton);

        firstButton.setVisibility(View.GONE);
        secondButton.setVisibility(View.GONE);
        deleteButton.setText("Delete Contact");
        deleteButton.setTextColor(Color.RED);

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbHandler db = new DbHandler(ContactPage.this);
                db.deleteContactById(savedContact.getId());
                db.close();
                Intent backToContacts = new Intent(ContactPage.this, MainActivity.class);
                startActivity(backToContacts);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
            setDetails((Contact) data.getSerializableExtra("updatedContactObject"));
        }
    }

    protected void setDetails(Contact contact) {
        String nameText = contact.getName().trim();
        String workplaceText = contact.getWorkplace().trim();
        String numberText = contact.getPhoneNumber().trim();
        String imageUriText = contact.getImageUri();

        Log.d("dbContacts", contact.getId() + "");

        if (nameText.isEmpty() && workplaceText.isEmpty()) {
            nameText = "Contact " + numberText;
        }
        if (numberText.isEmpty())
            contactPageLayout.getChildAt(2).setVisibility(View.GONE);
        else
            contactPageLayout.getChildAt(2).setVisibility(View.VISIBLE);

        contactName.setText(nameText);
        contactWorkplace.setText(workplaceText);
        contactNumber.setText(numberText);
        try {
            if(imageUriText!=null)
                contactImage.setImageURI(Uri.parse(imageUriText));
            else
                contactImage.setImageResource(R.drawable.ic_account_circle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}