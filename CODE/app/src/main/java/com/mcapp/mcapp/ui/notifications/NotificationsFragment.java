package com.mcapp.mcapp.ui.notifications;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.mcapp.mcapp.R;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class NotificationsFragment extends Fragment   {

    private NotificationsViewModel notificationsViewModel;
    Spinner category_spinner, payment_method;
    EditText amount_spent ;
    Button addNewItemBtn;
    String category_selected,payment_selected;
    EditText comment;

    ProgressBar pb;

    FirebaseFirestore db;
    //private DocumentReference nDocRef = FirebaseFirestore.getInstance().collection("MCCollection");
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

        category_spinner = root.findViewById(R.id.dd_category);
        payment_method = root.findViewById(R.id.dd_paymentMethod);
        amount_spent = root.findViewById(R.id.txt_amountSpent);
        addNewItemBtn = root.findViewById(R.id.add_newItem);
        comment = root.findViewById(R.id.txt_Comment);

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

        addNewItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewItem(v);
                /*amount_spent.setText(' ');
                comment.setText(' ');*/
            }
        });
        return root;
    }

    public void addNewItem(View v){
        try{
            pb = new ProgressBar(getContext());
           // pb.setTooltipText("savind");
            if(amount_spent.getText().toString().isEmpty()){return;}
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            // yyyy-MM-dd'T'HH:mm:ss      "EEE, d MMM yyyy, HH:mm"

            db = FirebaseFirestore.getInstance();
            String id = UUID.randomUUID().toString();

            Map<String,Object> dataToSave = new HashMap<String,Object>();
            dataToSave.put(AMOUNT_KEY,amount_spent.getText().toString());
            dataToSave.put(CATEGORY_KEY,category_selected);
            dataToSave.put(COMMENT_KEY, comment.getText().toString());
            dataToSave.put(DATE_KEY,date);
            dataToSave.put(PAYMENTMETHOD_KEY,payment_selected);

            db.collection("MCCollection").add(dataToSave)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(getContext(),"Data added successfully!!",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),"Failed to add data",Toast.LENGTH_SHORT).show();
                }
            });

            /*nDocRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getContext(),"Data added successfully!!",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),"Failed to add data",Toast.LENGTH_SHORT).show();
                }
            });*/
        }
        catch(Exception e){

        }
    }


}