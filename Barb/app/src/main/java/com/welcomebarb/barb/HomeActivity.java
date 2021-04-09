package com.welcomebarb.barb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;
import com.welcomebarb.barb.Model.Data;
import com.welcomebarb.barb.Model.BarbViewHolder;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FirestorePagingAdapter<Data, BarbViewHolder> mAdapter;
    private final FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private Query mQuery;
    Toolbar toolbar;
    private static final int PERMISSIONS_STORAGE_CODE = 1000;
    Bitmap mBitmap;
    String company,loc;
    Spinner sp_parent,sp_child;
    ArrayAdapter<String> arrayAdapter_child;
    ArrayAdapter Adapterselection,AdapterType;
    private final String TAG="HomeActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();

        FloatingActionButton fab=findViewById(R.id.btnSubmit);

        //DocumentReference docRef = mFirestore.collection("Users").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
//        docRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                DocumentSnapshot document = task.getResult();
//                if (document.exists()) {
//                    name=document.getString("name");
//                    email=document.getString("email");
//                    profileUrl=document.getString("profilePicture");
//                } else {
//                    Log.d(TAG, "No such document");
//                }
//            } else {
//                Log.d(TAG, "get failed with ", task.getException());
//            }
//        });


        CollectionReference mPostsCollection = mFirestore.collection("Barb");

        mQuery = mPostsCollection.orderBy("date", Query.Direction.DESCENDING);
        mRecyclerView = findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = findViewById(R.id.JobsprefreshLayout);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        setupAdapter();
        mSwipeRefreshLayout.setOnRefreshListener(() -> mAdapter.refresh());


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        HomeActivity.this,R.style.BottomSheetDialogTheme
                );
                final View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.order_by,
                                (RelativeLayout)findViewById(R.id.relative)
                        );
                sp_child=bottomSheetView.findViewById(R.id.spparent2);
                sp_parent=bottomSheetView.findViewById(R.id.spparent);
                final String[] selection = new String[] {"Maharashtra", "Gujarat"};
//                AdapterType=new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item);
//                sp_type.setAdapter(AdapterType);
                final String[] location = new String[]{"Surat","Ahmadabad"};
                final String[] companyName = new String[]{"Nagpur", "Pune", "Mumbai"};

