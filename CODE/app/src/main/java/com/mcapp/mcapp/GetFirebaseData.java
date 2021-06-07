package com.mcapp.mcapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mcapp.mcapp.ui.GlobalClass;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GetFirebaseData
{
    ProgressDialog progressDialog;
    ArrayList<Model> dashboardDataList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GlobalClass globalVariable;
    GeneralClass generalClass = new GeneralClass();
    Integer excessAmountSpent = 0;

    public Task<QuerySnapshot> fetchOnLoadData(final Context context, final Activity activity){
        globalVariable = (GlobalClass) activity.getApplicationContext();
      /*  progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCancelable(false);*/
        dashboardDataList.clear();
        //progressDialog.dismiss();
        return db.collection("MCCollection").whereEqualTo("UserId",generalClass.getSPUserId(activity))
                .whereEqualTo("AreaOfTransaction",generalClass.getSPAreaOfTransaction(activity)).get();
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
                    doc.getString("PaymentMethod"),
                    doc.getString("Favourite"),
                    doc.getString("Currency"),
                    doc.getString("CurrencySymbol")
            );
            data.add(model);
        }
        return data;
    }

    public void setMonthlyTotalAmountSpentAndDate(final Activity activity){
        final String areaOfTransaction = generalClass.getSPAreaOfTransaction(activity);
        db.collection("MCCollection").whereEqualTo("UserId",generalClass.getSPUserId(activity))
            .whereEqualTo("AreaOfTransaction",areaOfTransaction).get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot doc : task.getResult()) {
                        Model model = new Model(
                                doc.getId(),
                                doc.getString("TransactionName"),
                                doc.getString("Amount"),
                                doc.getString("Category"),
                                doc.getString("Comment"),
                                doc.getString("Date"),
                                doc.getString("PaymentMethod"),
                                doc.getString("Favourite"),
                                doc.getString("Currency"),
                                doc.getString("CurrencySymbol")
                        );
                        dashboardDataList.add(model);
                        //excessAmountSpent = calculateExcessBudget(activity,dashboardDataList);
                    }
                    Integer sumOfMonthlyExpenses=0, monthlyIncome = 0, monthlySavings = 0;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
                    Date date = null;
                    int month, year;
                    String currentDate = dateFormat.format(new Date());
                    for (Model mod : dashboardDataList) {
                        try {
                            date = dateFormat.parse(mod.getDate());
                            month = date.getMonth();// Integer.parseInt(dateFormat.format(date));
                            year = date.getYear();
                            if(dateFormat.parse(currentDate).getMonth() == month )  {
                                if((mod.getCategory()).equals("Income")){ monthlyIncome += Integer.parseInt(mod.getAmount());}
                                else if((mod.getCategory()).equals("Savings")){ monthlySavings += Integer.parseInt(mod.getAmount()); }
                                else{ sumOfMonthlyExpenses +=  Integer.parseInt(mod.getAmount()); }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    generalClass.putSPTotalAmountAndDate(activity, sumOfMonthlyExpenses, currentDate);
                    generalClass.putSPSavings(activity, monthlySavings);
                    generalClass.putSPIncome(activity, monthlyIncome);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
    }
    public Integer calculateExcessBudget(Activity activity, ArrayList<Model> data){
        Integer sumOfMonthlyExpenses=0, excessAmount = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
        Date date = null;
        int month, year;
        String currentDate = dateFormat.format(new Date());
        for (Model mod : data) {
            try {
                date = dateFormat.parse(mod.getDate());
                month = date.getMonth();// Integer.parseInt(dateFormat.format(date));
                year = date.getYear();
                if(dateFormat.parse(currentDate).getMonth() == month && !(mod.getCategory()).equals("Income") )  {
                    sumOfMonthlyExpenses +=  Integer.parseInt(mod.getAmount());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Integer budgetAmount = generalClass.getSPBudget(activity);
        if(sumOfMonthlyExpenses > budgetAmount){
            excessAmount = sumOfMonthlyExpenses - budgetAmount;
        }
        return excessAmount;
    }
}
