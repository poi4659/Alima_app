package com.example.alima_test;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShoulderActivity extends AppCompatActivity {

    private EditText shoulderSymptom;
    private Button btnSubmit;
    private LinearLayout symptomLayout;
    private ScrollView scrollView;

    // 파이어베이스 데이터베이스 참조 가져오기
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference myRef = firestore.collection("selftest");

    // 파이어베이스 인증 객체 참조 가져오기
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String userId;

    private List<Symptom> symptomList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoulder);

        scrollView = findViewById(R.id.scrollView);

        Spinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateSymptomViews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //뒤로가기 버튼
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShoulderActivity.this, SelfActivity.class);
                startActivity(intent);
            }
        });

        shoulderSymptom = findViewById(R.id.et_symptom);
        btnSubmit = findViewById(R.id.btn_submit);
        symptomLayout = findViewById(R.id.symptomLayout);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }

        // 초기 상태에서는 버튼을 비활성화합니다.
        btnSubmit.setEnabled(false);

        // EditText에 TextWatcher를 추가합니다.
        shoulderSymptom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력된 텍스트의 길이가 0보다 크면 버튼을 활성화하고 색상을 변경합니다.
                if (s.toString().trim().length() > 0) {
                    btnSubmit.setEnabled(true);
                    btnSubmit.setBackgroundResource(R.drawable.round_button_enabled_purple);
                } else { // 입력된 텍스트의 길이가 0이면 버튼을 비활성화하고 색상을 변경합니다.
                    btnSubmit.setEnabled(false);
                    btnSubmit.setBackgroundResource(R.drawable.round_button_disabled_dkgray);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 파이어스토어에서 데이터를 읽어서 화면에 표시합니다.
        myRef.document(userId).addSnapshotListener(this, (documentSnapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (documentSnapshot != null) {
                symptomList.clear();
                Map<String, Object> dataMap = documentSnapshot.getData();
                if (dataMap != null) {
                    for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                        if (entry.getValue() instanceof Map) {
                            String date = entry.getKey();
                            Map<String, Object> innerMap = (Map<String, Object>) entry.getValue();
                            if (innerMap.containsKey("어깨")) {
                                String symptom = innerMap.get("어깨").toString();
                                symptomList.add(new Symptom(date, symptom));
                            }
                        }
                    }
                }
                // 업데이트된 symptomList로 화면을 갱신합니다.
                updateSymptomViews();
            } else {
                Log.d(TAG, "Current data: null");
            }
        });

        //저장버튼 눌렀을 때 상황
        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String symptom = shoulderSymptom.getText().toString();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                    String currentDate = sdf.format(new Date());

                    // 파이어스토어에 데이터 저장
                    Map<String, Object> data = new HashMap<>();
                    Map<String, Object> innerData = new HashMap<>();
                    innerData.put("어깨", symptom);
                    data.put(currentDate, innerData);

                    myRef.document(userId).set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // 파이어스토어에 데이터가 성공적으로 저장되면 symptomList에 추가하고 화면에 표시합니다.
                            symptomList.add(new Symptom(currentDate, symptom));

                            // 새로운 내용을 하단에 추가하는 대신 화면에 표시되는 증상들을 재정렬합니다.
                            updateSymptomViews();
                        }
                    });

                    shoulderSymptom.setText(""); // EditText 내용 지우기
                }
            });
        }
    }

    private void updateSymptomViews() {
        Spinner spinner = findViewById(R.id.spinner);
        String selectedItem = (String) spinner.getSelectedItem();

        Collections.sort(symptomList, new Comparator<Symptom>() {
            @Override
            public int compare(Symptom o1, Symptom o2) {
                if (selectedItem.equals("등록순")) {
                    return o1.getDate().compareTo(o2.getDate());
                } else if (selectedItem.equals("최신순")) {
                    return o2.getDate().compareTo(o1.getDate());
                }
                return 0;
            }
        });

        symptomLayout.removeAllViews();

        for (Symptom symptom : symptomList) {
            addOrUpdateSymptomView(symptom.getDate(), symptom.getSymptom());
        }

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_UP);
            }
        });
    }

    private void addOrUpdateSymptomView(String date, String symptom) {
        TextView symptomView = findSymptomViewByDate(date);
        if (symptomView == null) {
            symptomView = new TextView(ShoulderActivity.this);
            symptomView.setBackground(ContextCompat.getDrawable(this, R.drawable.textview_bg));
            symptomView.setPadding(
                    convertDpToPx(10), // left
                    convertDpToPx(10), // top
                    convertDpToPx(10), // right
                    convertDpToPx(10)  // bottom
            );
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    convertDpToPx(360), // width를 360dp로 설정
                    convertDpToPx(90) // height를 80dp로 설정
            );
            params.setMargins(0, 0, 0, convertDpToPx(20));
            symptomView.setLayoutParams(params);
            symptomView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17); // 텍스트 크기를 18sp로 설정
            symptomLayout.addView(symptomView);
        }

        int gap = convertDpToPx(8); // gap을 8dp로 설정
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        SpannableString dateSpannable = new SpannableString(formatDate(date) + "\n");
        dateSpannable.setSpan(new ForegroundColorSpan(Color.GRAY), 0, dateSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString gapSpannable = new SpannableString("\n");
        gapSpannable.setSpan(new RelativeSizeSpan((float) gap / symptomView.getTextSize()), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 간격을 8dp로 설정
        SpannableString symptomSpannable = new SpannableString(symptom);
        symptomSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, symptom.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 볼드 처리
        symptomSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, symptom.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 색깔을 검정색으로 설정
        spannableStringBuilder.append(dateSpannable);
        spannableStringBuilder.append(gapSpannable);
        spannableStringBuilder.append(symptomSpannable);

        symptomView.setText(spannableStringBuilder);
    }

    private TextView findSymptomViewByDate(String date) {
        String formattedDate = formatDate(date);
        for (int i = 0; i < symptomLayout.getChildCount(); i++) {
            View child = symptomLayout.getChildAt(i);
            if (child instanceof TextView && ((TextView) child).getText().toString().startsWith(formattedDate)) {
                return (TextView) child;
            }
        }
        return null;
    }

    private int convertDpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private String formatDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            Date parsedDate = sdf.parse(date);
            sdf.applyPattern("yyyy년 MM월 dd일");
            return sdf.format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }
}
