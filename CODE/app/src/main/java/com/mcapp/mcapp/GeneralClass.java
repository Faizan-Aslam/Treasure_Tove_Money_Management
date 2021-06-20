import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class GeneralClass {
    public static SharedPreferences sharedPreferences;
    public AlertDialog.Builder builder;

    public static void hideKeyboardActivity(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboardFragment(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getSPAreaOfTransaction(Activity activity){
        sharedPreferences= activity.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        String sp = sharedPreferences.getString("SP_AREAOFTRANSACTION","");
        if(sp.equals("") || sp == null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("SP_AREAOFTRANSACTION","home");
            editor.apply();
            return "home";
        }
        else {
            return sp;
        }
    }

    public static void putSPAreaOfTransaction(Activity activity, String text){
        sharedPreferences= activity.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("SP_AREAOFTRANSACTION",text);
        editor.apply();
    }

    public static String getSPCurrency(Activity activity){
        sharedPreferences= activity.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        String sp = sharedPreferences.getString("SP_CURRENCY","EUR");
        return sp;
    }

    public static void putSPCurrency(Activity activity, String currencySymbol){
        sharedPreferences= activity.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("SP_CURRENCY",currencySymbol);
        editor.apply();
    }

    public static String getSPCurrencySymbol(Activity activity){
        sharedPreferences= activity.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        String sp = sharedPreferences.getString("SP_CURRENCY_SYMBOL","€");
        return sp;
    }

    public static void putSPCurrencySymbol(Activity activity, String currencySymbol){
        sharedPreferences= activity.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("SP_CURRENCY_SYMBOL",currencySymbol);
        editor.apply();
    }

    public static String getSPCountry(Activity activity){
        sharedPreferences= activity.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        String sp = sharedPreferences.getString("SP_COUNTRY","DE");
        return sp;
    }

    public static void putSPCountry(Activity activity, String countryCode){
        sharedPreferences= activity.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("SP_COUNTRY",countryCode);
        editor.apply();
    }

    public static Boolean getSPLoginToken(Activity activity){
        sharedPreferences= activity.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        Boolean sp = sharedPreferences.getBoolean("SP_LOGINTOKEN",false);
        return sp;
    }

    public static void putSPLoginToken(Activity activity, Boolean token){
        sharedPreferences= activity.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("SP_LOGINTOKEN",token);
        editor.apply();
    }

    public static String getSPUserId(Activity activity){
        sharedPreferences= activity.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        String sp = sharedPreferences.getString("SP_USERID","");
        return sp;
    }

    public static void putSPUserId(Activity activity, String userId){
        sharedPreferences= activity.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("SP_USERID",userId);
        editor.apply();
    }

    public static String getSPUserName(Activity activity){
        sharedPreferences= activity.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        String sp = sharedPreferences.getString("SP_USERNAME","");
        return sp;
    }

    public static void putSPUserName(final Activity activity, String name){
        sharedPreferences= activity.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        if(name == null || name.equals(null)){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("SP_USERNAME",name);
            editor.apply();
        }
        else {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(getSPUserId(activity)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (userProfile != null) {
                        editor.putString("SP_USERNAME", userProfile.name);
                        editor.apply();
                    } else {
                        Toast.makeText(activity, "Something wrong happened..!!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public static Integer getSPBudget(Activity activity){
        sharedPreferences= activity.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        if(getSPAreaOfTransaction(activity).equals("home")) {
            return sharedPreferences.getInt("SP_HOME_BUDGET", 0);
        }
        else {
            return sharedPreferences.getInt("SP_OFFICE_BUDGET", 0);
        }

    }

    public static void putSPBudget(Activity activity, Integer amount){
        sharedPreferences= activity.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(getSPAreaOfTransaction(activity).equals("home")) {
            editor.putInt("SP_HOME_BUDGET", amount);
        }else{
            editor.putInt("SP_OFFICE_BUDGET", amount);
        }
        editor.apply();
    }

    public Integer checkBudgetConstraints(Integer amount, Activity activity) {
        Integer excessBudgetAmount = 0;
        Integer currentBudget = getSPBudget(activity);
        Integer currentExpendituresTotal = getSPTotalAmountSpent(activity);
        String lastTransactionDate = getSPLastTransactionDate(activity);
        Integer newTotal = currentExpendituresTotal + amount;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
        Date date = null;
        int month, year;
        String currentDate = dateFormat.format(new Date());
        try{
        date = dateFormat.parse(lastTransactionDate);
        month = date.getMonth();// Integer.parseInt(dateFormat.format(date));
        year = date.getYear();
        if(dateFormat.parse(currentDate).getMonth() == month && dateFormat.parse(currentDate).getYear() == year )  {
            putSPTotalAmountAndDate(activity,newTotal,currentDate);
            if(newTotal > currentBudget){
                excessBudgetAmount =  newTotal - currentBudget;
            }
        }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return excessBudgetAmount;
    }

    public Integer updateAndCheckBudgetConstraints(Integer amountBeforeUpdate, Integer updatedAmount, Activity activity) {
        Integer excessBudgetAmount = 0;
        Integer currentBudget = getSPBudget(activity);
        Integer currentExpendituresTotal = getSPTotalAmountSpent(activity);
        String lastTransactionDate = getSPLastTransactionDate(activity);
        Integer newTotal = currentExpendituresTotal - amountBeforeUpdate + updatedAmount;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
        Date date = null;
        int month, year;
        String currentDate = dateFormat.format(new Date());
        try{
            date = dateFormat.parse(lastTransactionDate);
            month = date.getMonth();// Integer.parseInt(dateFormat.format(date));
            year = date.getYear();
            if(dateFormat.parse(currentDate).getMonth() == month && dateFormat.parse(currentDate).getYear() == year )  {
                putSPTotalAmountAndDate(activity,newTotal,currentDate);
                if(newTotal > currentBudget){
                    excessBudgetAmount =  newTotal - currentBudget;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return excessBudgetAmount;
    }

    public static void putSPTotalAmountAndDate(Activity activity, Integer amount, String date){
        sharedPreferences= activity.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(getSPAreaOfTransaction(activity).equals("home")) {
            editor.putInt("SP_HOME_TOTALAMOUNT", amount);
            editor.putString("SP_HOME_LASTTRANSACTION_DATE", date);
        }
        else {
            editor.putInt("SP_OFFICE_TOTALAMOUNT",amount);
            editor.putString("SP_OFFICE_LASTTRANSACTION_DATE",date);
        }
        editor.apply();
    }

    public static Integer getSPTotalAmountSpent(Activity activity){
        sharedPreferences= activity.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        if(getSPAreaOfTransaction(activity).equals("home")) {
            return sharedPreferences.getInt("SP_HOME_TOTALAMOUNT", 0);
        }
        else{
            return sharedPreferences.getInt("SP_OFFICE_TOTALAMOUNT",0);
        }
    }
    public static String getSPLastTransactionDate(Activity activity){
        sharedPreferences= activity.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
        if(getSPAreaOfTransaction(activity).equals("home")) {
            return  sharedPreferences.getString("SP_HOME_LASTTRANSACTION_DATE", dateFormat.format(new Date()));
        }
        else {
            return sharedPreferences.getString("SP_OFFICE_LASTTRANSACTION_DATE",dateFormat.format(new Date()));
        }
    }

    public static void putSPIncome(Activity activity, Integer amount){
        sharedPreferences= activity.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(getSPAreaOfTransaction(activity).equals("home")) {
            editor.putInt("SP_HOME_INCOME", amount + getSPIncome(activity));
        }
        else {
            editor.putInt("SP_OFFICE_INCOME",amount + getSPIncome(activity));
        }
        editor.apply();
    }
    public static Integer getSPIncome(Activity activity){
        sharedPreferences= activity.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        if(getSPAreaOfTransaction(activity).equals("home")) {
            return  sharedPreferences.getInt("SP_HOME_INCOME",0);
        }
        else {
            return sharedPreferences.getInt("SP_OFFICE_INCOME",0);
        }
    }
    public static void putSPSavings(Activity activity, Integer amount){
        sharedPreferences= activity.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(getSPAreaOfTransaction(activity).equals("home")) {
            editor.putInt("SP_HOME_SAVINGS", amount + getSPSavings(activity));
        }
        else {
            editor.putInt("SP_OFFICE_SAVINGS",amount + getSPSavings(activity));
        }
        editor.apply();
    }
    public static Integer getSPSavings(Activity activity){
        sharedPreferences= activity.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        if(getSPAreaOfTransaction(activity).equals("home")) {
            return  sharedPreferences.getInt("SP_HOME_SAVINGS",0);
        }
        else {
            return sharedPreferences.getInt("SP_OFFICE_SAVINGS",0);
        }
    }

    public static Boolean isDateCurrentMonthAndYear(String userDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
        Date date = null;
        int month, year;
        String currentDate = dateFormat.format(new Date());
        try {
            date = dateFormat.parse(userDate);
            month = date.getMonth();// Integer.parseInt(dateFormat.format(date));
            year = date.getYear();
            if (dateFormat.parse(currentDate).getMonth() == month && dateFormat.parse(currentDate).getYear() == year) {
                return true;
            }
            else{
                return false;
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
