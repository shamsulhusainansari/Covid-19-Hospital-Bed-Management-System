package com.welcomebarb.barb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;
import com.welcomebarb.barb.Model.BarbViewHolder;
import com.welcomebarb.barb.Model.Data;

import java.util.Objects;

public class SortActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FirestorePagingAdapter<Data, BarbViewHolder> mAdapter;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private Query mQuery;
    private FirebaseAuth mAuth;
    private static final int PERMISSIONS_STORAGE_CODE = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);
        mAuth = FirebaseAuth.getInstance();
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        //String collection1 = bundle.getString("collection");
        String selection1=bundle.getString("selection");
        String equalto1=bundle.getString("equalto");

        //assert collection1 != null;
        CollectionReference mPostsCollection = mFirestore.collection("Barb");

        assert selection1 != null;
        mQuery = mPostsCollection.orderBy("date", Query.Direction.DESCENDING).whereEqualTo("city",equalto1);

        mRecyclerView = findViewById(R.id.sort_recycler_view);

        mSwipeRefreshLayout = findViewById(R.id.refreshLayout);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        setupAdapter();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.refresh();
            }
        });

    }

    private void setupAdapter() {


        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(5)
                .setPageSize(3)
                .build();

        FirestorePagingOptions options = new FirestorePagingOptions.Builder<Data>()
                .setLifecycleOwner(this)
                .setQuery(mQuery, config, Data.class)
                .build();

        mAdapter = new FirestorePagingAdapter<Data, BarbViewHolder>(options) {
            @NonNull
            @Override
            public BarbViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = getLayoutInflater().inflate(R.layout.grid_card_layout, parent, false);
                return new BarbViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull BarbViewHolder viewHolder, int i, @NonNull final Data data) {
                viewHolder.bind(data);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                                SortActivity.this, R.style.BottomSheetDialogTheme
                        );
                        View bottomSheetView = LayoutInflater.from(SortActivity.this)
                                .inflate(R.layout.bottom_sheet, findViewById(R.id.bottomsheetcontainer));
                        TextView customer=bottomSheetView.findViewById(R.id.payName);
                        TextView custEmail=bottomSheetView.findViewById(R.id.payEmail);
                        TextView PayAmount=bottomSheetView.findViewById(R.id.payAmount);
                        int available=Integer.parseInt(data.getAvailableBed());

                        PayAmount.setText("No. of Available Bed: ".concat(data.getAvailableBed()));
                        TextView mainTitle=bottomSheetView.findViewById(R.id.mainTitle);
                        mainTitle.setText(data.getTitle());
                        custEmail.setText(mAuth.getCurrentUser().getEmail());
                        customer.setText(mAuth.getCurrentUser().getDisplayName());
                        ImageView productImage=bottomSheetView.findViewById(R.id.imageView2);
                        Picasso.get().load(data.getBackgroundImage()).into(productImage);

                        if (available>0){
                            bottomSheetView.findViewById(R.id.btnPay).setOnClickListener(v1 -> {
                                Intent intent=new Intent(SortActivity.this, ApplyActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("hospitalName", data.getTitle());
                                bundle.putString("authName", Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());
                                bundle.putString("authEmail",Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
                                intent.putExtras(bundle);
                                startActivity(intent);
                            });
                        }else {
                            bottomSheetView.findViewById(R.id.btnPay).setOnClickListener(v1 -> {
                                Toast.makeText(SortActivity.this, "Sorry! No Bed Available", Toast.LENGTH_SHORT).show();
                            });
                        }
                        bottomSheetView.findViewById(R.id.call).setOnClickListener(v1 -> {
                            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                                if (checkSelfPermission(Manifest.permission.CALL_PHONE)==
                                        PackageManager.PERMISSION_DENIED){

                                    String[] permissions={Manifest.permission.CALL_PHONE};
                                    requestPermissions(permissions,PERMISSIONS_STORAGE_CODE);

                                }else{
                                    try {
                                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                                        callIntent.setData(Uri.parse("tel:"+data.getPhone()));
                                        startActivity(callIntent);
                                    } catch (ActivityNotFoundException activityException) {
                                        Log.e("Calling a Phone Number", "Call failed", activityException);
                                    }
                                    bottomSheetDialog.dismiss();
                                }}
                        });

                        bottomSheetDialog.setContentView(bottomSheetView);
                        bottomSheetDialog.show();

                    }
                });
            }

            @Override
            protected void onError(@NonNull Exception e) {
                super.onError(e);
                Log.e("SortActivity", Objects.requireNonNull(e.getMessage()));
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:
                        Log.d("PAGINATION: ","Loading Initial Data!");
                    case LOADING_MORE:

                        mSwipeRefreshLayout.setRefreshing(true);
                        Log.d("PAGINATION: ","Loading More!");
                        break;

                    case LOADED:
                        mSwipeRefreshLayout.setRefreshing(false);
                        Log.d("PAGINATION: ","Loaded");
                        break;

                    case ERROR:
                        Log.d("PAGINATION: ","Error!");
                        Toast.makeText(
                                getApplicationContext(),
                                "Error Occurred!",
                                Toast.LENGTH_SHORT
                        ).show();

                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case FINISHED:
                        mSwipeRefreshLayout.setRefreshing(false);
                        Log.d("PAGINATION: ","All Item Loaded: "+getItemCount());
                        break;
                }
            }

        };

        mRecyclerView.setAdapter(mAdapter);

    }
}