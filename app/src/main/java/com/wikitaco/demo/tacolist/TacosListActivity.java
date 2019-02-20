package com.wikitaco.demo.tacolist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.wikitaco.demo.App;
import com.wikitaco.demo.R;
import com.wikitaco.demo.login.LoginActivity;
import com.wikitaco.demo.models.Taco;
import com.wikitaco.demo.tacodetail.TacoDetailActivity;

public class TacosListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnItemClickListener  {

    private App app;
    private RecyclerView rvTacos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tacos_list);

        app = (App) getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setProfileInformation();

        //recycler view
        rvTacos = (RecyclerView) findViewById(R.id.rvTacos);

        app.initLayoutManager();
        rvTacos.setLayoutManager(app.getLayoutManager());

        TacoRecyclerAdapter adapter = new TacoRecyclerAdapter(getApplicationContext(),
                app.getTacoListReference(), this);
        rvTacos.setAdapter(adapter);
    }

    private void setProfileInformation() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Drawer header values: picture, name, email
        View headerLayout = navigationView.getHeaderView(0);
        TextView txtName = (TextView) headerLayout.findViewById(R.id.txtName);
        TextView txtEmail = (TextView) headerLayout.findViewById(R.id.txtEmail);
        ImageView imgAvatar = (ImageView) headerLayout.findViewById(R.id.imgAvatar);

        String strWelcome = String.format(getString(R.string.greeting_message), app.getName());
        txtName.setText(strWelcome);

        String email = app.getEmail();
        if (!email.isEmpty()) {
            txtEmail.setText(app.getEmail());
        } else {
            txtEmail.setVisibility(View.GONE);
        }

        Uri photoUrl = app.getPhotoUrl();
        if (photoUrl != null) {
            Glide.with(this)
                    .load(photoUrl)
                    .into(imgAvatar);
        }

    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        supportFinishAfterTransition();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.taco_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_singout) {

            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            app.deinitLayoutManager();
                            Intent i = new Intent(TacosListActivity.this, LoginActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        }
                    });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemClick(Taco taco) {
        Intent intent = new Intent(this, TacoDetailActivity.class);
        intent.putExtra(TacoDetailActivity.TACO_ID_KEY, taco.getId());
        intent.putExtra(TacoDetailActivity.TACO_NAME_KEY, taco.getName());
        intent.putExtra(TacoDetailActivity.TACO_DESCRIPTION_KEY, taco.getDescription());
        intent.putExtra(TacoDetailActivity.TACO_RATING_KEY, taco.getRating());

        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, (View) findViewById(R.id.ivTacoImg), "tacoImg");
        startActivity(intent, options.toBundle());
    }
}
