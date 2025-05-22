package com.android.happ.medicine.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.happ.medicine.data.UserModel;
import com.android.happ.medicine.databinding.ActivitySingUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.Nullable;

/**
 * 회원가입 담당하는 액티비티
 */
public class SignUpActivity extends AppCompatActivity {

    private ActivitySingUpBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySingUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseApp.initializeApp(this);

        firebaseAuth = FirebaseAuth.getInstance();
        itemClick();
    }

    private void itemClick() {
        // 회원가입 버튼
        binding.btJoin.setOnClickListener(v -> {
            String email = binding.edEmail.getText().toString();
            String password = binding.edPassword.getText().toString();

            if (!email.isEmpty() && !password.isEmpty()) {
                // 이메일과 비밀번호가 공백이 아닌 경우
                createUser(email, password);
            } else {
                // 이메일과 비밀번호가 공백인 경우
                Toast.makeText(SignUpActivity.this, "모든 입력란을 작성해주세요.", Toast.LENGTH_LONG).show();
            }
        });

        // 뒤로가기 버튼
        binding.imBack.setOnClickListener(v -> finish());
    }

    // 유저의 데이터를 전송하여 회원가입을 진행한다.
    private void createUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UserModel userModel = new UserModel();
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            userModel.setEmail(binding.edEmail.getText().toString());
                            userModel.setPassword(binding.edPassword.getText().toString());

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("Users").document(uid).set(userModel)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(SignUpActivity.this, "회원가입 성공", Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(Exception e) {
                                            Log.e("SignUpActivity", "onFailure: " + e.getMessage());
                                        }
                                    });
                        } else {
                            task.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    Log.e("##ERROR", "error = task error = " + e.getMessage());
                                }
                            });
                            Toast.makeText(SignUpActivity.this, "회원가입 실패", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
