package com.credse.credseadmin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.credse.credseadmin.activity.aadhar.AadharSendOtp;
import com.credse.credseadmin.activity.aadhar.AadharVerifiedListActivity;
import com.credse.credseadmin.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        onClick();
    }

    private void onClick() {
        binding.createStaff.setOnClickListener(v -> startActivity(new Intent(context, StaffListActivity.class)));
        binding.createIndividuals.setOnClickListener(v -> startActivity(new Intent(context, AadharVerifiedListActivity.class)));
    }
}