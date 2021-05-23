package com.mcapp.mcapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class FAQActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_faq);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_faqactivity);

            TextView content1 = findViewById(R.id.faq_content1);
            TextView content2 = findViewById(R.id.faq_content2);
            TextView content3 = findViewById(R.id.faq_content3);
            TextView content4 = findViewById(R.id.faq_content4);
            TextView content5 = findViewById(R.id.faq_content5);
            TextView content6 = findViewById(R.id.faq_content6);
            TextView content7 = findViewById(R.id.faq_content7);
            TextView content8 = findViewById(R.id.faq_content8);
            TextView content9 = findViewById(R.id.faq_content9);
            TextView content10 = findViewById(R.id.faq_content10);
            content1.setText("1. What are all the modes of payment available while adding transaction ? \n" +
                    "A. There are 3 different modes of payment available. Google pay, Cash and Card.");
            content2.setText("2. Can I add my own custom category while adding an expense ? \n" +
                    " A. No, you cannot add you custom category. You need to select it from the dropdown list provided.");
            content3.setText("3. Is the data I'm entering in the applicatio secure ? \n" +
                    " A. Yes. As you will be needed to create an account before you use the application, the account is secure with the personal data.");
            content4.setText("4. Can I filter the data for a period of time ? \n" +
                    " A. No, Right now you can filter data with 'DATE' selected in filter dialog and with search text. This feature will be modified in later versions.");
            content5.setText("5. Can I edit date and time of the transaction ? \n" +
                    " A. No, you cannot edit the date and time now. This feature will be implemented in later releases.");
            content6.setText("6. Can I filter my data ? \n" +
                    " A. Yes. You  can filter your transactions based on the filters provided and with the search text on the dashboard.");
            content7.setText("7. Can I edit my transaction details ? \n" +
                    " A. Yes. You can edit the transaction details by selecting the particular transaction from dashboard, then update the data.");
            content8.setText("8. Can I delete my tranasction ? \n" +
                    " A. Yes.You can delete the transaction details by selecting the particular transaction from dashboard, then delete the data");
            content9.setText("9. Can I edit my phone number and name of my profile? \n" +
                    "A. No. You cannot edit your profile details now. This feature will be implemented in our future releases.");
            content10.setText("10. Can I see my expenses in charts ? \n" +
                    "A. Yes. You can see pie chart based on amount spent and monthly basis expenses in bar chart in home page");
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
}
