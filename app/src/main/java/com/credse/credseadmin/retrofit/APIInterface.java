package com.credse.credseadmin.retrofit;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {
    @POST("/credse_main.php")
    Call<ResponseBody> createStaff(@Body RequestBody params);

    @POST("/credse_main.php")
    Call<ResponseBody> getStaff(@Body RequestBody params);

    @POST("/credse_main.php")
    Call<ResponseBody> aadharSendOtp(@Body RequestBody params);

    @POST("/credse_main.php")
    Call<ResponseBody> verifyAadharOTP(@Body RequestBody params);

    @POST("/credse_main.php")
    Call<ResponseBody> saveAadharDetails(@Body RequestBody params);

    @POST("/credse_main.php")
    Call<ResponseBody> getVerifiedAadharList(@Body RequestBody params);
}
