package com.example.ai_job_predictor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
EditText email,password;
Button login;
FirebaseAuth fa;
FirebaseUser user;
TextView forgot,register;
ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email=findViewById(R.id.editText);
        password=findViewById(R.id.editText2);
        login=findViewById(R.id.button);
        fa=FirebaseAuth.getInstance();
        forgot=findViewById(R.id.reset);
        register=findViewById(R.id.link_signup);
        pb=findViewById(R.id.progressBar);
        user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            startActivity(new Intent(Login.this,MainActivity.class));
            finish();
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                String email_text=email.getText().toString();
                String password_text=password.getText().toString();
                if(email_text.isEmpty() || password_text.isEmpty()){
                    Toast.makeText(Login.this,"Please check your Email and Password",Toast.LENGTH_LONG).show();
                    if(email_text.isEmpty()){email.requestFocus(); email.setError("Email Invalid  or Empty");}
                    else if(password_text.isEmpty()){password.requestFocus();password.setError("Password Empty or Invalid");}
                    login.setBackgroundResource(R.drawable.rounded_orange);
                    pb.setVisibility(View.INVISIBLE);

                }
                else if(!email_text.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")){
                    Toast.makeText(Login.this,"Invalid Email",Toast.LENGTH_LONG).show();
                    email.setError("Invalid Email");
                    email.requestFocus();
                    pb.setVisibility(View.INVISIBLE);
                }
                else{
                    login.setBackgroundResource(R.drawable.rounded);
                    fa.signInWithEmailAndPassword(email_text,password_text).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Login.this,"Login failed due to:"+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            startActivity(new Intent(Login.this,MainActivity.class));
                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            finish();
                        }
                    });
                    pb.setVisibility(View.INVISIBLE);
                }


            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            forgotpassword();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,Register.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
            }
        });
    }
    public void forgotpassword(){
        pb.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError("Invalid Email");
            email.requestFocus();
            Toast.makeText(Login.this,"Enter a valid email to continue",Toast.LENGTH_LONG).show();
            pb.setVisibility(View.INVISIBLE);
        }
        else{
            pb.setVisibility(View.VISIBLE);
            fa.sendPasswordResetEmail(email.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(Login.this,"Check your email to reset your account.",Toast.LENGTH_LONG).show();
                        //pb.setVisibility(View.INVISIBLE);

                    }
                    else{
                        Toast.makeText(Login.this,"Cannot generate reset link."+task.getException(),Toast.LENGTH_LONG).show();
                        //pb.setVisibility(View.INVISIBLE);
                    }

                }
            });
            pb.setVisibility(View.INVISIBLE);
        }
    }
}