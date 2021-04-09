package com.welcomebarb.barb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.perf.util.Utils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.welcomebarb.barb.Model.Upload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


public class ApplyActivity extends AppCompatActivity {

    String hospitalName;

    private CircleImageView userImage;
    private TextInputEditText userName, userPhone, userAddress,userAge, userAadhar, usersymptoms;
    private String name,phone,address,age,aadhar,symptoms,hosId;
    private ProgressBar progressBar;
    private Button submit;
    private Uri imageUri = null;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    Uri downloadUri;
    private Bitmap compressed;
    private static final int PICK_IMAGE_REQUEST = 1;


    //private FirebaseAuth mAuth;
    //private Task<Uri> mUploadTask;
    private Uri mImageUri;
    private StorageReference storageRef;
    private String patientDocId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);

        //mAuth=FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        hospitalName = bundle.getString("hospitalName");
        hosId=bundle.getString("hosId");
        Toolbar toolbar = findViewById(R.id.applyBar);
        toolbar.setTitle(hospitalName);
        setSupportActionBar(toolbar);


        userImage = findViewById(R.id.user_image);
        userName = findViewById(R.id.textName);
        userPhone = findViewById(R.id.textContact);
        userAddress = findViewById(R.id.textAdd);
        userAge = findViewById(R.id.textAge);
        userAadhar = findViewById(R.id.textAadhar);
        usersymptoms = findViewById(R.id.textSymp);

        progressBar=findViewById(R.id.progress_bar);

        submit = findViewById(R.id.submit);
        firebaseAuth = FirebaseAuth.getInstance();



        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        userImage.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                                 if (ContextCompat.checkSelfPermission(ApplyActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                                                     //Toast.makeText(ApplyActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                                                     ActivityCompat.requestPermissions(ApplyActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                                                 } else {

                                                     openFileChooser();
                                                     //choseImage();

                                                 }

                                             } else {

                                                 openFileChooser();
                                                 //choseImage();

                                             }

                                         }

                                     }

        );


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 name = Objects.requireNonNull(userName.getText()).toString();
                 phone = Objects.requireNonNull(userPhone.getText()).toString();
                 address = Objects.requireNonNull(userAddress.getText()).toString();
                 age = Objects.requireNonNull(userAge.getText()).toString();
                 aadhar = Objects.requireNonNull(userAadhar.getText()).toString();
                 symptoms = Objects.requireNonNull(usersymptoms.getText()).toString();

                if (mImageUri==null ||TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address) || TextUtils.isEmpty(age) || TextUtils.isEmpty(aadhar) || TextUtils.isEmpty(symptoms)) {
                    Toast.makeText(getApplicationContext(), "Fill all the detail first!", Toast.LENGTH_SHORT).show();
                }else {

                    progressBar.setVisibility(View.VISIBLE);
                    submit.setEnabled(false);
                    uploadFile();
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(userImage);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) {
            StorageReference ref = storageRef.child(System.currentTimeMillis() + "" + "." + getFileExtension(mImageUri) + ".jpg");
            UploadTask image_path = ref.putFile(mImageUri);
            Task<Uri> urlTask = image_path.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (task.isSuccessful()) {

                        UploadTask.TaskSnapshot downloadUri = task.getResult();
                        Log.e("TASK:", "" + downloadUri.toString());
                    }


                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult(); //this is the download url that you need to pass to your database
                        Log.e("URL:", "" + downloadUri.toString());

                        Map<String, Object> hos = new HashMap<>();
                        hos.put("hospitalName", hospitalName);
                        hos.put("phone", phone);
                        hos.put("patientProfile", downloadUri.toString());
                        hos.put("patientName", name);
                        hos.put("patientAddress", address);
                        hos.put("patientStatus", "Pending");
                        hos.put("patientAge", age);
                        hos.put("patientSymptoms", symptoms);
                        hos.put("patientAadhar", aadhar);
                        hos.put("hosId", hosId);
                        hos.put("userId", Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());

                        firebaseFirestore.collection("Hospital")
                                .add(hos)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        patientDocId=documentReference.getId();
                                        Log.e("ApplyActivity", "DocumentSnapshot added with ID: "+patientDocId);


                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("ApplyActivity", "Error adding document", e);
                                        Toast.makeText(ApplyActivity.this, "Error: "+e, Toast.LENGTH_SHORT).show();
                                    }
                                });
                        Toast.makeText(ApplyActivity.this, "Patient Detailed Send, Wait for Approval", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(ApplyActivity.this,HomeActivity.class));
                        progressBar.setVisibility(View.GONE);
                        submit.setEnabled(true);

                    } else {
                        /// Handle failures
                        // ...
                        Toast.makeText(ApplyActivity.this, "Failed to send!!!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        submit.setEnabled(true);
                    }
                }
            });
        }

    }

}