package com.example.phonebook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.example.phonebook.R;

import java.util.ArrayList;
import java.util.List;

// implements Filterable
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Contact> contactList, contactListAll;

    public RecyclerViewAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
        this.contactListAll= new ArrayList<>(contactList);
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

        holder.contactName.setText(contact.getName());
        holder.contactWorkplace.setText(contact.getWorkplace());
    }
    @Override
    public int getItemCount() {
        return contactList.size();
    }

    /*@Override
    public Filter getFilter() {
        return filter;
    }
    Filter filter= new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            return null;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

        }
    };*/
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView contactName;
        TextView contactWorkplace;
        ImageView contactImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            contactName= itemView.findViewById(R.id.name);
            contactWorkplace= itemView.findViewById(R.id.workplace);
            contactImage= itemView.findViewById(R.id.image);

            contactImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position= this.getAdapterPosition();
            Contact contact= contactList.get(position);
            String nameVal= contact.getName();
            String workplaceVal= contact.getWorkplace();
            String numberVal= contact.getPhoneNumber();
            /*Toast.makeText(context, "Position: " + position +
                    " Name: " + nameVal + " (" + workplaceVal + ")" +
                    " Phone: " + numberVal, Toast.LENGTH_SHORT).show();*/
            //Log.d("clickedViewHolder", "ViewHolder Clicked.");

            Intent intent= new Intent(context, ContactPage.class);
            intent.putExtra("contact_name", nameVal);
            intent.putExtra("contact_workplace", workplaceVal);
            intent.putExtra("contact_number", numberVal);
            context.startActivity(intent);
        }
    }
}
