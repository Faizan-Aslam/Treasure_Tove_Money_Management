package com.mcapp.mcapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private ProgressBarActions progressBarActions = new ProgressBarActions();
    private GeneralClass generalClass = new GeneralClass();
    private TextView register, forgotPassword;
    private EditText editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    private Button signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            mAuth = FirebaseAuth.getInstance();

            register = (TextView) findViewById(R.id.register);
            register.setOnClickListener(this);
            forgotPassword = (TextView) findViewById(R.id.forgotPassword);
            forgotPassword.setOnClickListener(this);

            signin = (Button) findViewById(R.id.btn_login);
            signin.setOnClickListener(this);
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
                case R.id.register:
                    startActivity(new Intent(this, RegisterActivity.class));
                    break;
                case R.id.btn_login:
                    userLogin();
                    break;
                case R.id.forgotPassword:
                    startActivity(new Intent(this, ForgotPasswordActivity.class));
                    break;
            }
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void userLogin() {
        try {
            generalClass.hideKeyboardActivity(LoginActivity.this);

            final String email = editTextEmail.getText().toString().trim();
            final String password = editTextPassword.getText().toString().trim();

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
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextEmail.setError("Please provide a valid Email id");
                editTextEmail.requestFocus();
                return;
            }

            progressBarActions.showProgressBar(progressBar, LoginActivity.this);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        progressBarActions.hideProgressBar(progressBar, LoginActivity.this);
                    } else {
                        Toast.makeText(LoginActivity.this, "Failed to Login! Please check your credentials", Toast.LENGTH_LONG).show();
                        progressBarActions.hideProgressBar(progressBar, LoginActivity.this);
                    }
                }
            });
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
}
