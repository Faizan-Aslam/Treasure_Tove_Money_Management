package com.mcapp.mcapp.ui.home;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mcapp.mcapp.AboutActivity;
import com.mcapp.mcapp.ContactActivity;
import com.mcapp.mcapp.FAQActivity;
import com.mcapp.mcapp.GetFirebaseData;
import com.mcapp.mcapp.LoginActivity;
import com.mcapp.mcapp.Model;
import com.mcapp.mcapp.ProgressBarActions;
import com.mcapp.mcapp.R;
import com.mcapp.mcapp.ViewProfileActivity;
import com.mcapp.mcapp.ui.dashboard.DashboardFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
   /* private DashboardFragment dashboardFragment;*/
    private GetFirebaseData getFirebaseData = new GetFirebaseData();
    ArrayList<Model> dataList = new ArrayList<>() ;
    private ProgressBar progressBar;
    private ProgressBarActions progressBarActions = new ProgressBarActions();

    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList barEntries;

    PieChart pieChart;
    PieData pieData;
    PieDataSet pieDataSet;
    ArrayList<PieEntry> pieEntries;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        try{
            pieChart = root.findViewById(R.id.pieChart);
            barChart = root.findViewById(R.id.barChart);
            progressBar = (ProgressBar) root.findViewById(R.id.progress_bar);

            progressBarActions.showProgressBar(progressBar,getActivity());

            Task<QuerySnapshot> data = getFirebaseData.fetchOnLoadDataTest(this.getContext());
            data.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    dataList = getFirebaseData.getData(task);
                    //Toast.makeText(getContext(),"firebase data: "+  dataList.size(),Toast.LENGTH_SHORT).show();
                    // Pie chart
                    preparePieChart();

                    //Bar chart
                    prepareBarChart();
                    progressBarActions.hideProgressBar(progressBar, getActivity());
                }
            });
        }
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }


        return root;
    }

    private void preparePieChart(){
        try {
            getPieEntries();
            pieDataSet = new PieDataSet(pieEntries, "Amount Spent");
            pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            pieDataSet.setValueTextColor(Color.BLACK);
            pieDataSet.setValueTextSize(16F);

            pieData = new PieData(pieDataSet);
            pieChart.setData(pieData);
            pieChart.getDescription().setEnabled(false);
            pieChart.setCenterText("Amount Spent");
            pieChart.setEntryLabelColor(Color.BLACK);
            pieChart.animate();

            pieChart.notifyDataSetChanged(); // let the chart know it's data changed
            pieChart.invalidate();
        }
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    private void getPieEntries() {
        pieEntries = new ArrayList<>();
        try {
            String c1 = "Food", c2 = "House Rent", c3 = "Entertainment", c4 = "Savings", c5 = "Income";
            ArrayList<Model> c1Entries = new ArrayList<>(),
                    c2Entries = new ArrayList<>(),
                    c3Entries = new ArrayList<>(),
                    c4Entries = new ArrayList<>(),
                    c5Entries = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
            String currentDate = sdf.format(new Date());
            Date date = null;

            for (Model mod : dataList) {
                date = sdf.parse(mod.getDate());
                if(sdf.parse(currentDate).getYear() == date.getYear()) {
                    if (mod.getCategory().contains(c1))
                        c1Entries.add(mod);
                    else if (mod.getCategory().contains(c2))
                        c2Entries.add(mod);
                    else if (mod.getCategory().contains(c3))
                        c3Entries.add(mod);
                    else if (mod.getCategory().contains(c4))
                        c4Entries.add(mod);
                    else if (mod.getCategory().contains(c5))
                        c5Entries.add(mod);
                }
            }
            //Toast.makeText(this.getContext(),"food total : "+ dataList.size() + c1Entries.size() + c2Entries.size() + getSumOfEntries(c1Entries),Toast.LENGTH_SHORT).show();

            pieEntries.add(new PieEntry(getSumOfEntries(c1Entries), c1));
            pieEntries.add(new PieEntry(getSumOfEntries(c2Entries), c2));
            pieEntries.add(new PieEntry(getSumOfEntries(c3Entries), c3));
            pieEntries.add(new PieEntry(getSumOfEntries(c4Entries), c4));
            pieEntries.add(new PieEntry(getSumOfEntries(c5Entries), c5));
        }
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void prepareBarChart(){
        try {
            getBarEntries();
            barDataSet = new BarDataSet(barEntries, "Months");
            barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            barDataSet.setValueTextColor(Color.BLACK);
            barDataSet.setValueTextSize(16f);

            final ArrayList<String> labels = new ArrayList<String>();
            labels.add("Jan");
            labels.add("Feb");
            labels.add("Mar");
            labels.add("Apr");
            labels.add("May");
            labels.add("June");
            labels.add("July");
            labels.add("Aug");
            labels.add("Sep");
            labels.add("Oct");
            labels.add("Nov");
            labels.add("Dec");

            barData = new BarData(barDataSet);
            barChart.setData(barData);
            barChart.getDescription().setText("Bar Chart");
            barChart.animateY(2000);

            XAxis xAxis = barChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
            ValueFormatter formatter = new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return labels.get((int) value - 1);
                }
            };

            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(formatter);
            barChart.notifyDataSetChanged(); // let the chart know it's data changed
            barChart.invalidate();
        }
        catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private void getBarEntries() {
        barEntries = new ArrayList<>();
        /*barEntries.add(new BarEntry(2f, 0));
        barEntries.add(new BarEntry(4f, 1));
        barEntries.add(new BarEntry(6f, 1));
        barEntries.add(new BarEntry(8f, 3));
        barEntries.add(new BarEntry(7f, 4));
        barEntries.add(new BarEntry(3f, 3));*/
        try {
            int[] monthData = new int[12];
            //Toast.makeText(this.getContext(),"Months : "+ monthData.length + monthData[1],Toast.LENGTH_SHORT).show();

            Arrays.fill(monthData, 0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm",Locale.getDefault());
            Date date = null;
            int month, year;
            String currentDate = dateFormat.format(new Date());
            for (Model mod : dataList) {
                try {
                    date = dateFormat.parse(mod.getDate());
                    month = date.getMonth();// Integer.parseInt(dateFormat.format(date));
                    year = date.getYear();
                    if(dateFormat.parse(currentDate).getYear() == year) {
                        monthData[month] = monthData[month] + Integer.parseInt(mod.getAmount());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < monthData.length; i++) {
                barEntries.add(new BarEntry(i + 1, monthData[i]));
            }
        }
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private int getSumOfEntries(ArrayList<Model> list){
        int sum = 0;
        try {
            for (Model mod : list) {
                sum += Long.parseLong(mod.getAmount());
            }
        }
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return sum;
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
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(intent);
            }
        }
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}