package com.mcapp.mcapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewItemActivity extends AppCompatActivity {

    private TextView id,transactionName,amount,comment,date;
    String category_selected,payment_selected;
    private Spinner category_spinner,payment_spinner;
    Button btnDelete,btnUpdate;
    ProgressBar progressBar;
    AlertDialog.Builder builder;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressBarActions progressBarActions = new ProgressBarActions();
    private GeneralClass generalClass = new GeneralClass();
    String id_;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view_item);
            getSupportActionBar().setTitle(R.string.title_viewitemactivity);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            transactionName = findViewById(R.id.txt_transactionName);
            amount = findViewById(R.id.txt_amount);
            category_spinner = findViewById(R.id.spinner_category);
            comment = findViewById(R.id.txt_comment);
            date = findViewById(R.id.txt_date);
            payment_spinner = findViewById(R.id.spinner_paymentMethod);
            progressBar = (ProgressBar) findViewById(R.id.progress_bar);

            onLoadData();

            builder = new AlertDialog.Builder(this);

            btnDelete = findViewById(R.id.btn_delete);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete();
                }
            });

            btnUpdate = findViewById(R.id.btn_update);
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validateForm()) {
                        update();
                    }
                }
            });

            category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    category_selected = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            payment_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    payment_selected = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void onLoadData(){
        try {
            progressBarActions.showProgressBar(progressBar, ViewItemActivity.this);

            Intent intent = getIntent();
            id_ = intent.getStringExtra("id");

            String transactionName_ = getIntent().getStringExtra("transactionName");
            String amount_ = getIntent().getStringExtra("amount");
            category_selected = getIntent().getStringExtra("category");
            String comment_ = getIntent().getStringExtra("comment");
            String date_ = getIntent().getStringExtra("date");
            payment_selected = getIntent().getStringExtra("paymentMethod");

            String[] cate = getResources().getStringArray(R.array.category_names);
            String[] paym = getResources().getStringArray(R.array.payment_method);
            List<String> categoryList = new ArrayList<String>(Arrays.asList(cate));
            List<String> paymentList = new ArrayList<String>(Arrays.asList(paym));

            transactionName.setText(transactionName_);
            amount.setText(amount_);
            date.setText(date_);
            comment.setText(comment_);
            category_spinner.setSelection(categoryList.indexOf(category_selected));
            payment_spinner.setSelection(paymentList.indexOf(payment_selected));
            //Toast.makeText(getApplicationContext(),"category pos:  "+category_selected + categoryList.indexOf(category_selected),Toast.LENGTH_SHORT).show();

            progressBarActions.hideProgressBar(progressBar, ViewItemActivity.this);
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    private void delete(){
        try {
            //Setting message manually and performing action on button click
            builder.setMessage("Do you want to delete this transaction ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //finish();
                            deleteItem();
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
                            dialog.cancel();
                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Delete Transaction");
            alert.show();
        }
        catch ( Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void update(){
        try {
            generalClass.hideKeyboardActivity(ViewItemActivity.this);

            //Setting message manually and performing action on button click
            builder.setMessage("Do you want to update this transaction ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //finish();
                            updateItem();
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
                            dialog.cancel();
                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Update Transaction");
            alert.show();
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void updateItem(){
        try {
            progressBarActions.showProgressBar(progressBar, ViewItemActivity.this);
            Map<String, Object> dataToSave = new HashMap<String, Object>();
            dataToSave.put("TransactionName", transactionName.getText().toString().trim());
            dataToSave.put("Amount", amount.getText().toString().trim());
            dataToSave.put("Category", category_selected);
            dataToSave.put("Comment", comment.getText().toString().trim());
            dataToSave.put("PaymentMethod", payment_selected);
            dataToSave.put("Date", date.getText().toString());
            db.collection("MCCollection").document(id_)
                    .set(dataToSave)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBarActions.hideProgressBar(progressBar, ViewItemActivity.this);
                            Toast.makeText(getApplicationContext(), "Transaction updated..", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBarActions.hideProgressBar(progressBar, ViewItemActivity.this);
                            Toast.makeText(getApplicationContext(), "Failed to update..", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    private void deleteItem(){
        try {
            progressBarActions.showProgressBar(progressBar, ViewItemActivity.this);
            db.collection("MCCollection").document(id_)
                    .delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBarActions.hideProgressBar(progressBar, ViewItemActivity.this);
                            Toast.makeText(getApplicationContext(), "Transaction deleted..", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBarActions.hideProgressBar(progressBar, ViewItemActivity.this);
                    Toast.makeText(getApplicationContext(), "Failed to delete..", Toast.LENGTH_SHORT).show();
                    finish();
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

    public Boolean validateForm(){
        try {
            if (transactionName.getText().toString().trim().isEmpty()) {
                transactionName.setError("Please enter transaction name");
                transactionName.requestFocus();
                return false;
            }
            if (amount.getText().toString().trim().isEmpty()) {
                amount.setError("Please enter amount spent");
                amount.requestFocus();
                return false;
            }
            if (comment.getText().toString().trim().isEmpty()) {
                comment.setError("Please enter a comment");
                comment.requestFocus();
                return false;
            }
            if (amount.getText().length() <= 0) {
                amount.setError("Please enter a valid amount");
                amount.requestFocus();
                return false;
            }
            if (Integer.parseInt(amount.getText().toString()) <= 0) {
                amount.setError("Please enter a valid amount");
                amount.requestFocus();
                return false;
            }
            if (amount.getText().toString().length() > 8) {
                amount.setError("Amount should not exceed 99999999");
                amount.requestFocus();
                return false;
            }
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return true;
    }
}
