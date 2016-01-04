package hu.gyulavari.adam.caloclient.rest;

import java.io.Serializable;

/**
 * Created by Adam on 2016.01.02..
 */
public class UserResponse implements Serializable {
    public String email;
    public int id;
    public int entries_count;
    public int goal;
}
