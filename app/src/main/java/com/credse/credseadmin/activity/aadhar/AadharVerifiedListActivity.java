package com.credse.credseadmin.activity.aadhar;

import static android.view.View.GONE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.credse.credseadmin.R;
import com.credse.credseadmin.adapter.StaffAdapater;
import com.credse.credseadmin.adapter.VerifiedAadharAdapter;
import com.credse.credseadmin.databinding.ActivityAadharVerifiedListBinding;
import com.credse.credseadmin.model.Staff;
import com.credse.credseadmin.model.VerifiedList;
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

public class AadharVerifiedListActivity extends AppCompatActivity {
    private ActivityAadharVerifiedListBinding binding;
    private final String TAG = "";
    private final Context context = this;
    private ArrayList<VerifiedList> verifiedListArrayList;
    private APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAadharVerifiedListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiInterface = APIClient.getClient().create(APIInterface.class);
        verifiedListArrayList = new ArrayList<>();
        onClick();

    }

    private void onClick() {
        binding.new2.setOnClickListener(v -> startActivity(new Intent(context, AadharSendOtp.class)));
    }

    private void loadVerifiedAadharList() {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("acc", "get_verified_aadhar")
                .build();

        Call<ResponseBody> call = apiInterface.getVerifiedAadharList(requestBody);

        call.enqueue(new Callback<ResponseBody>() {
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
                                    VerifiedList verifiedList = new VerifiedList();
                                    JSONObject dataJSONObject = jsonArrayData.getJSONObject(i);
                                    verifiedList.setFirst_name(dataJSONObject.getString("first_name"));
                                    verifiedList.setAadhaar_number(dataJSONObject.getString("aadhar_no"));
                                    verifiedList.setDob(dataJSONObject.getString("dob"));
                                    verifiedList.setGender(dataJSONObject.getString("gender"));
                                    verifiedList.setFatherName(dataJSONObject.getString("father_name"));
                                    verifiedList.setDoorNo(dataJSONObject.getString("door_no"));
                                    verifiedList.setStreetName(dataJSONObject.getString("street"));
                                    verifiedList.setLandmark(dataJSONObject.getString("landmark"));
                                    verifiedList.setPostOffice(dataJSONObject.getString("post_office"));
                                    verifiedList.setPhoto(dataJSONObject.getString("photo"));
                                    verifiedListArrayList.add(verifiedList);
                                }
                                binding.recyclerView.setEnabled(true);
                                binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                binding.recyclerView.setHasFixedSize(true);
                                binding.recyclerView.setLayoutManager(linearLayoutManager);
                                binding.recyclerView.setItemViewCacheSize(20);
                                VerifiedAadharAdapter verifiedAadharAdapter = new VerifiedAadharAdapter(context,  verifiedListArrayList);
                                binding.recyclerView.setAdapter(verifiedAadharAdapter);
                                verifiedAadharAdapter.notifyDataSetChanged();
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

            }
        });

    }

    private void showError() {
        binding.shimmerLayout.setVisibility(View.GONE);
        Toast.makeText(context, "Unable to fetch aadhar verified list!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (verifiedListArrayList != null){
            verifiedListArrayList.clear();
        }
        binding.shimmerLayout.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.VISIBLE);
        binding.noRecords.setVisibility(View.GONE);
        loadVerifiedAadharList();
    }
}