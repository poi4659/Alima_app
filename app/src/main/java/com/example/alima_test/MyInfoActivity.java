package com.example.alima_test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class MyInfoActivity extends Activity {

    TextView textViewName, textViewAge;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db; // 파이어스토어 데이터베이스 객체
    ImageView profileImageView;

    private Button buttonLogOut;
    private static final int REQUEST_CODE_PICK_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        textViewName = findViewById(R.id.textView_name);
        textViewAge = findViewById(R.id.textView_age);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance(); // 파이어스토어 데이터베이스 객체 초기화

        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        profileImageView = findViewById(R.id.profileImageView);
        Button editImageButton = findViewById(R.id.editImageButton);

        // 뒤로가기 버튼
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyInfoActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // 이미지 편집 버튼 클릭 리스너 설정
        editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        ImageView profileImageView = findViewById(R.id.profileImageView);

        if (currentUser != null) {
            // 현재 사용자의 UID를 기반으로 사용자 정보를 가져오는 코드
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        int age = documentSnapshot.getLong("age").intValue();
                        String profileImageUrl = documentSnapshot.getString("profileImageUrl");

                        if (profileImageUrl != null) {
                            Glide.with(MyInfoActivity.this).load(profileImageUrl).into(profileImageView);
                        } else {
                            profileImageView.setImageResource(R.drawable.logo_alima);
                        }

                        textViewName.setText(name);
                        textViewAge.setText(String.valueOf(age));

                        String updatedName = getIntent().getStringExtra("updatedName");
                        int updatedAge = getIntent().getIntExtra("updatedAge", age);

                        if (updatedName != null) {
                            textViewName.setText(updatedName);
                        }

                        if (updatedAge != age) {
                            textViewAge.setText(String.valueOf(updatedAge));
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // 사용자 정보 가져오기 실패 시 처리할 코드
                }
            });
        }

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutAndNavigateToLogin();
            }
        });

        Button editInfoButton = findViewById(R.id.editInfoButton);
        editInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editInfo();
            }
        });

        Button withdrawalButton = findViewById(R.id.withdrawalButton);
        withdrawalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomWithdrawalConfirmationDialog();
            }
        });
    }

    // 이미지 선택 다이얼로그 표시
    public void selectImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyInfoActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_profile_edit, null);

        Button galleryButton = view.findViewById(R.id.galleryButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        builder.setView(view);
        AlertDialog dialog = builder.create();

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, REQUEST_CODE_PICK_PHOTO);
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == REQUEST_CODE_PICK_PHOTO && resultCode == RESULT_OK) {
            Uri selectedImage = imageReturnedIntent.getData();
            profileImageView.setImageURI(selectedImage);

            FirebaseUser currentUser = mAuth.getCurrentUser();

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child("profileImages/" + currentUser.getUid());
            UploadTask uploadTask = imageRef.putFile(selectedImage);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // 파이어스토어에 프로필 이미지 URL 업데이트
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("profileImageUrl", uri.toString());
                                    db.collection("users").document(currentUser.getUid())
                                            .set(data, SetOptions.merge()) // 기존 문서와 병합
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(MyInfoActivity.this, "프로필 이미지가 업로드되었습니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(MyInfoActivity.this, "프로필 이미지 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                                    Log.e("Firestore Upload Error", e.getMessage(), e);
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MyInfoActivity.this, "프로필 이미지 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            Log.e("Upload error", e.getMessage(), e);
                        }
                    });
        }
    }

    // 탈퇴 다이얼로그 표시
    private void showCustomWithdrawalConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        builder.setView(view);

        Button confirmButton = view.findViewById(R.id.cancelButton);
        Button cancelButton = view.findViewById(R.id.confirmButton);

        final AlertDialog dialog = builder.create();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyInfoActivity.this, WithdrawalActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // 로그아웃 및 로그인 화면으로 이동
    private void logoutAndNavigateToLogin() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MyInfoActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(MyInfoActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
    }

    // 사용자 정보 수정 화면으로 이동
    private void editInfo() {
        Intent intent = new Intent(this, EditInfoActivity.class);
        startActivity(intent);
    }
}
