package com.mcapp.mcapp.ui.charts;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcapp.mcapp.R;
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
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ChartsFragment extends Fragment implements View.OnClickListener  {
    private GetFirebaseData getFirebaseData = new GetFirebaseData();
    ArrayList<Model> dataList = new ArrayList<>() ;
    private ProgressBar progressBar;
    private ProgressBarActions progressBarActions = new ProgressBarActions();

    CardView piecharttab,piecharttab2, barcharttab;

    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet1;
    BarDataSet barDataSet2;
    ArrayList barEntries1;
    ArrayList barEntries2;

    PieChart pieChart;
    PieData pieData;
    PieDataSet pieDataSet;
    ArrayList<PieEntry> pieEntries;

    PieChart pieChart2;
    PieData pieData2;
    PieDataSet pieDataSet2;
    ArrayList<PieEntry> pieEntries2;


    ArrayList<IBarDataSet> dataSets = new ArrayList<>();
    float defaultBarWidth = -1;
    List<String> xAxisValues = new ArrayList<>(Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "June","July", "Aug", "Sep", "Oct", "Nov", "Dec"));


    ColorStateList def;
    TextView item1;
    TextView item2;
    TextView select;

    private ChartsViewModel mViewModel;

    public static ChartsFragment newInstance() {
        return new ChartsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.charts_fragment, container, false);
        try{
            item1 = root.findViewById(R.id.item1);
            item2 = root.findViewById(R.id.item2);
            item1.setOnClickListener(this);
            item2.setOnClickListener(this);
            select = root.findViewById(R.id.select);
            def = item2.getTextColors();

            piecharttab = root.findViewById(R.id.pieChartTab);
            barcharttab = root.findViewById(R.id.barChartTab);
            pieChart = root.findViewById(R.id.pieChart);
            pieChart2 = root.findViewById(R.id.pieChart2);
            barChart = root.findViewById(R.id.barChart);
            progressBar = (ProgressBar) root.findViewById(R.id.progress_bar);

            progressBarActions.showProgressBar(progressBar,getActivity());

            Task<QuerySnapshot> data = getFirebaseData.fetchOnLoadData(this.getContext(), getActivity());
            data.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    dataList = getFirebaseData.getData(task);
                    //Toast.makeText(getContext(),"firebase data: "+  dataList.size(),Toast.LENGTH_SHORT).show();
                    // Pie chart
                    preparePieChart();
                    preparePieChart2();

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ChartsViewModel.class);
        // TODO: Use the ViewModel
    }

    private void preparePieChart(){
        try {
            getPieEntries();
            pieDataSet = new PieDataSet(pieEntries, "");
            pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
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
    private void preparePieChart2(){
        try {
            getPieEntries2();
            pieDataSet2 = new PieDataSet(pieEntries2, "");
            pieDataSet2.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
            pieDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);
            pieDataSet2.setValueTextColor(Color.BLACK);
            pieDataSet2.setValueTextSize(16F);

            pieData2 = new PieData(pieDataSet2);
            pieChart2.setData(pieData2);
            pieChart2.getDescription().setEnabled(false);
            pieChart2.setCenterText("Savings/ Income");
            pieChart2.setEntryLabelColor(Color.BLACK);
            pieChart2.animate();

            pieChart2.notifyDataSetChanged(); // let the chart know it's data changed
            pieChart2.invalidate();
        }
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    private void getPieEntries() {
        pieEntries = new ArrayList<>();
        try {
            String c1 = "Food", c2 = "House Rent", c3 = "Entertainment";
            ArrayList<Model> c1Entries = new ArrayList<>(),
                    c2Entries = new ArrayList<>(),
                    c3Entries = new ArrayList<>();
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
                }
            }
            //Toast.makeText(this.getContext(),"food total : "+ dataList.size() + c1Entries.size() + c2Entries.size() + getSumOfEntries(c1Entries),Toast.LENGTH_SHORT).show();

            pieEntries.add(new PieEntry(getSumOfEntries(c1Entries), c1));
            pieEntries.add(new PieEntry(getSumOfEntries(c2Entries), c2));
            pieEntries.add(new PieEntry(getSumOfEntries(c3Entries), c3));
        }
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    private void getPieEntries2() {
        pieEntries2 = new ArrayList<>();
        try {
            String c4 = "Savings", c5 = "Income";
            ArrayList<Model> c4Entries = new ArrayList<>(),
                    c5Entries = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
            String currentDate = sdf.format(new Date());
            Date date = null;

            for (Model mod : dataList) {
                date = sdf.parse(mod.getDate());
                if(sdf.parse(currentDate).getYear() == date.getYear()) {
                    if (mod.getCategory().contains(c4))
                        c4Entries.add(mod);
                    else if (mod.getCategory().contains(c5))
                        c5Entries.add(mod);
                }
            }
            //Toast.makeText(this.getContext(),"food total : "+ dataList.size() + c1Entries.size() + c2Entries.size() + getSumOfEntries(c1Entries),Toast.LENGTH_SHORT).show();

            pieEntries2.add(new PieEntry(getSumOfEntries(c4Entries), c4));
            pieEntries2.add(new PieEntry(getSumOfEntries(c5Entries), c5));
        }
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void prepareBarChart(){
        try {
            getBarEntries();
            //barDataSet.setValueTextColor(Color.BLACK);
            //barDataSet.setValueTextSize(16f);
            barDataSet1 = new BarDataSet(barEntries1, "Income");
            barDataSet1.setColor(Color.GREEN);
            barDataSet2 = new BarDataSet(barEntries2, "Expenses");
            barDataSet2.setColor(Color.RED);

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

            float groupSpace = 0.01f;
            float barSpace = 0.02f; // x2 dataset
            float barWidth = 0.475f; // x2 dataset
            // (0.02 + 0.45) * 2 + 0.06 = 1.00 -> interval per "group"
            // (barWidth + barSpace)*count + groupSpace = 1

            barData = new BarData(barDataSet1,barDataSet2);
            barData.setBarWidth(barWidth); // set the width of each bar
            barChart.setData(barData);
            barChart.groupBars(0, groupSpace, barSpace); // perform the "explicit" grouping
            barChart.setFitBars(true);
            barChart.getDescription().setText("Bar Chart");
            barChart.animateY(2000);
            barChart.getDescription().setEnabled(false);

            XAxis xAxis = barChart.getXAxis();

            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            ValueFormatter formatter = new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return labels.get((int) value -1);
                }
            };
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            //xAxis.setGranularityEnabled(true);
            xAxis.setValueFormatter(formatter);
            //xAxis.setCenterAxisLabels(true);
            barChart.notifyDataSetChanged(); // let the chart know it's data changed
            barChart.invalidate();

            /*barDataSet1 = new BarDataSet(barEntries1, "Income");
            barDataSet1.setColor(Color.rgb(65, 168, 121));
            barDataSet1.setValueTextColor(Color.rgb(55, 70, 73));
            barDataSet1.setValueTextSize(10f);

            barDataSet2 = new BarDataSet(barEntries2, "Expense");
            barDataSet2.setColors(Color.rgb(241, 107, 72));
            barDataSet2.setValueTextColor(Color.rgb(55, 70, 73));
            barDataSet2.setValueTextSize(10f);

            dataSets.add(barDataSet1);
            dataSets.add(barDataSet2);

            BarData data = new BarData(dataSets);
            barChart.setData(data);
            barChart.getAxisLeft().setAxisMinimum(0);

            barChart.getDescription().setEnabled(false);
            barChart.getAxisRight().setAxisMinimum(0);
            barChart.setDrawBarShadow(false);
            barChart.setDrawValueAboveBar(true);
            barChart.setMaxVisibleValueCount(10);
            barChart.setPinchZoom(false);
            barChart.setDrawGridBackground(false);

            Legend l = barChart.getLegend();
            l.setWordWrapEnabled(true);
            l.setTextSize(14);
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            l.setDrawInside(false);
            l.setForm(Legend.LegendForm.CIRCLE);

            XAxis xAxis = barChart.getXAxis();
            xAxis.setGranularity(1f);
            xAxis.setCenterAxisLabels(true);
            xAxis.setDrawGridLines(false);
            xAxis.setLabelRotationAngle(-45);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setAxisMaximum(barEntries1.size());

            barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisValues));

            YAxis leftAxis = barChart.getAxisLeft();
            leftAxis.removeAllLimitLines();
            leftAxis.setTypeface(Typeface.DEFAULT);
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            leftAxis.setTextColor(Color.BLACK);
            leftAxis.setDrawGridLines(false);
            barChart.getAxisRight().setEnabled(false);

            setBarWidth(data, 2);
            barChart.invalidate();*/
        }
        catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private void setBarWidth(BarData barData, int size) {
        if (dataSets.size() > 1) {
            float barSpace = 0.02f;
            float groupSpace = 0.3f;
            defaultBarWidth = (1 - groupSpace) / dataSets.size() - barSpace;
            if (defaultBarWidth >= 0) {
                barData.setBarWidth(defaultBarWidth);
            } else {
                Toast.makeText(getContext(), "Default Barwdith " + defaultBarWidth, Toast.LENGTH_SHORT).show();
            }
            int groupCount = barEntries1.size();
            if (groupCount != -1) {
                barChart.getXAxis().setAxisMinimum(0);
                barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
                barChart.getXAxis().setCenterAxisLabels(true);
            } else {
                Toast.makeText(getContext(), "no of bar groups is " + groupCount, Toast.LENGTH_SHORT).show();
            }

            barChart.groupBars(0, groupSpace, barSpace); // perform the "explicit" grouping
            barChart.invalidate();
        }
    }
    private void getBarEntries() {
        barEntries1 = new ArrayList<>();
        barEntries2 = new ArrayList<>();
        try {
            int[] monthData = new int[12];
            int[] monthDataExpenses = new int[12];
            //Toast.makeText(this.getContext(),"Months : "+ monthData.length + monthData[1],Toast.LENGTH_SHORT).show();

            Arrays.fill(monthData, 0);
            Arrays.fill(monthDataExpenses,0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm",Locale.getDefault());
            Date date = null;
            int month, year;
            String currentDate = dateFormat.format(new Date());
            for (Model mod : dataList) {
                try {
                    date = dateFormat.parse(mod.getDate());
                    month = date.getMonth();// Integer.parseInt(dateFormat.format(date));
                    year = date.getYear();
                    if(dateFormat.parse(currentDate).getYear() == year && (mod.getCategory()).equals("Income") ) {
                        monthData[month] = monthData[month] + Integer.parseInt(mod.getAmount());
                    }
                    else if(dateFormat.parse(currentDate).getYear() == year && !(mod.getCategory()).equals("Income")){
                        monthDataExpenses[month] = monthDataExpenses[month] + Integer.parseInt(mod.getAmount());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < monthData.length; i++) {
                barEntries1.add(new BarEntry(i + 1, monthData[i]));
            }
            for (int i = 0; i < monthDataExpenses.length; i++) {
                barEntries2.add(new BarEntry(i + 1, monthDataExpenses[i]));
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
    public void onResume(){
        try {
            super.onResume();
            piecharttab.setVisibility(View.VISIBLE);
            barcharttab.setVisibility(View.GONE);
        }
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.item1){
            select.animate().x(0).setDuration(100);
            item1.setTextColor(Color.WHITE);
            item2.setTextColor(def);
            piecharttab.setVisibility(View.VISIBLE);
            barcharttab.setVisibility(View.GONE);
        } else{
            item1.setTextColor(def);
            item2.setTextColor(Color.WHITE);
            int size = item2.getWidth();
            select.animate().x(size).setDuration(100);
            barcharttab.setVisibility(View.VISIBLE);
            piecharttab.setVisibility(View.GONE);
        }
    }
}
