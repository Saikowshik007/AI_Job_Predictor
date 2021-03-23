package com.example.ai_job_predictor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {
CountryCodePicker ccp;
EditText namef,emailf,phonef,passwordf,agef,addressf;
TextView login;
Button register;
FirebaseAuth fa;
FirebaseFirestore fs;
    String userId, email, password, phone, name, address,age;
    public boolean validate(String name, String email, String password, String phone, int age) {
        if (TextUtils.isEmpty(name)) {
            namef.setError("Enter a valid name");
            namef.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(email) || !email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
            emailf.setError("Invalid Email");
            emailf.requestFocus();
            return false;
        }
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            passwordf.setError("password must contain special symbols,numbers and letters combination");
            passwordf.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(phone)) {
            phonef.setError("Enter a valid Phone");
            return false;
        }
        if(age<=17 ||age>=100){
            agef.setError("Age must be greater than 18");
            agef.requestFocus();
            return false;
        }else {
            return true;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ccp=findViewById(R.id.ccp);
        namef=findViewById(R.id.editText3);
        emailf=findViewById(R.id.editText4);
        phonef=findViewById(R.id.editText6);
        passwordf=findViewById(R.id.editText5);
        agef=findViewById(R.id.age);
        addressf=findViewById(R.id.address);
        login=findViewById(R.id.link_login);
        register=findViewById(R.id.button2);
        fa=FirebaseAuth.getInstance();
        fs=FirebaseFirestore.getInstance();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this,Login.class));
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                finish();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = namef.getText().toString().trim();
                email = emailf.getText().toString().trim();
                password = passwordf.getText().toString().trim();
                phone = "+"+ccp.getSelectedCountryCode() + phonef.getText().toString().trim();
                age = agef.getText().toString().trim();
                address=addressf.getText().toString().trim();

                if (validate(name, email, password, phone,Integer.valueOf(age))) {
                    createaccount();
                }
            }
        });
    }
    public void createaccount(){
        fa.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    //pb.setVisibility(View.INVISIBLE);
                    userId = fa.getCurrentUser().getUid();
                    Map<String, Object> user = new HashMap<>();
                    user.put("name",name);
                    user.put("phone",phone);
                    user.put("email",email);
                    user.put("age",age);
                    user.put("userId",userId);
                    user.put("address",address);
                    Toast.makeText(Register.this, "Authentication passed.", Toast.LENGTH_SHORT).show();
                    DocumentReference cf = fs.collection("Users").document(fa.getCurrentUser().getUid());
                    cf.set(user);
                    finish();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name).build();

                    fa.getCurrentUser().updateProfile(profileUpdates);

                } else {

                    Toast.makeText(Register.this, "Authentication failed."+task.getException(), Toast.LENGTH_LONG).show();
                }
                startActivity(new Intent(Register.this, MainActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
            }
        });

    }

}