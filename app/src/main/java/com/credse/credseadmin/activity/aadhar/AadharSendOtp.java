package com.credse.credseadmin.activity.aadhar;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.credse.credseadmin.R;
import com.credse.credseadmin.databinding.ActivitySendOtpBinding;
import com.credse.credseadmin.retrofit.APIClient;
import com.credse.credseadmin.retrofit.APIInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AadharSendOtp extends AppCompatActivity {
    private ActivitySendOtpBinding binding;
    private final Context context = this;
    private static final String TAG = "SendOtp_";
    private APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiInterface = APIClient.getClient().create(APIInterface.class);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        binding.edAadharNo.requestFocus();

        onClick();
    }

    private void onClick() {
        binding.sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aadharNo = binding.edAadharNo.getText().toString();
                if (aadharNo.isEmpty()) {
                    binding.edAadharNo.setError(getString(R.string.enter_aadhar_no));
                    binding.edAadharNo.requestFocus();
                    return;
                }
                if (aadharNo.length() < 12) {
                    binding.edAadharNo.setError(getString(R.string.enter_12_digits_aadhar_no));
                    binding.edAadharNo.requestFocus();
                    return;
                }

                sendOtp(aadharNo);
            }
        });

        binding.back.setOnClickListener(v -> finish());
    }

    private void sendOtp(String aadharNo) {

       showProgress();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("acc", "send_aadhar_otp")
                .addFormDataPart("aadhar_no", aadharNo)
                .build();

        Call<ResponseBody> call = apiInterface.aadharSendOtp(requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        try {
                         String res = response.body().string();

                         if (res.equals(" ")){
                             return;
                         }

                         if (res.isEmpty()){
                             return;
                         }

                            JSONObject jsonObject = new JSONObject(res);
                            String success = jsonObject.getString("success");

                            if (success.equals("false")) {
                                String message = jsonObject.getString("message");
                                if (!message.isEmpty()){
                                    binding.errorCon.setVisibility(View.VISIBLE);
                                    binding.error.setText(message);
                                    dismissProgress();
                                }
                                return;
                            }

                            JSONObject jsonObject2 = jsonObject.getJSONObject("data");
                            String client_id = jsonObject2.getString("client_id");

                            Intent intent = new Intent(context, AadharOtpVerification.class);
                            intent.putExtra("client_id", client_id);
                            intent.putExtra("aadhar_no", aadharNo);
                            startActivity(intent);

                            Log.e(TAG, client_id);

                        } catch (IOException | JSONException e) {
                           // throw new RuntimeException(e);
                            if (e.getMessage() != null){
                                Log.e(TAG, "IOException | JSONException e : " + e.getMessage());
                               showError();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (t.getMessage() == null){
                    showError();
                    Log.e(TAG, "onFailure :" + t.getMessage());
                }
            }
        });
    }

    private void showError() {
        dismissProgress();
        Toast.makeText(context, "Uanble to send OTP, Try again!", Toast.LENGTH_SHORT).show();
    }

    private void showProgress() {

        binding.errorCon.setVisibility(View.GONE);
        binding.error.setText("");

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        binding.sendOtp.setEnabled(false);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.progressText.setText("Sending OTP");
        binding.sendOtp.setEnabled(false);

        final int sdk = Build.VERSION.SDK_INT;
        if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
            binding.sendOtp.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_disabled_button) );
        } else {
            binding.sendOtp.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_disabled_button));
        }
    }

    private void dismissProgress(){
        binding.progressBar2.setVisibility(View.GONE);
        binding.progressText.setText("Send OTP");
        binding.sendOtp.setEnabled(true);

        final int sdk = Build.VERSION.SDK_INT;
        if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
            binding.sendOtp.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_black) );
        } else {
            binding.sendOtp.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_black));
        }
    }
}