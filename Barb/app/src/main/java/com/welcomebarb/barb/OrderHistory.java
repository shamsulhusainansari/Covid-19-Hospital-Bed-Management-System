package com.welcomebarb.barb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.welcomebarb.barb.Model.Data;
import com.welcomebarb.barb.Model.DataAdapter;

import java.util.Objects;

public class OrderHistory extends AppCompatActivity {


    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef;
    private DataAdapter adapter;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        Toolbar toolbar = findViewById(R.id.orderToolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        notebookRef = db.collection("Hospital");

        setUpRecyclerView();
    }
    private void setUpRecyclerView() {
        Query query = notebookRef.orderBy("patientAge", Query.Direction.DESCENDING).whereEqualTo("userId",mAuth.getCurrentUser().getUid());
        FirestoreRecyclerOptions<Data> options = new FirestoreRecyclerOptions.Builder<Data>()
                .setQuery(query, Data.class)
                .build();
        adapter = new DataAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);
    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}