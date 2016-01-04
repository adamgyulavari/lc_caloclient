package hu.gyulavari.adam.caloclient;

import android.app.Application;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import hu.gyulavari.adam.caloclient.manager.ApiManager;

/**
 * Created by Adam on 2016.01.03..
 */
public class CaloApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Iconify.with(new FontAwesomeModule());
        ApiManager.getInstance().setClientIdAndSecret(getString(R.string.client_id), getString(R.string.client_secret));
    }
}
