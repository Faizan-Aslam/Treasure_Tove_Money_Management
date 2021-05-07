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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

    private TextView id,amount,comment,date,paymentMethod;
    String category_selected,payment_selected;
    private Spinner category_spinner,payment_spinner;
    Button btnDelete,btnUpdate;
    AlertDialog.Builder builder;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String id_;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //id = findViewById(R.id.textView1);
        amount = findViewById(R.id.txt_amount);
        //category = findViewById(R.id.txt_category);
        category_spinner = findViewById(R.id.spinner_category);
        comment = findViewById(R.id.txt_comment);
        date = findViewById(R.id.txt_date);
        //paymentMethod = findViewById(R.id.txt_paymentMethod);
        payment_spinner = findViewById(R.id.spinner_paymentMethod);

        Intent intent = getIntent();
        id_ = intent.getStringExtra("id");
        Toast.makeText(getApplicationContext(),"Id : ."+ id_,Toast.LENGTH_SHORT).show();

        String amount_ = getIntent().getStringExtra("amount");
        category_selected = getIntent().getStringExtra("category");
        String comment_ = getIntent().getStringExtra("comment");
        String date_ = getIntent().getStringExtra("date");
        payment_selected = getIntent().getStringExtra("paymentMethod");

        String[] cate = getResources().getStringArray(R.array.category_names);
        String[] paym = getResources().getStringArray(R.array.payment_method);
        List<String> categoryList = new ArrayList<String>(Arrays.asList(cate));
        List<String> paymentList = new ArrayList<String>(Arrays.asList(paym));

        //id.setText(amount_);
        amount.setText(amount_);
        //category.setText(category_);
        comment.setText(comment_);
        date.setText(date_);
        category_spinner.setSelection(categoryList.indexOf(category_selected));
        payment_spinner.setSelection(paymentList.indexOf(payment_selected));
        Toast.makeText(getApplicationContext(),"category pos:  "+category_selected + categoryList.indexOf(category_selected),Toast.LENGTH_SHORT).show();


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
                update();
            }
        });

        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category_selected = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(),category_selected,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        payment_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                payment_selected = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void delete(){
        //Setting message manually and performing action on button click
        builder.setMessage("Do you want to delete this transaction ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        deleteItem();
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

    private void update(){
        //Setting message manually and performing action on button click
        builder.setMessage("Do you want to update this transaction ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        updateItem();
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

    private void updateItem(){
        Map<String,Object> dataToSave = new HashMap<String,Object>();
        dataToSave.put("Amount",amount.getText().toString());
        dataToSave.put("Category",category_selected);
        dataToSave.put("Comment", comment.getText().toString());
        dataToSave.put("PaymentMethod",payment_selected);
        dataToSave.put("Date",date.getText().toString());
        Toast.makeText(getApplicationContext(),category_selected,Toast.LENGTH_SHORT).show();
        db.collection("MCCollection").document(id_)
                .set(dataToSave)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Transaction updated..",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to update..", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void deleteItem(){
        db.collection("MCCollection").document(id_)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"Transaction deleted..",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to delete..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }
}
