package com.example.sagar.chatapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    private Toolbar toolbar;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabPagerAdapter mTabPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        mViewPager = findViewById(R.id.view_pager);
        mTabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabPagerAdapter);
        mTabLayout = findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);


        toolbar = findViewById(R.id.main_app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("My Chat");

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            signOutUser();
        }
    }

    private void signOutUser() {
        Intent intent = new Intent(getApplicationContext(), Welcom2Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);


        if (item.getItemId() == R.id.logout) {
            mAuth.signOut();
            signOutUser();
        }


        if (item.getItemId() == R.id.setting) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.show_all_users) {
            Intent intent = new Intent(MainActivity.this, AllUsersActivity.class);
            startActivity(intent);
        }


        return true;

    }
}
