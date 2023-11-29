package com.example.alima_test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private TextView displayTextView;
    private Button btn_self;
    private Button btn_health;
    private Button btn_my;
    private Button btn_map;
    private Button btn_week;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db; // Firestore 사용

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textViewName = findViewById(R.id.textView_name);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance(); // Firestore 초기화

        // 사용자 이름 가져오기
        DocumentReference userRef = db.collection("users").document(currentUser.getUid());
        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        textViewName.setText(name);
                    }
                })
                .addOnFailureListener(e -> {
                    // 처리하지 않음
                });

        // 화면 전환 버튼 찾기
        btn_self = findViewById(R.id.self_button);
        btn_health = findViewById(R.id.health_button);
        btn_my = findViewById(R.id.my_button);
        btn_map = findViewById(R.id.map_button);
        btn_week = findViewById(R.id.week_btn);

        // 자가진단 버튼 눌렀을 때 자가진단 화면으로 전환
        btn_self.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SelfActivity.class);
                startActivity(intent);
            }
        });

        // 건강 꿀팁 버튼 눌렀을 때 건강 꿀팁 화면으로 전환
        btn_health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HealthActivity.class);
                startActivity(intent);
            }
        });

        // 마이페이지 버튼 눌렀을 때 마이페이지 화면으로 전환
        btn_my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyInfoActivity.class);
                startActivity(intent);
            }
        });

        // 주간리포트 버튼 눌렀을 때 주간리포트 화면으로 전환
        btn_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ReportActivity.class);
                startActivity(intent);
            }
        });

        // 현재위치 버튼 눌렀을 때 현재위치 화면으로 전환
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }
}
