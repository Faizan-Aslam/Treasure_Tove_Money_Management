package com.mcapp.mcapp.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mcapp.mcapp.AboutActivity;
import com.mcapp.mcapp.AreaOfTransactionActivity;
import com.mcapp.mcapp.ContactActivity;
import com.mcapp.mcapp.FAQActivity;
import com.mcapp.mcapp.GeneralClass;
import com.mcapp.mcapp.GetFirebaseData;
import com.mcapp.mcapp.LoginActivity;
import com.mcapp.mcapp.Model;
import com.mcapp.mcapp.ProgressBarActions;
import com.mcapp.mcapp.R;
import com.mcapp.mcapp.ViewProfileActivity;
import com.mcapp.mcapp.ui.GlobalClass;
import com.mcapp.mcapp.ui.dashboard.DashboardFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    GeneralClass generalClass = new GeneralClass();
    GlobalClass globalVariable = new GlobalClass();
    GetFirebaseData getFirebaseData = new GetFirebaseData();
    Integer excessAmount = 0;
    TextView messageBox, userName, budget, areaOfTransaction, expenditures, income,savings;
    CardView cardView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        try{
            cardView = root.findViewById(R.id.cardview_mesageBox);
            messageBox = root.findViewById(R.id.txt_messageBox);
            userName = root.findViewById(R.id.txt_userName);
            budget = root.findViewById(R.id.txt_currentMonthBudget);
            areaOfTransaction = root.findViewById(R.id.txt_areaOfTransaction);
            expenditures = root.findViewById(R.id.txt_currentMonthExpenditures);
            income = root.findViewById(R.id.txt_currentMonthIncome);
            savings = root.findViewById(R.id.txt_currentMonthSavings);
            // getFirebaseData.setMonthlyTotalAmountSpentAndDate(getActivity());

        }
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }


        return root;
    }

    @Override
    public void onResume(){
        try {
            super.onResume();
            cardView.setVisibility(View.GONE);
            userName.setText("Hi " + generalClass.getSPUserName(getActivity()));
            areaOfTransaction.setText(generalClass.getSPAreaOfTransaction(getActivity()));
            budget.setText(generalClass.getSPCurrencySymbol(getActivity()) + " " + generalClass.getSPBudget(getActivity()));
            expenditures.setText(generalClass.getSPCurrencySymbol(getActivity()) + " " + generalClass.getSPTotalAmountSpent(getActivity()));
            income.setText(generalClass.getSPCurrencySymbol(getActivity()) + " " + generalClass.getSPIncome(getActivity()));
            savings.setText(generalClass.getSPCurrencySymbol(getActivity()) + " " + generalClass.getSPSavings(getActivity()));

            excessAmount =  generalClass.checkBudgetConstraints(0,getActivity());
            if(excessAmount > 0){
                cardView.setVisibility(View.VISIBLE);
                messageBox.setText("You have exceeded monthly budget by : "+ generalClass.getSPCurrencySymbol(getActivity()) + " " + excessAmount);
            }
        }
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            setHasOptionsMenu(true);
            super.onCreate(savedInstanceState);
        }
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        try {
            inflater.inflate(R.menu.help_menu, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        try{
            int id = menuItem.getItemId();
            if (id == R.id.action_about) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                getActivity().startActivity(intent);
            } else if (id == R.id.action_faq) {
                Intent intent = new Intent(getActivity(), FAQActivity.class);
                getActivity().startActivity(intent);
            } else if (id == R.id.action_contact) {
                Intent intent = new Intent(getActivity(), ContactActivity.class);
                getActivity().startActivity(intent);
            } else if (id == R.id.action_viewProfile) {
                Intent intent = new Intent(getActivity(), ViewProfileActivity.class);
                getActivity().startActivity(intent);
            } else if (id == R.id.action_logout) {
                FirebaseAuth.getInstance().signOut();
                generalClass.putSPLoginToken(getActivity(),false);
                generalClass.putSPUserId(getActivity(),null);
                generalClass.putSPUserName(getActivity(),null);
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(intent);
            } else if (id == R.id.action_areaoftransaction) {
                Intent intent = new Intent(getActivity(), AreaOfTransactionActivity.class);
                getActivity().startActivity(intent);
            }
        }
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}