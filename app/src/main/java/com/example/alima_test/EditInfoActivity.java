package com.example.alima_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditInfoActivity extends Activity {

    EditText editTextName, editTextAge;
    Button saveButton1, saveButton2;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        editTextName = findViewById(R.id.editTextName);
        editTextAge = findViewById(R.id.editTextAge);
        saveButton1 = findViewById(R.id.saveButton1);
        saveButton2 = findViewById(R.id.saveButton2);

        // 뒤로가기 버튼
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditInfoActivity.this, MyInfoActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            // Firestore에서 현재 사용자 정보를 가져와 EditText에 표시
            DocumentReference userRef = firestore.collection("users").document(currentUser.getUid());
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String name = task.getResult().getString("name");
                    int age = task.getResult().getLong("age").intValue();

                    editTextName.setText(name);
                    editTextAge.setText(String.valueOf(age));
                }
            });
        }

        // 이름 저장 버튼에 대한 클릭 리스너
        saveButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveUserName()) {
                    Toast.makeText(EditInfoActivity.this, "이름이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 나이 저장 버튼에 대한 클릭 리스너
        saveButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveUserAge()) {
                    Toast.makeText(EditInfoActivity.this, "나이가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 사용자 이름을 저장하는 메서드
    private boolean saveUserName() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String newName = editTextName.getText().toString();
            DocumentReference userRef = firestore.collection("users").document(currentUser.getUid());
            userRef.update("name", newName);
            return true;
        }
        return false;
    }

    // 사용자 나이를 저장하는 메서드
    private boolean saveUserAge() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            try {
                int newAge = Integer.parseInt(editTextAge.getText().toString());
                DocumentReference userRef = firestore.collection("users").document(currentUser.getUid());
                userRef.update("age", newAge);
                return true;
            } catch (NumberFormatException e) {
                // 숫자로 변환할 수 없는 경우
                return false;
            }
        }
        return false;
    }
}
