package hu.gyulavari.adam.caloclient.manager;

import android.telecom.Call;

import hu.gyulavari.adam.caloclient.model.Entry;
import hu.gyulavari.adam.caloclient.model.Filter;
import hu.gyulavari.adam.caloclient.rest.CaloApi;
import hu.gyulavari.adam.caloclient.rest.EntryResponse;
import hu.gyulavari.adam.caloclient.rest.TokenResponse;
import hu.gyulavari.adam.caloclient.rest.UserResponse;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Adam on 2016.01.02..
 */
public class ApiManager {
    private static ApiManager instance;
    private CaloApi api;
    private String token;

    private static String CLIENT_ID;
    private static String CLIENT_SECRET;

    private ApiManager() {
        api = CaloApi.Factory.create();
    }

    public static ApiManager getInstance() {
        if (instance == null)
            instance = new ApiManager();
        return instance;
    }

    public static void setClientIdAndSecret(String clientId, String clientSecret) {
        CLIENT_ID = clientId;
        CLIENT_SECRET = clientSecret;
    }

    public void register(String email, String password, Callback<UserResponse> cb) {
        api.register(email, password).enqueue(cb);
    }

    public void login(String email, String password, final Callback<TokenResponse> cb) {
        api.login("password", CLIENT_ID, CLIENT_SECRET, email, password).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Response<TokenResponse> response, Retrofit retrofit) {
                token = response.body().access_token;
                cb.onResponse(response, retrofit);
            }

            @Override
            public void onFailure(Throwable t) {
                cb.onFailure(t);
            }
        });
    }

    public void getUser(final Callback<UserResponse> cb) {
        api.getUser(token).enqueue(cb);
    }

    public void updateGoal(int id, int goal, final Callback<UserResponse> cb) {
        api.updateGoal(id, token, goal).enqueue(cb);
    }

    public void getEntries(Filter filter, Callback<EntryResponse> cb) {
        if (filter.noFilter())
            api.getEnries(token).enqueue(cb);
        else {
            api.getEnries(token, filter.fromDate, filter.toDate, filter.fromTime, filter.toTime)
                    .enqueue(cb);
        }
    }

    public void createEntry(Entry entry, Callback<EntryResponse> cb) {
        api.createEntry(token, entry.title, entry.num, entry.entry_date, entry.entry_time).enqueue(cb);
    }

    public void updateEntry(Entry entry, Callback<EntryResponse> cb) {
        api.updateEntry(entry.id, token, entry.title, entry.num, entry.entry_date, entry.entry_time).enqueue(cb);
    }

    public void deleteEntry(Entry entry, Callback<EntryResponse> cb) {
        api.deleteEntry(entry.id, token).enqueue(cb);
    }
}
