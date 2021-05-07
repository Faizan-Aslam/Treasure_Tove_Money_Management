package com.mcapp.mcapp;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;


import com.mcapp.mcapp.ui.dashboard.DashboardFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
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

    public MyAdapter(MyAdapterEvents myAdapterEvents,Context ct, ArrayList<Model> modelList) {
        this.myAdapterEvents = myAdapterEvents;
        this.context = ct;
        this.modelList = modelList;
        fullList =  new ArrayList<>(modelList);
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
        Model mod= modelList.get(position);
        String str = mod.getCategory() + "  [ " + mod.getAmount() + " ]";
        holder.text.setText(str);
        holder.description.setText(mod.getComment());

        String dateString = mod.getDate().substring(0,10);
        //Date dt1 = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(dateString);
        holder.dateDb.setText(dateString);

            switch(mod.getPaymentMethod()) {
                case "Cash":
                    holder.imageview.setImageResource(R.drawable.cash);
                    break;
                case "Card":
                    holder.imageview.setImageResource(R.drawable.card);
                    break;
                case "Paytm":
                    holder.imageview.setImageResource(R.drawable.paytm);
                    break;
                case "Google Pay":
                    holder.imageview.setImageResource(R.drawable.gpay);
                    break;
                default:
                    holder.imageview.setImageResource(R.drawable.cash);
                    break;
            }
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(context,"CLICKED: "+position,Toast.LENGTH_SHORT).show();
                myAdapterEvents.onMyAdapterClicked(modelList.get(position));
            }
        });

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
            List<Model> tempList = new ArrayList<Model>();
            if(searchText.length() == 0 || searchText.isEmpty()) {
                tempList.addAll(fullList);
            }
            else {
                int filterType =  dashboardFragment.FilterValues[0];
                int sortType = dashboardFragment.FilterValues[1];
                Toast.makeText(context,"filter type[0]: " + filterType,Toast.LENGTH_SHORT).show();

                for(Model item:fullList){
                        if (item.getCategory().toLowerCase().contains(searchText) || item.getPaymentMethod().toLowerCase().contains(searchText)
                                || item.getComment().toLowerCase().contains(searchText) || item.getDate().toLowerCase().contains(searchText)) {
                            tempList.add(item);
                        }
                    /*//if(filterType == 0){
                        if(item.getCategory().toLowerCase().contains(searchText) || item.getPaymentMethod().toLowerCase().contains(searchText)
                                || item.getComment().toLowerCase().contains(searchText) || item.getDate().toLowerCase().contains(searchText))
                        {
                   // }*/
                   /* else if(filterType == 1)
                    {
                        if(item.getCategory().toLowerCase().contains(searchText)){
                            tempList.add(item);
                        }
                    }
                    else if(filterType == 2)
                    {
                        if(item.getPaymentMethod().toLowerCase().contains(searchText)){
                            tempList.add(item);
                        }
                    }
                    else if(filterType == 3)
                    {
                        if(item.getDate().toLowerCase().contains(searchText)){
                            tempList.add(item);
                        }
                    }*/
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = tempList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            modelList.clear();
            modelList.addAll((Collection<? extends Model>) results.values);
            notifyDataSetChanged();
        }
    };


    public class MyViewHolder extends ViewHolder {
        public final TextView text,dateDb, description ;
        public final ImageView imageview;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.txt_row);
            imageview = itemView.findViewById(R.id.image_view);
            dateDb = itemView.findViewById(R.id.txt_date);
            description = itemView.findViewById(R.id.txt_description);
        }
    }

    public interface MyAdapterEvents{
        void onMyAdapterClicked(Model dataModel);
    }
}
