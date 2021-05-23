package com.mcapp.mcapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends AppCompatActivity {

    Button add,dashboard,updateDelete,searchFilter;
    Dialog dialog;
    String title = null, description = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_aboutactivity);

        TextView content = findViewById(R.id.about_content);
        content.setText("Treasure Trove application is build to help you keep track of your expenses. " +
                "The user can enter their expenses based on a specific Category, Payment Method of their choice" +
                "Once can see all their transactions in the dashboard section" +
                "You can also see the details of particular transaction and update / delete the transactions" +
                "Below you can find the details regarding specific sections of the app.");

        add = findViewById(R.id.btn_aboutNewTransaction);
        dashboard = findViewById(R.id.btn_aboutDashboard);
        updateDelete = findViewById(R.id.btn_aboutUpdateDelTransaction);
        searchFilter = findViewById(R.id.btn_aboutSearchFilter);

        dialog = new Dialog(this);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = "Add new transaction";
                description = "Click on the new transaction and proceed to enter the transaction name," +
                        "select the category and payment type. Also enter the amount spent and the date, time of the transaction.\n" +
                        "You can also enter the comments and click on 'ADD' button. Then you have successfully added a transaction." ;
                dialog(title,description);
            }
        });
        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = "Dashboard";
                description = "Click on the dashboard button in the navigation bar. You can see all your transactions.\n" +
                        "Scroll down to see more transactions. You can see the transaction name, date of transaction, type of transaction and " +
                        "category of transaction, comments which you have entered while adding the transaction.";
                dialog(title,description);
            }
        });
        updateDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = "Update/Delete transaction";
                description = "In the dashboard page, click on any transaction to see the details related to the particular transaction such as" +
                        "date & time of the transaction, category, type, name, comments.\n You can edit the transaction items and click on 'UPDATE' button to update the transaction with new data." +
                        "You can click 'DELETE' button to delete that particular transaction.\n" +
                        "Once done, you will be redirected to dashboard page to see the fresh list of transactions.";
                dialog(title,description);
            }
        });
        searchFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = "Search/Filter transaction";
                description = "In the top righ corner of the dashboard page, you can see two icons for earch and filter.\n" +
                        "Click on search and type some search text, you will get the filtered list based on search text.\n" +
                        "Click on filter and select category or transaction type or date to filter list accordingly and click on 'APPLY'.\n" +
                        "Click on filter and click 'CANCEL' to remove the filer." +
                        "Similarly, you can sort the list in ascending or descending order of the amount spent by selecting 'Low to high' or" +
                        "'High to Low' in filter dialog.";
                dialog(title,description);
            }
        });
    }

    public void dialog(String title,String description){
        try {
            dialog.setContentView(R.layout.customdialog);
            ImageButton btnClose = dialog.findViewById(R.id.btn_closeDialog);
            TextView txtViewtitle = dialog.findViewById(R.id.dialogTitle);
            TextView txtViewdescription = dialog.findViewById(R.id.dialogDescription);

            txtViewtitle.setText(title);
            txtViewdescription.setText(description);
            dialog.show();

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        try{
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
