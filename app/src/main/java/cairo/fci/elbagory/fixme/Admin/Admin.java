package cairo.fci.elbagory.fixme.Admin;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import cairo.fci.elbagory.fixme.GetFilePathFromDevice;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


import cairo.fci.elbagory.fixme.Models.Workers_Models;
import cairo.fci.elbagory.fixme.R;
import id.zelory.compressor.Compressor;

@RequiresApi(api = Build.VERSION_CODES.M)
public class Admin extends AppCompatActivity {
    EditText name, phone, password, Section;
    DatabaseReference databaseReference;

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private ImageView mImageView;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;
    byte[] final_img;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        String SEC = getIntent().getStringExtra("section");
        dialog = new ProgressDialog(this);

        name = findViewById(R.id.ad_name);
        phone = findViewById(R.id.ad_phone);
        password = findViewById(R.id.ad_pass);
        Section = findViewById(R.id.ad_section);



        mButtonChooseImage = findViewById(R.id.button5);
        mButtonUpload = findViewById(R.id.button3);
        mImageView = findViewById(R.id.imageView2);

        mStorageRef = FirebaseStorage.getInstance().getReference("ProfileImages");

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(Admin.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

        Section.setText(SEC);

    }


//REQUEST PERMISSION
private void checkandroidversion(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)

        {
            try {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 555);
            } catch (Exception e) {

            }
        } else

        {
            openFileChooser();
        }
    }

    //FOR ACTIVITY RESULT PERMISSION
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 555 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openFileChooser();
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 555);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void openFileChooser() {
        CropImage.startPickImageActivity(this);
    }

    private void croprequest(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //RESULT FROM SELECTED IMAGE
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            croprequest(imageUri);
        }
        //RESULT FROM CROPING ACTIVITY
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                    mImageUri = result.getUri();
                    Glide.with(this).load(mImageUri).into(mImageView);
                    String realPath = GetFilePathFromDevice.getPath(this, mImageUri);

                    File actualImage = new File(realPath);
                    try {
                        Bitmap compressedImage = new Compressor(this)
                                .compressToBitmap(actualImage);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                        final_img = baos.toByteArray();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }



            }


        }
    }

    private void uploadFile() {
        dialog.setMessage("please wait....");
        dialog.show();
        if (mImageUri != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));



            UploadTask uploadTask = fileReference.putBytes(final_img);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {




                                    Workers_Models workers_models = new Workers_Models();


                                    workers_models.setName(name.getText().toString());
                                    workers_models.setPhone(phone.getText().toString());
                                    workers_models.setImg(uri.toString());
                                    workers_models.setSection(Section.getText().toString());
                                    workers_models.setPassword(password.getText().toString());
                                    workers_models.setRate(0);
                                    workers_models.setIScheked(false);


                                    databaseReference.child("Workers").push().setValue(workers_models);

                                    Toast.makeText(Admin.this, "Done", Toast.LENGTH_SHORT).show();
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                    }

                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Admin.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    });

        } else {
            Toast.makeText(Admin.this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void logout(View view) {


    }


}