package com.mcapp.mcapp.ui.notifications;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mcapp.mcapp.GeneralClass;
import com.mcapp.mcapp.ProgressBarActions;
import com.mcapp.mcapp.R;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class NotificationsFragment extends Fragment   {

    private NotificationsViewModel notificationsViewModel;
    private ProgressBarActions progressBarActions = new ProgressBarActions();
    private GeneralClass generalClass = new GeneralClass();
    Spinner category_spinner, payment_method;
    EditText transaction_name,amount_spent, dateTime, comment ;
    String category_selected,payment_selected;
    Button addNewItemBtn;

    private ProgressBar progressBar;


    FirebaseFirestore db;
    //private DocumentReference nDocRef = FirebaseFirestore.getInstance().collection("MCCollection");
    public static final String TRANSACTIONNAME_KEY = "TransactionName";
    public static final String AMOUNT_KEY = "Amount";
    public static final String CATEGORY_KEY = "Category";
    public static final String COMMENT_KEY = "Comment";
    public static final String DATE_KEY = "Date";
    public static final String PAYMENTMETHOD_KEY = "PaymentMethod";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        try {
            transaction_name = root.findViewById(R.id.txt_transactionName);
            category_spinner = root.findViewById(R.id.dd_category);
            payment_method = root.findViewById(R.id.dd_paymentMethod);
            amount_spent = root.findViewById(R.id.txt_amountSpent);
            dateTime = root.findViewById(R.id.txt_dateTime);
            comment = root.findViewById(R.id.txt_Comment);
            addNewItemBtn = root.findViewById(R.id.add_newItem);
            progressBar = (ProgressBar) root.findViewById(R.id.progress_bar);

            dateTime.setText(getCurrentDateAndTime());

            category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    category_selected = parent.getItemAtPosition(position).toString();
                    //Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            payment_method.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    payment_selected = parent.getItemAtPosition(position).toString();
                    //Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            ImageButton btnDate = root.findViewById(R.id.btn_setNewDate);
            btnDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDateTimeDialog(dateTime);
                }
            });
            addNewItemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    generalClass.hideKeyboardFragment(getContext(), getView());
                    if (validateForm()) {
                        addNewItem(v);
                    }
                }
            });
        }
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return root;
    }

    public String getCurrentDateAndTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
        return sdf.format(new Date());
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
                    new TimePickerDialog(getContext(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
                }
            };
            new DatePickerDialog(getContext(), dateSetListener,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        }
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public Boolean validateForm(){
        try {
            if (transaction_name.getText().toString().trim().isEmpty()) {
                transaction_name.setError("Please enter transaction name");
                transaction_name.requestFocus();
                return false;
            }
            if (amount_spent.getText().toString().trim().isEmpty()) {
                amount_spent.setError("Please enter amount spent");
                amount_spent.requestFocus();
                return false;
            }
            if (dateTime.getText().toString().trim().isEmpty()) {
                dateTime.setError("Please enter the date");
                dateTime.requestFocus();
                return false;
            }
            if (comment.getText().toString().trim().isEmpty()) {
                comment.setError("Please enter a comment");
                comment.requestFocus();
                return false;
            }
            if (amount_spent.getText().length() <= 0) {
                amount_spent.setError("Please enter a valid amount");
                amount_spent.requestFocus();
                return false;
            }
            if (Integer.parseInt(amount_spent.getText().toString()) <= 0) {
                amount_spent.setError("Please enter a valid amount");
                amount_spent.requestFocus();
                return false;
            }
            if (amount_spent.getText().toString().length() > 8) {
                amount_spent.setError("Amount should not exceed 99999999");
                amount_spent.requestFocus();
                return false;
            }
        }
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return true;
    }

    public void addNewItem(View v){
        try{
            progressBarActions.showProgressBar(progressBar,getActivity());
            db = FirebaseFirestore.getInstance();

            Map<String,Object> dataToSave = new HashMap<String,Object>();
            dataToSave.put(TRANSACTIONNAME_KEY,transaction_name.getText().toString().trim());
            dataToSave.put(AMOUNT_KEY, amount_spent.getText().toString().trim());
            dataToSave.put(CATEGORY_KEY, category_selected);
            dataToSave.put(COMMENT_KEY, comment.getText().toString().trim());
            dataToSave.put(DATE_KEY, dateTime.getText().toString());
            dataToSave.put(PAYMENTMETHOD_KEY, payment_selected);

            db.collection("MCCollection").add(dataToSave)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(getContext(),"Data added successfully!!",Toast.LENGTH_SHORT).show();
                    progressBarActions.hideProgressBar(progressBar,getActivity());
                    amount_spent.setText("");
                    transaction_name.setText("");
                    comment.setText("");
                    dateTime.setText(getCurrentDateAndTime());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),"Failed to add data",Toast.LENGTH_SHORT).show();
                    progressBarActions.hideProgressBar(progressBar,getActivity());
                }
            });
        }
        catch(Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

}