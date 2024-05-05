package com.example.notea;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.notea.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeFragment());

        binding.bottomNavBarView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.notification) {
                NotificationFragment notificationFragment = new NotificationFragment();
                notificationFragment.setMainActivity(this);
                replaceFragment(notificationFragment);
            } else if (itemId == R.id.account) {
                AccountFragment accountFragment = new AccountFragment();
                accountFragment.setMainActivity(this);
                replaceFragment(accountFragment);
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.relative_layout, fragment);
        fragmentTransaction.commit();
    }

    public void logout(){
        Intent intent = new Intent(this, Loading.class);
        finish();
        startActivity(intent);
    };
}
