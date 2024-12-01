package com.credse.credseadmin.activity;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.credse.credseadmin.model.Staff;
import com.credse.credseadmin.adapter.StaffAdapater;
import com.credse.credseadmin.databinding.ActivityStaffListBinding;
import com.credse.credseadmin.retrofit.APIClient;
import com.credse.credseadmin.retrofit.APIInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffListActivity extends AppCompatActivity {
    private ActivityStaffListBinding binding;
    private static final String TAG = "StaffListActivity_";
    private final Context context = this;
    private ArrayList<Staff> staffArrayList;
    private APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStaffListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiInterface = APIClient.getClient().create(APIInterface.class);
        staffArrayList = new ArrayList<Staff>();
        onClick();
    }

    private void onClick() {
        binding.new2.setOnClickListener(v -> startActivity(new Intent(context, CreateStaffActivity.class)));
    }

    private void loadStaff() {

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("acc", "get_staff")
                .build();

        Call<ResponseBody> call = apiInterface.getStaff(requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()){
                        if (response.body() != null){
                            binding.shimmerLayout.setVisibility(View.GONE);
                            String res = response.body().string();

                            if (res.isEmpty()){
                                showError();
                                return;
                            }

                            JSONObject jsonObject = new JSONObject(res);
                            boolean mRecords =jsonObject.getBoolean("status");

                            if (mRecords){
                                JSONArray jsonArrayData = jsonObject.getJSONArray("data");

                                for (int i =0; i<jsonArrayData.length(); i++){
                                    Staff staff = new Staff();
                                    JSONObject dataJSONObject = jsonArrayData.getJSONObject(i);
                                    staff.setName(dataJSONObject.getString("name"));
                                    staff.setMobile(dataJSONObject.getString("mobile"));
                                    staffArrayList.add(staff);
                                }
                                binding.recyclerView.setEnabled(true);
                                binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                binding.recyclerView.setHasFixedSize(true);
                                binding.recyclerView.setLayoutManager(linearLayoutManager);
                                binding.recyclerView.setItemViewCacheSize(20);
                                StaffAdapater staffAdapater = new StaffAdapater(context,  staffArrayList);
                                binding.recyclerView.setAdapter(staffAdapater);
                                staffAdapater.notifyDataSetChanged();
                                return;
                            }
                            binding.shimmerLayout.setVisibility(GONE);
                            binding.recyclerView.setVisibility(GONE);
                            binding.noRecords.setVisibility(View.VISIBLE);
                        }else {
                            Log.e(TAG, "Null response!");
                        }
                    }
                } catch (Exception e) {
                  //  throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "Retrofit error! " + t.getMessage());
                showError();
            }
        });
    }

    private void showError() {
        binding.shimmerLayout.setVisibility(View.GONE);
        Toast.makeText(context, "Unable to fetch staff!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (staffArrayList != null){
            staffArrayList.clear();
        }
        binding.shimmerLayout.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.VISIBLE);
        binding.noRecords.setVisibility(View.GONE);
        loadStaff();
    }
}