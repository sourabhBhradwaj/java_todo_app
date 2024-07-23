package com.youngtechie.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.squareup.picasso.Picasso;
import com.youngtechie.todoapp.modelResponse.UserResponse;
import com.youngtechie.todoapp.networkClient.RetrofitService;
import com.youngtechie.todoapp.storage.SharedPreferenceClass;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    SharedPreferenceClass sharedPreferenceClass;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;

    TextView user_name, user_email;
    ImageView user_profile_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferenceClass = new SharedPreferenceClass(this);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        View headerView = navigationView.getHeaderView(0);
        user_email = headerView.findViewById(R.id.user_email);
        user_name = headerView.findViewById(R.id.username);
        user_profile_image = headerView.findViewById(R.id.profile_Avatar);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                setDrawerClick(item.getItemId());
                item.setChecked(true);
                drawerLayout.close();
                return true;
            }
        });
        initDrawer();
        getUserProfile();

    }


    private void getUserProfile() {
        Call<JsonObject> call = RetrofitService.getInstance(this).getApi().userProfile();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(response.body(), JsonObject.class);
                boolean success = false;

                try {
                    success = jsonObject.get("success").getAsBoolean();
                    if (success) {
                        JsonObject userResp = jsonObject.get("data").getAsJsonObject();
                        UserResponse user = gson.fromJson(userResp.toString(), UserResponse.class);
                        sharedPreferenceClass.setValue_string("token", user.getToken());
                        user_email.setText(user.getEmail());
                        user_name.setText(user.getUsername());
                        Picasso.get().load(user.getAvatar()).placeholder(R.drawable.logo_white).error(R.drawable.logo_white).into(user_profile_image);
                    }
                } catch (JsonParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_share:
//                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                sharingIntent.setType("text/plain");
//                String msg = "Hey! Try This Todo App";
//                sharingIntent.putExtra(Intent.EXTRA_TEXT, msg);
//                startActivity(Intent.createChooser(sharingIntent, "Sharing Via"));
//                return true;
//            case R.id.refresh_menu:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, new HomeFragment()).commit();
//                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initDrawer() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.fragment_content, new HomeFragment());
        ft.commit();
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(drawerToggle);
    }

    private void setDrawerClick(int itemId) {
//        switch (itemId) {
//            case R.id.action_finshedTask:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, new FinishedTaskFragment()).commit();
//                break;
//            case R.id.action_home:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, new HomeFragment()).commit();
//                break;
//            case R.id.action_logout:
//                sharedPreferenceClass.clear();
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
//                finish();
//                break;
//        }
    }
}