package com.example.githubtrailblazer;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.githubtrailblazer.ui.settings.SettingsFragment;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;

import com.example.githubtrailblazer.ghapi.GHUserBasicData;
import com.squareup.picasso.Picasso;

/**
 * DrawerActivity class
 */
public class DrawerActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private final String profileUrlBase = "https://github.com/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        // setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // drawer & navigation view refs
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // add status bar height as padding to drawer header
        View hView = navigationView.getHeaderView(0);
        hView.setPadding(0, getStatusBarHeight(this), 0, 0);

        // get user details from the GitHub API and update drawer
        GHUserBasicData basicData = new GHUserBasicData();
        basicData.queryAPI(new GHUserBasicData.GHUserBasicDataCallback() {
            @Override
            public void setData(String name, String username, String avatarUrl) {
                ((TextView) hView.findViewById(R.id.sideNav__txtAccount)).setText(username);
                ((TextView) hView.findViewById(R.id.sideNav__txtDisplayName)).setText(name);
                ImageView profilePicView = (ImageView) hView.findViewById(R.id.sideNav__profilePic);
                // Fetch the profile pic and update the imageView asynchronously.
                Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.get()
                                .load(avatarUrl)
                                .into(profilePicView);
                    }
                });
            }
        });

        // add onclick listener of settings, mimic nav menu item
        hView.findViewById(R.id.sideNav__btnSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment, new SettingsFragment());
                transaction.addToBackStack(null);
                transaction.commit();
                Menu menu = navigationView.getMenu();
                for (int i = 0; i < menu.size(); i++) menu.getItem(i).setChecked(false);
                setTitle("Settings");
                toolbar.setTitle("Settings");
                drawer.closeDrawers();
            }
        });

        // add onclick listener to GitHub button -> send user to their GitHub profile
        hView.findViewById(R.id.sideNav__btnGithub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = profileUrlBase + ((TextView) hView.findViewById(R.id.sideNav__txtAccount)).getText();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_feed, R.id.nav_saved, R.id.nav_myrepos)
                .setDrawerLayout(drawer)
                .build();

        // setup navigation
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Get height of status bar
     *
     * @param context - the context
     * @return the status bar height
     */
    public static int getStatusBarHeight(final Context context) {
        final Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return (resourceId > 0) ? resources.getDimensionPixelSize(resourceId) : (int) Math.ceil(25 * resources.getDisplayMetrics().density);
    }
}