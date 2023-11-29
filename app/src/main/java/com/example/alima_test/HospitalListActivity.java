package com.example.alima_test;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

public class HospitalListActivity extends AppCompatActivity {

    private ScrollView hospitalScrollView;

    private LinearLayout hospitalListLayout;
    private List<Hospital> hospitals; // 전체 병원 정보 저장할 리스트

    class Hospital {
        @SerializedName("Hospital Name")
        String name;
        @SerializedName("Hospital Contact")
        String phone;
        @SerializedName("Hospital Address")
        String address;
        @SerializedName("Hospital weekday")
        String weekday;
        @SerializedName("Hospital break time")
        String breakTime;
        @SerializedName("Hospital sat")
        String sat;
        @SerializedName("Hospital sun")
        String sun;

        Hospital(String name, String phone, String address, String weekday, String breakTime, String sat, String sun) {
            this.name = name;
            this.phone = phone;
            this.address = address;
            this.weekday = weekday;
            this.breakTime = breakTime;
            this.sat = sat;
            this.sun = sun;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_list);

        hospitalScrollView = findViewById(R.id.hospitalScrollView);  // XML에 있는 ScrollView의 id를 사용

        //뒤로가기 버튼
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HospitalListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        hospitalListLayout = findViewById(R.id.hospital_list_layout);

        //병원 지도보기 버튼
        Button map_button = findViewById(R.id.mapbutton);
        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HospitalListActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        // hospital_data.json 파일 읽어오기
        String json = loadJSONFromAsset();

        // JSON 파싱하여 병원 정보 추출
        Gson gson = new Gson();
        Type type = new TypeToken<List<Hospital>>() {}.getType();
        hospitals = gson.fromJson(json, type);

        // 전체병원 탭
        TextView allHospitalsTab = findViewById(R.id.all_hospitals_tab);

        // 주말 탭
        TextView weekendTab = findViewById(R.id.weekend_tab);

        // 응급실 탭
        TextView emergencyTab = findViewById(R.id.emergency_tab);

