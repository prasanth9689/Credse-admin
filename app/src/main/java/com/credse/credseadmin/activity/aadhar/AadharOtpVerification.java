package com.credse.credseadmin.activity.aadhar;

import android.content.Context;
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
import com.credse.credseadmin.databinding.ActivityAadharOtpVerificationBinding;
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

public class AadharOtpVerification extends AppCompatActivity {
    private ActivityAadharOtpVerificationBinding binding;
    private static final String TAG = "AadharOtpVerification_";
    private final Context context = this;
    private APIInterface apiInterface;
    private String OTP , mAadharNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAadharOtpVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiInterface = APIClient.getClient().create(APIInterface.class);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        binding.edAadharOtp.requestFocus();

        onClick();
        mAadharNo = getIntent().getStringExtra("aadhar_no");
    }

    private void onClick() {
        binding.verify.setOnClickListener(v -> {
            OTP = binding.edAadharOtp.getText().toString();
            if (OTP.isEmpty()) {
                binding.edAadharOtp.setError(getString(R.string.enter_otp));
                binding.edAadharOtp.requestFocus();
                return;
            }
            if (OTP.length() < 6) {
                binding.edAadharOtp.setError(getString(R.string.enter_4_digits_aadhar_otp));
                binding.edAadharOtp.requestFocus();
                return;
            }
            verifyOTP();
        });

        binding.back.setOnClickListener(v -> finish());
    }

    private void verifyOTP() {
        String client_id = getIntent().getStringExtra("client_id");
        if (client_id != null){
            if (!client_id.isEmpty()){

                showProgress();

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("acc", "verify_aadhar_otp")
                        .addFormDataPart("client_id", client_id)
                        .addFormDataPart("otp", OTP)
                        .build();

                Call<ResponseBody> call = apiInterface.verifyAadharOTP(requestBody);

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
                                    binding.progressText.setText("OTP Verification sucssess");

                                    saveAadharDetails(res);
                                    Log.i(TAG, jsonObject2.toString());
                                   // binding.response.setText(jsonObject2.toString());
                                    largeLog("res__", res);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

                    }
                });
            }
        }
    }

    private void saveAadharDetails(String res) {

        String mFullName, mProfileImage, mFatherName, mAadharData,mGender;
        String mDOB, mCountry, mDistrict , mState, mPostName, mLoc;
        String mVtc, mSubDistrict, mStreet, mHouse, mLandmark, mPinCode;
        try {
            JSONObject jsonObject = new JSONObject(res);
            JSONObject jsonObject2 = jsonObject.getJSONObject("data");
            JSONObject jsonObjectAddr = jsonObject2.getJSONObject("address");

            mAadharData = jsonObject2.toString();
            mFullName = jsonObject2.getString("full_name");
            mProfileImage = jsonObject2.getString("profile_image");
            mFatherName = jsonObject2.getString("care_of");
            mGender = jsonObject2.getString("gender");
            mDOB = jsonObject2.getString("dob");

            mPinCode = jsonObject2.getString("zip");

            mCountry = jsonObjectAddr.getString("country");
            mDistrict = jsonObjectAddr.getString("dist");

            mState = jsonObjectAddr.getString("state");
            mPostName = jsonObjectAddr.getString("po");

            mLoc = jsonObjectAddr.getString("loc"); // Not sql queried only
            mVtc = jsonObjectAddr.getString("vtc"); // Not sql queried only

            mSubDistrict = jsonObjectAddr.getString("subdist"); // Not sql queried only
            mStreet = jsonObjectAddr.getString("street");

            mHouse = jsonObjectAddr.getString("house"); // saving column name ( c_door_no )
            /*
                "house": "NO 124",
             */
            mLandmark = jsonObjectAddr.getString("landmark");

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Log.e("ff__", mProfileImage);

        binding.progressText.setText("Please wait");

        String NO_VALUE = "Nill";

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("acc", "save_aadhar_details")
                .addFormDataPart("aadhar_no", mAadharNo)
                .addFormDataPart("aadhar_data", mAadharData)
                .addFormDataPart("photo", mProfileImage)
                .addFormDataPart("title", "Mr")
                .addFormDataPart("first_name", mFullName)
                .addFormDataPart("surname", NO_VALUE)
                .addFormDataPart("father_name", mFatherName)
                .addFormDataPart("mother_name", NO_VALUE)
                .addFormDataPart("gender", "Male")
                .addFormDataPart("dob", mDOB)
                .addFormDataPart("country", mCountry)
                .addFormDataPart("district", mDistrict)
                .addFormDataPart("state", mState)
                .addFormDataPart("po", mPostName)
                .addFormDataPart("loc", mLoc) // Not sql queried only
                .addFormDataPart("vtc", mVtc) // Not sql queried only
                .addFormDataPart("subdist", mSubDistrict) // Not sql queried only
                .addFormDataPart("street", mStreet)
                .addFormDataPart("house", mHouse)
                .addFormDataPart("landmark", mLandmark)
                .addFormDataPart("zip", mPinCode)
                .build();

        Call<ResponseBody> call = apiInterface.saveAadharDetails(requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        if (res.equals(" ") || res.isEmpty()){
                            Log.e(TAG, "Empty or Not valid response!");
                            return;
                        }

                        JSONObject jsonObject = null;
                        String status = "";
                        try {
                            jsonObject = new JSONObject(res);
                            status = jsonObject.getString("status");
                            Log.i(TAG, "Response status" +status);
                        } catch (JSONException e) {
                          //  throw new RuntimeException(e);
                                Log.e(TAG, "JSON Parse Error!");
                            binding.verifyCon.setVisibility(View.GONE);
                            binding.successCon.setVisibility(View.VISIBLE);
                        }

                        if (!status.isEmpty()){
                            if (status.equals("true")){
                                Log.i(TAG, "Saved success");
                                binding.verifyCon.setVisibility(View.GONE);
                                binding.successCon.setVisibility(View.VISIBLE);
                                return;
                            }
                           Log.e(TAG, "Failed! try again");
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
               Log.e(TAG, "Failed!"+ t.getMessage());
            }
        });
    }

    public static void largeLog(String tag, String content) {
        if (content.length() > 4000) {
            Log.d(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.d(tag, content);
        }
    }

    private void dismissProgress() {
        binding.progressBar2.setVisibility(View.GONE);
        binding.progressText.setText("Verify");
        binding.verify.setEnabled(true);

        final int sdk = Build.VERSION.SDK_INT;
        if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
            binding.verify.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_black) );
        } else {
            binding.verify.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_black));
        }
    }

    private void showProgress() {
        binding.errorCon.setVisibility(View.GONE);
        binding.error.setText("");

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        binding.verify.setEnabled(false);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.progressText.setText("Verifying OTP");
        binding.verify.setEnabled(false);

        final int sdk = Build.VERSION.SDK_INT;
        if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
            binding.verify.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_disabled_button) );
        } else {
            binding.verify.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_disabled_button));
        }
    }
}