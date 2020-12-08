package com.example.project;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.regex.Pattern;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private TextView banner, registerUser;
    private EditText editTextFullName, editTextNickName, editTextEmail, editTextPassword;
    private ProgressBar progressBar;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        banner = (TextView) findViewById(R.id.banner);
        banner.setOnClickListener(this);

        registerUser = (Button) findViewById(R.id.register_btn);
        registerUser.setOnClickListener(this);

        editTextFullName = (EditText) findViewById(R.id.full_name_reg);
        editTextNickName = (EditText) findViewById(R.id.nickname_reg);
        editTextEmail = (EditText) findViewById(R.id.email_reg);
        editTextPassword = (EditText) findViewById(R.id.password_reg);

        progressBar = (ProgressBar) findViewById(R.id.progress_reg);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.banner:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.register_btn:
                registerUser();
                break;
                
        }
    }

    private void registerUser() {
        String fullName = editTextFullName.getText().toString().trim();
        String nickName = editTextNickName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(fullName.isEmpty()) {
            editTextFullName.setError("Full Name is required");
            editTextFullName.requestFocus();
            return;
        }

        if(nickName.isEmpty()) {
            editTextNickName.setError("Nickname is required");
            editTextNickName.requestFocus();
            return;
        }

        if(email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please provide valid email");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length() < 8) {
            editTextPassword.setError("Min password length should be 8 characters");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {
                            User user = new User(fullName, nickName, email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()) {
                                        Toast.makeText(RegisterUser.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);

                                        //redirect to Login Layout
                                    }else{
                                        Toast.makeText(RegisterUser.this, "Failed to register", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterUser.this, "Failed to register", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}