        // 전체병원 탭 클릭 시
        allHospitalsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllHospitals();
                // 선택된 탭 스타일 변경
                allHospitalsTab.setTextColor(Color.WHITE);
                allHospitalsTab.setBackgroundResource(R.drawable.box_purple);
                // 나머지 탭 스타일 초기화
                weekendTab.setTextColor(Color.GRAY);
                weekendTab.setBackgroundResource(R.drawable.border_gray);
                emergencyTab.setTextColor(Color.GRAY);
                emergencyTab.setBackgroundResource(R.drawable.border_gray);
                // 스크롤뷰를 최상단으로 이동
                hospitalScrollView.fullScroll(View.FOCUS_UP);
            }
        });

        // 주말 탭 클릭 시
        weekendTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWeekendHospitals();
                // 선택된 탭 스타일 변경
                weekendTab.setTextColor(Color.WHITE);
                weekendTab.setBackgroundResource(R.drawable.box_purple);
                // 나머지 탭 스타일 초기화
                allHospitalsTab.setTextColor(Color.GRAY);
                allHospitalsTab.setBackgroundResource(R.drawable.border_gray);
                emergencyTab.setTextColor(Color.GRAY);
                emergencyTab.setBackgroundResource(R.drawable.border_gray);
                // 스크롤뷰를 최상단으로 이동
                hospitalScrollView.fullScroll(View.FOCUS_UP);
            }
        });

        // 응급실 탭 클릭 시
        emergencyTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmergencyInfo();
                // 선택된 탭 스타일 변경
                emergencyTab.setTextColor(Color.WHITE);
                emergencyTab.setBackgroundResource(R.drawable.box_purple);
                // 나머지 탭 스타일 초기화
                allHospitalsTab.setTextColor(Color.GRAY);
                allHospitalsTab.setBackgroundResource(R.drawable.border_gray);
                weekendTab.setTextColor(Color.GRAY);
                weekendTab.setBackgroundResource(R.drawable.border_gray);
                // 스크롤뷰를 최상단으로 이동
                hospitalScrollView.fullScroll(View.FOCUS_UP);
            }
        });

        // 기본적으로 전체 병원 정보 표시
        showAllHospitals();
    }

    // 전체병원 정보 표시
    private void showAllHospitals() {
        hospitalListLayout.removeAllViews();

        // 모든 병원 정보 표시
        for (Hospital hospital : hospitals) {
            addHospitalTextView(hospital, false);
        }

        // 병원 정보가 없을 경우 안내 메시지 표시
        if (hospitalListLayout.getChildCount() == 0) {
            TextView emptyTextView = new TextView(this);
            emptyTextView.setText("표시할 병원이 없습니다.");
            hospitalListLayout.addView(emptyTextView);
        }
    }

    // 주말에 영업하는 병원 정보 표시
    private void showWeekendHospitals() {
        hospitalListLayout.removeAllViews();

        // 주말에 영업하는 병원 정보 표시
        for (Hospital hospital : hospitals) {
            if (isWeekendOpen(hospital)) {
                addHospitalTextView(hospital, true); // 평일과 휴게시간 가리기
            }
        }

        // 병원 정보가 없을 경우 안내 메시지 표시
        if (hospitalListLayout.getChildCount() == 0) {
            TextView emptyTextView = new TextView(this);
            emptyTextView.setText("주말에 영업하는 병원이 없습니다.");
            hospitalListLayout.addView(emptyTextView);
        }
    }

    // 주말에 영업하는 병원인지 확인하는 메서드
    private boolean isWeekendOpen(Hospital hospital) {
        return !hospital.sat.equals("정기휴무") && !hospital.sun.equals("정기휴무");
    }

    // 응급실에 대한 정보 표시
    private void showEmergencyInfo() {
        hospitalListLayout.removeAllViews();

        // 평일, 토요일, 일요일에 응급센터나 응급실인 병원 정보 표시
        for (Hospital hospital : hospitals) {
            if (isEmergencyHospital(hospital)) {
                addHospitalTextView(hospital, false);
            }
        }

        // 병원 정보가 없을 경우 안내 메시지 표시
        if (hospitalListLayout.getChildCount() == 0) {
            TextView emptyTextView = new TextView(this);
            emptyTextView.setText("응급실 정보가 없습니다.");
            hospitalListLayout.addView(emptyTextView);
        }
    }

    // 평일, 토요일, 일요일에 응급센터나 응급실인 병원인지 확인하는 메서드
    private boolean isEmergencyHospital(Hospital hospital) {
        boolean isWeekdayEmergency = hospital.weekday.contains("응급센터") || hospital.weekday.contains("응급실");
        boolean isSaturdayEmergency = hospital.sat.contains("응급센터") || hospital.sat.contains("응급실");
        boolean isSundayEmergency = hospital.sun.contains("응급센터") || hospital.sun.contains("응급실");

        return isWeekdayEmergency || isSaturdayEmergency || isSundayEmergency;
    }

    // 병원 정보를 네모박스로 추가
    private void addHospitalTextView(Hospital hospital, boolean hideWeekdayTime) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View hospitalView = inflater.inflate(R.layout.item_hospital, hospitalListLayout, false);
        LinearLayout itemHospitalLayout = hospitalView.findViewById(R.id.item_hospital);

        TextView nameTextView = hospitalView.findViewById(R.id.hospitalName);
        TextView addressTextView = hospitalView.findViewById(R.id.hospitalAddress);
        TextView phoneTextView = hospitalView.findViewById(R.id.hospitalPhone);
        TextView weekdayTextView = hospitalView.findViewById(R.id.hospitalWeekday);
        TextView weekdayTextViewLabel = hospitalView.findViewById(R.id.hospitalWeekdayLabel);
        TextView breakTimeTextView = hospitalView.findViewById(R.id.hospitalBreakTime);
        TextView satTextView = hospitalView.findViewById(R.id.hospitalSat);
        TextView sunTextView = hospitalView.findViewById(R.id.hospitalSun);
        TextView breakTimeTextViewLabel = hospitalView.findViewById(R.id.hospitalBreakTimeLabel);

        nameTextView.setText(hospital.name);
        addressTextView.setText(hospital.address);
        phoneTextView.setText(hospital.phone);

        if (hideWeekdayTime) {
            weekdayTextView.setVisibility(View.GONE);
            weekdayTextViewLabel.setVisibility(View.GONE);
            breakTimeTextView.setVisibility(View.GONE);
            breakTimeTextViewLabel.setVisibility(View.GONE);
        } else {
            weekdayTextView.setText(hospital.weekday);

            if (hospital.breakTime.isEmpty()) {
                breakTimeTextView.setVisibility(View.GONE);
                breakTimeTextViewLabel.setVisibility(View.GONE);
            } else {
                breakTimeTextView.setVisibility(View.VISIBLE);
                breakTimeTextViewLabel.setVisibility(View.VISIBLE);
                breakTimeTextView.setText(hospital.breakTime);
            }
        }

        satTextView.setText(hospital.sat);
        sunTextView.setText(hospital.sun);

        hospitalListLayout.addView(hospitalView);
    }


    // hospital_data.json 파일을 읽어오는 메서드
    private String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getAssets().open("hospital_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            int bytesRead = is.read(buffer);
            is.close();
            if (bytesRead > 0) {
                json = new String(buffer, "UTF-8");
                return json;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
