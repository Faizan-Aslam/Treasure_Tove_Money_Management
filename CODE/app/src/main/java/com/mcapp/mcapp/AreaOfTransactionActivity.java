package com.mcapp.mcapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.scrounger.countrycurrencypicker.library.Buttons.CountryCurrencyButton;
import com.scrounger.countrycurrencypicker.library.Country;
import com.scrounger.countrycurrencypicker.library.Currency;
import com.scrounger.countrycurrencypicker.library.Listener.CountryCurrencyPickerListener;

import worker8.com.github.radiogroupplus.RadioGroupPlus;

public class AreaOfTransactionActivity extends AppCompatActivity {

    RadioGroupPlus radioGroupPlus;
    GeneralClass generalClass = new GeneralClass();
    Button setBudgetButton;
    CountryCurrencyButton currencyButton;
    EditText txtBudgetAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_of_transaction);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_closeaction);
        getSupportActionBar().setTitle(R.string.title_preferencesactivity);

        radioGroupPlus = findViewById(R.id.radio_group);
        setBudgetButton = findViewById(R.id.btn_setBudget);
        txtBudgetAmount = findViewById(R.id.txt_budgetAmount);
        currencyButton = findViewById(R.id.btn_currency);

        String preferences = generalClass.getSPAreaOfTransaction(AreaOfTransactionActivity.this);
        if(preferences.equals("home")){
            RadioButton rb = findViewById(R.id.radio_home);
            rb.setChecked(true);
        }else {
            RadioButton rb = findViewById(R.id.radio_office);
            rb.setChecked(true);
        }
        setCountry();
        setBudget();
        radioGroupPlus.setOnCheckedChangeListener(new RadioGroupPlus.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroupPlus group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_home: // first button
                        generalClass.putSPAreaOfTransaction(AreaOfTransactionActivity.this,"home");
                        Toast.makeText(getApplicationContext(), "Selected HOME " , Toast.LENGTH_SHORT).show();
                        setBudget();
                        break;
                    case R.id.radio_office: // secondbutton
                        generalClass.putSPAreaOfTransaction(AreaOfTransactionActivity.this,"office");
                        Toast.makeText(getApplicationContext(), "Selected OFFICE ", Toast.LENGTH_SHORT).show();
                        setBudget();
                        break;
                }
            }
        });

        currencyButton.setOnClickListener(new CountryCurrencyPickerListener() {
            @Override
            public void onSelectCountry(Country country) {
                if (country.getCurrency() == null) {
                    Toast.makeText(AreaOfTransactionActivity.this,
                            String.format("name: %s\ncode: %s", country.getName(), country.getCode())
                            , Toast.LENGTH_SHORT).show();
                } else {
                    /*Toast.makeText(AreaOfTransactionActivity.this,
                            String.format("name: %s\ncurrencySymbol: %s", country.getName(), country.getCurrency().getSymbol())
                            , Toast.LENGTH_SHORT).show();*/
                    Toast.makeText(AreaOfTransactionActivity.this,
                            String.format("Selected :  %s",country.getCurrency().getSymbol())
                            , Toast.LENGTH_SHORT).show();
                    generalClass.putSPCurrency(AreaOfTransactionActivity.this,country.getCurrency().getCode());
                    generalClass.putSPCountry(AreaOfTransactionActivity.this,country.getCode());
                    generalClass.putSPCurrencySymbol(AreaOfTransactionActivity.this,country.getCurrency().getSymbol());
                }
            }

            @Override
            public void onSelectCurrency(Currency currency) {
            }
        });

        setBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generalClass.putSPBudget(AreaOfTransactionActivity.this,Integer.parseInt(txtBudgetAmount.getText().toString()));
                generalClass.hideKeyboardActivity(AreaOfTransactionActivity.this);
                Toast.makeText(getApplicationContext(),"Budget has been set successfully!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setBudget(){
        Integer preferencesBudget = generalClass.getSPBudget(AreaOfTransactionActivity.this);
        txtBudgetAmount.setText(preferencesBudget.toString());
    }

    private void setCountry(){
        String preferencesCountry = generalClass.getSPCountry(AreaOfTransactionActivity.this);
        currencyButton.setCountry(preferencesCountry);
        currencyButton.setShowCurrency(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home/back button
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
