package com.wikitaco.demo;

import android.app.Application;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class App extends Application{
    private FirebaseAuth firebaseAuth;
    private FirebaseAnalytics firebaseAnalytics;
    private FirebaseRemoteConfig firebaseRemoteConfig;

    private DatabaseReference firebaseDatabseReference;
    private StorageReference firebaseStorageReference;

    private boolean favoritesEnabled;
    private GridLayoutManager gridLayoutManager;

    private final static String NEW_TACOS_TOPIC = "tacos";
    private final static String TACO_LIST_CHILD = "tacos";
    private final static String TACO_LIST_EXPERIMENT_VARIANT_A = "variant_a";
    private final static String TACO_LIST_EXPERIMENT_VARIANT_B = "variant_b";

    @Override
    public void onCreate() {
        super.onCreate();

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //db & storage references
        firebaseDatabseReference = db.getReference();
        firebaseStorageReference = storage.getReference();

        //notifications
        FirebaseMessaging.getInstance().subscribeToTopic(NEW_TACOS_TOPIC);
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        //enable developer mode for frequent refreshes
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();

        firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
    }

    public FirebaseAuth getAuth() {
        return firebaseAuth;
    }

    public String getName(){
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String name = "";
        if (currentUser != null && currentUser.getDisplayName() != null) {
            return currentUser.getDisplayName();
        }
        return name;
    }

    public String getEmail(){
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String email = "";
        if (currentUser != null && currentUser.getEmail() != null) {
            return currentUser.getEmail();
        }
        return email;
    }

    public Uri getPhotoUrl(){
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        Uri photoUrl = null;
        if (currentUser != null) {
            return currentUser.getPhotoUrl();
        }
        return photoUrl;
    }

    public DatabaseReference getTacoListReference(){
        return firebaseDatabseReference.child(TACO_LIST_CHILD).getRef();
    }

    public StorageReference getTacoStorageReference(String key){
        return firebaseStorageReference.child(TACO_LIST_CHILD).child(key + ".jpg");
    }

    public void initLayoutManager(){
        gridLayoutManager = new GridLayoutManager(this, 1);
        long cacheExpiration = 3600; // 1 hour in seconds.
        if (firebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        firebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseRemoteConfig.activateFetched();

                            //fetch remote value
                            favoritesEnabled = firebaseRemoteConfig.getBoolean("favorites_enabled");
                            String tacoListColumnsExperiment = firebaseRemoteConfig
                                    .getString("taco_list_columns_experiment");

                            int tacoListColumns = 1;
                            if (tacoListColumnsExperiment.equals(TACO_LIST_EXPERIMENT_VARIANT_A)) {
                                tacoListColumns = 3;
                            } else if (tacoListColumnsExperiment.equals(TACO_LIST_EXPERIMENT_VARIANT_B)) {
                                tacoListColumns = 2;
                            }
                            gridLayoutManager.setSpanCount(tacoListColumns);

                            //analytics binding for a/b test
                            firebaseAnalytics.setUserProperty("taco_list_columns",tacoListColumnsExperiment);
                            //Log.e(tag,tacoListColumnsExperiment);
                        }
                    }
                });

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int maxSpan = gridLayoutManager.getSpanCount();
                if ((position % (maxSpan+1) == maxSpan)) {
                    return maxSpan;
                } else {
                    return 1;
                }
            }
        });

    }

    public void deinitLayoutManager(){
        gridLayoutManager = null;
    }


    public GridLayoutManager getLayoutManager() {
        return gridLayoutManager;
    }


    public void logEvent(String event, Bundle eventInfo) {
        firebaseAnalytics.logEvent(event, eventInfo);
    }
}
