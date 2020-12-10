package com.example.phonebook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phonebook.ContactModel.Contact;
import com.example.phonebook.ContactPage;
import com.example.phonebook.Data.DbHandler;
import com.example.phonebook.R;

import java.util.ArrayList;
import java.util.List;

// implements Filterable
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private Context context;
    private List<Contact> contactList;
    public RecyclerViewAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }
    // Where to get the single card as ViewHolder object.
    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ViewHolder(view);
    }
    // What will happen after creating the ViewHolder object?
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        Contact contact= contactList.get(position);
        if(position==getItemCount()-1){
            holder.dividerLine.setVisibility(View.GONE);
        }
        holder.contactName.setText(contact.getName().trim());
        holder.contactWorkplace.setText(contact.getWorkplace().trim());
    }
    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView contactName;
        TextView contactWorkplace;
        ImageView contactImage;
        View dividerLine;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            contactName= itemView.findViewById(R.id.name);
            contactWorkplace= itemView.findViewById(R.id.workplace);
            contactImage= itemView.findViewById(R.id.image);
            dividerLine= itemView.findViewById(R.id.dividerLine);

            contactImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position= this.getAdapterPosition();
            Contact contact= contactList.get(position);

            Intent contactPage= new Intent(context, ContactPage.class);
            contactPage.putExtra("contactObject", contact);
            context.startActivity(contactPage);
        }
    }
}
