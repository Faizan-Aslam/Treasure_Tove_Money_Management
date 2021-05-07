package com.mcapp.mcapp.ui.home;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mcapp.mcapp.GetFirebaseData;
import com.mcapp.mcapp.Model;
import com.mcapp.mcapp.R;
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
        pieChart = root.findViewById(R.id.pieChart);
        barChart = root.findViewById(R.id.barChart);

        Task<QuerySnapshot> data = getFirebaseData.fetchOnLoadDataTest(this.getContext());
        data.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                dataList = getFirebaseData.getData(task);
                Toast.makeText(getContext(),"firebase data: "+  dataList.size(),Toast.LENGTH_SHORT).show();
                // Pie chart
                preparePieChart();

                //Bar chart
                prepareBarChart();
            }
        });
        //Toast.makeText(this.getContext(),"dataList size : "+ dataList.size(),Toast.LENGTH_SHORT).show();


        return root;
    }

    private void preparePieChart(){
        getPieEntries();
        pieDataSet = new PieDataSet(pieEntries,"Amount Spent");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16F);

        pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Amount Spent");
        pieChart.animate();

        pieChart.notifyDataSetChanged(); // let the chart know it's data changed
        pieChart.invalidate();
    }
    private void getPieEntries() {
        pieEntries = new ArrayList<>();

       String c1 = "Food",c2 ="House Rent",c3= "Entertainment",c4="Savings",c5="Income";
        ArrayList<Model> c1Entries = new ArrayList<>(),
                c2Entries = new ArrayList<>(),
                c3Entries = new ArrayList<>(),
                c4Entries = new ArrayList<>(),
                c5Entries = new ArrayList<>();

        for (Model mod : dataList) {
            if(mod.getCategory().contains(c1))
                c1Entries.add(mod);
            else if(mod.getCategory().contains(c2))
                c2Entries.add(mod);
            else if(mod.getCategory().contains(c3))
                c3Entries.add(mod);
            else if(mod.getCategory().contains(c4))
                c4Entries.add(mod);
            else if(mod.getCategory().contains(c5))
                c5Entries.add(mod);
        }
        Toast.makeText(this.getContext(),"food total : "+ dataList.size() + c1Entries.size() + c2Entries.size() + getSumOfEntries(c1Entries),Toast.LENGTH_SHORT).show();

        pieEntries.add(new PieEntry(getSumOfEntries(c1Entries),c1));
        pieEntries.add(new PieEntry(getSumOfEntries(c2Entries),c2));
        pieEntries.add(new PieEntry(getSumOfEntries(c3Entries),c3));
        pieEntries.add(new PieEntry(getSumOfEntries(c4Entries),c4));
        pieEntries.add(new PieEntry(getSumOfEntries(c5Entries),c5));

    }

    private void prepareBarChart(){
        getBarEntries();
        barDataSet = new BarDataSet(barEntries, "Months");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        ArrayList<String> labels = new ArrayList<String>();
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

        barChart.notifyDataSetChanged(); // let the chart know it's data changed
        barChart.invalidate();
    }
    private void getBarEntries() {
        barEntries = new ArrayList<>();
        /*barEntries.add(new BarEntry(2f, 0));
        barEntries.add(new BarEntry(4f, 1));
        barEntries.add(new BarEntry(6f, 1));
        barEntries.add(new BarEntry(8f, 3));
        barEntries.add(new BarEntry(7f, 4));
        barEntries.add(new BarEntry(3f, 3));*/

        int[] monthData = new int[12];
        Toast.makeText(this.getContext(),"Months : "+ monthData.length + monthData[1],Toast.LENGTH_SHORT).show();

        Arrays.fill(monthData, 0);
        for (Model mod : dataList) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
            Date date = null;
            int month;
            try {
                date = dateFormat.parse(mod.getDate());
                month = date.getMonth();// Integer.parseInt(dateFormat.format(date));
                monthData[month-1] = monthData[month-1] + Integer.parseInt(mod.getAmount());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        for(int i=0; i<monthData.length; i++)
        {
            barEntries.add(new BarEntry(i+1, monthData[i]));
        }
    }

    private int getSumOfEntries(ArrayList<Model> list){
        int sum = 0;
        for(Model mod: list)
        {
            sum += Long.parseLong(mod.getAmount());
        }
        return sum;
    }
}