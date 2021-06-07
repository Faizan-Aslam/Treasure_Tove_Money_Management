package com.mcapp.mcapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.mcapp.mcapp.ui.GlobalClass;
import com.scrounger.countrycurrencypicker.library.Country;
import com.scrounger.countrycurrencypicker.library.CountryCurrencyPicker;
import com.scrounger.countrycurrencypicker.library.Currency;
import com.scrounger.countrycurrencypicker.library.Listener.CountryCurrencyPickerListener;
import com.scrounger.countrycurrencypicker.library.PickerType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewItemActivity extends AppCompatActivity {

    private TextView id,transactionName,amount,comment,date;
    String category_selected,payment_selected,currency_selected,currencysymbol_selected;
    private Spinner category_spinner,payment_spinner;
    Button btnDelete,btnUpdate,setCurrencyBtn;
    ProgressBar progressBar;
    AlertDialog.Builder builder;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressBarActions progressBarActions = new ProgressBarActions();
    GetFirebaseData getFirebaseData = new GetFirebaseData();
    private GeneralClass generalClass = new GeneralClass();
    private GlobalClass globalVariable;
    String id_;
    LikeButton likeButton;
    Boolean likedStatus = false ;
    Integer amountBeforeUpdate;
    String categoryBeforeUpdate;

    ArrayList<Model> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view_item);
            getSupportActionBar().setTitle(R.string.title_viewitemactivity);
            globalVariable = (GlobalClass) this.getApplicationContext();

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            transactionName = findViewById(R.id.txt_transactionName);
            amount = findViewById(R.id.txt_amount);
            category_spinner = findViewById(R.id.spinner_category);
            comment = findViewById(R.id.txt_comment);
            date = findViewById(R.id.txt_date);
            payment_spinner = findViewById(R.id.spinner_paymentMethod);
            likeButton = findViewById(R.id.like_button);
            progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            setCurrencyBtn = findViewById(R.id.btn_setCurrency);

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

            setCurrencyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCountryCurrencyPickerDialog();
                }
            });

            likeButton.setOnLikeListener(new OnLikeListener(){
                @Override
                public void liked(LikeButton likeButton) {
                    likeButton.setLiked(true);
                    likedStatus = likeButton.isLiked() == true ? true : false;
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    likeButton.setLiked(false);
                    likedStatus = likeButton.isLiked() ? true : false;
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
            currency_selected =  getIntent().getStringExtra("currency");
            currencysymbol_selected = getIntent().getStringExtra("currencySymbol");
            category_selected = getIntent().getStringExtra("category");
            String comment_ = getIntent().getStringExtra("comment");
            String date_ = getIntent().getStringExtra("date");
            payment_selected = getIntent().getStringExtra("paymentMethod");
            String likedStatus_ = getIntent().getStringExtra("favourite");

            amountBeforeUpdate = Integer.parseInt(getIntent().getStringExtra("amount"));
            categoryBeforeUpdate = getIntent().getStringExtra("category");
            String[] cate = getResources().getStringArray(R.array.category_names);
            String[] paym = getResources().getStringArray(R.array.payment_method);
            List<String> categoryList = new ArrayList<String>(Arrays.asList(cate));
            List<String> paymentList = new ArrayList<String>(Arrays.asList(paym));

            transactionName.setText(transactionName_);
            amount.setText(amount_);
            setCurrencyBtn.setText(currencysymbol_selected);
            date.setText(date_);
            comment.setText(comment_);
            category_spinner.setSelection(categoryList.indexOf(category_selected));
            payment_spinner.setSelection(paymentList.indexOf(payment_selected));
            likeButton.setLiked(Boolean.parseBoolean(likedStatus_));
            //Toast.makeText(getApplicationContext(),"category pos:  "+category_selected + categoryList.indexOf(category_selected),Toast.LENGTH_SHORT).show();

            progressBarActions.hideProgressBar(progressBar, ViewItemActivity.this);
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void showCountryCurrencyPickerDialog() {
        CountryCurrencyPicker pickerDialog = CountryCurrencyPicker.newInstance(PickerType.COUNTRYandCURRENCY, new CountryCurrencyPickerListener() {
            @Override
            public void onSelectCountry(Country country) {
                if (country.getCurrency() == null) {
                    Toast.makeText(ViewItemActivity.this,
                            String.format("name: %s\nsymbol: %s", country.getName(), country.getCode())
                            , Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ViewItemActivity.this,
                            String.format("Selected :  %s",country.getCurrency().getSymbol())
                            , Toast.LENGTH_SHORT).show();
                    setCurrencyBtn.setText(country.getCurrency().getSymbol());
                    currency_selected = country.getCurrency().getCode();
                    currencysymbol_selected = country.getCurrency().getSymbol();
                }
            }

            @Override
            public void onSelectCurrency(Currency currency) {
               /* if (currency.getCountries() == null) {
                    Toast.makeText(getActivity(),
                            String.format("name: %s\nsymbol: %s", currency.getName(), currency.getSymbol())
                            , Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(),
                            String.format("name: %s\ncurrencySymbol: %s\ncountries: %s", currency.getName(), currency.getSymbol(), TextUtils.join(", ", currency.getCountriesNames()))
                            , Toast.LENGTH_SHORT).show();
                }
                setCurrencyBtn.setText(currency.getSymbol());*/
            }
        });

        pickerDialog.show(getSupportFragmentManager(), CountryCurrencyPicker.DIALOG_NAME);
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
            dataToSave.put("UserId",generalClass.getSPUserId(ViewItemActivity.this));
            dataToSave.put("TransactionName", transactionName.getText().toString().trim());
            dataToSave.put("Amount", amount.getText().toString().trim());
            dataToSave.put("Category", category_selected);
            dataToSave.put("Comment", comment.getText().toString().trim());
            dataToSave.put("PaymentMethod", payment_selected);
            dataToSave.put("Date", date.getText().toString());
            dataToSave.put("Favourite",likedStatus.toString());
            dataToSave.put("AreaOfTransaction",generalClass.getSPAreaOfTransaction(ViewItemActivity.this));
            dataToSave.put("Currency",currency_selected);
            dataToSave.put("CurrencySymbol", currencysymbol_selected);
            db.collection("MCCollection").document(id_)
                    .set(dataToSave)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Integer excessAmount = 0;
                            if(generalClass.isDateCurrentMonthAndYear(date.getText().toString())) {
                                Integer amountLatest = Integer.parseInt(amount.getText().toString().trim());
                                if (categoryBeforeUpdate.equals(category_selected)) {
                                    Integer amountTobeUpdated = amountLatest - amountBeforeUpdate;
                                    if (category_selected.equals("Income")) {
                                        generalClass.putSPIncome(ViewItemActivity.this, amountTobeUpdated);
                                    } else if (category_selected.equals("Savings")) {
                                        generalClass.putSPSavings(ViewItemActivity.this, amountTobeUpdated);
                                    } else {
                                        excessAmount = generalClass.updateAndCheckBudgetConstraints(amountBeforeUpdate, amountLatest, ViewItemActivity.this);
                                        showBudgetExceededAlert(excessAmount);
                                    }
                                }
                                else {
                                    if (category_selected.equals("Income")) {
                                        generalClass.putSPIncome(ViewItemActivity.this, amountLatest);
                                    }
                                    else if (category_selected.equals("Savings")) {
                                        generalClass.putSPSavings(ViewItemActivity.this, amountLatest);
                                    }
                                    else{
                                        generalClass.putSPTotalAmountAndDate(ViewItemActivity.this,
                                                generalClass.getSPTotalAmountSpent(ViewItemActivity.this) + amountLatest,
                                                date.getText().toString());
                                    }

                                    if(categoryBeforeUpdate.equals("Income")) { generalClass.putSPIncome(ViewItemActivity.this, - amountBeforeUpdate); }
                                    else if(categoryBeforeUpdate.equals("Savings")) { generalClass.putSPSavings(ViewItemActivity.this, - amountBeforeUpdate); }
                                    else{ generalClass.putSPTotalAmountAndDate(ViewItemActivity.this,
                                            generalClass.getSPTotalAmountSpent(ViewItemActivity.this) - amountBeforeUpdate
                                            ,date.getText().toString());}

                                    if(generalClass.getSPTotalAmountSpent(ViewItemActivity.this) > generalClass.getSPBudget(ViewItemActivity.this)){
                                        showBudgetExceededAlert(generalClass.getSPTotalAmountSpent(ViewItemActivity.this) - generalClass.getSPBudget(ViewItemActivity.this));
                                    }
                                }
                            }
                            progressBarActions.hideProgressBar(progressBar, ViewItemActivity.this);
                            Toast.makeText(getApplicationContext(), "Transaction updated..", Toast.LENGTH_SHORT).show();
                            if(!(excessAmount > 0)){
                                finish();
                            }
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
                            if(generalClass.isDateCurrentMonthAndYear(date.getText().toString())) {
                                Integer amountLatest = Integer.parseInt(amount.getText().toString().trim());
                                if(category_selected.equals("Income")) { generalClass.putSPIncome(ViewItemActivity.this, -amountLatest ); }
                                else if(category_selected.equals("Savings")) { generalClass.putSPSavings(ViewItemActivity.this, - amountLatest); }
                                else {
                                    Integer excessAmount = generalClass.updateAndCheckBudgetConstraints(amountBeforeUpdate, amountLatest, ViewItemActivity.this);
                                    showBudgetExceededAlert(excessAmount);
                                }
                            }
                            if(!category_selected.equals("Income") && !category_selected.equals("Savings")) {
                                generalClass.updateAndCheckBudgetConstraints(amountBeforeUpdate, 0, ViewItemActivity.this);
                            }
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

    public  void showBudgetExceededAlert(Integer amount) {
        if (amount > 0) {
            builder = new AlertDialog.Builder(this);
            builder.setMessage("You have exceeded your monthly budget by : "+ generalClass.getSPCurrencySymbol(ViewItemActivity.this) + " " + amount)
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            ViewItemActivity.this.finish();
                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Budget Alert !!");
            alert.show();
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }
    }
}
