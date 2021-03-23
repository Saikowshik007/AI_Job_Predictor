package com.example.ai_job_predictor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.Date;

import static android.view.View.VISIBLE;

public class Profile extends AppCompatActivity {
    private static final int PIC_CROP =1 ;
    ImageButton back;
ImageView photo,pic;
Button update;
FirebaseFirestore fs;
FirebaseAuth fa;
String userId,filename,downloadUrl;
EditText a,b,c,d,f;
Cursor returnCursor;
int nameIndex, sizeIndex;
ProgressBar pb;
Uri muri;
Bitmap selectedBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        fs=FirebaseFirestore.getInstance();
        fa=FirebaseAuth.getInstance();
        update=findViewById(R.id.button5);
        a=findViewById(R.id.editTextTextPersonName2);
        b=findViewById(R.id.editTextTextPersonMail);
        c=findViewById(R.id.editTextTextPersonName);
        d=findViewById(R.id.editTextTextdate);
        f=findViewById(R.id.editTextAddress);
        pic=findViewById(R.id.imageView4);
        userId=fa.getCurrentUser().getUid();
        back=findViewById(R.id.back);
        photo=findViewById(R.id.imageView3);
        //pb=findViewById(R.id.progressBar4);
        download();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this,MainActivity.class));
                overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
                finish();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update(a.getText().toString().trim(),b.getText().toString().trim(),d.getText().toString().trim(),c.getText().toString().trim(),f.getText().toString().trim());
            }
        });
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pick();
            }
        });

    }

    @Override
    protected void onStart() {
        getdbData();
        super.onStart();
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(Profile.this, MainActivity.class));
        overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
        finish();
        super.onBackPressed();
    }
    public void getdbData(){
        //pb.setVisibility(VISIBLE);
        fs.collection("Users").whereEqualTo("userId", userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e!=null){}
                else {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            a.setText(doc.getDocument().getString("name"));
                            b.setText(doc.getDocument().getString("phone"));
                            c.setText(doc.getDocument().getString("email"));
                            d.setText(doc.getDocument().getString("age"));
                            String userId = doc.getDocument().getString("userId").trim();
                            downloadUrl=doc.getDocument().getString("url");
                            f.setText(doc.getDocument().getString("address").trim());
                            }


                        }
                    }
                }

        });
//pb.setVisibility(View.GONE);
    }
    public void update(String name,String phone,String age,String email,String address) {
        //pb.setVisibility(VISIBLE);
        DocumentReference ref = fs.collection("Users").document(fa.getCurrentUser().getUid());
        ref.update("name", name, "phone", phone, "age", age, "email", email, "address", address).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Profile.this, "success", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Profile.this, "Failed due to " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        //pb.setVisibility(View.GONE);
    }
        public void crop(Uri picUri){
            try {
                Intent cropIntent = new Intent("com.android.camera.action.CROP");
                cropIntent.setDataAndType(picUri, "image/*");
                cropIntent.putExtra("crop", true);
                cropIntent.putExtra("aspectX", 1);
                cropIntent.putExtra("aspectY", 1);
                cropIntent.putExtra("outputX", 500);
                cropIntent.putExtra("outputY", 500);
                cropIntent.putExtra("return-data", true);
                startActivityForResult(cropIntent, PIC_CROP);
            }
            catch (ActivityNotFoundException anfe) {
                String errorMessage = "Whoops - your device doesn't support the crop action!";
                Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                toast.show();
            }

        }
    public void pick() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    10001);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 10001:
                if (resultCode == RESULT_OK) {
                    muri = data.getData();
                    returnCursor =
                            getContentResolver().query(muri, null, null, null, null);
                    nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);

                    sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    returnCursor.moveToFirst();
                    filename = returnCursor.getString(nameIndex);
                    crop(muri);


                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PIC_CROP) {
            if (data != null) {
                Bundle extras = data.getExtras();
                selectedBitmap = extras.getParcelable("data");
                photo.setImageBitmap(selectedBitmap);
                upload();
            }
        }

    }
    public void upload() {
        //pb.setVisibility(View.VISIBLE);
        if (muri != null && returnCursor.getLong(sizeIndex) <= 26214400) {
           // pb.setVisibility(VISIBLE);
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference(fa.getCurrentUser().getUid());
            StorageTask<UploadTask.TaskSnapshot> task = mStorageRef.putFile(muri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(Profile.this, "Upload success!!", Toast.LENGTH_SHORT).show();
                            String downloadUrl = taskSnapshot.getUploadSessionUri().toString();
                            //pb.setVisibility(View.INVISIBLE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(Profile.this, "failed" + exception, Toast.LENGTH_SHORT).show();
                            //pb.setVisibility(View.INVISIBLE);
                        }
                    });
        } else if (returnCursor.getLong(sizeIndex) > 26214400) {
            Toast.makeText(Profile.this, "Please select a file less than 25 mb", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Profile.this, "Please select a file", Toast.LENGTH_SHORT).show();
        }
        //pb.setVisibility(View.GONE);
    }
    public void download(){
       // pb.setVisibility(VISIBLE);
        FirebaseStorage.getInstance().getReference(fa.getCurrentUser().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getApplicationContext()).load(uri).fit().centerInside().into(photo);
               // pb.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }

    }
