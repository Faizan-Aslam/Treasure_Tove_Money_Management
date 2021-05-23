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
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressBarActions progressBarActions = new ProgressBarActions();

    private EditText editTextEmail;
    private ProgressBar progressBar;
    private Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_forgot_password);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_forgotpasswordactivity);

            mAuth = FirebaseAuth.getInstance();
            editTextEmail = (EditText) findViewById(R.id.txt_email);
            progressBar = (ProgressBar) findViewById(R.id.progress_bar);

            submit = (Button) findViewById(R.id.btn_login);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetPassword();
                }
            });
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void resetPassword() {
        try{
            final String email = editTextEmail.getText().toString().trim();

            if(email.isEmpty()){
                editTextEmail.setError("Email id is required!");
                editTextEmail.requestFocus();
                return;
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                editTextEmail.setError("Please provide a valid Email id");
                editTextEmail.requestFocus();
                return;
            }
            progressBarActions.showProgressBar(progressBar,ForgotPasswordActivity.this);
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Check your email to reset your password!", Toast.LENGTH_LONG).show();
                        progressBarActions.hideProgressBar(progressBar, ForgotPasswordActivity.this);
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Tray again! Something wrong happened!", Toast.LENGTH_LONG).show();
                        progressBarActions.hideProgressBar(progressBar, ForgotPasswordActivity.this);
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
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return false;
    }
}
