package com.example.phonebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class NewContact extends AppCompatActivity {
    Button cancel, save;
    TextView pageTitleText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        //Toolbar
        cancel= findViewById(R.id.left);
        pageTitleText= findViewById(R.id.title);
        save= findViewById(R.id.right);

        cancel.setText(R.string.cancel);
        pageTitleText.setText(R.string.newContact);
        save.setText(R.string.save);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(NewContact.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}