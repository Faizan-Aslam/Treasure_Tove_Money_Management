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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.mcapp.mcapp.ProgressBarActions;
import com.mcapp.mcapp.R;
import com.mcapp.mcapp.ViewItemActivity;
import com.mcapp.mcapp.ui.GlobalClass;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment implements MyAdapter.MyAdapterEvents {

    private DashboardViewModel dashboardViewModel;
    private ProgressBarActions progressBarActions = new ProgressBarActions();
    private SearchView searchView ;

    MyAdapter myAdapter;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    private ProgressBar progressBar;

    public ArrayList<Model> dashboardDataList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GlobalClass globalVariable;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        try {
            recyclerView = root.findViewById(R.id.recycler_view);
            progressBar = (ProgressBar) root.findViewById(R.id.progress_bar);

            final ChipGroup filterChipGroup = root.findViewById(R.id.filter_chip_group);
            final ChipGroup choiceChipGroup = root.findViewById(R.id.choice_chip_group);

            globalVariable = (GlobalClass) getActivity().getApplicationContext();
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
                    if (filter != null) {
                        globalVariable.setFilterValue(filter.getText().toString());
                    }
                    if (sort != null) {
                        globalVariable.setSortValue(sort.getText().toString());
                    }
                    toggleFilter();
                }
            });

            Button btnCancelFilter = root.findViewById(R.id.btn_clear);
            btnCancelFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filterChipGroup.clearCheck();
                    choiceChipGroup.clearCheck();
                    globalVariable.setFilterValue(null);
                    globalVariable.setSortValue(null);
                    toggleFilter();
                }
            });
        }
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return root;
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
            inflater.inflate(R.menu.menu_main, menu);
            final MenuItem item = menu.findItem(R.id.action_search);
            searchView = (SearchView) item.getActionView();
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
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            int id = item.getItemId();
            if (id == R.id.action_sort) {
                toggleFilter();
                //Toast.makeText(getContext(),"Filter clicked ",Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void toggleFilter(){
        try {
            LinearLayout filterLayout = (LinearLayout) this.getView().findViewById(R.id.filter_layout);
            if (filterLayout.getVisibility() == View.VISIBLE) {
                filterLayout.setVisibility(this.getView().GONE);
            } else {
                filterLayout.setVisibility(this.getView().VISIBLE);
            }
            if (!searchView.isIconified()) {
                searchView.setIconified(true);
            }
        }
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
        //ChipGroup filtergroup = this.getView().findViewById( R.id.filter_chip_group);
        //ChipGroup choiceChipGroup = this.getView().findViewById(R.id.choice_chip_group);
    }
    @Override
    public void onResume(){
        try {
            super.onResume();
            globalVariable.setFilterValue(null);
            globalVariable.setSortValue(null);
            fetchOnLoadData();
        }
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    public void fetchOnLoadData(){
        try {
            dashboardDataList.clear();
            progressBarActions.showProgressBar(progressBar, getActivity());
            final MyAdapter.MyAdapterEvents events = this;

            db.collection("MCCollection").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                Model model = new Model(
                                        doc.getId(),
                                        doc.getString("TransactionName"),
                                        doc.getString("Amount"),
                                        doc.getString("Category"),
                                        doc.getString("Comment"),
                                        doc.getString("Date"),
                                        doc.getString("PaymentMethod")
                                );
                                dashboardDataList.add(model);
                            }
                            progressBarActions.hideProgressBar(progressBar, getActivity());
                            myAdapter = new MyAdapter(events, getContext(), dashboardDataList, getActivity().getApplicationContext());
                            recyclerView.setAdapter(myAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            //Toast.makeText(getContext(),"fetched data: "+  dashboardDataList.size(),Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Failed to get data..", Toast.LENGTH_SHORT).show();
                    progressBarActions.hideProgressBar(progressBar, getActivity());
                }
            });
        }
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void openItemActivity(Model data){
        try {
            Intent intent = new Intent(getActivity(), ViewItemActivity.class);
            intent.putExtra("id", data.getId());
            intent.putExtra("transactionName", data.getTransactionName());
            intent.putExtra("amount", data.getAmount());
            intent.putExtra("category", data.getCategory());
            intent.putExtra("comment", data.getComment());
            intent.putExtra("date", data.getDate());
            intent.putExtra("paymentMethod", data.getPaymentMethod());
            getActivity().startActivity(intent);
        }
        catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMyAdapterClicked(Model dataModel) {
        openItemActivity(dataModel);
    }

}