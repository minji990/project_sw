package com.android.happ.medicine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.happ.medicine.data.UserModel;
import com.android.happ.medicine.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        itemClick();

        binding.edEmail.setText("test@test.com");
        binding.edPassword.setText("aaaaaa");

        binding.btLogin.performClick();
    }

    private void itemClick() {
        // 로그인 버튼 클릭 시 Firebase Auth로 유저 정보를 전송
        binding.btLogin.setOnClickListener(view -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String email = binding.edEmail.getText().toString();
            String password = binding.edPassword.getText().toString();

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            getUserInfoToDatabase();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Log.i("##INFO", "onComplete(): failure", task.getException());
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e ->
                            Log.e("##ERROR", "Login Error: " + e.getMessage())
                    );
        });

        // 회원가입 페이지로 이동
        binding.btSignup.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    // 유저가 로그인 했을 때 유저 데이터를 가져오는 메서드
    private void getUserInfoToDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String id = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (id != null) {
            db.collection("Users").document(id).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        UserModel userInfo = documentSnapshot.toObject(UserModel.class);
                        if (userInfo != null) {
                            // 유저 정보 로드 성공 처리
                        }
                    })
                    .addOnFailureListener(e ->
                            Log.d(TAG, "onFailure: " + e.getMessage())
                    );
        } else {
            Log.i("##INFO", "UID is null");
        }
    }

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
}
