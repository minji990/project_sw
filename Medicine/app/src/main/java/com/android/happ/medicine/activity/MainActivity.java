 package com.android.happ.medicine.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.android.happ.medicine.databinding.ActivityMainBinding;
import com.android.happ.medicine.fragment.MainFragment;

 public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FragmentManager fManger;

     private String[] permissionList = {Manifest.permission.POST_NOTIFICATIONS};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setFullScreen();

        requestPermission();
    }

     private void requestPermission() {
         try {
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                 // SDK 30 이상
                 requestPermissions(permissionList, 1001);

             } else {
                 // SDK 30 미만
                 if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                     // 권한이 없는 경우 권한 요청
                     ActivityCompat.requestPermissions(this, permissionList, 1001);
                 }
             }
         } catch (IllegalArgumentException e) {
             Log.e("##ERROR", "requestPermission()   IllegalArgumentException : " + e);
         } catch (Exception e) {
             Log.e("##ERROR", "requestPermission()   Exception : " + e);
         }
     }

     /**
      * 외부 저장소 권한 요청
      */



     private void init() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        fManger = getSupportFragmentManager();
        fManger.beginTransaction().replace(binding.fragmentContainer.getId(), new MainFragment()).commit();
    }


    public void navigationToFragment(Fragment fragment) {
        FragmentManager fManger = getSupportFragmentManager();
        FragmentTransaction fTransaction = fManger.beginTransaction();
        fTransaction.replace(binding.fragmentContainer.getId(), fragment);
        fTransaction.addToBackStack(null);
        fTransaction.commit();
    }

     private void setFullScreen() {
         WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
         WindowInsetsControllerCompat con = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
         if (con != null) {
             con.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
             con.hide(WindowInsetsCompat.Type.statusBars());
             con.hide(WindowInsetsCompat.Type.navigationBars());
         }

         // 화면에 노치가 있을 경우, 노치를 무시하고 전체화면으로 보여준다.
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
             getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
         }
     }

 }