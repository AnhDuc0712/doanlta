package api;

import model.User;
import model.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface UserApi {

    @Headers("Content-Type: application/json")
    @POST("/api/auth/register")
    Call<UserResponse> register(@Body User user);

    @Headers("Content-Type: application/json")
    @POST("/api/auth/login")
    Call<UserResponse> login(@Body User user);
}