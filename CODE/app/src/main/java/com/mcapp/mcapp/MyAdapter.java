package com.mcapp.mcapp;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;


import com.mcapp.mcapp.ui.GlobalClass;
import com.mcapp.mcapp.ui.dashboard.DashboardFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements Filterable {
    private MyAdapterEvents myAdapterEvents;

    Context context;
    ArrayList<Model> modelList;
    List<Model> fullList;
    DashboardFragment dashboardFragment = new DashboardFragment();
    GlobalClass globalVariable;

    public MyAdapter(MyAdapterEvents myAdapterEvents,Context ct, ArrayList<Model> modelList, Context applicationContext) {
        try {
            this.myAdapterEvents = myAdapterEvents;
            this.context = ct;
            this.modelList = modelList;
            fullList = new ArrayList<>(modelList);
            globalVariable = (GlobalClass) applicationContext;
        }
        catch (Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        try {
            Model mod = modelList.get(position);
            //String str = mod.getCategory() + "  [ " + mod.getAmount() + " ]";
            holder.text.setText("Category : " + mod.getCategory());
            holder.amountSpent.setText("Amount Spent : $ " + mod.getAmount());
            holder.description.setText(mod.getTransactionName());

            String dateString = mod.getDate().substring(0, 10);
            //Date dt1 = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(dateString);
            holder.dateDb.setText(dateString);

            switch (mod.getPaymentMethod()) {
                case "Cash":
                    holder.imageview.setImageResource(R.drawable.cash);
                    break;
                case "Card":
                    holder.imageview.setImageResource(R.drawable.card);
                    break;
                case "Google Pay":
                    holder.imageview.setImageResource(R.drawable.gpay);
                    break;
                default:
                    holder.imageview.setImageResource(R.drawable.cash);
                    break;
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myAdapterEvents.onMyAdapterClicked(modelList.get(position));
                }
            });
        }
        catch (Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    @Override
    public Filter getFilter() {
        return filterRecyclerViewData;
    }

    private Filter filterRecyclerViewData = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String searchText = constraint.toString().toLowerCase();
            String filterValue = globalVariable.getFilterValue();
            String sortValue = globalVariable.getSortValue();
            List<Model> tempList = new ArrayList<Model>();
            try {
                if (searchText == null || searchText.isEmpty()) {
                    tempList.addAll(fullList);
                } else {
                    //int filterType =  dashboardFragment.FilterValues[0];
                    //int sortType = dashboardFragment.FilterValues[1];
                    //Toast.makeText(context,"Filter value : {0} , Sort value : {1} " + globalVariable.getFilterValue() + globalVariable.getSortValue(),Toast.LENGTH_SHORT).show();

                    for (Model item : fullList) {
                        if (filterValue == null) {
                            if (item.getTransactionName().toLowerCase().contains(searchText) || item.getCategory().toLowerCase().contains(searchText) || item.getPaymentMethod().toLowerCase().contains(searchText)
                                    || item.getComment().toLowerCase().contains(searchText) || item.getDate().toLowerCase().contains(searchText) || item.getAmount().toLowerCase().contains(searchText)) {
                                tempList.add(item);
                            }
                        } else if (filterValue.equals("Category")) {
                            Log.i("Item:", item.getCategory());
                            Log.i("Search Text:", searchText);
                            if (item.getCategory().toLowerCase().contains(searchText)) {
                                tempList.add(item);
                            }
                        } else if (filterValue.equals("Payment Method")) {
                            if (item.getPaymentMethod().toLowerCase().contains(searchText)) {
                                tempList.add(item);
                            }
                        } else if (filterValue.equals("Date")) {
                            if (item.getDate().toLowerCase().contains(searchText)) {
                                tempList.add(item);
                            }
                        }
                    }
                }
                if(tempList.size() > 1 && sortValue != null) {
                    if (sortValue.equals("Low to high")) {
                        Collections.sort(tempList, new Comparator<Model>() {
                            @Override
                            public int compare(Model m1, Model m2) {
                                return Integer.parseInt(m1.getAmount()) - Integer.parseInt(m2.getAmount());
                            }
                        });
                    } else if (sortValue.equals("High to low")) {
                        Collections.sort(tempList, new Comparator<Model>() {
                            @Override
                            public int compare(Model m1, Model m2) {
                                return Integer.parseInt(m2.getAmount()) - Integer.parseInt(m1.getAmount());
                            }
                        });
                    }
                 }
            }
            catch (Exception e){
                Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = tempList;
            filterResults.count =tempList.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            try {
                modelList.clear();
                //modelList.addAll((Collection<? extends Model>) results.values);
                modelList = (ArrayList<Model>) results.values;
                notifyDataSetChanged();
            }
            catch (Exception e){
                Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    };


    public class MyViewHolder extends ViewHolder {
        public final TextView text,dateDb, description,amountSpent ;
        public final ImageView imageview;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.txt_row);
            imageview = itemView.findViewById(R.id.image_view);
            dateDb = itemView.findViewById(R.id.txt_date);
            description = itemView.findViewById(R.id.txt_description);
            amountSpent = itemView.findViewById(R.id.txt_rowamount);
        }
    }

    public interface MyAdapterEvents{
        void onMyAdapterClicked(Model dataModel);
    }

}
