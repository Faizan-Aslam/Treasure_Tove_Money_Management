package com.mcapp.mcapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewProfileActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ProgressBarActions progressBarActions = new ProgressBarActions();

    private String userId;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view_profile);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_viewprofileactivity);

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            userId = firebaseUser.getUid();

            final EditText nameEditText = (EditText) findViewById(R.id.txt_name);
            final EditText contactNoEditText = (EditText) findViewById(R.id.txt_contactnum);
            final EditText emailEditText = (EditText) findViewById(R.id.txt_email);
            progressBar = (ProgressBar) findViewById(R.id.progress_bar);

            progressBarActions.showProgressBar(progressBar, ViewProfileActivity.this);
            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);
                    if (userProfile != null) {
                        nameEditText.setText(userProfile.name);
                        contactNoEditText.setText(userProfile.contactNo);
                        emailEditText.setText(userProfile.email);
                        progressBarActions.hideProgressBar(progressBar, ViewProfileActivity.this);
                    } else {
                        Toast.makeText(ViewProfileActivity.this, "Something wrong happened..!!", Toast.LENGTH_LONG).show();
                        progressBarActions.hideProgressBar(progressBar, ViewProfileActivity.this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                finish();
                return true;
            }
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return false;
    }
}
