package com.mcapp.mcapp.ui.dashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mcapp.mcapp.MainActivity;
import com.mcapp.mcapp.Model;
import com.mcapp.mcapp.MyAdapter;
import com.mcapp.mcapp.R;
import com.mcapp.mcapp.ViewItemActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment implements MyAdapter.MyAdapterEvents {

    private DashboardViewModel dashboardViewModel;
    public Integer[] FilterValues = {0,0}; // index-0 ->[1, category],[2, paymentMethod],[3, date], index-1 -> [1, lowToHigh], [2, highToLow]

    MyAdapter myAdapter;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;

    public ArrayList<Model> dashboardDataList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        recyclerView = root.findViewById(R.id.recycler_view);

        final ChipGroup filterChipGroup = root.findViewById(R.id.filter_chip_group);
        final ChipGroup choiceChipGroup = root.findViewById(R.id.choice_chip_group);

        /*filterChipGroup.clearCheck();
        choiceChipGroup.clearCheck();
        FilterValues[0] = FilterValues[1] = 0;*/
        filterChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {
            }
        });

        choiceChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, @IdRes int i) {
            }
        });

        Button btnApplyFilter = root.findViewById(R.id.btn_apply);
        btnApplyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chip filter = filterChipGroup.findViewById(filterChipGroup.getCheckedChipId());
                Chip sort = choiceChipGroup.findViewById(choiceChipGroup.getCheckedChipId());
                if(filter.getText() == "Category"){ FilterValues[0] = 1;}
                else if(filter.getText() == "Payment Method"){ FilterValues[0] = 2;}
                else if(filter.getText() == "Date"){ FilterValues[0] = 3;}
                if(sort.getText() == "Low to high"){ FilterValues[1] = 1;}
                if(sort.getText() == "High to low"){ FilterValues[1] = 2;}
                Toast.makeText(getContext(),filter.getText(),Toast.LENGTH_SHORT).show();
            }
        });

        Button btnCancelFilter = root.findViewById(R.id.btn_clear);
        btnCancelFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterChipGroup.clearCheck();
                choiceChipGroup.clearCheck();
                FilterValues[0] = FilterValues[1] = 0;
            }
        });
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // filter listview
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                myAdapter.getFilter().filter(s.toString());
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_sort){
            LinearLayout filterLayout = (LinearLayout) this.getView().findViewById(R.id.filter_layout);

            ChipGroup filtergroup = this.getView().findViewById( R.id.filter_chip_group);
            ChipGroup choiceChipGroup = this.getView().findViewById(R.id.choice_chip_group);

            if(filterLayout.getVisibility() == View.VISIBLE ) {
                filterLayout.setVisibility(this.getView().GONE);
            }
            else{
                filterLayout.setVisibility(this.getView().VISIBLE);
            }

            Toast.makeText(getContext(),"Filter clicked ",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        fetchOnLoadData();
    }

    public void fetchOnLoadData(){
        progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dashboardDataList.clear();
        final MyAdapter.MyAdapterEvents events = this;

        db.collection("MCCollection").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot doc: task.getResult()){
                            Model model = new Model(
                                    doc.getId(),
                                    doc.getString("Amount"),
                                    doc.getString("Category"),
                                    doc.getString("Comment"),
                                    doc.getString("Date"),
                                    doc.getString("PaymentMethod")
                                    );
                            dashboardDataList.add(model);
                        }
                        progressDialog.dismiss();
                        myAdapter = new MyAdapter(events,getContext(),dashboardDataList);
                        recyclerView.setAdapter(myAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        Toast.makeText(getContext(),"fetched data: "+  dashboardDataList.size(),Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Failed to get data..",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openItemActivity(Model data){
            Intent intent = new Intent(getActivity(), ViewItemActivity.class);
            intent.putExtra("id",data.getId());
            intent.putExtra("amount",data.getAmount());
            intent.putExtra("category",data.getCategory());
            intent.putExtra("comment",data.getComment());
            intent.putExtra("date",data.getDate());
            intent.putExtra("paymentMethod",data.getPaymentMethod());
            getActivity().startActivity(intent);
    }

    public void deleteData(int index)
    {
        db.collection("MCCollection").document(dashboardDataList.get(index).getId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(),"Deleted..",Toast.LENGTH_SHORT).show();
                        fetchOnLoadData();
                    }
                }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(getContext(), "Failed to delete data..", Toast.LENGTH_SHORT).show();
        }
    });
    }

    @Override
    public void onMyAdapterClicked(Model dataModel) {
        openItemActivity(dataModel);
    }

}