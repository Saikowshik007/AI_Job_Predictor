package com.example.ai_job_predictor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;

import org.tensorflow.lite.Interpreter;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.sql.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int PIC_CROP =1 ;
    EditText et;
    Button b,c;
    ProgressBar pb;
    FirebaseAuth fa;
    private NavigationView navigationView;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    boolean stop =true;
    Interpreter interpreter;
    @Override
    protected void onStart(){
        super.onStart();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        actionBarDrawerToggle.syncState();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        et = findViewById(R.id.editTextTextMultiLine);
        Classifier classifier = new Classifier(this, "dict.json");
        classifier.setMaxlength(2000);
        b = (Button)findViewById(R.id.button);
        c = (Button)findViewById(R.id.button2);
        pb=findViewById(R.id.progressBar);
        fa=FirebaseAuth.getInstance();
        if(fa.getCurrentUser()==null) {startActivity(new Intent(MainActivity.this,Welcome.class));finish();}
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_orange));
                pb.setVisibility(View.VISIBLE);
                final String message = et.getText().toString().trim().toLowerCase();
                // Toast.makeText(getBaseContext(),"message:"+message,Toast.LENGTH_LONG).show();
                if (message.length()<150)
                {Toast.makeText(getBaseContext(), "Please enter text length of >150",Toast.LENGTH_LONG).show(); pb.setVisibility(View.INVISIBLE);}
                else{
                    classifier.setCallback(new Classifier.DataCallback() {
                        @Override
                        public void onDataProcessed(HashMap<String, Integer> result) {
                            try {
                                classifier.setVocab(result);
                                ArrayList<Integer> tokenizedMessage = classifier.tokenize(message);
                                ArrayList<Integer> paddedMessage = classifier.padSequence(tokenizedMessage);
                                classifySequence(paddedMessage);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    classifier.loadData();

                }
            }
        });
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et.setText("");
                b.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded));
            }
        });

    }
    public void classifySequence(List<Integer>sequence) throws IOException {
        final String s ="";
        String p="";
        ByteBuffer input=ByteBuffer.allocateDirect(2000*4).order(ByteOrder.nativeOrder());
       float[][] outputs=new float[1][25];
        for (float value: sequence) { input.putFloat(value); }
       FirebaseCustomRemoteModel remoteModel = new FirebaseCustomRemoteModel.Builder("converted_model.tflite").build();
     FirebaseModelManager.getInstance().getLatestModelFile(remoteModel)
                .addOnCompleteListener(new OnCompleteListener<File>() {
                    @Override
                    public void onComplete(@NonNull Task<File> task) {
                        float[][] output = new float[1][25];
                        File modelFile = task.getResult();
                        if (modelFile != null) {
                            interpreter = new Interpreter(modelFile);
                        } else {
                            try {
                                InputStream inputStream = getAssets().open("converted_model.tflite");
                                byte[] model = new byte[inputStream.available()];
                                inputStream.read(model);
                                ByteBuffer buffer = ByteBuffer.allocateDirect(model.length)
                                        .order(ByteOrder.nativeOrder());
                                buffer.put(model);
                                interpreter = new Interpreter(buffer);
                                Log.d("loaded", "Loaded");
                            } catch (IOException e) {
                                // File not found?
                            }
                        }
                        interpreter.run(input, output);
                        startActivity(new Intent(MainActivity.this,Results.class).putExtra("list",output[0]));
                       pb.setVisibility(View.INVISIBLE);

                    }
                });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_profile:
                startActivity(new Intent(MainActivity.this,Profile.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
                break;
            case R.id.about_app:
                startActivity(new Intent(MainActivity.this, About.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
                break;

            case R.id.logout_user:
                fa.signOut();
                startActivity(new Intent(this,Welcome.class));
                overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
                finish();
                break;

            case R.id.refresh:
                if (stop) {
                    pb.setVisibility(View.VISIBLE);
                    finish();
                    startActivity(getIntent());
                    getSupportActionBar().setTitle("Good to go..");
                    stop = true;
                    pb.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(MainActivity.this, "No refresh required", Toast.LENGTH_LONG).show();

                }
                break;
            case R.id.nav_share:
                Intent shareIntent =   new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Now get blood donations with ease at your location");
                String app_url = "https://drive.google.com/drive/folders/1FlfmK-cDQMoGbUzicx4BtYpwHR6fanr1?usp=sharing";
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,app_url);
                startActivity(Intent.createChooser(shareIntent, "Share via"));
                break;
            case R.id.rate:
                Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
                }
                break;
        }

        // menuItem.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }
//    public void pick() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("*/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        try {
//            startActivityForResult(
//                    Intent.createChooser(intent, "Select a File to Upload"),
//                    101);
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(this, "Please install a File Manager.",
//                    Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        String content;
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==101){
//        Uri selectedfile_uri = data.getData();
//            try {
//                InputStream in = getContentResolver().openInputStream(selectedfile_uri);
//                BufferedReader r = new BufferedReader(new InputStreamReader(in, "utf-8"));
//                StringBuilder total = new StringBuilder();
//                for (String line; (line = r.readLine()) != null; ) {
//                    Log.d("out",line);
//                    total.append(line).append('\n');
//                }
//
//                content = total.toString();
//                et.setText(content);
//
//
//
//            }catch (Exception e) {
//
//            }
//
//        }
//
//    }
//

}