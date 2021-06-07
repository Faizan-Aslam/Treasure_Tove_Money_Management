package com.mcapp.mcapp.ui.dashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.os.Environment;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mcapp.mcapp.GeneralClass;
import com.mcapp.mcapp.MainActivity;
import com.mcapp.mcapp.Model;
import com.mcapp.mcapp.MyAdapter;
import com.mcapp.mcapp.ProgressBarActions;
import com.mcapp.mcapp.R;
import com.mcapp.mcapp.ViewItemActivity;
import com.mcapp.mcapp.ui.GlobalClass;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class DashboardFragment extends Fragment implements MyAdapter.MyAdapterEvents {

    private DashboardViewModel dashboardViewModel;
    private ProgressBarActions progressBarActions = new ProgressBarActions();
    private SearchView searchView ;
    GeneralClass generalClass = new GeneralClass();

    MyAdapter myAdapter;
    RecyclerView recyclerView;
    private ProgressBar progressBar;
    TextView noDataTextView;

    public ArrayList<Model> dashboardDataList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GlobalClass globalVariable;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        try {
            recyclerView = root.findViewById(R.id.recycler_view);
            progressBar = (ProgressBar) root.findViewById(R.id.progress_bar);
            noDataTextView = root.findViewById(R.id.txt_NoData);

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
            else if(id == R.id.action_download){
                exportToExcel();
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

            db.collection("MCCollection").whereEqualTo("UserId",generalClass.getSPUserId(getActivity()))
                    .whereEqualTo("AreaOfTransaction",generalClass.getSPAreaOfTransaction(getActivity())).get()
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
                                        doc.getString("PaymentMethod"),
                                        doc.getString("Favourite"),
                                        doc.getString("Currency"),
                                        doc.getString("CurrencySymbol")
                                );
                                dashboardDataList.add(model);
                            }
                            progressBarActions.hideProgressBar(progressBar, getActivity());
                            myAdapter = new MyAdapter(events, getContext(), dashboardDataList, getActivity());
                            recyclerView.setAdapter(myAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            if(dashboardDataList.size() == 0){
                                noDataTextView.setVisibility(View.VISIBLE);
                            }
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
            intent.putExtra("favourite",data.getFavourite());
            intent.putExtra("currency",data.getCurrency());
            intent.putExtra("currencySymbol",data.getCurrencySymbol());
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

    public void exportToExcel(){
        if(dashboardDataList.size() == 0){
            Toast.makeText(getContext(),"No data to export", Toast.LENGTH_LONG).show();
        }
        else{
            AssetManager am = getResources().getAssets();
            try {
                InputStream iStream = am.open("TreasureTrove_Data.xls");
                //Workbook wb = Workbook.getWorkbook(iStream);
                Workbook existingWorkbook = Workbook.getWorkbook(iStream);
                File path = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS);
                File file = new File(path, "/" + "TreasureTrove_Data.xls");
                WritableWorkbook writableWb = Workbook.createWorkbook(file,existingWorkbook);
                WritableSheet st = writableWb.getSheet(0);

                WritableCell cellName = new Label(2,0,generalClass.getSPUserName(getActivity()));
                WritableCell cellArea = new Label(2,1,generalClass.getSPAreaOfTransaction(getActivity()));
                st.addCell(cellName);
                st.addCell(cellArea);

                int rowNumber = 4, count = 1;
                for (Model mod :dashboardDataList){
                    WritableCell cell0 = new Label(0,rowNumber,String.valueOf(count));
                    WritableCell cell1 = new Label(1,rowNumber,mod.getTransactionName());
                    WritableCell cell2 = new Label(2,rowNumber,mod.getCategory());
                    WritableCell cell3 = new Label(3,rowNumber,mod.getPaymentMethod());
                    WritableCell cell4 = new Label(4,rowNumber,mod.getAmount());
                    WritableCell cell5 = new Label(5,rowNumber,mod.getDate());
                    WritableCell cell6 = new Label(6,rowNumber,mod.getComment());

                    st.addCell(cell0);
                    st.addCell(cell1);
                    st.addCell(cell2);
                    st.addCell(cell3);
                    st.addCell(cell4);
                    st.addCell(cell5);
                    st.addCell(cell6);

                    rowNumber++;
                    count++;
                }
                writableWb.write();
                writableWb.close();
                existingWorkbook.close();
                Toast.makeText(getContext(),"Data exported successfully !!", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (BiffException e) {
                e.printStackTrace();
            } catch (RowsExceededException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        }
    }
}