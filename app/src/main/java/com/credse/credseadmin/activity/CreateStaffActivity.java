package com.credse.credseadmin.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.credse.credseadmin.R;
import com.credse.credseadmin.databinding.ActivityCreateStaffBinding;
import com.credse.credseadmin.retrofit.APIClient;
import com.credse.credseadmin.retrofit.APIInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateStaffActivity extends AppCompatActivity {
    private ActivityCreateStaffBinding binding;
    private static final String TAG = "CreateStaffActivity_";
    private final Context context = this;
    private String mName, mMobile, mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateStaffBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        onClick();
    }

    private void onClick() {
        binding.create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMobile = binding.mobile.getText().toString().trim();
                mPassword = binding.password.getText().toString().trim();
                mName = binding.name.getText().toString().trim();

                if ("".equals(mName) || mName.isEmpty()) {
                    binding.name.setError("Enter staff name");
                    binding.name.requestFocus();
                    return;
                }
                if ("".equals(mMobile) || mMobile.isEmpty()) {
                    binding.mobile.setError("Enter mobile no");
                    binding.mobile.requestFocus();
                    return;
                }
                if (mMobile.length() < 10) {
                    binding.mobile.setError(getString(R.string.mobile_no_must_10_digits));
                    binding.mobile.requestFocus();
                    return;
                }
                if (mPassword.isEmpty()) {
                    binding.password.setError("Enter mobile no");
                    binding.password.requestFocus();
                    return;
                }
                loginNow();
            }
        });

        binding.back2.setOnClickListener(v -> finish());

        binding.new2.setOnClickListener(v -> {
            binding.conCreate.setVisibility(View.VISIBLE);
            binding.conSuccess.setVisibility(View.GONE);
            binding.createText.setVisibility(View.VISIBLE);
            binding.progressBar2.setVisibility(View.GONE);
            binding.create.setEnabled(true);
            binding.create.setFocusable(true);

            binding.name.setText("");
            binding.mobile.setText("");
            binding.password.setText("");
        });

        binding.name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.errorCon.setVisibility(View.GONE);
                binding.error.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.errorCon.setVisibility(View.GONE);
                binding.error.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.errorCon.setVisibility(View.GONE);
                binding.error.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void loginNow() {
        binding.createText.setVisibility(View.GONE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.create.setEnabled(false);
        binding.create.setFocusable(false);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("acc", "create_staff")
                .addFormDataPart("name", mName)
                .addFormDataPart("mobile", mMobile)
                .addFormDataPart("password", mPassword)
                .build();

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.createStaff(requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        try {
                            dismissProgress();
                            String res = response.body().string();

                            JSONArray jsonArray = new JSONArray(res);
                            for (int i = 0; i<jsonArray.length(); i++){
                                JSONObject jsonObject =jsonArray.getJSONObject(i);

                                String status = jsonObject.getString("status");
                                Log.i(TAG, status);
                                if (status.equals("1")){
                                    binding.conCreate.setVisibility(View.GONE);
                                    binding.conSuccess.setVisibility(View.VISIBLE);
                                    String userId = jsonObject.getString("user_id");
                                }else if (status.equals("2")) {
                                    Log.i(TAG, "User already exists!");
                                    binding.errorCon.setVisibility(View.VISIBLE);
                                    binding.error.setText("User already exists!");
                                }else {
                                    Log.e(TAG, "Error! to creating user. server side");
                                    showError();
                                }
                            }

                        } catch (IOException | JSONException e) {
                            showError();
                            Log.e(TAG, "Json parse error! " + e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Log.e(TAG, "Retrofit error! " + t.getMessage());
                    showError();
            }
        });

    }

    private void showProgress(){
        binding.createText.setVisibility(View.GONE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.create.setEnabled(false);
        binding.create.setFocusable(false);
    }

    private void dismissProgress(){
        binding.createText.setVisibility(View.VISIBLE);
        binding.progressBar2.setVisibility(View.GONE);
        binding.create.setEnabled(true);
        binding.create.setFocusable(true);
    }

    private void showError() {
        dismissProgress();
        binding.errorCon.setVisibility(View.VISIBLE);
        binding.error.setText("Unable to create user!");
    }
}