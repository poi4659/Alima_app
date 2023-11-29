package com.example.alima_test;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db; // Firestore 사용을 위한 변수
    private EditText editTextEmail, editTextPassword, editTextName, editTextAge;
    private Button buttonJoin;
    private Spinner emailSpinner;

    private String[] emailDomains = {"선택", "naver.com", "gmail.com", "daum.net", "hanmail.com"};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Firestore 초기화

        editTextEmail = findViewById(R.id.editText_email);
        editTextPassword = findViewById(R.id.editText_password);
        editTextName = findViewById(R.id.editText_name);
        editTextAge = findViewById(R.id.editText_age);
        buttonJoin = findViewById(R.id.btn_join);
        emailSpinner = findViewById(R.id.email_spinner);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, emailDomains);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emailSpinner.setAdapter(spinnerAdapter);

        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextEmail.getText().toString().equals("") && !editTextPassword.getText().toString().equals("") &&
                        !editTextName.getText().toString().equals("") && !editTextAge.getText().toString().equals("") &&
                        !emailSpinner.getSelectedItem().toString().equals("선택")) {
                    createUser(editTextEmail.getText().toString() + "@" + emailSpinner.getSelectedItem().toString(), editTextPassword.getText().toString(),
                            editTextName.getText().toString(), Integer.parseInt(editTextAge.getText().toString()));
                } else {
                    Toast.makeText(SignUpActivity.this, "모든 필드를 채워주세요.", Toast.LENGTH_LONG).show();
                }
            }
        });

        editTextEmail.addTextChangedListener(fieldWatcher);
        editTextPassword.addTextChangedListener(fieldWatcher);
        editTextName.addTextChangedListener(fieldWatcher);
        editTextAge.addTextChangedListener(fieldWatcher);

        emailSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkFieldsForEmptyValues();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                checkFieldsForEmptyValues();
            }
        });

        checkFieldsForEmptyValues();
    }

    private TextWatcher fieldWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkFieldsForEmptyValues();
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    void checkFieldsForEmptyValues() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String name = editTextName.getText().toString();
        String age = editTextAge.getText().toString();

        if(!email.equals("") && !password.equals("") && !name.equals("") && !age.equals("") && !emailSpinner.getSelectedItem().toString().equals("선택")) {
            buttonJoin.setEnabled(true);
            buttonJoin.setBackgroundResource(R.drawable.round_button_enabled_purple);
        } else {
            buttonJoin.setEnabled(false);
            buttonJoin.setBackgroundResource(R.drawable.round_button_disabled_dkgray);
        }
    }

    private void createUser(String email, String password, final String name, final int age) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 회원가입 성공 시 사용자 정보를 Firestore에 저장
                        String userId = firebaseAuth.getCurrentUser().getUid();
                        DocumentReference currentUserDb = db.collection("users").document(userId);

                        Map<String, Object> user = new HashMap<>();
                        user.put("name", name);
                        user.put("age", age);

                        currentUserDb.set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(SignUpActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(SignUpActivity.this, "데이터베이스 오류", Toast.LENGTH_SHORT).show();
                                });

                    } else {
                        Toast.makeText(SignUpActivity.this, "이미 존재하는 계정입니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
