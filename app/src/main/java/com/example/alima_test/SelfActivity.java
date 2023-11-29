package com.example.alima_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SelfActivity extends AppCompatActivity{

    private Button btn_eye;
    private Button btn_head;
    private Button btn_nose;

    private Button btn_arms;
    private Button btn_stomach;

    private Button btn_foot;
    private Button btn_legs;
    private Button btn_shoulder;
    private Button btn_neck;
    private Button btn_ear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self);

        // 각 버튼에 대한 참조 초기화
        btn_eye = findViewById(R.id.btn_eye);
        btn_head = findViewById(R.id.btn_head);
        btn_nose = findViewById(R.id.btn_nose);
        btn_arms = findViewById(R.id.btn_arms);
        btn_stomach = findViewById(R.id.btn_stomach);
        btn_foot = findViewById(R.id.btn_foot);
        btn_legs = findViewById(R.id.btn_legs);
        btn_shoulder = findViewById(R.id.btn_shoulder);
        btn_neck = findViewById(R.id.btn_neck);
        btn_ear = findViewById(R.id.btn_ear);

        //뒤로가기 버튼
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelfActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // 버튼 눌렀을 때 눈 통증 화면으로 전환
        btn_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelfActivity.this, EyeActivity.class);
                startActivity(intent);
            }
        });

        // 버튼 눌렀을 때 머리 통증 화면으로 전환
        btn_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelfActivity.this, HeadActivity.class);
                startActivity(intent);
            }
        });

        // 버튼 눌렀을 때 코 통증 화면으로 전환
        btn_nose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelfActivity.this, NoseActivity.class);
                startActivity(intent);
            }
        });

        // 버튼 눌렀을 때 팔 통증 화면으로 전환
        btn_arms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelfActivity.this, ArmsActivity.class);
                startActivity(intent);
            }
        });

        // 버튼 눌렀을 때 복부 통증 화면으로 전환
        btn_stomach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelfActivity.this, StomachActivity.class);
                startActivity(intent);
            }
        });


        // 버튼 눌렀을 때 발 통증 화면으로 전환
        btn_foot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelfActivity.this, FootActivity.class);
                startActivity(intent);
            }
        });

        // 버튼 눌렀을 때 다리 통증 화면으로 전환
        btn_legs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelfActivity.this, LegsActivity.class);
                startActivity(intent);
            }
        });

        // 버튼 눌렀을 때 어깨 통증 화면으로 전환
        btn_shoulder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelfActivity.this, ShoulderActivity.class);
                startActivity(intent);
            }
        });

        // 버튼 눌렀을 때 목 통증 화면으로 전환
        btn_neck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelfActivity.this, NeckActivity.class);
                startActivity(intent);
            }
        });

        // 버튼 눌렀을 때 귀 통증 화면으로 전환
        btn_ear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelfActivity.this, EarActivity.class);
                startActivity(intent);
            }
        });
    }
}
