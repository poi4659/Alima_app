package com.example.alima_test;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

//회원탈퇴 자바

public class WithdrawalActivity extends Activity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db; // Firestore 사용
    private Button withdrawButton;
    private CheckBox checkBox;
    private EditText editText;
    private TextView userNameTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Firestore 초기화

        withdrawButton = findViewById(R.id.withdrawButton);
        checkBox = findViewById(R.id.checkbox1);
        editText = findViewById(R.id.editText);

        userNameTextView = findViewById(R.id.userNameTextView);

        loadUserName();

        // 체크박스 상태에 따라 버튼 활성화/비활성화 및 텍스트 색상 변경
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    withdrawButton.setEnabled(true);
                    withdrawButton.setTextColor(Color.WHITE); // 체크박스가 체크된 경우 텍스트 색상은 하얀색
                } else {
                    withdrawButton.setEnabled(false);
                    withdrawButton.setTextColor(Color.DKGRAY); // 체크박스가 체크되지 않은 경우 텍스트 색상은 어두운 회색.
                }
            }
        });


        withdrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    saveUserFeedback();
                    withdrawUser();
                }
            }
        });
    }

    private void saveUserFeedback() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String feedback = editText.getText().toString();

            // feedbacks 경로 아래에 사용자 ID의 child를 만들고, 그곳에 피드백을 저장합니다.
            DocumentReference feedbackRef = db.collection("feedbacks").document(currentUser.getUid());

            // 사용자 아이디를 가진 문서 안에 피드백 내용을 저장
            feedbackRef.set(new Feedback(feedback))
                    .addOnSuccessListener(aVoid -> Toast.makeText(WithdrawalActivity.this, "피드백 저장 성공", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(WithdrawalActivity.this, "피드백 저장 실패", Toast.LENGTH_SHORT).show());
        }
    }



    private void withdrawUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());

            userRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        currentUser.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            logoutAndNavigateToLogin();
                                        } else {
                                            Toast.makeText(WithdrawalActivity.this, "Firebase Authentication 계정 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    })
                    .addOnFailureListener(e -> Toast.makeText(WithdrawalActivity.this, "회원 탈퇴에 실패했습니다.", Toast.LENGTH_SHORT).show());
        }
    }

    private void logoutAndNavigateToLogin() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(WithdrawalActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(WithdrawalActivity.this, "회원 탈퇴에 성공했습니다.", Toast.LENGTH_SHORT).show();
    }

    private void loadUserName() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());
            userRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            if (name != null) {
                                userNameTextView.setText(name + "님");
                            }
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(WithdrawalActivity.this, "유저 정보 로딩 실패", Toast.LENGTH_SHORT).show());
        }
    }
}
