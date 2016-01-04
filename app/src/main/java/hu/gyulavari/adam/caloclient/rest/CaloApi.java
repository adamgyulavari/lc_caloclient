package hu.gyulavari.adam.caloclient.rest;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import hu.gyulavari.adam.caloclient.BuildConfig;
import hu.gyulavari.adam.caloclient.model.Entry;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Adam on 2016.01.02..
 */
public interface CaloApi {

    String VERSION = "/api/v1";

    @FormUrlEncoded
    @POST(VERSION + "/users")
    Call<UserResponse> register(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("/oauth/token")
    Call<TokenResponse> login(@Field("grant_type") String grantType,
                              @Field("client_id") String clientId,
                              @Field("client_secret") String clientSecret,
                              @Field("username") String username,
                              @Field("password") String password);

    @GET(VERSION + "/users")
    Call<UserResponse> getUser(@Query("access_token") String token);

    @FormUrlEncoded
    @PUT(VERSION + "/users/{id}")
    Call<UserResponse> updateGoal(@Path("id")int id, @Query("access_token") String token,
                                  @Field("goal") int goal);

    @GET(VERSION + "/entries")
    Call<EntryResponse> getEnries(@Query("access_token") String token);

    @GET(VERSION + "/entries")
    Call<EntryResponse> getEnries(@Query("access_token") String token,
                                  @Query("from_date") String fromDate,
                                  @Query("to_date") String toDate,
                                  @Query("from_time") int fromTime,
                                  @Query("to_time") int toTime);


    @FormUrlEncoded
    @POST(VERSION + "/entries")
    Call<EntryResponse> createEntry(@Query("access_token") String token,
                                    @Field("title") String title,
                                    @Field("num") int num,
                                    @Field("entry_date") String entry_date,
                                    @Field("entry_time") int entry_time);

    @FormUrlEncoded
    @PUT(VERSION + "/entries/{id}")
    Call<EntryResponse> updateEntry(@Path("id") int id,
                                    @Query("access_token") String token,
                                    @Field("title") String title,
                                    @Field("num") int num,
                                    @Field("entry_date") String entry_date,
                                    @Field("entry_time") int entry_time);

    @DELETE(VERSION + "/entries/{id}")
    Call<EntryResponse> deleteEntry(@Path("id") int id,
                                    @Query("access_token") String access_token);



    class Factory {
        public static CaloApi create() {
            OkHttpClient client = new OkHttpClient();
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            if (BuildConfig.DEBUG)
                client.interceptors().add(interceptor);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.SERVER_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            return retrofit.create(CaloApi.class);
        }
    }
}
