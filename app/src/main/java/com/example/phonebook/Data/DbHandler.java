package com.example.phonebook.Data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.phonebook.ContactModel.Contact;
import com.example.phonebook.Parameters.Parameters;

import java.util.ArrayList;
import java.util.List;

public class DbHandler extends SQLiteOpenHelper {

    //Constructor
    public DbHandler(Context context) {
        super(context, Parameters.DB_NAME, null, Parameters.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createDb = "CREATE TABLE IF NOT EXISTS " + Parameters.TABLE_NAME + "("
                + Parameters.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Parameters.KEY_NAME + " TEXT, "
                + Parameters.KEY_WORKPLACE + " TEXT, "
                + Parameters.KEY_PHONE + " TEXT, "
                + Parameters.KEY_IMG_URI + " TEXT" + ")";
        db.execSQL(createDb);
        Log.d("dbContacts", Parameters.TABLE_NAME + " successfully created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int addContact(Contact contact) {
        long newRowId = 0;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            //Store data
            ContentValues contactValues = new ContentValues();
            contactValues.put(Parameters.KEY_NAME, contact.getName());
            contactValues.put(Parameters.KEY_WORKPLACE, contact.getWorkplace());
            contactValues.put(Parameters.KEY_PHONE, contact.getPhoneNumber());
            contactValues.put(Parameters.KEY_IMG_URI, contact.getImageUri());

            newRowId = db.insert(Parameters.TABLE_NAME, null, contactValues);
            Log.d("dbContacts", "Contact successfully Inserted with id: "+newRowId);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (int) newRowId;
    }

    public List<Contact> getContactList() {
        List<Contact> contactList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        //Query to read data from the database.
        String selectData = "SELECT * FROM " + Parameters.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectData, null);

        //Traverse through data
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setWorkplace(cursor.getString(2));
                contact.setPhoneNumber(cursor.getString(3));
                try {
                    contact.setImageUri(cursor.getString(4));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                contactList.add(contact);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return contactList;
    }

    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contactValues = new ContentValues();
        contactValues.put(Parameters.KEY_NAME, contact.getName());
        contactValues.put(Parameters.KEY_WORKPLACE, contact.getWorkplace());
        contactValues.put(Parameters.KEY_PHONE, contact.getPhoneNumber());

        // Convert to byte Array
        try {
            contactValues.put(Parameters.KEY_IMG_URI, contact.getImageUri());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return db.update(Parameters.TABLE_NAME, contactValues, Parameters.KEY_ID + "=?", new String[]{String.valueOf(contact.getId())});
    }

    public void deleteContactById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Parameters.TABLE_NAME, Parameters.KEY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public int countContacts() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectData = "SELECT * FROM " + Parameters.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectData, null);
        return cursor.getCount();
    }
}
