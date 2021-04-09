package com.welcomebarb.barb.Model;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.welcomebarb.barb.R;
import com.squareup.picasso.Picasso;

public class BarbViewHolder extends RecyclerView.ViewHolder {

    TextView title,location;
    ImageView backgroundImage;
    public BarbViewHolder(@NonNull View itemView) {
        super(itemView);

        title=itemView.findViewById(R.id.txtTitle);
        backgroundImage=itemView.findViewById(R.id.imageView2);
        location=itemView.findViewById(R.id.location);

    }

    public void bind(Data data) {
        title.setText(data.getTitle());
        location.setText(data.getLocation().concat(","+data.getCity()).concat(","+data.getPincode()));
        Picasso.get().load(data.getBackgroundImage()).into(backgroundImage);

    }

}
