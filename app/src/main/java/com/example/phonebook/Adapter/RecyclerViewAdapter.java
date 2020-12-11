package com.example.phonebook.Adapter;

import android.content.Context;
import android.content.Intent;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.provider.CalendarContract;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phonebook.ContactModel.Contact;
import com.example.phonebook.ContactPage;

import com.example.phonebook.R;

import java.util.ArrayList;
import java.util.List;

// implements Filterable
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements Filterable{
    private Context context;
    private List<Contact> contactList, contactListBackup;
    String queryText="";
    public RecyclerViewAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
        this.contactListBackup= new ArrayList<>(contactList);
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
        String dataText= contact.getName();
        if(queryText!=null && !queryText.isEmpty()){
            int startPos= dataText.toLowerCase().indexOf(queryText.toLowerCase());
            int endPos= startPos + queryText.length();
            if(startPos!=-1){
                Spannable spannable= new SpannableString(dataText);
                ColorStateList colorStateList= new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.parseColor("#03DAC5")});
                TextAppearanceSpan textAppearanceSpan= new TextAppearanceSpan(null, Typeface.NORMAL, -1, colorStateList, null);
                spannable.setSpan(textAppearanceSpan, startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.contactName.setText(spannable);
                holder.contactWorkplace.setText(contact.getWorkplace().trim());
            }
            else{
                holder.contactName.setText(contact.getName().trim());
                holder.contactWorkplace.setText(contact.getWorkplace().trim());
            }
        }
        else {
            holder.contactName.setText(contact.getName().trim());
            holder.contactWorkplace.setText(contact.getWorkplace().trim());
        }
        if(position==getItemCount()-1)
            holder.dividerLine.setVisibility(View.GONE);
        else
            holder.dividerLine.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter= new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence keyword) {
            ArrayList<Contact> filteredList= new ArrayList<>();
            if(keyword.toString().isEmpty()) {
                queryText= null;
                filteredList.addAll(contactListBackup);
            }

            else {
                queryText= keyword.toString();
                for(Contact curContact: contactListBackup){
                    if(curContact.getName().toLowerCase().contains(keyword.toString().toLowerCase()))
                        filteredList.add(curContact);
                }
            }

            FilterResults filteredResult= new FilterResults();
            filteredResult.values= filteredList;
            filteredResult.count= filteredList.size();
            return filteredResult;
        }

        @Override
        protected void publishResults(CharSequence keyword, FilterResults filteredResult) {
            contactList.clear();
            contactList.addAll((ArrayList<Contact>)filteredResult.values);
            notifyDataSetChanged();
        }
    };
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
