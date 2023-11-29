package com.example.alima_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // Firebase Firestore 인스턴스를 가져옵니다.
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); // Firebase Authentication 인스턴스를 가져옵니다.
    private FirebaseUser currentUser = firebaseAuth.getCurrentUser(); // 현재 로그인한 사용자 정보를 가져옵니다.

    // 각 부위의 아이콘에 대한 ImageView 변수를 선언합니다. 빨간색과 주황색 두 가지 상태를 나타냅니다.
    private ImageView redFoot, orangeFoot, redStomach, orangeStomach, redArms, orangeArms,
            redShoulder, orangeShoulder, redLegs, orangeLegs, redNeck, orangeNeck, redHead,
            orangeHead, redNose, orangeNose, redEye, orangeEye, redEar, orangeEar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report1); // 레이아웃을 설정합니다.

        // TextView와 LinearLayout 변수들을 선언하고 id를 통해 찾아 할당합니다.
        TextView dangerTextView = findViewById(R.id.dangerTextView);
        TextView doubtTextView = findViewById(R.id.doubtTextView);
        TextView noTextView1 = findViewById(R.id.noTextView1);
        TextView noTextView2 = findViewById(R.id.noTextView2);
        LinearLayout noDataText = findViewById(R.id.noDataText);
        LinearLayout healthText = findViewById(R.id.healthText);
        LinearLayout sevenDangerText = findViewById(R.id.sevenDangerText);
        LinearLayout sevenDoubtText = findViewById(R.id.sevenDoubtText);

        // ImageView 변수들을 id를 통해 찾아 할당합니다.
        redFoot = findViewById(R.id.red_foot);
        orangeFoot = findViewById(R.id.orange_foot);
        redStomach = findViewById(R.id.red_stomach);
        orangeStomach = findViewById(R.id.orange_stomach);
        redArms = findViewById(R.id.red_arms);
        orangeArms = findViewById(R.id.orange_arms);
        redShoulder = findViewById(R.id.red_shoulder);
        orangeShoulder = findViewById(R.id.orange_shoulder);
        redLegs = findViewById(R.id.red_legs);
        orangeLegs = findViewById(R.id.orange_legs);
        redNeck = findViewById(R.id.red_neck);
        orangeNeck = findViewById(R.id.orange_neck);
        redHead = findViewById(R.id.red_head);
        orangeHead = findViewById(R.id.orange_head);
        redNose = findViewById(R.id.red_nose);
        orangeNose = findViewById(R.id.orange_nose);
        redEye = findViewById(R.id.red_eye);
        orangeEye = findViewById(R.id.orange_eye);
        redEar = findViewById(R.id.red_ear);
        orangeEar = findViewById(R.id.orange_ear);

        //뒤로가기 버튼
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // 모든 아이콘을 초기에 숨깁니다.
        setAllIconsInvisible();

        // 현재 사용자가 로그인 상태라면
        if (currentUser != null) {
            String userId = currentUser.getUid(); // 사용자 아이디를 가져옵니다.

            // Firestore에서 해당 사용자의 selftest 문서를 가져옵니다.
            db.collection("selftest").document(userId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) {
                                    Map<String, Integer> symptomCounts = new HashMap<>();

                                    String firstDate = Collections.min(document.getData().keySet());
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                                    Calendar firstDateCalendar = Calendar.getInstance();
                                    try {
                                        firstDateCalendar.setTime(sdf.parse(firstDate));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    Calendar currentCalendar = Calendar.getInstance();

                                    // 현재 시간과 파이어스토어에 저장된 첫 날짜를 비교
                                    if (currentCalendar.getTimeInMillis() - firstDateCalendar.getTimeInMillis() < 7 * 24 * 60 * 60 * 1000) {
                                        // 7일이 지나지 않았다면, noDataText를 보여주고, dangerTextView, doubtTextView, healthText, sevenDangerText, sevenDoubtText를 숨김
                                        noDataText.setVisibility(View.VISIBLE);
                                        dangerTextView.setVisibility(View.GONE);
                                        doubtTextView.setVisibility(View.GONE);
                                        healthText.setVisibility(View.GONE);
                                        sevenDangerText.setVisibility(View.GONE);
                                        sevenDoubtText.setVisibility(View.GONE);

                                    } else {
                                        // 7일이 지났다면, noDataText와 healthText를 숨기고, dangerTextView와 doubtTextView를 보여줌
                                        noDataText.setVisibility(View.GONE);
                                        healthText.setVisibility(View.GONE);
                                        dangerTextView.setVisibility(View.VISIBLE);
                                        doubtTextView.setVisibility(View.VISIBLE);
                                    }

                                    for (Map.Entry<String, Object> entry : document.getData().entrySet()) {
                                        Map<String, Object> dailySymptoms = (Map<String, Object>) entry.getValue();
                                        // 각 증상에 대해, 그 증상이 나타난 횟수를 symptomCounts에 추가
                                        for (String symptom : dailySymptoms.keySet()) {
                                            symptomCounts.put(symptom, symptomCounts.getOrDefault(symptom, 0) + 1);
                                        }
                                    }

                                    // 증상이 2회 미만으로 나타난 경우를 가정하고 시작
                                    boolean isSymptomCountLessThanTwo = true;
                                    for (Map.Entry<String, Integer> entry : symptomCounts.entrySet()) {
                                        // 증상이 4회 이상 나타난 경우, 해당 증상을 dangerTextView에 추가하고, 해당 부위의 빨간색 아이콘을 보여줌
                                        if (entry.getValue() >= 4) {
                                            dangerTextView.append(entry.getKey() + ", ");
                                            setIconVisibility(entry.getKey(), true, false, dangerTextView, doubtTextView);
                                            // 증상이 4회 이상 나타났으므로, isSymptomCountLessThanTwo를 false로 설정
                                            isSymptomCountLessThanTwo = false;

                                        } else if (entry.getValue() >= 2) {
                                            // 증상이 2회 이상, 4회 미만으로 나타난 경우, 해당 증상을 doubtTextView에 추가하고, 해당 부위의 주황색 아이콘을 보여줌
                                            doubtTextView.append(entry.getKey() + ", ");
                                            setIconVisibility(entry.getKey(), false, true, dangerTextView, doubtTextView);
                                            // 증상이 2회 이상 나타났으므로, isSymptomCountLessThanTwo를 false로 설정
                                            isSymptomCountLessThanTwo = false;
                                        }
                                    }

                                    // 증상 개수가 2회 미만인 경우에는 healthText만 보여주고, 다른 TextView들은 숨깁니다.
                                    if (isSymptomCountLessThanTwo) {
                                        healthText.setVisibility(View.VISIBLE);
                                        dangerTextView.setVisibility(View.GONE);
                                        noTextView1.setVisibility(View.GONE);
                                        doubtTextView.setVisibility(View.GONE);
                                        noTextView2.setVisibility(View.GONE);
                                        sevenDangerText.setVisibility(View.GONE);
                                        sevenDoubtText.setVisibility(View.GONE);
                                    } else {
                                        // dangerTextView와 doubtTextView가 비어있지 않다면 noTextView1과 noTextView2를 숨깁니다.
                                        if (dangerTextView.getText().length() > 0) {
                                            dangerTextView.setText(dangerTextView.getText().subSequence(0, dangerTextView.getText().length() - 2));
                                            noTextView1.setVisibility(View.GONE);
                                        } else {
                                            dangerTextView.setVisibility(View.GONE);
                                            noTextView1.setVisibility(View.VISIBLE);
                                        }

                                        if (doubtTextView.getText().length() > 0) {
                                            doubtTextView.setText(doubtTextView.getText().subSequence(0, doubtTextView.getText().length() - 2));
                                            noTextView2.setVisibility(View.GONE);
                                        } else {
                                            doubtTextView.setVisibility(View.GONE);
                                            noTextView2.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            }
                        }
                    });
        }
    }

    private void setIconVisibility(String symptom, boolean redVisible, boolean orangeVisible, TextView dangerTextView, TextView doubtTextView) {
        // 만약 dangerTextView와 doubtTextView에 해당 증상이 없다면, 빨간색과 주황색 아이콘을 모두 보이지 않게 설정합니다.
        if (!dangerTextView.getText().toString().contains(symptom) && !doubtTextView.getText().toString().contains(symptom)) {
            redVisible = false;
            orangeVisible = false;
        }

        // 증상에 따라 해당하는 아이콘의 가시성을 설정합니다.
        if (symptom.equals("발")) {
            setSingleIconVisibility(redFoot, orangeFoot, redVisible, orangeVisible);
        } else if (symptom.equals("배")) {
            setSingleIconVisibility(redStomach, orangeStomach, redVisible, orangeVisible);
        } else if (symptom.equals("팔")) {
            setSingleIconVisibility(redArms, orangeArms, redVisible, orangeVisible);
        } else if (symptom.equals("어깨")) {
            setSingleIconVisibility(redShoulder, orangeShoulder, redVisible, orangeVisible);
        } else if (symptom.equals("다리")) {
            setSingleIconVisibility(redLegs, orangeLegs, redVisible, orangeVisible);
        } else if (symptom.equals("목")) {
            setSingleIconVisibility(redNeck, orangeNeck, redVisible, orangeVisible);
        } else if (symptom.equals("머리")) {
            setSingleIconVisibility(redHead, orangeHead, redVisible, orangeVisible);
        } else if (symptom.equals("코")) {
            setSingleIconVisibility(redNose, orangeNose, redVisible, orangeVisible);
        } else if (symptom.equals("눈")) {
            setSingleIconVisibility(redEye, orangeEye, redVisible, orangeVisible);
        } else if (symptom.equals("귀")) {
            setSingleIconVisibility(redEar, orangeEar, redVisible, orangeVisible);
        } else {
            // Handle the case when the symptom is not recognized
            // For example, you can log a message or add additional handling logic
        }
    }



    private void setAllIconsInvisible() {
        // 모든 아이콘을 보이지 않게 설정합니다. 각 아이콘에 대해 setSingleIconVisibility 메소드를 호출합니다.
        setSingleIconVisibility(redFoot, orangeFoot, false, false);
        setSingleIconVisibility(redStomach, orangeStomach, false, false);
        setSingleIconVisibility(redArms, orangeArms, false, false);
        setSingleIconVisibility(redShoulder, orangeShoulder, false, false);
        setSingleIconVisibility(redLegs, orangeLegs, false, false);
        setSingleIconVisibility(redNeck, orangeNeck, false, false);
        setSingleIconVisibility(redHead, orangeHead, false, false);
        setSingleIconVisibility(redNose, orangeNose, false, false);
        setSingleIconVisibility(redEye, orangeEye, false, false);
        setSingleIconVisibility(redEar, orangeEar, false, false);
    }

    private void setSingleIconVisibility(ImageView redIcon, ImageView orangeIcon, boolean redVisible, boolean orangeVisible) {
        // 파라미터에 따라 아이콘의 가시성을 설정합니다.
        redIcon.setVisibility(redVisible ? View.VISIBLE : View.GONE);
        orangeIcon.setVisibility(orangeVisible ? View.VISIBLE : View.GONE);
    }
}