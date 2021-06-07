package com.mcapp.mcapp.ui.favourites;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mcapp.mcapp.GeneralClass;
import com.mcapp.mcapp.Model;
import com.mcapp.mcapp.MyAdapter;
import com.mcapp.mcapp.ProgressBarActions;
import com.mcapp.mcapp.R;
import com.mcapp.mcapp.ViewFavouriteActivity;
import com.mcapp.mcapp.ViewItemActivity;
import com.mcapp.mcapp.ui.GlobalClass;

import java.util.ArrayList;

public class FavouritesFragment extends Fragment implements MyAdapter.MyAdapterEvents {

    private FavouritesViewModel mViewModel;
    private ProgressBarActions progressBarActions = new ProgressBarActions();
    GeneralClass generalClass = new GeneralClass();

    MyAdapter myAdapter;
    RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView noDataTextView;

    public ArrayList<Model> dashboardDataList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GlobalClass globalVariable;

    public static FavouritesFragment newInstance() {
        return new FavouritesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.favourites_fragment, container, false);

        try {
            globalVariable = (GlobalClass) getActivity().getApplicationContext();
            recyclerView = root.findViewById(R.id.recycler_view);
            progressBar = (ProgressBar) root.findViewById(R.id.progress_bar);
            noDataTextView = root.findViewById(R.id.txt_NoData);
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

            db.collection("MCCollection").whereEqualTo("UserId", generalClass.getSPUserId(getActivity()))
                    .whereEqualTo("AreaOfTransaction",generalClass.getSPAreaOfTransaction(getActivity()))
                    .whereEqualTo("Favourite", "true").get()
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
            Intent intent = new Intent(getActivity(), ViewFavouriteActivity.class);
            intent.putExtra("id", data.getId());
            intent.putExtra("transactionName", data.getTransactionName());
            intent.putExtra("amount", data.getAmount());
            intent.putExtra("category", data.getCategory());
            intent.putExtra("comment", data.getComment());
            intent.putExtra("date", data.getDate());
            intent.putExtra("paymentMethod", data.getPaymentMethod());
            intent.putExtra("currency",data.getCurrency());
            intent.putExtra("currencySymbol",data.getCurrencySymbol());
            //intent.putExtra("favourite",data.getFavourite());
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FavouritesViewModel.class);
        // TODO: Use the ViewModel
    }

}
