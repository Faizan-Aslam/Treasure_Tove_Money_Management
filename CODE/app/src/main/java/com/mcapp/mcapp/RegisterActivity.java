package com.mcapp.mcapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private ProgressBarActions progressBarActions = new ProgressBarActions();
    GeneralClass generalClass = new GeneralClass();
    private EditText editTextName, editTextContactNo, editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_registeractivity);

            // Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance();

            submit = (Button) findViewById(R.id.btn_register);
            submit.setOnClickListener(this);
            editTextName = (EditText) findViewById(R.id.txt_name);
            editTextContactNo = (EditText) findViewById(R.id.txt_contactnum);
            editTextEmail = (EditText) findViewById(R.id.txt_email);
            editTextPassword = (EditText) findViewById(R.id.txt_password);
            progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void submit(View view) {
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btn_register:
                    registerUser();
                    break;
            }
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void registerUser() {
        try {
            generalClass.hideKeyboardActivity(RegisterActivity.this);

            final String email = editTextEmail.getText().toString().trim();
            final String password = editTextPassword.getText().toString().trim();
            final String name = editTextName.getText().toString().trim();
            final String contactNo = editTextContactNo.getText().toString().trim();

            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

            if (name.isEmpty()) {
                editTextName.setError("Name is required!");
                editTextName.requestFocus();
                return;
            }
            if (contactNo.isEmpty()) {
                editTextContactNo.setError("Contact number is required!");
                editTextContactNo.requestFocus();
                return;
            }
            if (email.isEmpty()) {
                editTextEmail.setError("Email id is required!");
                editTextEmail.requestFocus();
                return;
            }
            if (password.isEmpty()) {
                editTextPassword.setError("Password is required!");
                editTextPassword.requestFocus();
                return;
            }
            if (!email.matches(emailPattern)) {
                editTextEmail.setError("Please provide a valid Email id");
                editTextEmail.requestFocus();
                return;
            }
            if (contactNo.length() > 11) {
                editTextContactNo.setError("Contact number should not be more than 11 digits!");
                editTextContactNo.requestFocus();
                return;
            }
            if (password.length() < 6) {
                editTextPassword.setError("Min password length should be 6 characters");
                editTextPassword.requestFocus();
                return;
            }

            progressBarActions.showProgressBar(progressBar, RegisterActivity.this);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                User user = new User(name, contactNo, email);
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "Registration successful..!!", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            progressBarActions.hideProgressBar(progressBar, RegisterActivity.this);
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                                            progressBarActions.hideProgressBar(progressBar, RegisterActivity.this);
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(RegisterActivity.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                                progressBarActions.hideProgressBar(progressBar, RegisterActivity.this);
                            }
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
        catch ( Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return false;
    }
}
