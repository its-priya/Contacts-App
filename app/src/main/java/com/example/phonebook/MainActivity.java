package com.example.phonebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phonebook.Adapter.RecyclerViewAdapter;
import com.example.phonebook.ContactModel.Contact;
import com.example.phonebook.Data.DbHandler;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    public static RecyclerView recyclerView;
    protected static RecyclerViewAdapter recyclerViewAdapter;
    protected static ArrayList<Contact> contactArrayList;
    private Button editContacts, addContact;
    private TextView pageTitleText;
    public static TextView noContactsMsg;
    public static SearchView searchView;
    public static CheckBox isSelectedAll;
    protected static DbHandler db;
    public static boolean isDeleteMode;
    private int contactPosition;
    public static ArrayList<Contact> selectedContactList= new ArrayList<>();
    public static int counter=0;
    public static final int CALL_REQUEST_CODE = 4;
    protected static final String callPermission = Manifest.permission.CALL_PHONE;

    public static FragmentManager fragmentManager;
    public static Fragment deleteToolbarFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DbHandler(MainActivity.this);
        contactArrayList = new ArrayList<>(db.getContactList());

        editContacts = findViewById(R.id.left);
        pageTitleText = findViewById(R.id.title);
        addContact = findViewById(R.id.right);
        searchView = findViewById(R.id.searchView);
        isSelectedAll= findViewById(R.id.selectAll);
        noContactsMsg= findViewById(R.id.noContactsMsg);

        editContacts.setText(R.string.edit);
        pageTitleText.setText(R.string.contacts);
        isDeleteMode= false;
        addContact.setText("");
        addContact.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_person_add, 0);

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newContactPage = new Intent(MainActivity.this, NewContact.class);
                startActivity(newContactPage);
            }
        });
        Log.d("dbContacts", "You have " + db.countContacts() + " contact(s)");

        // RecyclerView
        recyclerView = findViewById(R.id.recyclerView);            // RecyclerView Initialization
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this, contactArrayList);
        recyclerView.setAdapter(recyclerViewAdapter);

        if(contactArrayList.size()==0){
            recyclerView.setVisibility(View.GONE);
            noContactsMsg.setText(getText(R.string.noContacts));
            noContactsMsg.setVisibility(View.VISIBLE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            noContactsMsg.setVisibility(View.GONE);
        }
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

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
        editContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDeleteMode= true;
                searchView.setVisibility(View.GONE);
                isSelectedAll.setVisibility(View.VISIBLE);
                fragmentManager= getSupportFragmentManager();
                deleteToolbarFrag= new OnDeleteToolbar();
                fragmentManager.beginTransaction().replace(R.id.mainToolbar, deleteToolbarFrag).addToBackStack(null).commit();
                recyclerViewAdapter.notifyDataSetChanged();
            }
        });
        isSelectedAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                recyclerViewAdapter.notifyDataSetChanged();
                selectedContactList.clear();
                if(isChecked) {
                    selectedContactList.addAll(contactArrayList);
                    counter= selectedContactList.size();
                    OnDeleteToolbar.deleteButton.setAlpha(1.0f);
                    OnDeleteToolbar.deleteButton.setClickable(true);
                }
                else {
                    counter = 0;
                    OnDeleteToolbar.deleteButton.setAlpha(0.4f);
                    OnDeleteToolbar.deleteButton.setClickable(false);
                }
                OnDeleteToolbar.updateToolbarText(counter);
            }
        });

    }

    public static void startSelection(View curView, int curPos){
        if(((CheckBox)curView).isChecked()) {
            selectedContactList.add(contactArrayList.get(curPos));
            counter++;
            OnDeleteToolbar.updateToolbarText(counter);
            OnDeleteToolbar.deleteButton.setAlpha(1.0f);
            OnDeleteToolbar.deleteButton.setClickable(true);
        }
        else{
            selectedContactList.remove(contactArrayList.get(curPos));
            counter--;
            OnDeleteToolbar.updateToolbarText(counter);
            if(counter==0) {
                OnDeleteToolbar.deleteButton.setAlpha(0.4f);
                OnDeleteToolbar.deleteButton.setClickable(false);
            }
        }
    }
    protected void callContact() {
        Contact contact = contactArrayList.get(contactPosition);
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + contact.getPhoneNumber()));
        startActivity(callIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callContact();
            }
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        contactPosition = position;
        if (ActivityCompat.checkSelfPermission(this, callPermission) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{callPermission}, CALL_REQUEST_CODE);
        callContact();
        recyclerViewAdapter.notifyItemChanged(contactPosition);
    }
    public static void showDeleteDialog(final Context context) {
        LayoutInflater layoutInflater= LayoutInflater.from(context);
        View dialogView = layoutInflater.inflate(R.layout.alert_dialog, null);
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
        if(isSelectedAll.isChecked())
            deleteButton.setText(R.string.deleteAllContacts);
        else if(counter==1)
            deleteButton.setText(R.string.deleteContact);
        else
            deleteButton.setText("Delete " + counter + " Contacts");
        deleteButton.setTextColor(Color.RED);
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(Contact curContact: selectedContactList){
                    db.deleteContactById(curContact.getId());
                    contactArrayList.remove(curContact);
                }
                alertDialog.dismiss();
                Toast.makeText(context, counter + "contact(s) deleted.", Toast.LENGTH_SHORT).show();
                removeDeleteToolbarFrag(deleteToolbarFrag);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }
    public static void removeDeleteToolbarFrag(Fragment fragment){
        counter= 0;
        selectedContactList.clear();
        isDeleteMode= false;
        recyclerViewAdapter.notifyDataSetChanged();
        searchView.setVisibility(View.VISIBLE);
        isSelectedAll.setVisibility(View.GONE);
        fragmentManager.beginTransaction().remove(fragment).commit();
    }
}