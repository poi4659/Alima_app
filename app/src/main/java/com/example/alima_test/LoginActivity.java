package com.example.alima_test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogIn;
    private Button buttonSignUp;

    private CheckBox checkBoxRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.edittext_email);
        editTextPassword = findViewById(R.id.edittext_password);

        buttonSignUp = findViewById(R.id.btn_signup);

        checkBoxRememberMe = findViewById(R.id.checkbox_remember_me);

        SharedPreferences sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("rememberMe", false)) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SignUpActivity 연결
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        buttonLogIn = findViewById(R.id.btn_login);
        buttonLogIn.setEnabled(false);
        buttonLogIn.setBackgroundResource(R.drawable.round_button_disabled_dkgray);

        editTextEmail.addTextChangedListener(textWatcher);
        editTextPassword.addTextChangedListener(textWatcher);

        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextEmail.getText().toString().equals("") && !editTextPassword.getText().toString().equals("")) {
                    loginUser(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                    if (checkBoxRememberMe.isChecked()) {
                        sharedPreferences.edit().putBoolean("rememberMe", true).apply();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                }
            }
        });

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                }
            }
        };
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String emailInput = editTextEmail.getText().toString().trim();
            String passwordInput = editTextPassword.getText().toString().trim();

            buttonLogIn.setEnabled(!emailInput.isEmpty() && !passwordInput.isEmpty());

            if (buttonLogIn.isEnabled()) {
                buttonLogIn.setBackgroundResource(R.drawable.round_button_enabled_purple);
            } else {
                buttonLogIn.setBackgroundResource(R.drawable.round_button_disabled_dkgray);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    public void loginUser(String email, String password) {
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // 로그인 성공
                                Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                                firebaseAuth.addAuthStateListener(firebaseAuthListener);
                            } else {
                                // 로그인 실패
                                Exception exception=task.getException();
                                if(exception!=null){
                                    Toast.makeText(LoginActivity.this,"로그인 실패: "+exception.getMessage(),Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(LoginActivity.this,"로그인 실패",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }
}
