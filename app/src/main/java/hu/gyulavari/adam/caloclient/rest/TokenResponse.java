package hu.gyulavari.adam.caloclient.rest;

/**
 * Created by Adam on 2016.01.02..
 */
public class TokenResponse {
    public String access_token;
    public String token_type;
    public int expires_in;
    public String refresh_token;
    public long created_at;
}
