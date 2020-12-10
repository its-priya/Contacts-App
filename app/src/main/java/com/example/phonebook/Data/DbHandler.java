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
    public DbHandler(Context context){
        super(context, Parameters.DB_NAME, null, Parameters.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createDb= "CREATE TABLE " + Parameters.TABLE_NAME + "("
                + Parameters.KEY_ID + " INTEGER PRIMARY KEY, "
                + Parameters.KEY_NAME + " TEXT, "
                + Parameters.KEY_WORKPLACE + " TEXT, "
                + Parameters.KEY_PHONE + " TEXT" + ")";
        db.execSQL(createDb);
        Log.d("dbContacts", Parameters.TABLE_NAME+" successfully created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void addContact(Contact contact){
        SQLiteDatabase db= this.getWritableDatabase();

        ContentValues contactValues= new ContentValues();
        contactValues.put(Parameters.KEY_NAME, contact.getName());
        contactValues.put(Parameters.KEY_WORKPLACE, contact.getWorkplace());
        contactValues.put(Parameters.KEY_PHONE, contact.getPhoneNumber());

        db.insert(Parameters.TABLE_NAME, null, contactValues);
        Log.d("dbContacts", "Contact successfully Inserted");
        db.close();
    }
    public List<Contact> getContactList(){
        List<Contact> contactList = new ArrayList<>();
        SQLiteDatabase db= this.getReadableDatabase();

        //Query to read data from the database.
        String selectData= "SELECT * FROM " + Parameters.TABLE_NAME;
        Cursor cursor= db.rawQuery(selectData, null);

        //Traverse through data
        if(cursor.moveToFirst()){
            do{
                Contact contact= new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setWorkplace(cursor.getString(2));
                contact.setPhoneNumber(cursor.getString(3));
                contactList.add(contact);
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return contactList;
    }
    public Contact getContact(int id){
        SQLiteDatabase db= this.getReadableDatabase();
        String selectData= "SELECT KEY_NAME, KEY_WORKPLACE, KEY_PHONE FROM "+Parameters.TABLE_NAME + " WHERE " + Parameters.KEY_ID + "=?" + id;
        Cursor cursor= db.rawQuery(selectData, null);
        Contact contact = new Contact();
        contact.setName(cursor.getString(0));
        contact.setWorkplace(cursor.getString(1));
        contact.setPhoneNumber(cursor.getString(2));
        return contact;
    }
    public int updateContactById(Contact contact, int id){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues contactValues= new ContentValues();
        contactValues.put(Parameters.KEY_NAME, contact.getName());
        contactValues.put(Parameters.KEY_WORKPLACE, contact.getWorkplace());
        contactValues.put(Parameters.KEY_PHONE, contact.getPhoneNumber());
        return db.update(Parameters.TABLE_NAME, contactValues,Parameters.KEY_ID + "=?", new String[]{String.valueOf(id)});
    }
    public int updateContact(Contact contact){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues contactValues= new ContentValues();
        contactValues.put(Parameters.KEY_NAME, contact.getName());
        contactValues.put(Parameters.KEY_WORKPLACE, contact.getWorkplace());
        contactValues.put(Parameters.KEY_PHONE, contact.getPhoneNumber());
        return db.update(Parameters.TABLE_NAME, contactValues,Parameters.KEY_ID + "=?", new String[]{String.valueOf(contact.getId())});
    }
    public void deleteContactById(int id){
        SQLiteDatabase db= this.getWritableDatabase();
        db.delete(Parameters.TABLE_NAME, Parameters.KEY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
    public void deleteContact(String contactName){
        SQLiteDatabase db= this.getWritableDatabase();
        db.delete(Parameters.TABLE_NAME, Parameters.KEY_NAME + "=?", new String[]{String.valueOf(contactName)});
        db.close();
    }
    public int countContacts(){
        SQLiteDatabase db= this.getReadableDatabase();
        String selectData= "SELECT * FROM "+ Parameters.TABLE_NAME;
        Cursor cursor= db.rawQuery(selectData, null);
        return cursor.getCount();
    }
}
