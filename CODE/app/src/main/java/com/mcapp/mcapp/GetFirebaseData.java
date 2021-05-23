package com.mcapp.mcapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GetFirebaseData
{
    ProgressDialog progressDialog;
     ArrayList<Model> dashboardDataList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public  ArrayList<Model> fetchOnLoadData(final Context context){
        progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCancelable(false);
        dashboardDataList.clear();

        db.collection("MCCollection").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot doc: task.getResult()){
                            Model model = new Model(
                                    doc.getId(),
                                    doc.getString("TransactionName"),
                                    doc.getString("Amount"),
                                    doc.getString("Category"),
                                    doc.getString("Comment"),
                                    doc.getString("Date"),
                                    doc.getString("PaymentMethod")
                            );
                            dashboardDataList.add(model);
                        }
                        //Toast.makeText(context,"fetched data: "+  dashboardDataList.size(),Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Failed to get data..",Toast.LENGTH_SHORT).show();
            }
        });
        progressDialog.dismiss();
        return  dashboardDataList;
    }

    public Task<QuerySnapshot> fetchOnLoadDataTest(final Context context){
        progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCancelable(false);
        dashboardDataList.clear();
        progressDialog.dismiss();
        return db.collection("MCCollection").get();
    }

    public ArrayList<Model> getData(Task<QuerySnapshot> task){
        ArrayList<Model> data = new ArrayList<>();
        for(DocumentSnapshot doc: task.getResult()){
            Model model = new Model(
                    doc.getId(),
                    doc.getString("TransactionName"),
                    doc.getString("Amount"),
                    doc.getString("Category"),
                    doc.getString("Comment"),
                    doc.getString("Date"),
                    doc.getString("PaymentMethod")
            );
            data.add(model);
        }
        return data;
    }
}
