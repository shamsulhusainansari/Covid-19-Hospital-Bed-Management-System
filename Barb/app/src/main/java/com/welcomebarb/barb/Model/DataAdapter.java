package com.welcomebarb.barb.Model;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;
import com.welcomebarb.barb.R;

public class DataAdapter extends FirestoreRecyclerAdapter<Data, DataAdapter.DataHolder> {
    public DataAdapter(@NonNull FirestoreRecyclerOptions<Data> options) {
        super(options);
    }
    @Override
    protected void onBindViewHolder(@NonNull DataHolder holder, int position, @NonNull Data model) {
        holder.patientName.setText(model.getPatientName());
        holder.patientMob.setText(model.getPhone());
        if (model.patientStatus.equals("Approved")){
            holder.patientStatus.setTextColor(Color.GREEN);
            holder.patientStatus.setText(String.valueOf(model.getPatientStatus()));
        }else {
            holder.patientStatus.setTextColor(Color.RED);
            holder.patientStatus.setText(String.valueOf(model.getPatientStatus()));
        }

        holder.hospitalName.setText(String.valueOf(model.getHospitalName()));
        Picasso.get().load(model.getPatientProfile()).into(holder.patientProfile);
    }
    @NonNull
    @Override
    public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item,
                parent, false);
        return new DataHolder(v);
    }
    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }
    static class DataHolder extends RecyclerView.ViewHolder {
        TextView patientName,patientMob,patientStatus,hospitalName;
        ImageView patientProfile;
        public DataHolder(View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.patientName);
            patientMob = itemView.findViewById(R.id.patientContact);
            patientStatus = itemView.findViewById(R.id.status);
            hospitalName = itemView.findViewById(R.id.hospitalName);
            patientProfile = itemView.findViewById(R.id.patientProfile);

        }
    }
}