//                sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                        typeString = parent.getItemAtPosition(position).toString().trim();
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> parent) {
//                        Log.e("Error","Please select"+parent);
//                    }
//                });


                Adapterselection=new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,selection);
                sp_parent.setAdapter(Adapterselection);
                sp_parent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        company=parent.getItemAtPosition(position).toString().trim();

                        if (position==0){
                            arrayAdapter_child=new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,companyName);

                        }else {
                            arrayAdapter_child=new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,location);
                        }
                        sp_child.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                loc=parent.getItemAtPosition(position).toString().trim();

                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                Log.e("Error","Please select"+parent);
                            }
                        });
                        sp_child.setAdapter(arrayAdapter_child);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Log.e("Error","Please select"+parent);
                    }
                });

                bottomSheetView.findViewById(R.id.shortButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        bottomSheetDialog.dismiss();
                        Log.d("Table: ",""+company+" "+loc);
                        //Toast.makeText(HomeActivity.this, "Sort: "+company+"_"+loc, Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(HomeActivity.this,SortActivity.class);
                        Bundle bundle = new Bundle();

                        //bundle.putString("collection", typeString);
                        bundle.putString("selection", company);
                        bundle.putString("equalto", loc);
                        intent.putExtras(bundle);
                        startActivity(intent);


                    }
                });

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);

        MenuItem profile=menu.findItem(R.id.setting_menu);

        String drawableRes= Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getPhotoUrl()).toString();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        try {
            URL url = new URL(drawableRes);


            InputStream inputStream=url.openStream();
            mBitmap= BitmapFactory.decodeStream(inputStream);

            mBitmap= getCircularBitmap(mBitmap);
            profile.setIcon(new BitmapDrawable(getResources(),mBitmap));



        } catch (IOException e) {
            Log.e("ProfileImage: ", e.getMessage());
            profile.setIcon(R.drawable.ic_launcher_background);

        }



        profile.setOnMenuItemClickListener(item1 -> {
            final Dialog dialog=new Dialog(HomeActivity.this);
            dialog.setContentView(R.layout.custom_popup);
            Button orderHistory=dialog.findViewById(R.id.orderHistory);
            Button cart=dialog.findViewById(R.id.cart);
            Button shareApp= dialog.findViewById(R.id.shareApp);
            TextView txtName= dialog.findViewById(R.id.txtName);
            TextView txtEmail= dialog.findViewById(R.id.txtEmail);
            TextView txtPos= dialog.findViewById(R.id.pos);
            TextView txtTos= dialog.findViewById(R.id.tos);
            CircleImageView profile1 =dialog.findViewById(R.id.circleImage);

            txtEmail.setText(mAuth.getCurrentUser().getEmail());
            txtName.setText(mAuth.getCurrentUser().getDisplayName());

            Picasso.get().load(Objects.requireNonNull(mAuth.getCurrentUser().getPhotoUrl()).toString()).into(profile1);

            orderHistory.setOnClickListener(v -> {
                mAuth.signOut();
                startActivity(new Intent(HomeActivity.this,MainActivity.class));
                finish();
            });
            cart.setOnClickListener(v -> {
                startActivity(new Intent(HomeActivity.this, OrderHistory.class));
                dialog.dismiss();
            });
            txtPos.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://git-source.000webhostapp.com/Privacy%20Policy.html"));
                startActivity(browserIntent);
                dialog.dismiss();
            });
            txtTos.setOnClickListener(v -> {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://git-source.000webhostapp.com/Terms%20of%20Service.html"));
                startActivity(browserIntent);
                dialog.dismiss();
            });
            shareApp.setOnClickListener(v -> {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "https://gitsource.page.link/GitSource";
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                dialog.dismiss();
            });


            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            return false;
        });
        return true;
    }



    private Bitmap getCircularBitmap(Bitmap srcBitmap) {

        int squareBitmapWidth = Math.min(srcBitmap.getWidth(), srcBitmap.getHeight());
        Bitmap dstBitmap = Bitmap.createBitmap (
                squareBitmapWidth,
                squareBitmapWidth,
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(dstBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Rect rect = new Rect(0, 0, squareBitmapWidth, squareBitmapWidth);
        RectF rectF = new RectF(rect);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        float left = (squareBitmapWidth-srcBitmap.getWidth())/2;
        float top = (squareBitmapWidth-srcBitmap.getHeight())/2;
        canvas.drawBitmap(srcBitmap, left, top, paint);
        srcBitmap.recycle();

        return dstBitmap;
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

            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull BarbViewHolder viewHolder, int i, @NonNull final Data data) {
                viewHolder.bind(data);
                viewHolder.itemView.setOnClickListener(v -> {


                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                            HomeActivity.this, R.style.BottomSheetDialogTheme
                    );
                    View bottomSheetView = LayoutInflater.from(HomeActivity.this)
                            .inflate(R.layout.bottom_sheet, findViewById(R.id.bottomsheetcontainer));
                    TextView customer=bottomSheetView.findViewById(R.id.payName);
                    TextView custEmail=bottomSheetView.findViewById(R.id.payEmail);
                    TextView PayAmount=bottomSheetView.findViewById(R.id.payAmount);
                    int available=Integer.parseInt(data.getAvailableBed());

                    PayAmount.setText("No. of Available Bed: ".concat(data.getAvailableBed()));
                    TextView mainTitle=bottomSheetView.findViewById(R.id.mainTitle);
                    mainTitle.setText(data.getTitle());
                    custEmail.setText(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
                    customer.setText(mAuth.getCurrentUser().getDisplayName());
                    ImageView productImage=bottomSheetView.findViewById(R.id.imageView2);
                    Picasso.get().load(data.getBackgroundImage()).into(productImage);

                    if (available>0){
                        bottomSheetView.findViewById(R.id.btnPay).setOnClickListener(v1 -> {

                            Intent intent=new Intent(HomeActivity.this, ApplyActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("hospitalName", data.getTitle());
                            bundle.putString("hosId", data.getHosId());
                            bundle.putString("authName", Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());
                            bundle.putString("authEmail",Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
                            intent.putExtras(bundle);
                            startActivity(intent);

                        });
                    }else {
                        bottomSheetView.findViewById(R.id.btnPay).setOnClickListener(v1 -> {
                            Toast.makeText(HomeActivity.this, "Sorry! No Bed Available", Toast.LENGTH_SHORT).show();
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

                    //Toast.makeText(HomeActivity.this, "Available Bed: "+data.getAvailableBed(), Toast.LENGTH_SHORT).show();
//                    Pair sham = new Pair<>(viewHolder.itemView, "myTitle");
//                    Intent intent=new Intent(HomeActivity.this, ProductActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("title", data.getTitle());
//                    bundle.putString("backgroundImage", data.getBackgroundImage());
//                    bundle.putString("authName", Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());
//                    bundle.putString("authEmail",Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
//                    bundle.putString("authProfile", Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getPhotoUrl()).toString());
//                    intent.putExtras(bundle);
//                    ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(HomeActivity.this,sham);
//                    startActivity(intent,activityOptions.toBundle());

                });
            }

            @Override
            protected void onError(@NonNull Exception e) {
                super.onError(e);
                Log.e("HomeActivity", Objects.requireNonNull(e.getMessage()));
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:
                        Log.d("PAGINATION: ", "Loading Initial Data!");
                    case LOADING_MORE:

                        mSwipeRefreshLayout.setRefreshing(true);
                        Log.d("PAGINATION: ", "Loading More!");
                        break;

                    case LOADED:
                        mSwipeRefreshLayout.setRefreshing(false);
                        Log.d("PAGINATION: ", "Loaded");
                        break;

                    case ERROR:
                        Log.d("PAGINATION: ", "Error!");
                        Toast.makeText(HomeActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();

                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case FINISHED:
                        mSwipeRefreshLayout.setRefreshing(false);
                        Log.d("PAGINATION: ", "All Item Loaded: " + getItemCount());
                        break;
                }
            }

        };

        mRecyclerView.setAdapter(mAdapter);

    }
}
