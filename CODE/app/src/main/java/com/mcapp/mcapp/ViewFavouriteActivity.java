package com.mcapp.mcapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.mcapp.mcapp.ui.GlobalClass;
import com.scrounger.countrycurrencypicker.library.Country;
import com.scrounger.countrycurrencypicker.library.CountryCurrencyPicker;
import com.scrounger.countrycurrencypicker.library.Currency;
import com.scrounger.countrycurrencypicker.library.Listener.CountryCurrencyPickerListener;
import com.scrounger.countrycurrencypicker.library.PickerType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ViewFavouriteActivity extends AppCompatActivity {

    private TextView id,transactionName,amount,comment;
    private EditText dateTime;
    String category_selected,payment_selected,currency_selected, currencysymbol_selected;
    private Spinner category_spinner,payment_spinner;
    Button btnAdd,setCurrencyBtn;
    ProgressBar progressBar;
    AlertDialog.Builder builder;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressBarActions progressBarActions = new ProgressBarActions();
    private GeneralClass generalClass = new GeneralClass();
    private GlobalClass globalVariable;
    String id_;
    LikeButton likeButton;
    Boolean likedStatus = false ;

    GetFirebaseData getFirebaseData = new GetFirebaseData();

    //private DocumentReference nDocRef = FirebaseFirestore.getInstance().collection("MCCollection");
    public static final String USERID_KEY = "UserId";
    public static final String TRANSACTIONNAME_KEY = "TransactionName";
    public static final String AMOUNT_KEY = "Amount";
    public static final String CATEGORY_KEY = "Category";
    public static final String COMMENT_KEY = "Comment";
    public static final String DATE_KEY = "Date";
    public static final String PAYMENTMETHOD_KEY = "PaymentMethod";
    public static final String FAVOURITE_KEY = "Favourite";
    public static final String AREAOFTRANSACTION_KEY = "AreaOfTransaction";
    public static final String CURRENCY_KEY = "Currency";
    public static final String CURRENCYSYMBOL_KEY = "CurrencySymbol";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view_favourite);
            getSupportActionBar().setTitle(R.string.title_viewfavactivity);
            globalVariable = (GlobalClass) this.getApplicationContext();

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            transactionName = findViewById(R.id.txt_transactionName);
            amount = findViewById(R.id.txt_amount);
            category_spinner = findViewById(R.id.spinner_category);
            comment = findViewById(R.id.txt_comment);
            dateTime = findViewById(R.id.txt_dateTime);
            payment_spinner = findViewById(R.id.spinner_paymentMethod);
            likeButton = findViewById(R.id.like_button);
            progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            setCurrencyBtn = findViewById(R.id.btn_setCurrency);

            onLoadData();
            dateTime.setText(getCurrentDateAndTime());

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

            ImageButton btnDate = findViewById(R.id.btn_setNewDate);
            btnDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDateTimeDialog(dateTime);
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

            btnAdd = findViewById(R.id.add_newItemFav);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addNewItem(v);
                }
            });
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public String getCurrentDateAndTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void onLoadData(){
        try {
            progressBarActions.showProgressBar(progressBar, ViewFavouriteActivity.this);

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
            //String likedStatus_ = getIntent().getStringExtra("favourite");

            String[] cate = getResources().getStringArray(R.array.category_names);
            String[] paym = getResources().getStringArray(R.array.payment_method);
            List<String> categoryList = new ArrayList<String>(Arrays.asList(cate));
            List<String> paymentList = new ArrayList<String>(Arrays.asList(paym));

            transactionName.setText(transactionName_);
            amount.setText(amount_);
            setCurrencyBtn.setText(currencysymbol_selected);
            dateTime.setText(date_);
            comment.setText(comment_);
            category_spinner.setSelection(categoryList.indexOf(category_selected));
            payment_spinner.setSelection(paymentList.indexOf(payment_selected));
            //likeButton.setLiked(Boolean.parseBoolean(likedStatus_));
            //Toast.makeText(getApplicationContext(),"category pos:  "+category_selected + categoryList.indexOf(category_selected),Toast.LENGTH_SHORT).show();

            progressBarActions.hideProgressBar(progressBar, ViewFavouriteActivity.this);
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
                    Toast.makeText(ViewFavouriteActivity.this,
                            String.format("name: %s\nsymbol: %s", country.getName(), country.getCode())
                            , Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ViewFavouriteActivity.this,
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

    public void showDateTimeDialog(final EditText dateTime){
        try {
            final Calendar calendar = Calendar.getInstance();
            DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
                            dateTime.setText(simpleDateFormat.format(calendar.getTime()));
                            dateTime.setError(null);
                            dateTime.clearFocus();
                        }
                    };
                    new TimePickerDialog(ViewFavouriteActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
                }
            };
            new DatePickerDialog(ViewFavouriteActivity.this, dateSetListener,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        }
        catch (Exception e){
            Toast.makeText(ViewFavouriteActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void addNewItem(View v){
        try{
            generalClass.hideKeyboardActivity(ViewFavouriteActivity.this);
            progressBarActions.showProgressBar(progressBar,ViewFavouriteActivity.this);
            db = FirebaseFirestore.getInstance();

            Map<String,Object> dataToSave = new HashMap<String,Object>();
            dataToSave.put(USERID_KEY,generalClass.getSPUserId(ViewFavouriteActivity.this));
            dataToSave.put(TRANSACTIONNAME_KEY,transactionName.getText().toString().trim());
            dataToSave.put(AMOUNT_KEY, amount.getText().toString().trim());
            dataToSave.put(CATEGORY_KEY, category_selected);
            dataToSave.put(COMMENT_KEY, comment.getText().toString().trim());
            dataToSave.put(DATE_KEY, dateTime.getText().toString());
            dataToSave.put(PAYMENTMETHOD_KEY, payment_selected);
            dataToSave.put(FAVOURITE_KEY,likedStatus.toString());
            dataToSave.put(AREAOFTRANSACTION_KEY,generalClass.getSPAreaOfTransaction(ViewFavouriteActivity.this));
            dataToSave.put(CURRENCY_KEY,currency_selected);
            dataToSave.put(CURRENCYSYMBOL_KEY, currencysymbol_selected);

            db.collection("MCCollection").add(dataToSave)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Integer excessAmount = 0;
                            if(generalClass.isDateCurrentMonthAndYear(dateTime.getText().toString())) {
                                Integer amountUpdated = Integer.parseInt(amount.getText().toString().trim());
                                if(category_selected.equals("Income")) { generalClass.putSPIncome(ViewFavouriteActivity.this,amountUpdated); }
                                else if(category_selected.equals("Savings")) { generalClass.putSPSavings(ViewFavouriteActivity.this,amountUpdated); }
                                else {
                                    excessAmount = generalClass.checkBudgetConstraints(amountUpdated, ViewFavouriteActivity.this);
                                    showBudgetExceededAlert(excessAmount);
                                }
                            }
                            Toast.makeText(getApplicationContext(),"Data added successfully!!",Toast.LENGTH_SHORT).show();
                            progressBarActions.hideProgressBar(progressBar,ViewFavouriteActivity.this);
                            if(!(excessAmount > 0)){
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Failed to add data",Toast.LENGTH_SHORT).show();
                    progressBarActions.hideProgressBar(progressBar,ViewFavouriteActivity.this);
                    finish();
                }
            });
        }
        catch(Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public  void showBudgetExceededAlert(Integer amount) {
        if (amount > 0) {
            builder = new AlertDialog.Builder(this);
            builder.setMessage("You have exceeded your monthly budget by : "+ generalClass.getSPCurrencySymbol(ViewFavouriteActivity.this) + " " + amount)
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            ViewFavouriteActivity.this.finish();
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
