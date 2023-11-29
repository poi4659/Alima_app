package com.example.alima_test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private LinearLayout boxLayout;
    private boolean isBoxVisible = false;
    private Marker lastClickedMarker = null;

    private Button listButton; // listButton을 멤버 변수로 선언

    class Hospital {
        String name;
        String phone;
        String address;
        String weekday;
        String breakTime;
        String sat;
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

    private HashMap<Marker, Hospital> markerHospitalMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        listButton = findViewById(R.id.listButton); // onCreate() 메서드 안에서 초기화

        //병원 목록보기 버튼
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, HospitalListActivity.class);
                startActivity(intent);
            }
        });

        //뒤로가기 버튼
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        boxLayout = findViewById(R.id.box_layout);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Places.initialize(getApplicationContext(), "AIzaSyC48hQERgOJsVW3xi4BUpgAmroLap7KIkc");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                                Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                                String addressStr = "";
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    if (addresses != null && !addresses.isEmpty()) {
                                        Address address = addresses.get(0);
                                        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                                            addressStr += address.getAddressLine(i) + " ";
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                mMap.addMarker(new MarkerOptions()
                                        .position(currentLocation)
                                        .title("현재 위치")
                                        .snippet(addressStr));
                            }
                        }
                    });

            addHospitalMarkersFromJsonFile();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (isBoxVisible) {
                    hideBoxWithAnimation();
                    if (lastClickedMarker != null) {
                        lastClickedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        lastClickedMarker = null;
                    }
                    listButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void addHospitalMarkersFromJsonFile() {
        try {
            InputStream is = getAssets().open("hospital_data.json");
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONArray jsonArray = new JSONArray(sb.toString());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String name = jsonObject.getString("Hospital Name");
                String phone = jsonObject.getString("Hospital Contact");
                String address = jsonObject.getString("Hospital Address");
                String weekday = jsonObject.getString("Hospital weekday");
                String breakTime = jsonObject.getString("Hospital break time");
                String sat = jsonObject.getString("Hospital sat");
                String sun = jsonObject.getString("Hospital sun");

                Geocoder geocoder = new Geocoder(this);
                List<Address> addresses = geocoder.getFromLocationName(address, 1);
                if (!addresses.isEmpty()) {
                    Address geoAddress = addresses.get(0);
                    LatLng latLng = new LatLng(geoAddress.getLatitude(), geoAddress.getLongitude());

                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(name)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                    markerHospitalMap.put(marker, new Hospital(name, phone, address, weekday, breakTime, sat, sun));
                }
            }

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Hospital hospital = markerHospitalMap.get(marker);
                    if (hospital != null) {
                        // 이전에 클릭한 마커가 있으면 그 색상을 원래대로 돌립니다.
                        if (lastClickedMarker != null) {
                            lastClickedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        }
                        // 클릭한 마커의 색상을 변경합니다.
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

                        // 마지막으로 클릭한 마커를 업데이트합니다.
                        lastClickedMarker = marker;

                        // 박스가 보여질 때
                        if (isBoxVisible) {
                            hideBoxWithAnimation();
                            if (lastClickedMarker != null) {
                                lastClickedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                                lastClickedMarker = null;
                            }

                            // listButton 가시성 설정
                            listButton.setVisibility(View.GONE);
                        }

                        showBoxWithAnimation(marker);

                        TextView nameTextView = findViewById(R.id.hospitalName);
                        TextView addressTextView = findViewById(R.id.hospitalAddress);
                        TextView phoneTextView = findViewById(R.id.hospitalPhone);
                        TextView weekdayTextView = findViewById(R.id.hospitalWeekday);
                        TextView breakTimeTextView = findViewById(R.id.hospitalBreakTime);
                        TextView satTextView = findViewById(R.id.hospitalSat);
                        TextView sunTextView = findViewById(R.id.hospitalSun);

                        TextView weekdayTextViewLabel = findViewById(R.id.hospitalWeekdayTextView);
                        TextView breakTimeTextViewLabel = findViewById(R.id.hospitalBreakTimeTextView);
                        TextView satTextViewLabel = findViewById(R.id.hospitalSatTextView);
                        TextView sunTextViewLabel = findViewById(R.id.hospitalSunTextView);

                        nameTextView.setText(hospital.name);
                        addressTextView.setText(hospital.address);
                        phoneTextView.setText(hospital.phone);
                        weekdayTextView.setText(hospital.weekday);

                        if (hospital.breakTime.isEmpty()) {
                            breakTimeTextView.setVisibility(View.GONE);
                            breakTimeTextViewLabel.setVisibility(View.GONE);
                        } else {
                            breakTimeTextView.setVisibility(View.VISIBLE);
                            breakTimeTextViewLabel.setVisibility(View.VISIBLE);
                            breakTimeTextView.setText(hospital.breakTime);
                        }

                        satTextView.setText(hospital.sat);
                        sunTextView.setText(hospital.sun);

                        weekdayTextViewLabel.setVisibility(View.VISIBLE);
                        satTextViewLabel.setVisibility(View.VISIBLE);
                        sunTextViewLabel.setVisibility(View.VISIBLE);
                    }
                    return true;
                }
            });

            reader.close();
            isr.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showBoxWithAnimation(Marker marker) {
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0);
        animation.setDuration(500);
        boxLayout.startAnimation(animation);
        boxLayout.setVisibility(View.VISIBLE);
        isBoxVisible = true;

        // listButton 가시성 설정
        listButton.setVisibility(View.GONE);
    }

    private void hideBoxWithAnimation() {
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1);
        animation.setDuration(500);
        boxLayout.startAnimation(animation);
        boxLayout.setVisibility(View.GONE);
        isBoxVisible = false;

        if (lastClickedMarker != null) {
            lastClickedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            lastClickedMarker = null;
        }

        // listButton 가시성 설정
        listButton.setVisibility(View.VISIBLE);
    }
}
