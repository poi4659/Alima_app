package com.example.alima_test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class HealthActivity extends AppCompatActivity {

    TextView textViewName1; // 이름 출력
    TextView textViewName2; // 이름 출력
    private FirebaseAuth mAuth; // FirebaseAuth 인스턴스로, Firebase Authentication 기능을 사용할 수 있게 해줌
    private FirebaseFirestore db; // Firestore 사용

    private ProgressBar progressBar; // 영상 로딩 상태 표시

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);

        textViewName1 = findViewById(R.id.textView_name1);
        textViewName2 = findViewById(R.id.textView_name2);

        //뒤로가기 버튼
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HealthActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // 데이터베이스 접근 및 사용자 정보 출력:
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance(); // Firestore 초기화

        if (currentUser != null) {
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());
            userRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            if (name != null) {
                                textViewName1.setText(name);
                                textViewName2.setText(name);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // 에러 처리 로직 추가 가능
                    });
        }

        // WebView 인스턴스를 생성하고 초기화합니다.
        WebView webView1 = findViewById(R.id.webViewTip1);
        WebSettings webSettings = webView1.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // ProgressBar 추가
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        // WebViewClient를 설정하여 로딩 상태를 처리합니다.
        webView1.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // 페이지 로딩이 시작될 때 ProgressBar를 보여줍니다.
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // 페이지 로딩이 끝나면 ProgressBar를 숨깁니다.
                progressBar.setVisibility(View.GONE);
            }
        });

        // YouTube 동영상의 URL을 로드합니다.
        String youtubeUrl = "https://www.youtube.com/embed/dZewQEbQQM0";
        webView1.loadUrl(youtubeUrl);


        // 추가 동영상 WebView 인스턴스를 생성하고 초기화합니다.
        WebView webView2 = findViewById(R.id.webViewTip2);
        WebSettings webSettings2 = webView2.getSettings();
        webSettings2.setJavaScriptEnabled(true);

        // WebViewClient를 설정하여 로딩 상태를 처리합니다.
        webView2.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // 페이지 로딩이 시작될 때 ProgressBar를 보여줍니다.
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // 페이지 로딩이 끝나면 ProgressBar를 숨깁니다.
                progressBar.setVisibility(View.GONE);
            }
        });

        // 추가 동영상의 URL을 로드합니다.
        String additionalVideoUrl = "https://www.youtube.com/embed/jJJiR7nNeqQ";
        webView2.loadUrl(additionalVideoUrl);


        //수면장애 버튼 클릭 시 SleepActivity 이동
        ImageButton sleepBtn = findViewById(R.id.sleepingBtn);
        sleepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HealthActivity.this, SleepActivity.class);
                startActivity(intent);
            }
        });

        //스트레스 버튼 클릭 시 SleepActivity 이동
        ImageButton stressBtn = findViewById(R.id.stressBtn);
        stressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HealthActivity.this, StressActivity.class);
                startActivity(intent);
            }
        });

        //스트레칭 버튼 클릭 시 SleepActivity 이동
        ImageButton stretchingBtn = findViewById(R.id.stretchingBtn);
        stretchingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HealthActivity.this, StretchingActivity.class);
                startActivity(intent);
            }
        });
    }
}
