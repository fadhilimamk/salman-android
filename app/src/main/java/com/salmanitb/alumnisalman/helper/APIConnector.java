package com.salmanitb.alumnisalman.helper;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.salmanitb.alumnisalman.model.About;
import com.salmanitb.alumnisalman.model.BaseResponse;
import com.salmanitb.alumnisalman.model.CheckEmailResponse;
import com.salmanitb.alumnisalman.model.GeocodingResponse;
import com.salmanitb.alumnisalman.model.SalmanActivity;
import com.salmanitb.alumnisalman.model.User;
import com.salmanitb.alumnisalman.model.UserAuth;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Fadhil Imam Kurnia on 01/04/2018.
 */

public class APIConnector{
    private static String BASE_URL = "http://pplk2h.if.itb.ac.id";
    private static APIConnector apiConnector;

    public static APIConnector getInstance() {
        if (apiConnector == null)
            apiConnector = new APIConnector();
        return apiConnector;
    }

    public interface ApiCallback<T> {
        void onSuccess(T response);
        void onFailure(Throwable t);
    }


    public void doLogin(final String email, final String password, final ApiCallback<UserAuth> callback) {
        String hashedPassword = getMD5(password);

        Call<BaseResponse<UserAuth>> call = WebService.APIServiceImplementation.getInstance().doLogin(email, hashedPassword);
        call.enqueue(new Callback<BaseResponse<UserAuth>>() {
            @Override
            public void onResponse(Call<BaseResponse<UserAuth>> call, Response<BaseResponse<UserAuth>> response) {
                BaseResponse<UserAuth> responseBody = response.body();
                if (responseBody == null) {
                    callback.onFailure(new Throwable("Terjadi kesalahan pada sistem kami"));
                    return;
                }

                if (!responseBody.isSuccess()) {
                    if (responseBody.getError() != null)
                        callback.onFailure(new Throwable(responseBody.getError().getMessage()));
                    else
                        callback.onFailure(new Throwable("Terjadi kesalahan!"));
                } else {
                    callback.onSuccess(responseBody.getData());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<UserAuth>> call, Throwable t) {
                Log.e("Error", t.getMessage());
                callback.onFailure(new Throwable("Periksa koneksi anda!"));
            }
        });
    }

    public void doRegister(final User user, final ApiCallback<UserAuth> callback) {
        String hashedPassword = getMD5(user.getPassword());
        Gson gson = new Gson();
        ArrayList<String> activities = new ArrayList<>();
        ArrayList<String> activitiesYear = new ArrayList<>();
        for (SalmanActivity activity : user.getActivities()) {
            activities.add(activity.getTitle());
            StringBuilder sb = new StringBuilder();
            sb.append(activity.getStartYear());
            if (!activity.getEndYear().trim().equals(""))
                sb.append("-");
            sb.append(activity.getEndYear());
            activitiesYear.add(sb.toString());
        }
        Call<BaseResponse<UserAuth>> call = WebService.APIServiceImplementation.getInstance().doRegister(
                user.getName(),
                user.getEmail(),
                hashedPassword,
                user.getSex(),
                user.getCountry(),
                user.getCity(),
                user.getAddress(),
                user.getLatitude(),
                user.getLongitude(),
                user.getPhonenumber(),
                user.getUniversity(),
                user.getMajor(),
                user.getYearUniversity(),
                user.getLmd(),
                user.getJob(),
                user.getCompany(),
                gson.toJson(activities),
                gson.toJson(activitiesYear),
                user.getQuestion1(),
                user.getQuestion2(),
                user.getAnswer1(),
                user.getAnswer2()
        );

        call.enqueue(new Callback<BaseResponse<UserAuth>>() {
            @Override
            public void onResponse(Call<BaseResponse<UserAuth>> call, Response<BaseResponse<UserAuth>> response) {
                BaseResponse<UserAuth> responseBody = response.body();
                if (responseBody != null) {
                    if (!responseBody.isSuccess()) {
                        callback.onFailure(new Throwable(responseBody.getError().getMessage()));
                        return;
                    }
                    callback.onSuccess(responseBody.getData());
                    return;
                }
                callback.onFailure(new Throwable("Terjadi kesahalah sistem, coba beberapa saat lagi"));
            }

            @Override
            public void onFailure(Call<BaseResponse<UserAuth>> call, Throwable t) {
                Log.e("Error", t.getMessage());
                callback.onFailure(new Throwable("Periksa koneksi anda!"));
            }
        });
    }

    public void checkEmail(String email, final ApiCallback<CheckEmailResponse> callback) {
        Call<BaseResponse<CheckEmailResponse>> call = WebService.APIServiceImplementation.getInstance().checkEmail(email);
        call.enqueue(new Callback<BaseResponse<CheckEmailResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<CheckEmailResponse>> call, Response<BaseResponse<CheckEmailResponse>> response) {
                BaseResponse<CheckEmailResponse> responseBody = response.body();
                if (responseBody == null) {
                    callback.onFailure(new Throwable("Terjadi kesalahan pada sistem kami"));
                    return;
                }

                if (!responseBody.isSuccess()) {
                    if (responseBody.getError() != null)
                        callback.onFailure(new Throwable(responseBody.getError().getMessage()));
                    else
                        callback.onFailure(new Throwable("Terjadi kesalahan!"));
                } else {
                    callback.onSuccess(responseBody.getData());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<CheckEmailResponse>> call, Throwable t) {
                Log.e("Error", t.getMessage());
                callback.onFailure(new Throwable("Periksa koneksi anda!"));
            }
        });
    }

    public void getAbout(final ApiCallback<About> callback) {
        WebService.APIServiceImplementation.getInstance().getAbout().enqueue(new Callback<About>() {
            @Override
            public void onResponse(@NonNull Call<About> call, @NonNull Response<About> response) {
                if (response.body() == null) {
                    callback.onFailure(new Throwable("Empty response"));
                    return;
                }
                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<About> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void checkAddress(final String address, final ApiCallback<GeocodingResponse> callback) {
        Call<GeocodingResponse> call = WebService.APIServiceImplementation
                .getGeocodingInstance()
                .checkAddress(
                        GeocodingWebService.GOOGLE_KEY,
                        address,
                        GeocodingWebService.DEFAULT_LANGUAGE);
        call.enqueue(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                GeocodingResponse responseBody = response.body();
                if (responseBody == null || !responseBody.getStatus().equals("OK")) {
                    callback.onFailure(new Throwable("Terjadi kesalahan sistem"));
                    return;
                }
                callback.onSuccess(responseBody);
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                Log.e("Error", t.getMessage());
                callback.onFailure(new Throwable("Periksa koneksi anda!"));
            }
        });
    }


    public static String getMD5(String input) {
        if (input == null)
            input = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


}
