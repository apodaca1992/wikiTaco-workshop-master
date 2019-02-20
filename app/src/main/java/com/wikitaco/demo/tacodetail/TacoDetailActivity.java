package com.wikitaco.demo.tacodetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.storage.StorageReference;
import com.wikitaco.demo.App;
import com.wikitaco.demo.R;

public class TacoDetailActivity extends AppCompatActivity {
    public final static String TACO_ID_KEY = "id";
    public final static String TACO_NAME_KEY = "name";
    public final static String TACO_DESCRIPTION_KEY = "description";
    public final static String TACO_RATING_KEY = "rating";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taco_detail);
        App app = (App)getApplicationContext();

        Intent intent = getIntent();
        String id = intent.getStringExtra(TACO_ID_KEY);
        String name = intent.getStringExtra(TACO_NAME_KEY);
        //boolean favorite = intent.getBooleanExtra(TACO_FAVORITE_KEY, false);
        String description = intent.getStringExtra(TACO_DESCRIPTION_KEY);
        float rating = intent.getFloatExtra(TACO_RATING_KEY, 0);

        ImageView ivTacoImg = (ImageView)findViewById(R.id.ivTacoImg);
        StorageReference storageReference = app.getTacoStorageReference(id);
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .centerCrop()
                .crossFade()
                .into(ivTacoImg);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(name);

        TextView txtDescription = (TextView) findViewById(R.id.txtDescription);
        if (description == null) {
            description = "";
            FirebaseCrash.log("No description available for " + id);
        }
        txtDescription.setText(description);

        RatingBar rbTacoRating = (RatingBar) findViewById(R.id.rbTacoRating);
        if (rating == 0.0f) {
            rating = 0;
            FirebaseCrash.log("No rating available for " + id);
        }
        rbTacoRating.setRating(rating);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Taco saved as favorite!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        app.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);

    }
}
