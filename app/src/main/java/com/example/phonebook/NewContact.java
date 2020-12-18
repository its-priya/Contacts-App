package com.example.phonebook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;

import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.phonebook.ContactModel.Contact;
import com.example.phonebook.Data.DbHandler;


public class NewContact extends AppCompatActivity {
    private Button cancelB, saveB;
    private ImageButton addImage;
    private TextView pageTitleText;
    private EditText addName, addWorkplace, addNumber;
    private String nameVal, workplaceVal, numberVal;
    private Uri imageUri;
    private Bitmap imageBitmap = null;
    private boolean existContact;
    protected Contact editContactObject;
    private AlertDialog alertDialog;
    private static boolean imageChanged;

    public static final int CAMERA_REQUEST_CODE = 1;
    public static final int GALLARY_REQUEST_CODE = 2;
    protected static final String cameraPermission = Manifest.permission.CAMERA;
    protected static final String gallaryPermission = Manifest.permission.READ_EXTERNAL_STORAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        addName = findViewById(R.id.addName);
        addWorkplace = findViewById(R.id.addWorkplace);
        addNumber = findViewById(R.id.addNumber);
        addImage = findViewById(R.id.addImage);

        //Toolbar
        cancelB = findViewById(R.id.left);
        pageTitleText = findViewById(R.id.title);
        saveB = findViewById(R.id.right);

        cancelB.setText(R.string.cancel);
        pageTitleText.setText(R.string.newContact);
        saveB.setText(R.string.save);
        imageChanged= false;

        // Editing a Contact
        existContact = false;
        editContactObject = (Contact) getIntent().getSerializableExtra("contactObject");
        if (editContactObject != null)
            existContact = true;

        if (existContact) {
            Log.d("dbContacts", editContactObject.getId() + "");
            pageTitleText.setText(R.string.editContact);
            addName.setText(editContactObject.getName());
            addWorkplace.setText(editContactObject.getWorkplace());
            addNumber.setText(editContactObject.getPhoneNumber());
            if (editContactObject.getImageUri() != null)
                addImage.setImageURI(Uri.parse(editContactObject.getImageUri()));
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
                    if(imageUri!=null)
                        editContactObject.setImageUri(imageUri.toString());

                    if (db.updateContact(editContactObject) != 0) {
                        Log.d("dbContacts", "Updated Successfully!");
                        Intent updatedContactIntent = new Intent();
                        updatedContactIntent.putExtra("updatedContactObject", editContactObject);
                        db.close();
                        setResult(Activity.RESULT_OK, updatedContactIntent);
                        finish();
                    }
                } else {
                    //New Contact
                    Contact newContact = new Contact(nameVal, workplaceVal, numberVal);
                    if (imageUri != null)
                        newContact.setImageUri(imageUri.toString());

                    int newContactId = db.addContact(newContact);
                    newContact.setId(newContactId);
                    db.close();

                    Intent contactDetailsIntent = new Intent(NewContact.this, ContactPage.class);
                    contactDetailsIntent.putExtra("contactObject", newContact);
                    startActivity(contactDetailsIntent);
                }
            }
        });
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageAlertDialog();
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

    protected void showImageAlertDialog() {
        LayoutInflater dialogInflator = LayoutInflater.from(this);
        View dialogView = dialogInflator.inflate(R.layout.alert_dialog, null);
        Button takePhoto, selectPhoto, cancelPhoto;
        takePhoto = dialogView.findViewById(R.id.firstButton);
        selectPhoto = dialogView.findViewById(R.id.secondButton);
        cancelPhoto = dialogView.findViewById(R.id.cancelButton);

        alertDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(NewContact.this, cameraPermission) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(NewContact.this, new String[]{cameraPermission}, CAMERA_REQUEST_CODE);
                else {
                    openCamera();
                    alertDialog.dismiss();
                }
            }
        });
        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(NewContact.this, gallaryPermission) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(NewContact.this, new String[]{gallaryPermission}, GALLARY_REQUEST_CODE);
                else {
                    openGallary();
                    alertDialog.dismiss();
                }
            }
        });
        cancelPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChanged= false;
                alertDialog.dismiss();
            }
        });
        imageChanged= false;
    }

    public void openCamera() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePhotoIntent, CAMERA_REQUEST_CODE);
    }

    public void openGallary() {
        Intent gallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(gallaryIntent, "Select Picture"), GALLARY_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                    alertDialog.dismiss();
                }
                alertDialog.dismiss();
                break;
            }
            case GALLARY_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallary();
                    alertDialog.dismiss();
                }
                alertDialog.dismiss();
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                try {
                    imageUri = data.getData();
                    imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    addImage.setImageBitmap(imageBitmap);
                    imageChanged = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case GALLARY_REQUEST_CODE: {
                try {
                    imageUri = data.getData();
                    addImage.setImageURI(imageUri);
                    imageChanged = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        boolean hasAnyData= (!addName.getText().toString().trim().isEmpty()
                        || !addNumber.getText().toString().trim().isEmpty()
                        || !addWorkplace.getText().toString().trim().isEmpty());
        if(imageChanged && hasAnyData){
            saveB.setEnabled(true);
            saveB.setTextColor(getColor(R.color.colorAccent));
        }
        imageChanged= false;
    }
}